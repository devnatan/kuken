package gg.kuken.feature.blueprint.repository

import gg.kuken.feature.blueprint.entity.BlueprintEntity
import gg.kuken.feature.blueprint.model.BlueprintHeader
import gg.kuken.feature.blueprint.model.BlueprintStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface BlueprintRepository {
    suspend fun findAll(): List<BlueprintEntity>

    suspend fun find(id: Uuid): BlueprintEntity?

    suspend fun create(
        id: Uuid,
        origin: String,
        createdAt: Instant,
        status: BlueprintStatus,
        header: BlueprintHeader,
    ): BlueprintEntity

    suspend fun delete(id: Uuid)
}
