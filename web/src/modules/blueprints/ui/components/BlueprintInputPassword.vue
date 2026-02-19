<script lang="ts" setup>
import type { BlueprintBuildInputPassword } from "@/modules/blueprints/api/models/blueprint.spec.model.ts"
import VFieldSet from "@/modules/platform/ui/components/form/VFieldSet.vue"
import VInput from "@/modules/platform/ui/components/form/VInput.vue"
import VLabel from "@/modules/platform/ui/components/form/VLabel.vue"

const props = defineProps<BlueprintBuildInputPassword>()
const model = defineModel({ type: String })
</script>

<template>
  <VFieldSet>
    <VLabel>
      {{ props.label }}
      <VInput v-model="model" required="true" type="password" />
    </VLabel>
  </VFieldSet>
</template>

<style lang="scss" scoped></style>
