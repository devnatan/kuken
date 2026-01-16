package gg.kuken.feature.blueprint

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = BlueprintSpecSource.Serializer::class)
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

    class Serializer : KSerializer<BlueprintSpecSource> {
        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("gg.kuken.feature.blueprint.BlueprintSpecSource")

        override fun serialize(
            encoder: Encoder,
            value: BlueprintSpecSource,
        ) {
            encoder.encodeString(value.uri)
        }

        override fun deserialize(decoder: Decoder): BlueprintSpecSource {
            val raw = decoder.decodeString()
            val protocol = raw.substringBefore("://")
            require(protocol.isNotBlank()) { "Protocol can't be blank" }

            val substring = raw.substringAfter("$protocol://")

            return when (protocol) {
                "file" -> Local(substring)
                "http", "https" -> Remote(substring)
                else -> error("Unsupported protocol: $protocol")
            }
        }
    }
}
