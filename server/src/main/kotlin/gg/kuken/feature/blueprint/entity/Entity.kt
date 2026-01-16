package gg.kuken.feature.blueprint.entity

import gg.kuken.feature.blueprint.repository.BlueprintRepository
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.statements.api.ExposedBlob
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

object BlueprintTable : UUIDTable("blueprints") {
    val origin = varchar("origin", 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val content = blob("content")
}

class BlueprintEntity(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BlueprintEntity>(BlueprintTable)

    var origin: String by BlueprintTable.origin
    var createdAt: Instant by BlueprintTable.createdAt
    var updatedAt: Instant by BlueprintTable.updatedAt
    var content: ExposedBlob by BlueprintTable.content
}

class BlueprintRepositoryImpl(
    private val database: Database,
) : BlueprintRepository {
    init {
        transaction(db = database) {
            SchemaUtils.createMissingTablesAndColumns(BlueprintTable)
        }
    }

    override suspend fun findAll(): List<BlueprintEntity> =
        suspendTransaction(db = database) {
            BlueprintEntity.all().notForUpdate().toList()
        }

    override suspend fun find(id: Uuid): BlueprintEntity? =
        suspendTransaction(db = database) {
            BlueprintEntity.findById(id.toJavaUuid())
        }

    override suspend fun create(
        id: Uuid,
        origin: String,
        spec: ByteArray,
        createdAt: Instant,
    ): BlueprintEntity =
        suspendTransaction(db = database) {
            BlueprintEntity.new(id.toJavaUuid()) {
                content = ExposedBlob(spec)
                this.origin = origin
                this.createdAt = createdAt
                this.updatedAt = createdAt
            }
        }

    override suspend fun delete(id: Uuid) {
        suspendTransaction(db = database) {
            BlueprintEntity.findById(id.toJavaUuid())?.delete()
        }
    }
}
