import type { Blueprint } from "@/modules/blueprints/api/models/blueprint.model.ts"

export type Instance = {
  id: string
  blueprint: Blueprint
}
