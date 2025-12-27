package gg.kuken.feature.instance.websocket

import gg.kuken.feature.instance.InstanceService
import gg.kuken.http.websocket.WebSocketClientMessageContext
import gg.kuken.http.websocket.WebSocketClientMessageHandler
import gg.kuken.http.websocket.WebSocketOpCodes
import gg.kuken.http.websocket.respond
import gg.kuken.http.websocket.uuid
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import me.devnatan.dockerkt.DockerClient
import me.devnatan.dockerkt.resource.container.ContainerNotFoundException
import me.devnatan.dockerkt.resource.container.logs

class InstanceLogsRequestWebSocketClientMessageHandler(
    val instanceService: InstanceService,
    val dockerClient: DockerClient,
) : WebSocketClientMessageHandler() {
    override suspend fun WebSocketClientMessageContext.handle() {
        val id = packet.uuid("iid")
        val instance = instanceService.getInstance(id)

        val containerId = instance.containerId
        if (containerId == null) {
            respond(WebSocketOpCodes.InstanceUnavailable)
            return
        }

        try {
            dockerClient.containers
                .logs(containerId)
                .onStart {
                    respond(WebSocketOpCodes.InstanceLogsRequestStarted)
                }.onCompletion {
                    respond(WebSocketOpCodes.InstanceLogsRequestFinished)
                }.collect { frame ->
                    respond(
                        op = WebSocketOpCodes.InstanceLogsRequestFrame,
                        data = frame,
                    )
                }
        } catch (_: ContainerNotFoundException) {
            respond(WebSocketOpCodes.InstanceUnavailable)
        }
    }
}
