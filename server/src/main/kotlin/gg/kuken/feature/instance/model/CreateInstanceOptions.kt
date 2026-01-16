package gg.kuken.feature.instance.model

import gg.kuken.feature.blueprint.processor.BlueprintResolutionContextEnv
import gg.kuken.feature.blueprint.processor.BlueprintResolutionContextInputs
import kotlin.uuid.Uuid

data class CreateInstanceOptions(
    val address: HostPort,
    val blueprint: Uuid,
    val inputs: BlueprintResolutionContextInputs,
    val env: BlueprintResolutionContextEnv,
)
