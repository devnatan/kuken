package gg.kuken.feature.blueprint

import gg.kuken.feature.account.IdentityGeneratorService
import gg.kuken.feature.blueprint.entity.BlueprintEntity
import gg.kuken.feature.blueprint.model.Blueprint
import gg.kuken.feature.blueprint.processor.BlueprintResolutionContext
import gg.kuken.feature.blueprint.processor.Resolvable
import gg.kuken.feature.blueprint.processor.ResolvedBlueprint
import gg.kuken.feature.blueprint.repository.BlueprintRepository
import gg.kuken.feature.instance.DockerInstanceService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.devnatan.dockerkt.DockerClient
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

class BlueprintService(
    private val blueprintRepository: BlueprintRepository,
    private val blueprintSpecProvider: BlueprintSpecProvider,
    private val identityGeneratorService: IdentityGeneratorService,
    private val dockerClient: DockerClient,
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

    suspend fun processBlueprint(
        blueprintId: Uuid,
        resolutionContext: BlueprintResolutionContext,
    ): ResolvedBlueprint {
        val blueprint = getBlueprint(blueprintId)

        val dockerImage: String =
            when (val dockerImageProperty = blueprint.spec.build.docker.image) {
                is Resolvable.EnvVarRef, is Resolvable.InputRef, is Resolvable.Interpolated, is Resolvable.RuntimeRef -> {
                    val replaced =
                        DockerInstanceService.performSubstitutions(
                            property = dockerImageProperty,
                            blueprint = blueprint.spec,
                            resolutionContext = resolutionContext,
                        )

                    requireNotNull(replaced)
                }

                is Resolvable.Literal -> {
                    dockerImageProperty.value
                }

                Resolvable.Null -> {
                    error("Missing Docker image")
                }
            }

        resolveBlueprint(
            origin = blueprint.origin,
            blueprint = blueprint.spec,
            dockerImage = dockerImage,
        )

        return blueprint.spec
    }

    private suspend fun saveBlueprint(
        source: BlueprintSpecSource,
        processed: ResolvedBlueprint,
    ): Blueprint {
        val encoded = json.encodeToString(processed).encodeToByteArray()
        val entity =
            blueprintRepository.create(
                id = identityGeneratorService.generate(),
                origin = source.uri,
                spec = encoded,
                createdAt = Clock.System.now(),
            )

        return toModel(entity, processed)
    }

    suspend fun importBlueprint(source: BlueprintSpecSource): Blueprint {
        logger.debug("Importing {}", source)
        val blueprint = blueprintSpecProvider.provide(source)
        val saved = saveBlueprint(source, blueprint)
        return saved
    }

    private suspend fun resolveBlueprint(
        origin: String,
        blueprint: ResolvedBlueprint,
        dockerImage: String,
    ) {
        logger.debug("Resolving blueprint from {} with {}", origin, dockerImage)
        val resourcesDir = File(".kuken/resources")

        if (blueprint.resources.isNotEmpty()) {
            logger.debug("Downloading required resources...")

            blueprint.resources.forEach { resource ->
                logger.debug("Downloading \"{}\" from {}...", resource.name, origin)

                if (resource.source.startsWith("file://")) {
                    val resourceUrl = origin.substringBeforeLast("/") + "/" + resource.source.replace("file://", "")
                    logger.debug("Downloading {}", resourceUrl)

                    val client = HttpClient(CIO)
                    val outputFile = File(resourcesDir, resource.name)
                    outputFile.parentFile.mkdirs()
                    outputFile.createNewFile()

                    try {
                        val httpResponse: HttpResponse =
                            client.get(resourceUrl) {
                                onDownload { bytesSentTotal, contentLength ->
                                    logger.debug("Received $bytesSentTotal bytes from $contentLength")
                                }
                            }

                        val responseBody: ByteArray = httpResponse.body()
                        outputFile.writeBytes(responseBody)
                        logger.debug("Download of {} finished: {}", resource.name, outputFile.absolutePath)
                    } catch (e: Exception) {
                        logger.debug("Download failed: ${e.message}")
                    } finally {
                        client.close()
                    }
                }
            }
        }
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
            origin = entity.origin,
            spec = spec,
        )
    }
}
