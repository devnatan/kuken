package gg.kuken.feature.instance.model

import kotlinx.serialization.Serializable

@Serializable
data class HostPort(
    val host: String?,
    val port: UShort,
) {
    override fun toString(): String =
        when {
            host == null -> ":$port"
            else -> "$host:$port"
        }
}
