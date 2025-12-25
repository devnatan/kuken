package gg.kuken.http.websocket

typealias WebSocketOp = Int

@Suppress("ConstPropertyName")
object WebSocketOpCodes {
    const val InstanceLogsRequest: WebSocketOp = 1

    const val InstanceUnavailable: WebSocketOp = 2
}
