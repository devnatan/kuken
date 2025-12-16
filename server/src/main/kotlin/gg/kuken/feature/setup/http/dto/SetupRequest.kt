package gg.kuken.feature.setup.http.dto

import gg.kuken.feature.account.http.dto.RegisterRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class SetupRequest(
    @field:NotNull
    val account: RegisterRequest,
    @field:NotBlank
    @field:Size(min = 2, max = 24)
    val organizationName: String,
)
