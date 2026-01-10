<script lang="ts" setup>
import Breadcrumb from "@/modules/platform/ui/components/Breadcrumb.vue"
import { useHead } from "@unhead/vue"

useHead({
    title: "Game Directory"
})
</script>

<template>
    <Breadcrumb />
    <router-view />
</template>

<style lang="scss" scoped></style>
