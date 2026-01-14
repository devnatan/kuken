package gg.kuken.feature.blueprint

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import com.typesafe.config.ConfigUtil
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueFactory
import gg.kuken.feature.blueprint.model.ProcessedBlueprint
import gg.kuken.feature.blueprint.model.ProcessedBlueprint.Property
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible

@OptIn(ExperimentalSerializationApi::class)
class BlueprintProcessor {
    val hocon =
        Hocon {
            useConfigNamingConvention = true
        }

    fun process(
        input: String,
        source: String,
    ): ProcessedBlueprint {
        val parseOptions = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
        val parsed = ConfigFactory.parseString(input, parseOptions)
        val processed =
            process(
                key = "",
                config = parsed,
            ).withValue("source", ConfigValueFactory.fromAnyRef(source))

        return hocon.decodeFromConfig<ProcessedBlueprint>(processed)
    }

    @OptIn(InternalSerializationApi::class)
    fun process(
        key: String,
        config: Config,
    ): Config {
        var config = config
        var dummy = ConfigFactory.empty()
        for ((path, node) in config.entrySet()) {
            val path = if (key.isEmpty()) path else ConfigUtil.joinPath(key, path)

            // Include all root values in descriptor field
            if (key == "" && !path.contains(".")) {
                config = config.withoutPath(path)
                dummy =
                    dummy.withValue(
                        "descriptor.$path",
                        node,
                    )
                continue
            }

            if (path.equals("remote.assets.icon-url")) {
                config = config.withoutPath(path)
                dummy =
                    dummy.withValue(
                        "assets.${ProcessedBlueprint.Asset.ICON}",
                        ConfigValueFactory.fromMap(
                            mapOf(
                                "type" to "image",
                                "source" to "url://${node.unwrapped()}",
                            ),
                        ),
                    )
                continue
            }

            if (path.contains("runtime")) continue

            when {
                node::class.qualifiedName.equals("com.typesafe.config.impl.ConfigConcatenation") -> {
                    val member = (node::class.members.first { it.name == "pieces" } as KProperty<*>)
                    member.isAccessible = true

                    val pieces = member.call(node) as List<*>
                    lateinit var left: ProcessedBlueprint.Property
                    val dependencies = mutableListOf<ProcessedBlueprint.Property>()
                    var literal = ""

                    pieces.filterNotNull().forEachIndexed { index, piece ->
                        val value: ProcessedBlueprint.Property
                        if (piece::class.qualifiedName.equals("com.typesafe.config.impl.ConfigReference")) {
                            value = configReferenceToProperty(piece as ConfigValue)

                            @OptIn(InternalSerializationApi::class)
                            val serialName = value::class.serializer().descriptor.serialName
                            literal += "%{$serialName}"
                        } else {
                            val unwrapped = (piece as ConfigValue).unwrapped().toString()
                            value = ProcessedBlueprint.Property.Constant(unwrapped.trim())
                            literal += unwrapped
                        }

                        when (index) {
                            0 -> left = value
                            else -> dependencies.add(value)
                        }
                    }

                    val property = ProcessedBlueprint.Property.Dynamic(literal, left, dependencies)
                    val bean =
                        hocon
                            .encodeToConfig(property)
                            .withValue("type", ConfigValueFactory.fromAnyRef("dynamic"))
                            .root()

                    config = config.withoutPath(path)
                    dummy =
                        dummy.withValue(
                            path,
                            bean,
                        )
                    continue
                }

                node::class.qualifiedName.equals("com.typesafe.config.impl.ConfigReference") -> {
                    val property = configReferenceToProperty(node)
                    config = config.withoutPath(path)

                    if (property is ProcessedBlueprint.Property.Unresolved) {
                        continue
                    }

                    val typeName = property::class.serializer().descriptor.serialName
                    val bean =
                        hocon
                            .encodeToConfig(property)
                            .withValue("type", ConfigValueFactory.fromAnyRef(typeName))
                            .root()

                    dummy =
                        dummy.withValue(
                            path,
                            bean,
                        )
                    continue
                }
            }

            if (path.startsWith("build.env")) {
                val property = Property.Constant(node.unwrapped().toString())
                val bean =
                    hocon
                        .encodeToConfig(property)
                        .withValue(
                            "type",
                            ConfigValueFactory.fromAnyRef(property::class.serializer().descriptor.serialName),
                        ).root()

                config = config.withoutPath(path)
                dummy =
                    dummy.withValue(
                        path,
                        bean,
                    )
                continue
            }

            if (path.startsWith("inputs") || path.startsWith("build.docker")) {
                config = config.withoutPath(path)
                dummy =
                    dummy.withValue(
                        path,
                        node,
                    )
                continue
            }

            if (node is ConfigObject) {
                config = process(path, node.toConfig())
            }
        }

        return dummy
    }

    fun configReferenceToProperty(node: ConfigValue): ProcessedBlueprint.Property {
        val member = (node::class.members.first { it.name == "expr" } as KProperty<*>)
        member.isAccessible = true

        try {
            val expr =
                member.call(node).toString().run {
                    substring(
                        startIndex = 2,
                        endIndex = length - 1,
                    )
                }

            if (expr.startsWith("placeholders.")) {
                val type =
                    when (val name = expr.substringAfter("placeholders.")) {
                        "build.port" -> ProcessedBlueprint.Property.Placeholder.Type.SERVER_PORT
                        "commands.template" -> ProcessedBlueprint.Property.Placeholder.Type.COMMAND_TEMPLATE
                        else -> error("Unsupported placeholder type $name")
                    }

                return ProcessedBlueprint.Property.Placeholder(type.substitution)
            }

            if (expr.startsWith("build.env.")) {
                return ProcessedBlueprint.Property.EnvironmentVariable(expr.substringAfter("build.env."))
            }

            if (expr.startsWith("inputs.")) {
                return ProcessedBlueprint.Property.Input(expr.substringAfter("inputs."))
            }

            return ProcessedBlueprint.Property.Unresolved
        } finally {
            member.isAccessible = false
        }
    }
}
