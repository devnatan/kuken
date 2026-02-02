<template>
  <router-view />
</template>

<script lang="ts" setup>
import { useHead } from "@unhead/vue"
import { usePlatformStore } from "@/modules/platform/platform.store.ts"

const organizationName = usePlatformStore().getBackendInfo.organization.name
useHead({
  title: `Welcome to ${organizationName}`
})
</script>
