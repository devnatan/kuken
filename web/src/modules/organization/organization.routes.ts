import { importPage } from "@/router.ts"
import type { RouteRecordRaw } from "vue-router"

export const OrganizationRoutes: Array<RouteRecordRaw> = [
  {
    path: "organization",
    name: "organization",
    component: importPage("organization", "Organization")
  }
]
