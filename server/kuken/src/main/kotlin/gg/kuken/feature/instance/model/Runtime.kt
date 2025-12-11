package gg.kuken.feature.instance.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class InstanceRuntime(
    val id: String,
    val network: InstanceRuntimeNetwork,
    val platform: String?,
    val exitCode: Int,
    val outOfMemory: Boolean,
    val error: String?,
    val status: String,
    val pid: Int,
    val fsPath: String?,
    val startedAt: Instant?,
    val finishedAt: Instant?,
    val mounts: List<InstanceRuntimeMount>,
)
