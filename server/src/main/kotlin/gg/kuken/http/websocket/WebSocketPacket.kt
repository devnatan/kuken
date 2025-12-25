package gg.kuken.http.websocket

import gg.kuken.http.websocket.WebSocketPacket.Companion.DATA
import gg.kuken.http.websocket.WebSocketPacket.Companion.OP
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.serializer
import kotlin.reflect.jvm.jvmName

@Serializable
data class WebSocketPacket(
    @SerialName(OP) val op: Int,
    @SerialName(DATA) val data: JsonObject? = null,
) {
    companion object {
        const val OP = "o"
        const val DATA = "d"

        const val TARGET_ID = "tid"
        const val VALUE = "v"
    }
}

data class WebSocketPacketContext(
    val packet: WebSocketPacket,
    val session: WebSocketSession,
)

fun WebSocketPacketContext.stringData(key: String): String {
    val text = packet.data?.get(key) as? JsonPrimitive
    return text?.contentOrNull ?: error("Required key $key not found in packet data")
}

@Serializable(with = WebSocketResponseSerializer::class)
data class WebSocketResponse<T>(
    @SerialName(OP) val op: WebSocketOp,
    @SerialName(DATA) val data: T,
)

class WebSocketResponseSerializer<T>(
    private val dataSerializer: KSerializer<T>,
) : KSerializer<WebSocketResponse<T>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(WebSocketResponse::class.jvmName) {
            element<Int>(OP)
            element(DATA, dataSerializer.descriptor)
        }

    override fun serialize(
        encoder: Encoder,
        value: WebSocketResponse<T>,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor = descriptor,
                index = 0,
                serializer = dataSerializer,
                value = value.data,
            )
            encodeIntElement(descriptor = descriptor, index = 1, value = value.op)
        }
    }

    override fun deserialize(decoder: Decoder): WebSocketResponse<T> =
        decoder.decodeStructure(descriptor) {
            val data = decodeSerializableElement(descriptor = descriptor, index = 0, deserializer = dataSerializer)
            val op = decodeIntElement(descriptor = descriptor, index = 1)
            return@decodeStructure WebSocketResponse(op, data)
        }
}

suspend inline fun <reified T> WebSocketPacketContext.respond(
    data: T,
    code: Int = packet.op,
) {
    session.send(
        serializer = WebSocketResponseSerializer(serializer<T>()),
        message = WebSocketResponse(code, data),
    )
}
