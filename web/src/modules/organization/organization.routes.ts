import OrganizationPage from "@/modules/organization/ui/pages/OrganizationPage.vue"
import type { RouteRecordRaw } from "vue-router"

export const OrganizationRoutes: Array<RouteRecordRaw> = [
  {
    path: "organization",
    name: "organization.home",
    props: true,
    component: OrganizationPage
  }
]
