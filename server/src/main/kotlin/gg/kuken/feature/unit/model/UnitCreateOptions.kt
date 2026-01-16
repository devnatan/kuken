package gg.kuken.feature.unit.model

import gg.kuken.feature.blueprint.processor.BlueprintResolutionContextEnv
import gg.kuken.feature.blueprint.processor.BlueprintResolutionContextInputs
import kotlin.uuid.Uuid

data class UnitCreateOptions(
    val name: String,
    val blueprintId: Uuid,
    val inputs: BlueprintResolutionContextInputs,
    val env: BlueprintResolutionContextEnv,
    val actorId: Uuid?,
    val externalId: String?,
)
