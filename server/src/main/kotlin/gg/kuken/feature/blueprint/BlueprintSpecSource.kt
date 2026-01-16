package gg.kuken.feature.blueprint

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BlueprintSpecSource {
    abstract val uri: String

    abstract fun replacing(path: String): BlueprintSpecSource

    @Serializable
    @SerialName("local")
    data class Local(
        val filePath: String,
    ) : BlueprintSpecSource() {
        override val uri: String get() = "file://$filePath"

        override fun replacing(path: String): BlueprintSpecSource = Local(filePath = path.substringBeforeLast('/') + "/" + path)
    }

    @Serializable
    @SerialName("remote")
    data class Remote(
        val url: String,
    ) : BlueprintSpecSource() {
        override val uri: String get() = url

        override fun replacing(path: String): BlueprintSpecSource = Remote(url = url.substringBeforeLast('/') + "/" + path)
    }
}
