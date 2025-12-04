package gg.kuken.feature.identity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class IdentityGeneratorService {

    fun generate(): Uuid = Uuid.random()
}
