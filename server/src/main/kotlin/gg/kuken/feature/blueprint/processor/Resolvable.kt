package gg.kuken.feature.blueprint.processor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Resolvable<out T> {
    @Serializable
    @SerialName("null")
    data object Null : Resolvable<Nothing>()

    @Serializable
    @SerialName("literal")
    data class Literal(
        val value: String,
    ) : Resolvable<String>()

    @Serializable
    @SerialName("input")
    data class InputRef(
        val inputName: String,
    ) : Resolvable<String>()

    @Serializable
    @SerialName("ref")
    data class RuntimeRef(
        val refPath: String,
    ) : Resolvable<String>()

    @Serializable
    @SerialName("env")
    data class EnvVarRef(
        val envVarName: String,
    ) : Resolvable<String>()

    @Serializable
    @SerialName("conditional")
    data class ConditionalRef(
        val inputName: String,
        val value: String,
    ) : Resolvable<String>()

    @Serializable
    @SerialName("interpolated")
    data class Interpolated(
        val template: String,
        val parts: List<Resolvable<*>>,
    ) : Resolvable<String>()

    fun toTemplateString(): String =
        when (this) {
            Null -> {
                "null"
            }

            is Literal -> {
                value
            }

            is InputRef -> {
                $$"${input:$$inputName}"
            }

            is RuntimeRef -> {
                $$"${ref:$$refPath}"
            }

            is EnvVarRef -> {
                $$"${env:$$envVarName}"
            }

            is ConditionalRef -> {
                $$"${cond:$$inputName:$$value}"
            }

            is Interpolated -> {
                parts.joinToString("") { part -> part.toTemplateString() }
            }
        }
}
