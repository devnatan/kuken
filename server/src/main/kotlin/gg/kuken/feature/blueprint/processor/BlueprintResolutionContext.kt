package gg.kuken.feature.blueprint.processor

import gg.kuken.feature.instance.model.HostPort
import kotlin.uuid.Uuid

typealias BlueprintResolutionContextInputs = Map<String, String>

typealias BlueprintResolutionContextEnv = Map<String, String>

class BlueprintResolutionContext(
    val instanceId: Uuid,
    val instanceName: String,
    val inputs: BlueprintResolutionContextInputs,
    val env: BlueprintResolutionContextEnv,
    val address: HostPort,
)
