package gg.kuken.feature.blueprint.processor

typealias BlueprintResolutionContextInputs = Map<String, String>

typealias BlueprintResolutionContextEnv = Map<String, String>

class BlueprintResolutionContext(
    val inputs: BlueprintResolutionContextInputs,
    val env: BlueprintResolutionContextEnv,
)
