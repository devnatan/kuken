import type { BlueprintProperty } from "@/modules/blueprints/api/models/blueprint.model.ts"
import BlueprintPropertyLiteral from "@/modules/blueprints/ui/components/property/BlueprintPropertyLiteral.vue"
import { h, type VNode } from "vue"

export function useBlueprintProperty(property: BlueprintProperty): VNode {
    if (property.type === "literal") h(BlueprintPropertyLiteral, { value: property.value })

    return h(BlueprintPropertyLiteral, { value: "<unknown>" })
}
