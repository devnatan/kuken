<script setup lang="ts">
import { useHead } from "@unhead/vue"
import { useI18n } from "petite-vue-i18n"

const { t } = useI18n()
useHead({
  title: t("org.home.docTitle")
})
</script>

<template>
  <router-view />
</template>

<style scoped lang="scss"></style>
