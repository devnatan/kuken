@file:OptIn(ExperimentalUuidApi::class)

package gg.kuken.features.account.repository

import gg.kuken.features.account.entity.AccountEntity
import gg.kuken.features.account.model.Account
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal interface AccountRepository {

    suspend fun findAll(): List<AccountEntity>

    suspend fun findById(id: Uuid): AccountEntity?

    suspend fun findByEmail(email: String): AccountEntity?

    suspend fun findHashByEmail(email: String): String?

    suspend fun addAccount(account: Account, hash: String)

    suspend fun deleteAccount(id: Uuid)

    suspend fun existsByEmail(email: String): Boolean
}
