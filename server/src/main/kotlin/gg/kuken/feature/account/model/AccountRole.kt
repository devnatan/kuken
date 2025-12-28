package gg.kuken.feature.account.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class AccountRole(
    val id: Uuid,
    val accountId: Uuid,
    val roleId: Uuid,
    val grantedAt: Instant,
    val grantedBy: Uuid,
    val expiresAt: Instant?,
)
