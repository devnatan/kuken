package gg.kuken.feature.blueprint

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private const val FILE_PROTOCOL = "file://"
private const val INTERNET_SECURE_PROTOCOL = "https://"
private const val INTERNET_INSECURE_PROTOCOL = "http://"

@Serializable(with = BlueprintSpecSource.Serializer::class)
sealed class BlueprintSpecSource {
    abstract val uri: String

    @Serializable
    @SerialName("local")
    data class Local(
        override val uri: String,
    ) : BlueprintSpecSource() {
        val filePath: String get() = uri.substringAfter(FILE_PROTOCOL)
    }

    @Serializable
    @SerialName("remote")
    data class Remote(
        private val _url: String,
        private val secure: Boolean,
    ) : BlueprintSpecSource() {
        override val uri: String get() = _url
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
            return when {
                raw.startsWith(FILE_PROTOCOL) -> {
                    Local(raw)
                }

                raw.startsWith(INTERNET_SECURE_PROTOCOL) -> {
                    Remote(
                        _url = raw,
                        secure = true,
                    )
                }

                raw.startsWith(INTERNET_INSECURE_PROTOCOL) -> {
                    Remote(
                        _url = raw,
                        secure = false,
                    )
                }

                else -> {
                    error("Unsupported protocol: $raw")
                }
            }
        }
    }
}
