package gg.kuken.feature.blueprint.processor

import org.pkl.core.DataSize
import org.pkl.core.PObject

private const val NEW_KEYWORD = "new "
private const val RUNTIME_REF_DELIMITERS = "__"
private const val RUNTIME_REF_IDENTIFIER = RUNTIME_REF_DELIMITERS + "REF" + RUNTIME_REF_DELIMITERS

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

    private val CONDITION_PROP_PATTERN =
        Regex(
            """new\s+ConditionProperty\s*\{\s*name\s*=\s*"([^"]+)"\s*;\s*value\s*=\s*"([^"]+)"\s*}""",
            RegexOption.MULTILINE,
        )

    @Suppress("UNCHECKED_CAST")
    fun <T> parseValue(value: Any?): Resolvable<T> =
        when (value) {
            is String -> parseString(value.replace("\n ", "").trim())
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
        val condMatches = CONDITION_PROP_PATTERN.findAll(value).toList()

        if (inputMatches.isEmpty() && refMatches.isEmpty() && envVarMatches.isEmpty() && condMatches.isEmpty()) {
            return Resolvable.Literal(value) as Resolvable<T>
        }

        val trimmed = value.trim()
        if (refMatches.size == 1 && trimmed.startsWith(RUNTIME_REF_IDENTIFIER)) {
            val refPath = refMatches[0].groupValues[1]
            return Resolvable.RuntimeRef(refPath) as Resolvable<T>
        }

        if (trimmed.startsWith(NEW_KEYWORD)) {
            if (inputMatches.size == 1 && refMatches.isEmpty() && envVarMatches.isEmpty() && condMatches.isEmpty()) {
                val inputName = inputMatches[0].groupValues[2]
                return Resolvable.InputRef(inputName) as Resolvable<T>
            }

            if (envVarMatches.size == 1 && inputMatches.isEmpty() && refMatches.isEmpty() && condMatches.isEmpty()) {
                val envVarName = envVarMatches[0].groupValues[1]
                return Resolvable.EnvVarRef(envVarName) as Resolvable<T>
            }

            if (condMatches.size == 1 && inputMatches.isEmpty() && refMatches.isEmpty() && envVarMatches.isEmpty()) {
                val inputName = condMatches[0].groupValues[1]
                val value = condMatches[0].groupValues[2]
                return Resolvable.ConditionalRef(inputName, value) as Resolvable<T>
            }
        }

        return parseInterpolated(
            value,
            inputMatches,
            refMatches,
            envVarMatches,
            condMatches,
        ) as Resolvable<T>
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

            className == "Ref" -> {
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
        condMatches: List<MatchResult>,
    ): Resolvable.Interpolated {
        val parts = mutableListOf<Resolvable<*>>()

        val allMatches =
            buildList {
                addAll(inputMatches.map { Match(it, MatchType.INPUT, it.range) })
                addAll(refMatches.map { Match(it, MatchType.REF, it.range) })
                addAll(envVarMatches.map { Match(it, MatchType.ENV_VAR, it.range) })
                addAll(condMatches.map { Match(it, MatchType.CONDITIONAL, it.range) })
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

                MatchType.CONDITIONAL -> {
                    template =
                        template.replaceRange(
                            match.range,
                            $$"${cond:$${match.match.groupValues[1]}:$${match.match.groupValues[2]}}",
                        )
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
                    val refPath = match.match.groupValues[2]
                    parts.add(Resolvable.RuntimeRef(refPath.removeSurrounding(prefix = ":", suffix = "__")))
                }

                MatchType.ENV_VAR -> {
                    val envVarName = match.match.groupValues[1]
                    parts.add(Resolvable.EnvVarRef(envVarName))
                }

                MatchType.CONDITIONAL -> {
                    val inputName = match.match.groupValues[1]
                    val value = match.match.groupValues[2]
                    parts.add(Resolvable.ConditionalRef(inputName, value))
                }
            }

            lastIndex = match.range.last + 1
        }

        if (lastIndex < value.length) {
            val text = value.substring(lastIndex)

            if (text.isNotEmpty()) {
                // We do not catch trailing {} it in Inputs regex
                if (text == " }") {
                    template = template.removeSuffix(text)
                } else {
                    parts.add(Resolvable.Literal(text))
                }
            }
        }

        return Resolvable.Interpolated(template, parts)
    }

    private fun extractRefPath(obj: PObject): String {
        val str = obj.toString()
        return str
            .substringAfter(RUNTIME_REF_IDENTIFIER)
            .substringBefore(RUNTIME_REF_DELIMITERS) // delimiters
            .substringAfter(":") // reference type
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
        CONDITIONAL,
    }
}

data class ObjectCache(
    val inputs: Map<String, Any> = emptyMap(),
    val envVars: Map<String, Any> = emptyMap(),
)
