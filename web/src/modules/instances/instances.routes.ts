import type { RouteRecordRaw } from "vue-router"
import { importPage } from "@/router.ts"

export const InstancesRoutes: Array<RouteRecordRaw> = [
  {
    path: "instances/:instanceId",
    name: "instance",
    props: true,
    component: importPage("instances", "InstanceMain"),
    children: [
      {
        path: "overview",
        name: "instance.overview",
        component: importPage("instances", "InstanceOverview"),
        meta: {
          title: "Overview"
        }
      },
      {
        path: "console",
        name: "instance.console",
        component: importPage("instances", "InstanceConsole"),
        meta: {
          title: "Console",
          layout: null
        }
      },
      {
        path: "files",
        component: importPage("instances", "files/InstanceFiles"),
        meta: {
          title: "File System"
        },
        children: [
          {
            path: "",
            name: "instance.files",
            props: true,
            component: importPage("instances", "files/InstanceFileList"),
            meta: {
              title: "File List"
            }
          },
          {
            path: "editor",
            name: "instance.file.editor",
            props: true,
            component: importPage("instances", "files/InstanceFileEditor"),
            meta: {
              title: "File Contents"
            }
          }
        ]
      }
    ]
  }
]
