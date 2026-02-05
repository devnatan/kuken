package gg.kuken.feature.instance

import gg.kuken.KukenConfig
import gg.kuken.core.io.FileEntry
import gg.kuken.core.io.FileSystem
import gg.kuken.core.io.FileType
import gg.kuken.core.io.requireSafePath
import gg.kuken.core.io.safePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.devnatan.dockerkt.DockerClient
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFilePermissions
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.fileSize
import kotlin.io.path.isDirectory
import kotlin.io.path.isExecutable
import kotlin.io.path.isHidden
import kotlin.io.path.isReadable
import kotlin.io.path.isRegularFile
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.isWritable
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.relativeToOrSelf
import kotlin.io.path.writeText
import kotlin.time.toKotlinInstant
import kotlin.uuid.Uuid

class HostDockerContainerFileSystem(
    val instanceId: Uuid,
    val dockerClient: DockerClient,
    config: KukenConfig,
) : FileSystem {
    override val root: Path =
        config.engine.instancesDataDirectory
            .resolve(instanceId.toString())

    override suspend fun listDirectory(path: String): List<FileEntry> =
        requireSafePath(path)
            .listDirectoryEntries()
            .map(::toFileEntry)

    override suspend fun readFileContents(path: String): String = requireSafePath(path).readText()

    override suspend fun writeFileContents(
        path: String,
        contents: String,
    ) {
        requireSafePath(path).writeText(contents)
    }

    @OptIn(ExperimentalPathApi::class)
    override suspend fun deleteFile(path: String) {
        requireSafePath(path).deleteRecursively()
    }

    override suspend fun renameFile(
        path: String,
        newName: String,
    ) {
        val file = safePath(path).toFile()
        val dir = file.parentFile
        val newFile = dir.resolve(newName)

        file.renameTo(newFile)
    }

    private fun toFileEntry(path: Path): FileEntry {
        val type =
            when {
                path.isSymbolicLink() -> FileType.SYMLINK
                path.isDirectory() -> FileType.DIRECTORY
                path.isRegularFile() -> FileType.FILE
                else -> FileType.OTHER
            }

        val attrs = Files.readAttributes(path, BasicFileAttributes::class.java)
        val permissions =
            try {
                Files.getPosixFilePermissions(path).let(PosixFilePermissions::toString)
            } catch (_: UnsupportedOperationException) {
                null
            }

        return FileEntry(
            relativePath = path.relativeToOrSelf(root).pathString,
            name = path.name,
            size = path.fileSize(),
            type = type,
            createdAt = attrs.creationTime().toInstant().toKotlinInstant(),
            accessedAt = attrs.lastAccessTime().toInstant().toKotlinInstant(),
            modifiedAt = attrs.lastModifiedTime().toInstant().toKotlinInstant(),
            isExecutable = path.isExecutable(),
            isReadable = path.isReadable(),
            isWritable = path.isWritable(),
            hidden = path.isHidden(),
            permissions = permissions,
        )
    }

    override suspend fun touchFile(path: String): String {
        val file = safePath(path).toFile()
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                Files.createDirectories(file.parentFile.toPath())
                file.createNewFile()
            }
        }

        return file.absolutePath
    }
}
