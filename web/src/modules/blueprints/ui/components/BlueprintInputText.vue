<script lang="ts" setup>
import VInput from "@/modules/platform/ui/components/form/VInput.vue"
import VLabel from "@/modules/platform/ui/components/form/VLabel.vue"
import VFieldSet from "@/modules/platform/ui/components/form/VFieldSet.vue"
import type { BlueprintBuildInput } from "@/modules/blueprints/api/models/blueprint.model.ts"

const props = defineProps<Partial<BlueprintBuildInput>>()
const model = defineModel()
</script>

<template>
    <VFieldSet>
        <VLabel>
            {{ props.label }}
            <VInput
                ref="input"
                :placeholder="props.placeholder"
                :type="props.sensitive ? 'password' : 'text'"
                :value="model"
                autoFocus
                required="true"
            />
        </VLabel>
    </VFieldSet>
</template>

<style lang="scss" scoped></style>
