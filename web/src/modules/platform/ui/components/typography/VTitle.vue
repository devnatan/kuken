<template>
  <h2 :class="{ centered: centered }">
    <slot />
  </h2>
</template>
<script lang="ts" setup>
defineProps({
  centered: { type: Boolean, required: false, default: false }
})
</script>
<style lang="scss" scoped>
h2 {
  position: relative;
  font-weight: bold;
  width: fit-content;
  margin-bottom: 2.4rem;
  user-select: none;

  &:after {
    content: "";
    display: inline;
    border-top: 2px solid var(--kt-content-primary);
    width: 23%;
    bottom: 3px;
    position: absolute;
    right: 15%;
  }
}

.centered {
  text-align: center;
  width: 100%;

  &:after {
    display: none;
  }
}
</style>
