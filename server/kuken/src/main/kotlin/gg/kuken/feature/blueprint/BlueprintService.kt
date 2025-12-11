package gg.kuken.feature.blueprint

import gg.kuken.feature.blueprint.model.Blueprint
import gg.kuken.feature.blueprint.repository.BlueprintRepository
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class BlueprintService(
    private val blueprintRepository: BlueprintRepository,
    private val blueprintSpecProvider: BlueprintSpecProvider,
) {
    private val json: Json =
        Json {
            coerceInputValues = false
            prettyPrint = true
        }

    fun getBlueprint(blueprintId: Uuid): Blueprint {
        error("")
    }
}
