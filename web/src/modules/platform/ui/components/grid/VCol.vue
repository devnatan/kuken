<template>
  <div :class="`col--${size}`">
    <slot />
  </div>
</template>
<script setup lang="ts">
defineProps<{ size: number }>()
</script>
