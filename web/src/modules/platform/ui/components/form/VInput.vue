<template>
    <input
        ref="input"
        :class="{
            'input--onSurface': onSurface
        }"
        :disabled="disabled"
        :placeholder="placeholder"
        :readonly="disabled"
        :value="modelValue"
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
    modelValue?: string
}>()

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
