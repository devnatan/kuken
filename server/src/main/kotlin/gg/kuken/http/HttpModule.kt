package gg.kuken.http

import gg.kuken.http.websocket.WebSocketClientMessageHandler
import gg.kuken.http.websocket.WebSocketOp
import io.ktor.server.application.Application
import org.koin.core.component.KoinComponent

abstract class HttpModule : KoinComponent {
    open val priority: Int get() = 0

    abstract fun install(app: Application)

    open fun webSocketHandlers(): Map<WebSocketOp, WebSocketClientMessageHandler> = emptyMap()
}
