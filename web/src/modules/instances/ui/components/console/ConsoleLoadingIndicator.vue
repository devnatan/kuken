<script lang="ts" setup>
defineProps<{ position: "top" | "bottom" }>()
</script>

<template>
  <div :class="position" class="loading-indicator">
    <div class="loading-spinner"></div>
    <span>{{ position == "top" ? "Loading previous logs..." : "Loading logs..." }}</span>
  </div>
</template>

<style lang="scss" scoped>
.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 12px;
  font-size: 12px;
  color: #606060;
  background-color: #111;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #303030;
  border-top-color: #606060;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
