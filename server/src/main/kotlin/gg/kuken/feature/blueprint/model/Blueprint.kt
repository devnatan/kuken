package gg.kuken.feature.blueprint.model

import gg.kuken.feature.blueprint.BlueprintSpecSource
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class Blueprint(
    val id: Uuid,
    val origin: BlueprintSpecSource,
    val header: BlueprintHeader,
    val createdAt: Instant,
    val status: BlueprintStatus,
)

fun Blueprint.isOfficial(): Boolean =
    origin is BlueprintSpecSource.Remote &&
        origin.uri.startsWith("https://raw.githubusercontent.com/kuken-project/blueprints")
