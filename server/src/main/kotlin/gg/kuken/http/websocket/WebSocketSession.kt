package gg.kuken.http.websocket

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class WebSocketSession(
    val id: Int,
    val connection: DefaultWebSocketServerSession,
    @Transient private val json: Json,
) {
    suspend fun <T> send(
        serializer: KSerializer<WebSocketResponse<T>>,
        message: WebSocketResponse<T>,
    ) {
        connection.outgoing.send(Frame.Text(json.encodeToString(serializer, message)))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebSocketSession

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + connection.hashCode()
        return result
    }
}
