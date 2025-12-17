import type { NavigationGuard, NavigationGuardNext, RouteLocationNormalized } from "vue-router"
import setupService from "@/modules/setup/api/services/setup.service.ts"
import { AxiosError } from "axios"
import { AUTH_LOGIN_ROUTE } from "@/modules/auth/auth.routes.ts"
import { SETUP_ROUTE } from "@/router.ts"
import { type Setup, SetupStepType } from "@/modules/setup/api/models/setup.model.ts"

export const RequireSetupGuard: NavigationGuard = async (
    _to: RouteLocationNormalized,
    _from: RouteLocationNormalized,
    next: NavigationGuardNext
) => {
    const comingFromLogin = _to.name == AUTH_LOGIN_ROUTE
    let setup: Setup

    try {
        setup = await setupService.getSetup()
    } catch (error) {
        if (!(error instanceof AxiosError)) throw error

        if (error.response?.status === 423 /* Locked */) {
            return comingFromLogin ? next() : next({ name: AUTH_LOGIN_ROUTE })
        }

        console.error("Failed to get setup status", error)
        return next(false)
    }

    const isInSettingUp = _to.name == SETUP_ROUTE
    if (setup.completed) {
        if (isInSettingUp) return next()
        else return next({ name: SETUP_ROUTE })
    }

    if (comingFromLogin) {
        const needsToCreateAccount =
            setup.remainingSteps.findIndex((step) => step.type === SetupStepType.CREATE_ACCOUNT) >=
            0

        if (needsToCreateAccount) {
            return next({ name: SETUP_ROUTE })
        }
    }

    return isInSettingUp ? next(true) : next({ name: SETUP_ROUTE })
}
