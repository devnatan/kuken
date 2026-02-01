<script lang="ts" setup>
import VLabel from "@/modules/platform/ui/components/form/VLabel.vue"
import VFieldSet from "@/modules/platform/ui/components/form/VFieldSet.vue"
import type { BlueprintBuildInputSelect } from "@/modules/blueprints/api/models/blueprint.spec.model.ts"
import { onMounted } from "vue"
import VOption from "@/modules/platform/ui/components/form/VOption.vue"
import VSelect from "@/modules/platform/ui/components/form/VSelect.vue"

const props = defineProps<BlueprintBuildInputSelect>()
const model = defineModel()

onMounted(() => {
    model.value = props.items[0]
})
</script>

<template>
    <VFieldSet>
        <VLabel>
            {{ props.label }}
            <VSelect v-model="model">
                <VOption v-for="(label, key) in props.items" :key="key" :value="label">
                    {{ label }}
                </VOption>
            </VSelect>
        </VLabel>
    </VFieldSet>
</template>

<style lang="scss" scoped>
label {
    display: flex;
    justify-content: space-between;
}
</style>
