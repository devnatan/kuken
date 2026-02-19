<template>
  <input
    ref="input"
    v-model="model"
    :class="{
      'input--onSurface': onSurface
    }"
    :disabled="disabled"
    :placeholder="placeholder"
    :readonly="disabled"
    class="input"
    @input="onInputChange"
  />
</template>

<script lang="ts" setup>
import { onMounted, useTemplateRef } from "vue"

const emits = defineEmits(["update:modelValue"])
const props = defineProps<{
  disabled?: boolean
  placeholder?: string
  onSurface?: boolean
  autoFocus?: boolean
}>()
const model = defineModel<unknown>()

function onInputChange(event: Event): void {
  emits("update:modelValue", (event.target as HTMLInputElement).value)
}

const rootElement = useTemplateRef<HTMLInputElement>("input")
onMounted(() => {
  if (props.autoFocus) {
    rootElement.value!.focus()
  }
})
</script>
