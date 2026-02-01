package gg.kuken.feature.blueprint.http.dto

import gg.kuken.feature.blueprint.BlueprintSpecSource
import gg.kuken.feature.blueprint.model.Blueprint
import gg.kuken.feature.blueprint.model.BlueprintHeader
import gg.kuken.feature.blueprint.model.BlueprintStatus
import gg.kuken.feature.blueprint.model.isOfficial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class BlueprintResponse(
    @SerialName("id") val id: Uuid,
    @SerialName("created-at") val createdAt: Instant,
    @SerialName("status") val status: BlueprintStatus,
    @SerialName("origin") val origin: BlueprintSpecSource,
    @SerialName("header") val header: BlueprintHeader,
    @SerialName("official") val official: Boolean,
) {
    constructor(blueprint: Blueprint) : this(
        id = blueprint.id,
        createdAt = blueprint.createdAt,
        status = blueprint.status,
        origin = blueprint.origin,
        header = blueprint.header,
        official = blueprint.isOfficial(),
    )
}
