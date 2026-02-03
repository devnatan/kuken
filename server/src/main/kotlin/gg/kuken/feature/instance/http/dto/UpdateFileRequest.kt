package gg.kuken.feature.instance.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateFileRequest(
    val newName: String? = null,
)
