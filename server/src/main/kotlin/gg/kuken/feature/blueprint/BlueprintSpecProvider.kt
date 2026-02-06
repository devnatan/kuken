package gg.kuken.feature.blueprint

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.io.File
import java.nio.channels.UnresolvedAddressException

interface BlueprintSpecProvider {
    val providerId: String

    suspend fun provide(source: BlueprintSpecSource): String
}

data class CombinedBlueprintSpecProvider(
    val providers: List<BlueprintSpecProvider>,
) : BlueprintSpecProvider {
    override val providerId: String
        get() = providers.joinToString(" + ", transform = BlueprintSpecProvider::providerId)

    override suspend fun provide(source: BlueprintSpecSource): String {
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

private const val GITHUB_RAW_DOWNLOAD_HEADER = "x-raw-download"

class RemoteBlueprintSpecProvider : BlueprintSpecProvider {
    private val httpClient: HttpClient = HttpClient()
    override val providerId: String get() = "remote"

    override suspend fun provide(source: BlueprintSpecSource): String {
        if (source !is BlueprintSpecSource.Remote) {
            throw UnsupportedBlueprintSpecSource()
        }

        val response =
            try {
                httpClient.get(source.uri)
            } catch (_: UnresolvedAddressException) {
                throw BlueprintSpecNotFound()
            }

        if (response.headers.contains(GITHUB_RAW_DOWNLOAD_HEADER)) {
            return provide(BlueprintSpecSource.Remote(response.headers[GITHUB_RAW_DOWNLOAD_HEADER]!!, secure = true))
        }

        return response.body()
    }
}

class LocalBlueprintSpecProvider : BlueprintSpecProvider {
    override val providerId: String get() = "local"

    override suspend fun provide(source: BlueprintSpecSource): String {
        if (source !is BlueprintSpecSource.Local) {
            throw UnsupportedBlueprintSpecSource()
        }

        val file = File(source.filePath)
        return file.readText()
    }
}
