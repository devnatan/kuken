package gg.kuken.feature.blueprint.processor

import org.pkl.core.DataSize
import org.pkl.core.PObject

private const val NEW_KEYWORD = "new "
private const val INTERNAL_REFERENCE = "__REF__"

object UniversalPklParser {
    private val INPUT_PATTERN =
        Regex(
            """new\s+(TextInput|PasswordInput|PortInput|CheckboxInput|SelectInput|DataSizeInput)\s*\{[^}]*name\s*=\s*"([^"]+)"[^}]*}""",
            RegexOption.MULTILINE,
        )

    private val REF_PATTERN =
        Regex(
            """new\s+Ref\s*\{[^}]*key\s*=\s"(\w+\s*)(.*)"\s*}""",
            RegexOption.MULTILINE,
        )

    private val ENV_VAR_PATTERN =
        Regex(
            """new\s+EnvironmentVariable\s*\{\s*name\s*=\s*"([A-Z_][A-Z0-9_]*)"\s*;[^}]*}""",
            RegexOption.MULTILINE,
        )

    @Suppress("UNCHECKED_CAST")
    fun <T> parseValue(value: Any?): Resolvable<T> =
        when (value) {
            is String -> parseString(value)
            is Int -> Resolvable.Literal(value.toString())
            is Long -> Resolvable.Literal(value.toString())
            is Double -> Resolvable.Literal(value.toString())
            is Float -> Resolvable.Literal(value.toString())
            is Boolean -> Resolvable.Literal(value.toString())
            is PObject -> parseObject(value)
            is DataSize -> Resolvable.Literal(value.value.toLong().toString())
            null -> Resolvable.Null
            else -> Resolvable.Literal(value.toString())
        } as Resolvable<T>

    @Suppress("UNCHECKED_CAST")
    private fun <T> parseString(value: String): Resolvable<T> {
        val inputMatches = INPUT_PATTERN.findAll(value).toList()
        val refMatches = REF_PATTERN.findAll(value).toList()
        val envVarMatches = ENV_VAR_PATTERN.findAll(value).toList()

        if (inputMatches.isEmpty() && refMatches.isEmpty() && envVarMatches.isEmpty()) {
            return Resolvable.Literal(value) as Resolvable<T>
        }

        val trimmed = value.trim()

        if (inputMatches.size == 1 && refMatches.isEmpty() && envVarMatches.isEmpty() &&
            trimmed.startsWith(NEW_KEYWORD)
        ) {
            val inputName = inputMatches[0].groupValues[2]
            return Resolvable.InputRef(inputName) as Resolvable<T>
        }

        if (refMatches.size == 1 && inputMatches.isEmpty() && envVarMatches.isEmpty() &&
            trimmed.startsWith(
                INTERNAL_REFERENCE,
            )
        ) {
            val refPath = refMatches[0].groupValues[1]
            return Resolvable.RuntimeRef(refPath) as Resolvable<T>
        }

        if (envVarMatches.size == 1 && inputMatches.isEmpty() && refMatches.isEmpty() &&
            trimmed.startsWith(NEW_KEYWORD)
        ) {
            val envVarName = envVarMatches[0].groupValues[1]
            return Resolvable.EnvVarRef(envVarName) as Resolvable<T>
        }

        return parseInterpolated(value, inputMatches, refMatches, envVarMatches) as Resolvable<T>
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> parseObject(obj: PObject): Resolvable<T> {
        val className = obj.classInfo.simpleName

        return when {
            className.endsWith("Input") -> {
                val inputName = obj.getProperty("name") as String
                Resolvable.InputRef(inputName) as Resolvable<T>
            }

            className == "EnvironmentVariable" -> {
                val envVarName = obj.getProperty("name") as String
                Resolvable.EnvVarRef(envVarName) as Resolvable<T>
            }

            obj.toString().contains("__REFS__") -> {
                val refPath = extractRefPath(obj)
                Resolvable.RuntimeRef(refPath) as Resolvable<T>
            }

            else -> {
                val stringValue = obj.toString()
                parseString(stringValue)
            }
        }
    }

    private fun parseInterpolated(
        value: String,
        inputMatches: List<MatchResult>,
        refMatches: List<MatchResult>,
        envVarMatches: List<MatchResult>,
    ): Resolvable.Interpolated {
        val parts = mutableListOf<Resolvable<*>>()

        val allMatches =
            buildList {
                addAll(inputMatches.map { Match(it, MatchType.INPUT, it.range) })
                addAll(refMatches.map { Match(it, MatchType.REF, it.range) })
                addAll(envVarMatches.map { Match(it, MatchType.ENV_VAR, it.range) })
            }.sortedBy { it.range.first }

        var lastIndex = 0
        var template = value

        for (match in allMatches.reversed()) {
            when (match.type) {
                MatchType.INPUT -> {
                    template =
                        template.replaceRange(match.range, $$"${input:$${match.match.groupValues[2]}}")
                }

                MatchType.REF -> {
                    template =
                        template.replaceRange(
                            match.range,
                            $$"${ref:$${match.match.groupValues[1]}$${match.match.groupValues[2]}}",
                        )
                }

                MatchType.ENV_VAR -> {
                    template =
                        template.replaceRange(match.range, $$"${env:$${match.match.groupValues[1]}}")
                }
            }
        }

        for (match in allMatches) {
            if (match.range.first > lastIndex) {
                val text = value.substring(lastIndex, match.range.first)
                if (text.isNotEmpty()) {
                    parts.add(Resolvable.Literal(text))
                }
            }

            when (match.type) {
                MatchType.INPUT -> {
                    val inputName = match.match.groupValues[2]
                    parts.add(Resolvable.InputRef(inputName))
                }

                MatchType.REF -> {
                    val refPath = match.match.groupValues[1] + match.match.groupValues[2]
                    parts.add(Resolvable.RuntimeRef(refPath))
                }

                MatchType.ENV_VAR -> {
                    val envVarName = match.match.groupValues[1]
                    parts.add(Resolvable.EnvVarRef(envVarName))
                }
            }

            lastIndex = match.range.last + 1
        }

        if (lastIndex < value.length) {
            val text = value.substring(lastIndex)
            if (text.isNotEmpty()) {
                parts.add(Resolvable.Literal(text))
            }
        }

        return Resolvable.Interpolated(template, parts)
    }

    private fun extractRefPath(obj: PObject): String {
        val str = obj.toString()
        return str
            .substringAfter(INTERNAL_REFERENCE)
            .substringBefore("__")
    }

    private data class Match(
        val match: MatchResult,
        val type: MatchType,
        val range: IntRange,
    )

    private enum class MatchType {
        INPUT,
        REF,
        ENV_VAR,
    }
}

data class ObjectCache(
    val inputs: Map<String, Any> = emptyMap(),
    val envVars: Map<String, Any> = emptyMap(),
)
