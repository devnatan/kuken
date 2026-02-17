package gg.kuken

import java.nio.file.Files
import kotlin.io.path.pathString

fun createTestKukenConfig() =
    KukenConfig(
        http =
            KukenConfig.HttpConfig(
                host = "",
                port = 8080,
                fileUploadLimit = 100,
            ),
        db =
            KukenConfig.DBConfig(
                url = "jdbc:postgresql://localhost:5432/kuken",
                user = "kuken",
                password = "kuken",
            ),
        redis =
            KukenConfig.RedisConfig(
                url = "redis://localhost:6379",
            ),
        node = "kuken0",
        docker =
            KukenConfig.DockerConfig(
                network = KukenConfig.DockerConfig.Network(name = "kuken0"),
                apiVersion = "1.52",
            ),
        engine =
            KukenConfig.EngineConfig(
                _dataDirectory = Files.createTempDirectory("kuken").also { it.toFile().deleteOnExit() }.pathString,
            ),
    )
