import type { RouteRecordRaw } from "vue-router"
import { importPage } from "@/router.ts"

export const UnitsRoutes: Array<RouteRecordRaw> = [
    {
        path: "new",
        name: "units.create",
        props: {
            blueprintId: {
                required: false
            }
        },
        component: importPage("units", "CreateUnit"),
        meta: {
            title: "Create new"
        }
    }
]
