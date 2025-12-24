package gg.kuken.http.websocket

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

abstract class WebSocketPacketEventHandler : CoroutineScope {
    override lateinit var coroutineContext: CoroutineContext set

    abstract suspend fun WebSocketPacketContext.handle()
}
