import { createRouter, createWebHistory } from "vue-router"
import { AuthRoutes } from "@/modules/auth/auth.routes"
import { AccountsRoute } from "@/modules/accounts/accounts.routes"
import { AuthenticatedOnlyGuard } from "@/modules/auth/guards/authenticated-only.guard"
import { HomeRoutes } from "@/modules/home/home.routes"
import { BlueprintsRoutes } from "@/modules/blueprints/blueprints.routes"
import { RequireSetupGuard } from "@/modules/setup/guards/require-setup.guard.ts"

export function importPage(module: string, path: string): () => Promise<unknown> {
    const comps = import.meta.glob(`./modules/**/ui/pages/**/*.vue`)
    return comps[`./modules/${module}/ui/pages/${path}Page.vue`]
}

export const SETUP_ROUTE = "setup"

export default createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        ...AuthRoutes,
        {
            path: "/",
            component: importPage("platform", "Root"),
            beforeEnter: [RequireSetupGuard, AuthenticatedOnlyGuard],
            children: [...HomeRoutes, ...AccountsRoute, ...BlueprintsRoutes]
        },
        {
            path: "/setup",
            name: SETUP_ROUTE,
            component: importPage("setup", "Setup"),
            beforeEnter: [RequireSetupGuard],
            meta: {
                title: "Set Up"
            }
        }
    ]
})
