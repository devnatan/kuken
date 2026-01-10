<template>
    <VContainer class="container">
        <router-view />
    </VContainer>
</template>

<script lang="ts" setup>
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"
import { useHead } from "@unhead/vue"
import { usePlatformStore } from "@/modules/platform/platform.store.ts"

const organizationName = usePlatformStore().getBackendInfo.organization.name
useHead({
    title: `Welcome to ${organizationName}`
})
</script>

<style lang="scss" scoped>
.container {
    padding: 48px;
    display: flex;
    flex-direction: column;
}
</style>
