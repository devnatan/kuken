<script lang="ts" setup>
import TheHeader from "@/modules/platform/ui/components/TheHeader.vue"
import { useUnitStore } from "@/modules/units/units.store.ts"

const unit = useUnitStore().getUnit
const title = `${unit.instance.blueprint.header.name} - ${unit.name}`
</script>
<template>
  <div class="instance-console-layout">
    <TheHeader>
      <div class="title">
        <h4>Console</h4>
        <p>{{ title }}</p>
      </div>
    </TheHeader>
    <div class="content">
      <slot />
    </div>
  </div>
</template>
<style lang="scss" scoped>
.instance-console-layout {
  display: flex;
  height: 100vh;
  flex-grow: 1;
  flex-direction: column;
  overflow: auto;
  background-color: rgb(30, 39, 46);
  color: #fff;

  .content {
    background-color: rgb(30, 39, 46);
    height: 100%;
    overflow-y: auto;
  }

  header {
    .title {
      display: flex;
      flex-direction: column;
      gap: 0;
      line-height: 1.2;

      p {
        color: var(--kt-content-neutral);
      }
    }
  }
}
</style>
