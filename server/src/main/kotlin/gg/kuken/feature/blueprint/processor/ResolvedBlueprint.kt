package gg.kuken.feature.blueprint.processor

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
data class ResolveBlueprintInputDefinitions(
    val inputs: List<UserInput>,
    val startup: Resolvable<String>,
)

@Serializable
data class ResolvedBlueprint(
    val metadata: BlueprintMetadata,
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
    val author: String,
    val assets: AppAssets,
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
    val commandExecutor: InstanceSettingsCommandExecutor?,
)

@Serializable
sealed class InstanceSettingsCommandExecutor {
    @OptIn(InternalSerializationApi::class)
    val type: String get() = this::class.serializer().descriptor.serialName

    @Serializable
    @SerialName("rcon")
    data class Rcon(
        val port: Resolvable<Int>,
        val password: Resolvable<String>,
        val template: String,
    ) : InstanceSettingsCommandExecutor()

    @Serializable
    @SerialName("ssh")
    data class SSH(
        val template: String,
    ) : InstanceSettingsCommandExecutor()
}

typealias InputName = String
typealias InputLabel = String

@Serializable
sealed class UserInput {
    abstract val name: InputName
    abstract val label: InputLabel
    abstract val description: String?
}

@Serializable
@SerialName("text")
data class TextInput(
    override val name: InputName,
    override val label: InputLabel,
    override val description: String?,
) : UserInput()

@Serializable
@SerialName("password")
data class PasswordInput(
    override val name: InputName,
    override val label: InputLabel,
    override val description: String?,
) : UserInput()

@Serializable
@SerialName("port")
data class PortInput(
    override val name: InputName,
    override val label: InputLabel,
    override val description: String?,
    val default: Int?,
) : UserInput()

@Serializable
@SerialName("checkbox")
data class CheckboxInput(
    override val name: InputName,
    override val label: InputLabel,
    override val description: String?,
    val default: Boolean,
) : UserInput()

@Serializable
@SerialName("select")
data class SelectInput(
    override val name: InputName,
    override val label: InputLabel,
    override val description: String?,
    val items: Map<String, String>,
) : UserInput()

@Serializable
@SerialName("datasize")
data class DataSizeInput(
    override val name: InputName,
    override val label: InputLabel,
    override val description: String?,
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
