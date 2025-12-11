package gg.kuken.feature.instance.model

import kotlinx.serialization.Serializable

@Serializable
enum class InstanceStatus(
    val value: String,
) {
    Created(value = "created"),
    NetworkAssignmentFailed(value = "network-assignment-failed"),
    Unavailable(value = "unavailable"),
    Unknown(value = "unknown"),
    ImagePullInProgress(value = "image-pull"),
    ImagePullNeeded(value = "image-pull-needed"),
    ImagePullFailed(value = "image-pull-failed"),
    ImagePullCompleted(value = "image-pull-completed"),
    Dead(value = "dead"),
    Paused(value = "paused"),
    Exited(value = "exited"),
    Running(value = "running"),
    Stopped(value = "stopped"),
    Starting(value = "starting"),
    Removing(value = "removing"),
    Stopping(value = "stopping"),
    Restarting(value = "restarting"),
    ;

    val isInitialStatus: Boolean
        get() =
            when (this) {
                ImagePullInProgress, ImagePullNeeded, ImagePullFailed, ImagePullCompleted -> true
                else -> false
            }

    val isRuntimeStatus: Boolean
        get() =
            when (this) {
                Dead, Paused, Exited, Running, Stopped, Starting, Removing, Stopping, Restarting -> true
                else -> false
            }
}

@Serializable
@Suppress("detekt.MagicNumber")
sealed class InstanceUpdateCode(
    val name: String,
    val code: Int,
) {
    object Start : InstanceUpdateCode(name = "start", code = 1)

    object Stop : InstanceUpdateCode(name = "stop", code = 2)

    object Restart : InstanceUpdateCode(name = "restart", code = 3)

    object Kill : InstanceUpdateCode(name = "kill", code = 4)

    companion object {
        private val mappings: Map<Int, InstanceUpdateCode> by lazy {
            listOf(Start, Stop, Restart, Kill).associateBy(InstanceUpdateCode::code)
        }

        @JvmStatic
        fun getByCode(code: Int): InstanceUpdateCode? = mappings[code]
    }

    override fun toString(): String = "$name ($code)"
}
