import type { NavigationGuard, NavigationGuardNext, RouteLocationNormalized } from "vue-router"
import setupService from "@/modules/setup/api/services/setup.service.ts"
import { AxiosError } from "axios"
import { AUTH_LOGIN_ROUTE } from "@/modules/auth/auth.routes.ts"
import { SETUP_ROUTE } from "@/router.ts"

export const RequireSetupGuard: NavigationGuard = async (
    _to: RouteLocationNormalized,
    _from: RouteLocationNormalized,
    next: NavigationGuardNext
) => {
    try {
        const setup = await setupService.getSetup()
        if (!setup.completed) {
            if (_to.name == SETUP_ROUTE) return next(true)
            else return next({ name: SETUP_ROUTE })
        }
    } catch (error) {
        if (error instanceof AxiosError && error.response?.status === 423 /* Locked */) {
            return next({ name: AUTH_LOGIN_ROUTE })
        }
    }

    return next()
}
