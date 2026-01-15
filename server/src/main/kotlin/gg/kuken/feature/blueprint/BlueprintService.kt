package gg.kuken.feature.blueprint

import gg.kuken.feature.account.IdentityGeneratorService
import gg.kuken.feature.blueprint.entity.BlueprintEntity
import gg.kuken.feature.blueprint.model.Blueprint
import gg.kuken.feature.blueprint.processor.ResolvedBlueprint
import gg.kuken.feature.blueprint.repository.BlueprintRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.logging.log4j.LogManager
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

class BlueprintService(
    private val blueprintRepository: BlueprintRepository,
    private val blueprintSpecProvider: BlueprintSpecProvider,
    private val identityGeneratorService: IdentityGeneratorService,
) {
    private val logger = LogManager.getLogger(BlueprintService::javaClass)

    private companion object {
        private val json: Json =
            Json {
                coerceInputValues = false
            }
    }

    suspend fun listBlueprints(): List<Blueprint> = blueprintRepository.findAll().map(::toModel)

    suspend fun getBlueprint(id: Uuid): Blueprint =
        blueprintRepository.find(id)?.let(::toModel)
            ?: throw BlueprintNotFoundException()

    suspend fun importBlueprint(source: BlueprintSpecSource): Blueprint {
        logger.debug("Importing {}", source)
        val processed = blueprintSpecProvider.provide(source)

        val encoded = json.encodeToString(processed).encodeToByteArray()
        val entity =
            blueprintRepository.create(
                id = identityGeneratorService.generate(),
                spec = encoded,
                createdAt = Clock.System.now(),
            )

        return toModel(entity, processed)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun toModel(
        entity: BlueprintEntity,
        spec: ResolvedBlueprint? = null,
    ): Blueprint {
        val spec = spec ?: json.decodeFromStream<ResolvedBlueprint>(entity.content.inputStream)
        return Blueprint(
            id = entity.id.value.toKotlinUuid(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            spec = spec,
        )
    }
}
