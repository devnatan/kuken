import { AccountsRoutes } from "@/modules/accounts/accounts.routes.ts"
import { AuthRoutes } from "@/modules/auth/auth.routes"
import { AuthenticatedOnlyGuard } from "@/modules/auth/guards/authenticated-only.guard"
import { HomeRoutes } from "@/modules/home/home.routes"
import { OrganizationRoutes } from "@/modules/organization/organization.routes.ts"
import { UnitsRoutes } from "@/modules/units/units.routes.ts"
import { createRouter, createWebHistory } from "vue-router"

export function importPage(module: string, path: string): () => Promise<unknown> {
  const comps = import.meta.glob(`./modules/**/ui/pages/**/*.vue`)
  return comps[`./modules/${module}/ui/pages/${path}Page.vue`]!
}

export function importPageRelative(path: string): () => Promise<unknown> {
  return () => import(`ui/pages/${path}Page.vue`)
}

export const SETUP_ROUTE = "setup"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...AuthRoutes,
    {
      path: "/",
      component: importPage("platform", "Root"),
      beforeEnter: [AuthenticatedOnlyGuard],
      children: [...HomeRoutes, ...UnitsRoutes, ...OrganizationRoutes, ...AccountsRoutes]
    },
    {
      path: "/setup",
      name: SETUP_ROUTE,
      component: importPage("setup", "Setup"),
      meta: {
        title: "Set Up"
      }
    },
    {
      path: "/access-denied",
      name: "access-denied",
      component: importPage("platform", "AccessDenied")
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found",
      component: importPage("platform", "NotFound")
    }
  ]
})

export default router
