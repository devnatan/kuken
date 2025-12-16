<script setup lang="ts">
import TheHeader from "@/modules/platform/ui/components/TheHeader.vue";
</script>
<template>
  <div :class="$style.root">
    <!-- <TheSidebar /> -->
    <main :class="$style.body">
      <TheHeader />
      <div :class="$style.content">
        <slot />
      </div>
    </main>
  </div>
</template>
<style lang="scss" module>
.root {
	display: flex;
	height: 100%;
	overflow: hidden;
}

.body {
	display: flex;
	flex-grow: 1;
	flex-direction: column;
	overflow: auto;
  background-color: #D85F34;
}

.content {
  background-color: #fff;
  border-radius: 20px;
  margin: 0 32px 32px 32px;
  height: 100%;
}
</style>
