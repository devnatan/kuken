package gg.kuken

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.random.Random
import kotlin.random.nextUInt

@Serializable
data class KukenConfig(
    val http: HttpConfig,
    val db: DBConfig,
    val redis: RedisConfig,
    val node: String,
    val docker: DockerConfig,
    val engine: EngineConfig,
) {
    val devMode: Boolean = System.getenv("DEV_MODE")?.toBooleanStrictOrNull() == true

    @Serializable
    data class EngineConfig(
        @SerialName("data-directory")
        private val _dataDirectory: String,
    ) {
        val dataDirectory = Path(_dataDirectory)

        val instancesDataDirectory: Path = dataDirectory.resolve("instances")
        val blueprintsDataDirectory: Path = dataDirectory.resolve("blueprints")
    }

    @Serializable
    data class HttpConfig(
        val host: String,
        val port: Int,
        @SerialName("file-upload-limit")
        val fileUploadLimit: Long,
    ) {
        val fileUploadLimitBytes get() = fileUploadLimit * 1024 * 1024
    }

    @Serializable
    data class DBConfig(
        val url: String,
        val user: String,
        val password: String,
    )

    @Serializable
    data class RedisConfig(
        val url: String,
    )

    @Serializable
    data class DockerConfig(
        val network: Network,
        @SerialName("api-version") val apiVersion: String,
    ) {
        @Serializable
        data class Network(
            val name: String,
        )
    }

    companion object {
        const val TMP_FILE_PREFIX = "kuken_"

        @JvmStatic
        fun tempDir(path: Path): Path = Files.createTempDirectory(path, null)

        @JvmStatic
        fun tempDirRecursively(path: String): Path {
            val tmp = Files.createTempDirectory(null)
            val dir = tmp.resolve(path)
            dir.createDirectories()

            return dir
        }

        @JvmStatic
        fun tempFile(suffix: String = Random.nextUInt().toString()): Path = Files.createTempFile(TMP_FILE_PREFIX, suffix)
    }
}
