package gg.kuken.core.io

interface FileSystem {

    suspend fun listDirectory(path: String): List<FileEntry>

    suspend fun readFileContents(path: String): String

    suspend fun writeFileContents(path: String, contents: String)

    suspend fun deleteFile(path: String)
}