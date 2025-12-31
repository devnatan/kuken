package gg.kuken.feature.instance.event

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class InstanceStartedEvent(val instanceId: Uuid)