<script lang="ts" setup>
import VInput from "@/modules/platform/ui/components/form/VInput.vue"
import VLabel from "@/modules/platform/ui/components/form/VLabel.vue"
import VFieldSet from "@/modules/platform/ui/components/form/VFieldSet.vue"
import { onMounted } from "vue"
import type { BlueprintBuildInputPortNumber } from "@/modules/blueprints/api/models/blueprint.spec.model.ts"

const props = defineProps<BlueprintBuildInputPortNumber>()
const model = defineModel()

onMounted(() => {
  model.value = props.default
})
</script>

<template>
  <VFieldSet>
    <VLabel>
      {{ props.label }}
      <VInput v-model="model" type="number" />
      <template v-if="props.description" #description>{{ props.description }}</template>
    </VLabel>
  </VFieldSet>
</template>

<style lang="scss" scoped></style>
