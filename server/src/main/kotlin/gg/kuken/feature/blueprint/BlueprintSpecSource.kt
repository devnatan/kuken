package gg.kuken.feature.blueprint

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BlueprintSpecSource {
    @Serializable
    @SerialName("local")
    data class Local(
        val filePath: String,
    ) : BlueprintSpecSource()

    @Serializable
    @SerialName("remote")
    data class Remote(
        val url: String,
    ) : BlueprintSpecSource()
}
