<template>
  <img alt="Kuken Logo" aria-label="Logo" :src="src" />
</template>
<script setup lang="ts">
import { computed } from "vue"

const props = defineProps<{ color?: boolean; grayscale?: boolean }>()
const src = computed(
  () => `/img/${props.color ? "logo-color" : props.grayscale ? "logo-grayscale" : "logo"}.svg`
)
</script>
