package gg.kuken.core.io

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class FileEntry(
    val relativePath: String,
    val name: String,
    val size: Long,
    val type: FileType,
    val createdAt: Instant?,
    val accessedAt: Instant,
    val modifiedAt: Instant,
    val permissions: String?,
    val isExecutable: Boolean,
    val isReadable: Boolean,
    val isWritable: Boolean,
    val hidden: Boolean,
    val mimeType: String,
)
