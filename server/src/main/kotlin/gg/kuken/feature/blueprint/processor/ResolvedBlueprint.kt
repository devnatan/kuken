package gg.kuken.feature.blueprint.processor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResolvedBlueprint(
    val metadata: BlueprintMetadata,
    val assets: AppAssets? = null,
    val resources: List<AppResource>,
    val hooks: AppHooks,
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

typealias AppResourceName = String

@Serializable
data class AppResource(
    val name: AppResourceName = "",
    val source: String,
)

@Serializable
data class AppHooks(
    val onInstall: AppResource? = null,
)

@Serializable
data class AppAssets(
    val icon: String,
)

@Serializable
data class InstanceSettings(
    val startup: Resolvable<String>,
)

typealias InputName = String
typealias InputLabel = String

@Serializable
sealed class UserInput {
    abstract val name: InputName
    abstract val label: InputLabel
}

@Serializable
@SerialName("text")
data class TextInput(
    override val name: InputName,
    override val label: InputLabel,
) : UserInput()

@Serializable
@SerialName("password")
data class PasswordInput(
    override val name: InputName,
    override val label: InputLabel,
) : UserInput()

@Serializable
@SerialName("port")
data class PortInput(
    override val name: InputName,
    override val label: InputLabel,
    val default: Resolvable<Int>,
) : UserInput()

@Serializable
@SerialName("checkbox")
data class CheckboxInput(
    override val name: InputName,
    override val label: InputLabel,
    val default: Resolvable<Boolean>,
) : UserInput()

@Serializable
@SerialName("select")
data class SelectInput(
    override val name: InputName,
    override val label: InputLabel,
    val items: List<String>,
) : UserInput()

@Serializable
@SerialName("datasize")
data class DataSizeInput(
    override val name: InputName,
    override val label: InputLabel,
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
