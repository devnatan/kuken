package gg.kuken.core.command

import gg.kuken.core.security.SensitiveText
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class CommandExecutionStrategy<T : CommandExecutionStrategy.Context>(
    val strategyName: String,
    val settingsType: KClass<T>,
) {
    interface Context

    data object DockerExec : CommandExecutionStrategy<DockerExec.Context>(
        strategyName = "exec",
        settingsType = Context::class,
    ) {
        data class Context(
            val shell: String,
            val timeout: Int,
            val template: String,
        ) : CommandExecutionStrategy.Context
    }

    data object RCON : CommandExecutionStrategy<RCON.Context>(
        strategyName = "rcon",
        settingsType = Context::class,
    ) {
        data class Context(
            val port: String,
            val password: SensitiveText,
            val template: String,
        ) : CommandExecutionStrategy.Context
    }
}
