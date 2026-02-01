package gg.kuken.feature.blueprint.processor

import org.pkl.core.resource.ResourceReader
import java.net.URI
import java.util.Optional

object NoopBlueprintResourceReader : ResourceReader {
    val REGEX = Regex("kuken:__(\\w+)__(\\w+)")

    override fun getUriScheme(): String = "kuken"

    override fun read(uri: URI): Optional<in Any> {
        val match = REGEX.find(uri.toString()) ?: return Optional.empty()
        val value: String? =
            when (val kind = match.groupValues[1]) {
                "REF" -> {
                    when (val valueType = match.groupValues[2]) {
                        "Int", "DataSize" -> "0"
                        "String", "Any" -> ""
                        "Boolean" -> "false"
                        else -> error("Unsupported dynamic value type: $valueType")
                    }
                }

                else -> {
                    error("Unsupported dynamic value replacement kind: $kind")
                }
            }

        return value?.let(Optional<String>::of) ?: Optional.empty()
    }

    override fun hasHierarchicalUris(): Boolean = false

    override fun isGlobbable(): Boolean = false
}
