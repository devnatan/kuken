package gg.kuken.feature.account.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class Account(
    val id: Uuid,
    val email: String,
    val displayName: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastLoggedInAt: Instant?,
    val avatar: String?,
)
