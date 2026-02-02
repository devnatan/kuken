package gg.kuken.core.command

import gg.kuken.core.security.SensitiveText
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class CommandExecutionStrategy<T : CommandExecutionStrategy.Settings>(
    val strategyName: String,
    val settingsType: KClass<T>,
) {
    interface Settings

    data object DockerExec : CommandExecutionStrategy<DockerExec.Settings>(
        strategyName = "exec",
        settingsType = Settings::class,
    ) {
        data class Settings(
            val shell: String,
            val timeout: Int,
            val template: String,
        ) : CommandExecutionStrategy.Settings
    }

    data object RCON : CommandExecutionStrategy<RCON.Settings>(
        strategyName = "rcon",
        settingsType = Settings::class,
    ) {
        data class Settings(
            val port: String,
            val password: SensitiveText,
            val template: String,
        ) : CommandExecutionStrategy.Settings
    }
}
