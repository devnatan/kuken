import { importPageRelative } from "@/router.ts"
import type { RouteRecordRaw } from "vue-router"

export const OrganizationRoutes: Array<RouteRecordRaw> = [
  {
    path: "organization",
    name: "organization",
    component: importPageRelative("Organization")
  }
]
