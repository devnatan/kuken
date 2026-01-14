package gg.kuken.feature.unit.model

import kotlin.uuid.Uuid

data class UnitCreateOptions(
    val name: String,
    val blueprintId: Uuid,
    val inputs: Map<String, String>,
    val actorId: Uuid?,
    val externalId: String?,
)
