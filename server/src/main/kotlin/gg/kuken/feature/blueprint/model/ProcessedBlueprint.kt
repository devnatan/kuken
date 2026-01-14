package gg.kuken.feature.blueprint.model

import com.typesafe.config.ConfigException
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.CompositeDecoder.Companion.UNKNOWN_NAME
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.serializer

typealias ProcessedBlueprintSource = String

@Serializable
data class ProcessedBlueprint(
    val source: ProcessedBlueprintSource,
    val descriptor: Descriptor,
    val build: Build,
    val assets: Map<String, Asset>,
    val inputs: Map<String, Input>,
    @Transient val instance: Instance = Instance(nameFormat = ""),
) {
    @Serializable
    data class Descriptor(
        val name: String,
        val version: String,
    )

    object Source {
        fun externalUrl(url: String): String = "url://$url"

        fun fileSystem(filePath: String): String = "file://$filePath"
    }

    @Serializable
    sealed class Input {
        abstract val label: String
        abstract val placeholder: String?

        @Serializable
        @SerialName("text")
        data class Text(
            override val label: String,
            override val placeholder: String? = null,
            val sensitive: Boolean = false,
        ) : Input()

        @Serializable
        @SerialName("port")
        data class Port(
            override val label: String,
            override val placeholder: String? = null,
            @SerialName("default")
            val defaultValue: UShort,
        ) : Input()
    }

    @Serializable
    data class Instance(
        val nameFormat: String,
    )

    @Serializable
    data class Asset(
        val source: ProcessedBlueprintSource,
        val type: AssetType,
    ) {
        companion object {
            const val ICON = "icon"
        }
    }

    enum class AssetType {
        @SerialName("image")
        IMAGE,

        @SerialName("unknown")
        UNKNOWN,
    }

    @Serializable
    data class Build(
        val env: Map<String, Property> = emptyMap(),
        val ports: Map<String, Property> = emptyMap(),
        val docker: Docker,
    ) {
        @Serializable
        data class Docker(
            @Serializable(with = DynamicPropertySerializer::class)
            val image: Property,
        ) {
            class DynamicPropertySerializer : KSerializer<Property> {
                @OptIn(InternalSerializationApi::class)
                override val descriptor: SerialDescriptor =
                    mapSerialDescriptor(
                        String.serializer().descriptor,
                        Property.serializer().descriptor,
                    )

                @OptIn(InternalSerializationApi::class)
                override fun serialize(
                    encoder: Encoder,
                    value: Property,
                ) {
                    when (value) {
                        is Property.Constant -> {
                            encoder.encodeSerializableValue(
                                value =
                                    linkedMapOf(
                                        "type" to value::class.serializer().descriptor.serialName,
                                        "value" to value.value,
                                    ),
                                serializer =
                                    MapSerializer(
                                        keySerializer = String.serializer(),
                                        valueSerializer = String.serializer(),
                                    ),
                            )
                        }

                        is Property.Dynamic -> {
                            encoder.encodeSerializableValue(
                                value = value,
                                serializer = Property.Dynamic.Serializer(),
                            )
                        }

                        is Property.EnvironmentVariable -> {}

                        is Property.Input -> {
                            encoder.encodeSerializableValue(
                                value =
                                    linkedMapOf(
                                        "type" to value::class.serializer().descriptor.serialName,
                                        "name" to value.name,
                                    ),
                                serializer =
                                    MapSerializer(
                                        keySerializer = String.serializer(),
                                        valueSerializer = String.serializer(),
                                    ),
                            )
                        }

                        is Property.Placeholder -> {
                            encoder.encodeSerializableValue(
                                value =
                                    linkedMapOf(
                                        "type" to value::class.serializer().descriptor.serialName,
                                        "expr" to value.expr,
                                    ),
                                serializer =
                                    MapSerializer(
                                        keySerializer = String.serializer(),
                                        valueSerializer = String.serializer(),
                                    ),
                            )
                        }

                        Property.Unresolved -> {
                            Unit
                        }
                    }
                }

                override fun deserialize(decoder: Decoder): Property =
                    try {
                        deserializeAsStructure(decoder)
                    } catch (_: ConfigException.WrongType) {
                        val value = decoder.decodeString()
                        Property.Constant(value)
                    }

                private fun deserializeAsStructure(decoder: Decoder): Property {
                    if (decoder is JsonDecoder) {
                        val json = decoder.decodeJsonElement()

                        return Json {}.decodeFromJsonElement(json)
                    }

                    return decoder.decodeStructure(descriptor) {
                        lateinit var property: ProcessedBlueprint.Property
                        lateinit var dependencies: List<ProcessedBlueprint.Property>
                        lateinit var literal: String

                        // HOCON properties are NOT ordered so element indexes are not predictable
                        // we need to iterate over each index and search for them
                        while (true) {
                            val index = decodeElementIndex(descriptor)
                            if (index == DECODE_DONE) {
                                break
                            }

                            if (index == UNKNOWN_NAME) {
                                continue
                            }

                            val field: String
                            try {
                                field = decodeStringElement(descriptor, index)
                            } catch (_: SerializationException) {
                                continue
                            }

                            if (field == "property") {
                                property =
                                    decodeSerializableElement(
                                        descriptor = descriptor,
                                        index = index + 1,
                                        deserializer = ProcessedBlueprint.Property.Constant.serializer(),
                                    )
                            }

                            if (field == "literal") {
                                literal = decodeStringElement(descriptor, index + 1)
                            }

                            if (field == "dependencies") {
                                dependencies =
                                    decodeSerializableElement(
                                        descriptor = descriptor,
                                        index = index + 1,
                                        deserializer = ListSerializer(ProcessedBlueprint.Property.serializer()),
                                    )
                            }
                        }

                        ProcessedBlueprint.Property.Dynamic(literal, property, dependencies)
                    }
                }
            }
        }
    }

    @Serializable
    @SerialName("type")
    sealed class Property {
        @Serializable
        @SerialName("constant")
        data class Constant(
            val value: String,
        ) : Property()

        @Serializable
        @SerialName("input")
        data class Input(
            val name: String,
        ) : Property()

        @Serializable
        @SerialName("env")
        data class EnvironmentVariable(
            val env: String,
        ) : Property()

        @Serializable
        @SerialName("placeholder")
        data class Placeholder(
            val expr: String,
        ) : Property() {
            val type: Type get() = Type.entries.first { it.substitution == expr }

            enum class Type(
                val substitution: String,
            ) {
                COMMAND_TEMPLATE("command"),
                SERVER_PORT("build.port"),
            }
        }

        @Serializable
        @SerialName("dynamic")
        data class Dynamic(
            val literal: String,
            val property: Property,
            val dependencies: List<Property>,
        ) : Property() {
            class Serializer : KSerializer<Dynamic> {
                @OptIn(InternalSerializationApi::class)
                override val descriptor =
                    buildSerialDescriptor(
                        serialName = "dynamic",
                        kind = SerialKind.CONTEXTUAL,
                    ) {
                        element<String>("type")
                        element<String>("literal")
                        element<Property>("property")
                        element("dependencies", ListSerializer(Property.serializer()).descriptor)
                    }

                override fun serialize(
                    encoder: Encoder,
                    value: Dynamic,
                ) = encoder.encodeStructure(descriptor) {
                    encodeStringElement(descriptor, 0, descriptor.serialName)
                    encodeStringElement(descriptor, 1, value.literal)
                    encodeSerializableElement(descriptor, 2, Property.serializer(), value.property)
                    encodeSerializableElement(descriptor, 3, ListSerializer(Property.serializer()), value.dependencies)
                }

                override fun deserialize(decoder: Decoder): Dynamic =
                    decoder.decodeStructure(descriptor) {
                        decodeStringElement(descriptor, 0)
                        val literal = decodeStringElement(descriptor, 1)
                        val property = decodeSerializableElement(descriptor, 2, Property.serializer())
                        val dependencies = decodeSerializableElement(descriptor, 3, ListSerializer(Property.serializer()))

                        Dynamic(literal, property, dependencies)
                    }
            }
        }

        @Serializable
        @SerialName("unresolved")
        data object Unresolved : Property()
    }
}
