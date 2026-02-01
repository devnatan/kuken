package gg.kuken.feature.blueprint.model

import gg.kuken.feature.blueprint.BlueprintSpecSource
import kotlinx.serialization.Serializable

@Serializable
data class BlueprintHeader(
    val name: String,
    val version: String,
    val author: String,
    val url: String,
    val assets: Assets,
) {
    @Serializable
    data class Assets(
        val icon: BlueprintSpecSource,
    )
}
