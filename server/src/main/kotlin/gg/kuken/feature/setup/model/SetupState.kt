package gg.kuken.feature.setup.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetupState(
    val completed: Boolean,
    val remainingSteps: Set<Step>,
) {
    @Serializable
    sealed class Step {
        @Serializable
        @SerialName("create-account")
        data object CreateAccount : Step()

        @Serializable
        @SerialName("organization-name")
        data object OrganizationName : Step()
    }
}
