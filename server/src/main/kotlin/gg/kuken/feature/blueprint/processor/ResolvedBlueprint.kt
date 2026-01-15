package gg.kuken.feature.blueprint.processor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResolvedBlueprint(
    val metadata: BlueprintMetadata,
    val assets: AppAssets? = null,
    val inputs: List<UserInput>,
    val build: BuildConfig,
    val instanceSettings: InstanceSettings?,
)

@Serializable
data class BlueprintMetadata(
    val name: String,
    val version: String,
    val url: String,
)

@Serializable
data class AppAssets(
    val icon: String,
)

@Serializable
data class InstanceSettings(
    val startup: Resolvable<String>,
)

@Serializable
sealed class UserInput {
    abstract val name: Resolvable<String>
    abstract val label: Resolvable<String>
}

@Serializable
@SerialName("text")
data class TextInput(
    override val name: Resolvable<String>,
    override val label: Resolvable<String>,
) : UserInput()

@Serializable
@SerialName("password")
data class PasswordInput(
    override val name: Resolvable<String>,
    override val label: Resolvable<String>,
) : UserInput()

@Serializable
@SerialName("port")
data class PortInput(
    override val name: Resolvable<String>,
    override val label: Resolvable<String>,
    val default: Resolvable<Int>,
) : UserInput()

@Serializable
@SerialName("checkbox")
data class CheckboxInput(
    override val name: Resolvable<String>,
    override val label: Resolvable<String>,
    val default: Resolvable<Boolean>,
) : UserInput()

@Serializable
@SerialName("select")
data class SelectInput(
    override val name: Resolvable<String>,
    override val label: Resolvable<String>,
) : UserInput()

@Serializable
@SerialName("datasize")
data class DataSizeInput(
    override val name: Resolvable<String>,
    override val label: Resolvable<String>,
) : UserInput()

@Serializable
data class BuildConfig(
    val docker: DockerConfig,
    val environmentVariables: List<EnvironmentVariable>,
)

@Serializable
data class DockerConfig(
    val image: Resolvable<String>,
)

@Serializable
data class EnvironmentVariable(
    val name: String,
    val value: Resolvable<String>,
)
