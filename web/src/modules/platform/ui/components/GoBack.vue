<script setup lang="ts">
import VIcon from "@/modules/platform/ui/components/icons/VIcon.vue"
</script>

<template>
  <div class="goBack" @click="$router.back()">
    <VIcon name="AngleLeft" />
    Go back
  </div>
</template>

<style scoped lang="scss">
.goBack {
  display: flex;
  flex-direction: row;
  align-items: center;
  color: var(--kt-content-neutral-low);
  fill: var(--kt-content-neutral-low);

  &:hover {
    cursor: pointer;
    color: var(--kt-content-neutral-high);
    fill: var(--kt-content-neutral-high);
  }
}
</style>
