package gg.kuken.core.docker

import gg.kuken.core.EventDispatcher
import gg.kuken.core.EventDispatcherImpl
import gg.kuken.feature.instance.event.InstanceEvent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import me.devnatan.dockerkt.DockerClient
import me.devnatan.dockerkt.models.system.Event
import me.devnatan.dockerkt.models.system.EventAction
import me.devnatan.dockerkt.models.system.EventActor
import me.devnatan.dockerkt.models.system.EventType
import me.devnatan.dockerkt.resource.system.events
import org.slf4j.LoggerFactory
import kotlin.uuid.Uuid

class DockerEventDispatcher(
    delegate: EventDispatcherImpl = EventDispatcherImpl(),
    val dockerClient: DockerClient,
) : EventDispatcher by delegate {
    private val log = LoggerFactory.getLogger(DockerEventDispatcher::class.java)

    init {
        launch(
            context = CoroutineName("DockerEventDispatcher"),
        ) {
            dockerClient.system
                .events {
                    filterByType(EventType.Container)
                    filterByAction(EventAction.Start)
                }.collect(::interceptDockerEvent)
        }
    }

    private suspend fun interceptDockerEvent(event: Event) {
        val localEvent =
            when (event.action) {
                EventAction.Start -> event.actor.getInstanceId()?.let(InstanceEvent::InstanceStartedEvent)
                EventAction.Stop -> event.actor.getInstanceId()?.let(InstanceEvent::InstanceStoppedEvent)
                else -> Unit
            }

        if (localEvent != null) {
            log.info("Dispatching Docker event ${event.type}#${event.action}: $localEvent")
            dispatch(localEvent)
        }
    }

    private fun EventActor.getInstanceId(): Uuid? = attributes["gg.kuken.instance.id"]?.let(Uuid.Companion::parse)
}
