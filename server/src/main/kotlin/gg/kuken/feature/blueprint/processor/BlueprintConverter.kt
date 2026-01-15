package gg.kuken.feature.blueprint.processor

import org.pkl.core.ModuleSource
import org.pkl.core.PModule
import org.pkl.core.PNull
import org.pkl.core.PObject
import org.pkl.core.resource.ResourceReader
import java.lang.AutoCloseable
import java.net.URI
import java.util.Optional

class BlueprintConverter : AutoCloseable {
    private val evaluator =
        org.pkl.core.EvaluatorBuilder
            .preconfigured()
            .setAllowedResources(listOf(Regex("^kuken:.+$").toPattern()))
            .addResourceReader(
                object : ResourceReader {
                    override fun getUriScheme(): String = "kuken"

                    override fun read(uri: URI?): Optional<in Any> = Optional.of("<unresolved:$uri>")

                    override fun hasHierarchicalUris(): Boolean = false

                    override fun isGlobbable(): Boolean = false
                },
            ).build()
    var objectCache: ObjectCache = ObjectCache()

    fun convertFromText(pklFileText: String): ResolvedBlueprint {
        val moduleSource = ModuleSource.text(pklFileText)
        val module = evaluator.evaluate(moduleSource)

        return convertModule(module)
    }

    @Suppress("UNCHECKED_CAST")
    private fun convertModule(module: PModule): ResolvedBlueprint {
        val inputsObj = module.getProperty("inputs") as List<PObject>
        val inputs = collectInputs(inputsObj)

        val buildObj = module.getProperty("build") as PObject
        val envVarsObj = (buildObj.getProperty("environmentVariables") as List<PObject>)
        val envVars = collectEnvVars(envVarsObj)

        objectCache =
            ObjectCache(
                inputs =
                    inputs.associate {
                        when (val name = it.name) {
                            is Resolvable.Literal -> name.value to it
                            else -> "" to it
                        }
                    },
                envVars = envVars.associateBy { it.name },
            )

        val metadata =
            BlueprintMetadata(
                name = module.getProperty("name") as String,
                version = module.getProperty("version") as String,
                url = module.getProperty("url") as String,
            )

        val assets = module.getPropertyOrNull("assets")?.takeUnless { it is PNull }?.let { convertAssets(it) }
        val build = convertBuildConfig(buildObj, envVars)

        val instanceSettings =
            module
                .getProperty("instance")
                ?.takeUnless { it is PNull }
                ?.let { it as PObject }
                ?.let {
                    val startup = UniversalPklParser.parseValue<String>(it.getProperty("startup"))
                    InstanceSettings(startup = startup)
                }

        return ResolvedBlueprint(
            metadata = metadata,
            assets = assets,
            inputs = inputs,
            build = build,
            instanceSettings = instanceSettings,
        )
    }

    private fun collectInputs(inputsObj: List<PObject>): List<UserInput> = inputsObj.map(::convertUserInput)

    private fun convertUserInput(inputObj: PObject): UserInput {
        val type = inputObj.getProperty("type") as String
        val name: Resolvable<String> = UniversalPklParser.parseValue(inputObj.getProperty("name"))
        val label: Resolvable<String> = UniversalPklParser.parseValue(inputObj.getProperty("label"))

        return when (type) {
            "text" -> {
                TextInput(name, label)
            }

            "password" -> {
                PasswordInput(name, label)
            }

            "port" -> {
                val default: Resolvable<Int> =
                    UniversalPklParser.parseValue(inputObj.getProperty("default"))
                PortInput(name, label, default)
            }

            "checkbox" -> {
                val default: Resolvable<Boolean> =
                    UniversalPklParser.parseValue(inputObj.getProperty("default"))
                CheckboxInput(name, label, default)
            }

            "select" -> {
                SelectInput(name, label)
            }

            "datasize" -> {
                DataSizeInput(name, label)
            }

            else -> {
                throw IllegalArgumentException("Unknown input type: $type")
            }
        }
    }

    private fun collectEnvVars(envVarsObj: List<PObject>): List<EnvironmentVariable> {
        val envVars = mutableListOf<EnvironmentVariable>()

        for (element in envVarsObj) {
            val name = element.getProperty("name") as String
            val valueObj: Resolvable<String> = UniversalPklParser.parseValue(element.getProperty("value"))

            envVars.add(EnvironmentVariable(name, valueObj))
        }

        return envVars
    }

    private fun convertAssets(assetsObj: Any): AppAssets {
        val assetsMap = assetsObj as PObject
        val icon = assetsMap.getProperty("icon") as String
        return AppAssets(icon = icon)
    }

    private fun convertBuildConfig(
        buildObj: PObject,
        environmentVariables: List<EnvironmentVariable>,
    ): BuildConfig {
        val dockerObj = buildObj.getProperty("docker") as PObject
        val docker = convertDockerConfig(dockerObj)

        return BuildConfig(docker = docker, environmentVariables = environmentVariables)
    }

    private fun convertDockerConfig(dockerObj: PObject): DockerConfig {
        val image: Resolvable<String> = UniversalPklParser.parseValue(dockerObj.getProperty("image"))
        return DockerConfig(image = image)
    }

    override fun close() {
        evaluator.close()
    }
}
