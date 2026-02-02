<template>
  <label
    :class="{
      'label--onColor': onColor,
      'label--hight': high,
      'label--required': required
    }"
    class="label"
  >
    <slot v-if="$slots.default" />
    <span v-if="$slots.description" class="description">
      <slot name="description" />
    </span>
  </label>
</template>

<script lang="ts" setup>
defineProps<{
  onColor?: boolean
  high?: boolean
  required?: boolean
}>()

defineSlots<{ default: string; description: string }>()
</script>

<style lang="scss" scoped>
input + .description {
  display: block;
  margin-top: 0.4rem;
}

.description {
  color: var(--kt-content-neutral);
  font-weight: 400;
}
</style>
