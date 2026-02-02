<script lang="ts" setup>
defineProps<{ html: string }>()
</script>

<template>
  <code v-html="html" />
</template>
