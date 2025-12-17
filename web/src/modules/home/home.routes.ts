import type { RouteRecordRaw } from "vue-router"
import { importPage } from "@/router"

export const HOME_ROUTE = "home"

export const HomeRoutes: Array<RouteRecordRaw> = [
    {
        path: "/home",
        name: HOME_ROUTE,
        component: importPage("home", "Home"),
        meta: {
            title: "Home"
        }
    }
]
