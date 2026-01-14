package gg.kuken.feature.blueprint

import gg.kuken.feature.blueprint.model.ProcessedBlueprint
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.File
import java.nio.channels.UnresolvedAddressException

interface BlueprintSpecProvider {
    val providerId: String

    suspend fun provide(source: BlueprintSpecSource): ProcessedBlueprint
}

data class CombinedBlueprintSpecProvider(
    val providers: List<BlueprintSpecProvider>,
) : BlueprintSpecProvider {
    override val providerId: String
        get() = providers.joinToString(" + ", transform = BlueprintSpecProvider::providerId)

    override suspend fun provide(source: BlueprintSpecSource): ProcessedBlueprint {
        for (provider in providers) {
            try {
                return provider.provide(source)
            } catch (_: UnsupportedBlueprintSpecSource) {
                continue
            }
        }

        throw NoMatchingBlueprintSpecProviderException()
    }
}

class RemoteBlueprintSpecProvider(
    private val processor: BlueprintProcessor,
) : BlueprintSpecProvider {
    private val httpClient: HttpClient = HttpClient()
    override val providerId: String get() = "remote"

    override suspend fun provide(source: BlueprintSpecSource): ProcessedBlueprint {
        if (source !is BlueprintSpecSource.Remote) {
            throw UnsupportedBlueprintSpecSource()
        }

        val response =
            try {
                httpClient.get(source.url)
            } catch (_: UnresolvedAddressException) {
                throw BlueprintSpecNotFound()
            }

        val contents: String = response.body()
        return processor.process(
            input = contents,
            source = ProcessedBlueprint.Source.externalUrl(source.url)
        )
    }
}

class LocalBlueprintSpecProvider(
    private val parser: BlueprintProcessor,
) : BlueprintSpecProvider {
    override val providerId: String get() = "local"

    override suspend fun provide(source: BlueprintSpecSource): ProcessedBlueprint {
        if (source !is BlueprintSpecSource.Local) {
            throw UnsupportedBlueprintSpecSource()
        }

        val file = File(source.filePath)
        val contents = file.readText()
        return parser.process(
            input = contents,
            source = ProcessedBlueprint.Source.fileSystem(file.absolutePath)
        )
    }
}
