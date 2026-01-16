package gg.kuken.feature.unit.http.dto

import gg.kuken.feature.blueprint.processor.BlueprintResolutionContextEnv
import gg.kuken.feature.blueprint.processor.BlueprintResolutionContextInputs
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
internal data class CreateUnitRequest(
    @field:NotBlank(message = "Name must be provided")
    @field:Size(
        min = 2,
        max = 64,
        message = "Name must have a minimum length of {min} and at least {max} characters.",
    )
    val name: String = "",
    @field:NotNull(message = "Blueprint id must be provided.")
    val blueprint: Uuid,
    val inputs: BlueprintResolutionContextInputs = emptyMap(),
    val env: BlueprintResolutionContextEnv = emptyMap(),
    val externalId: String? = null,
)
