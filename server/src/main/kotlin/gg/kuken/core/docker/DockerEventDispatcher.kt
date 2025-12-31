package gg.kuken.core.docker

import gg.kuken.core.EventDispatcher
import gg.kuken.core.EventDispatcherImpl
import gg.kuken.feature.instance.event.InstanceStartedEvent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import me.devnatan.dockerkt.DockerClient
import me.devnatan.dockerkt.models.system.Event
import me.devnatan.dockerkt.models.system.EventActor
import me.devnatan.dockerkt.models.system.EventType
import me.devnatan.dockerkt.resource.system.events
import kotlin.uuid.Uuid

class DockerEventDispatcher(
    dispatcher: EventDispatcherImpl = EventDispatcherImpl(),
    val dockerClient: DockerClient,
) : EventDispatcher by dispatcher {

    init {
        launch(
            context = CoroutineName("DockerEventDispatcher#consumer")
        ) {
            dockerClient.system
                .events { filterByType(EventType.CONTAINER) }
                .collect(::interceptDockerEvent)
        }
    }

    private suspend fun interceptDockerEvent(event: Event) = supervisorScope {
        when (event.action) {
            "start" -> launch(start = CoroutineStart.ATOMIC) { dispatch(InstanceStartedEvent(event.actor.getInstanceId())) }
            else -> Unit
        }
    }

    private fun EventActor.getInstanceId(): Uuid = requireNotNull(attributes["gg.kuken.instance.id"]?.let(Uuid.Companion::parse)) {
        "Instance id not found in event actor attributes: $attributes"
    }
}