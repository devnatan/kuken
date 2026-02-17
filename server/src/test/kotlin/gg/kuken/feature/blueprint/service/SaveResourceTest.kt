package gg.kuken.feature.blueprint.service

import gg.kuken.createTestKukenConfig
import gg.kuken.feature.account.IdentityGeneratorService
import gg.kuken.feature.blueprint.BlueprintSpecSource
import gg.kuken.feature.blueprint.CombinedBlueprintSpecProvider
import gg.kuken.feature.blueprint.LocalBlueprintSpecProvider
import gg.kuken.feature.blueprint.RemoteBlueprintSpecProvider
import gg.kuken.feature.blueprint.processor.BlueprintConverter
import gg.kuken.feature.blueprint.processor.BlueprintProcessor
import gg.kuken.feature.blueprint.repository.FakeBlueprintRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.test.runTest
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class SaveResourceTest {
    private lateinit var blueprintDataDir: Path

    private val blueprintId = Uuid.random()

    private val blueprintService =
        BlueprintService(
            blueprintRepository = FakeBlueprintRepository(),
            blueprintSpecProvider =
                CombinedBlueprintSpecProvider(
                    listOf(
                        LocalBlueprintSpecProvider(),
                        RemoteBlueprintSpecProvider(),
                    ),
                ),
            identityGeneratorService = IdentityGeneratorService(),
            blueprintConverter = BlueprintConverter(),
            blueprintProcessor = BlueprintProcessor(blueprintConverter = BlueprintConverter()),
            kukenConfig = createTestKukenConfig(),
            httpClient = HttpClient(CIO),
        )

    @BeforeTest
    fun setup() {
        blueprintDataDir = createTempDirectory("kuken-test")
    }

    @AfterTest
    fun teardown() {
        blueprintDataDir.toFile().deleteRecursively()
    }

    @Test
    fun `local resource within blueprint directory succeeds`() =
        runTest {
            val resourceFile = blueprintDataDir.resolve("icon.png").also { it.writeBytes(byteArrayOf(1, 2, 3)) }

            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Local(blueprintDataDir.absolutePathString()),
                    resourceOrigin = "file://icon.png",
                )

            assertIs<BlueprintService.SaveResourceResult.Success>(result)
            assertEquals(resourceFile.absolutePathString(), result.source.filePath)
        }

    @Test
    fun `local resource in subdirectory within blueprint directory succeeds`() =
        runTest {
            val blueprintDir = blueprintDataDir.resolve("my-blueprint").createDirectories()
            val resourceFile = blueprintDir.resolve("assets/icon.png")
            resourceFile.parent.createDirectories()
            resourceFile.writeBytes(byteArrayOf(1, 2, 3))

            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Local(blueprintDir.absolutePathString()),
                    resourceOrigin = "file://assets/icon.png",
                )

            assertIs<BlueprintService.SaveResourceResult.Success>(result)
        }

    @Test
    fun `local resource escaping base directory returns LocalEscapingBaseDirectory`() =
        runTest {
            val blueprintDir = blueprintDataDir.resolve("my-blueprint").createDirectories()

            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Local(blueprintDir.absolutePathString()),
                    resourceOrigin = "file://../secret/file.txt",
                )

            assertEquals(BlueprintService.SaveResourceResult.LocalEscapingBaseDirectory, result)
        }

    @Test
    fun `local resource with deeply nested traversal escaping base directory returns LocalEscapingBaseDirectory`() =
        runTest {
            val blueprintDir = blueprintDataDir.resolve("my-blueprint").createDirectories()

            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Local(blueprintDir.absolutePathString()),
                    resourceOrigin = "file://assets/../../etc/passwd",
                )

            assertEquals(BlueprintService.SaveResourceResult.LocalEscapingBaseDirectory, result)
        }

    @Test
    fun `local resource file not found returns LocalFileNotFound`() =
        runTest {
            val blueprintDir = blueprintDataDir.resolve("my-blueprint").createDirectories()

            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Local(blueprintDir.absolutePathString()),
                    resourceOrigin = "file://nonexistent.png",
                )

            assertEquals(BlueprintService.SaveResourceResult.LocalFileNotFound, result)
        }

    @Test
    fun `local resource with remote blueprint downloads from resolved URL`() =
        runTest {
            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Remote("https://example.com/blueprints/my-blueprint.pkl"),
                    resourceOrigin = "file://icon.png",
                )

            assertIs<BlueprintService.SaveResourceResult.Success>(result)
            assertTrue(
                result.source.uri.endsWith(Path(blueprintId.toString()).resolve("icon.png").toString()),
            )
        }

    @Test
    fun `remote resource is saved using filename from URL`() =
        runTest {
            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Local("/some/blueprint/dir"),
                    resourceOrigin = "https://picsum.photos/seed/picsum/200/300.jpg",
                )

            assertIs<BlueprintService.SaveResourceResult.Success>(result)
            assertEquals(result.filename, Path(result.source.filePath).name)
        }

    @Test
    fun `remote resource content is written correctly`() =
        runTest {
            val result =
                blueprintService.saveResource(
                    blueprintId = blueprintId,
                    blueprintSource = BlueprintSpecSource.Local("/some/blueprint/dir"),
                    resourceOrigin = "https://picsum.photos/seed/picsum/200/300",
                )

            assertIs<BlueprintService.SaveResourceResult.Success>(result)
            assertTrue(Path(result.source.filePath).readBytes().isNotEmpty())
        }
}
