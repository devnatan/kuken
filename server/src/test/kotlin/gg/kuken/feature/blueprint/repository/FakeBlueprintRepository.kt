package gg.kuken.feature.blueprint.repository

import gg.kuken.feature.blueprint.entity.BlueprintEntity
import gg.kuken.feature.blueprint.model.BlueprintHeader
import gg.kuken.feature.blueprint.model.BlueprintStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

class FakeBlueprintRepository : BlueprintRepository {
    override suspend fun findAll(): List<BlueprintEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun find(id: Uuid): BlueprintEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun create(
        id: Uuid,
        origin: String,
        createdAt: Instant,
        status: BlueprintStatus,
        header: BlueprintHeader,
    ): BlueprintEntity {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Uuid) {
        TODO("Not yet implemented")
    }
}
