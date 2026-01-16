package gg.kuken.feature.blueprint.model

import gg.kuken.feature.blueprint.processor.ResolvedBlueprint
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class Blueprint(
    val id: Uuid,
    val createdAt: Instant,
    val updatedAt: Instant,
    val origin: String,
    val spec: ResolvedBlueprint,
)
