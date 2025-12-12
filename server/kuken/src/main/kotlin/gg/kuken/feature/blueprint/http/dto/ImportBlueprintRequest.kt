package gg.kuken.feature.blueprint.http.dto

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable

@Serializable
internal data class ImportBlueprintRequest(
    @field:NotBlank(message = "Url must be provided")
    val url: String = "",
)
