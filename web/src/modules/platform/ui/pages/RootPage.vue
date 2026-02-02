<template>
  <component :is="layout" v-if="layout != null">
    <RouterView />
  </component>
  <RouterView v-else />
</template>
<script lang="ts" setup>
import { useRoute } from "vue-router"
import RootLayout from "@/modules/platform/ui/layouts/RootLayout.vue"
import { computed } from "vue"
import { isUndefined } from "@/utils"

const currentRoute = useRoute()
const layout = computed(() =>
  isUndefined(currentRoute.meta.layout) ? RootLayout : currentRoute.meta.layout
)
</script>
