package gg.kuken.feature.blueprint.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ProcessBlueprintRequest(
    val inputs: Map<String, String> = emptyMap(),
    val env: Map<String, String> = emptyMap(),
)
