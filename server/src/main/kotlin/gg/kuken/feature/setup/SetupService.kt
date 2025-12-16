package gg.kuken.feature.setup

import gg.kuken.feature.account.AccountService
import gg.kuken.feature.setup.http.dto.SetupRequest
import gg.kuken.feature.setup.model.SetupState
import kotlinx.coroutines.coroutineScope

class SetupService(
    private val accountService: AccountService,
) {
    fun state(): SetupState = SetupState(
        completed = false,
        remainingSteps = listOf(SetupState.Step.CreateAccount)
    )

    suspend fun tryComplete(request: SetupRequest): SetupState {
        coroutineScope {
            accountService.createAccount(
                email = request.account.email,
                password = request.account.password,
            )
        }

        return SetupState(
            completed = true,
            remainingSteps = emptyList()
        )
    }
}
