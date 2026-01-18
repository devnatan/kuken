package gg.kuken.feature.instance.websocket

import gg.kuken.feature.instance.InstanceService
import gg.kuken.websocket.WebSocketClientMessageContext
import gg.kuken.websocket.WebSocketClientMessageHandler
import gg.kuken.websocket.WebSocketOpCodes
import gg.kuken.websocket.respond
import gg.kuken.websocket.respondAsync
import gg.kuken.websocket.uuid
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.Serializable
import me.devnatan.dockerkt.DockerClient
import me.devnatan.dockerkt.models.Frame
import me.devnatan.dockerkt.models.Stream
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

        val inspection = dockerClient.containers.inspect(containerId)
        val isContainerRunning = inspection.state.isRunning

        try {
            captureLogs(containerId, isContainerRunning)
        } catch (_: ContainerNotFoundException) {
            respond(WebSocketOpCodes.InstanceUnavailable)
        }
    }

    private suspend fun WebSocketClientMessageContext.captureLogs(
        containerId: String,
        isContainerRunning: Boolean,
    ) {
        dockerClient.containers
            .logs(containerId) {
                follow = true
                showTimestamps = true
                stdout = true
                stderr = true
            }.onStart {
                respondAsync(
                    op = WebSocketOpCodes.InstanceLogsRequestStarted,
                    data = mapOf("running" to isContainerRunning.toString()),
                )
            }.onCompletion {
                respondAsync<Unit>(WebSocketOpCodes.InstanceLogsRequestFinished)
            }.collect { frame ->
                respondAsync(
                    op = WebSocketOpCodes.InstanceLogsRequestFrame,
                    data = toLog(frame),
                )
            }
    }

    private fun toLog(frame: Frame) =
        with(frame) {
            LogFrame(
                value = value.substringAfter(" "),
                timestamp = value.substringBefore(" "),
                length = length,
                stream = stream,
            )
        }

    @Serializable
    data class LogFrame(
        val value: String,
        val timestamp: String,
        val length: Int,
        val stream: Stream,
    )
}
