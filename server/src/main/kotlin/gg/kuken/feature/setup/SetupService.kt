package gg.kuken.feature.setup

import gg.kuken.feature.account.AccountService
import gg.kuken.feature.setup.model.SetupState

class SetupService(
    private val accountService: AccountService,
) {
    fun state(): SetupState = SetupState(completed = false)
}
