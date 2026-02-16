<script setup lang="ts">
import { useAccount } from "@/modules/accounts/accounts.store.ts"
import PageWrapper from "@/modules/platform/ui/components/PageWrapper.vue"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"
import VLayout from "@/modules/platform/ui/components/grid/VLayout.vue"
import VSection from "@/modules/platform/ui/components/typography/VSection.vue"
import VTitle from "@/modules/platform/ui/components/typography/VTitle.vue"
import { useHead } from "@unhead/vue"
import { useColorMode } from "@vueuse/core"
import { useI18n } from "petite-vue-i18n"

const account = useAccount()
const colorMode = useColorMode({
  emitAuto: true,
  selector: "body",
  storageKey: "kk-theme",
  attribute: "color-scheme"
})
const { t } = useI18n()

useHead({
  title: t("profile.docTitle", { email: account.value.email })
})
</script>

<template>
  <PageWrapper>
    <VContainer>
      <VTitle>{{ t("profile.pageTitle") }}</VTitle>
      <VSection>
        <template #title>{{ t("profile.appearence.title") }}</template>
        <VLayout direction="horizontal" gap="sm">
          <VButton variant="primary" :disabled="colorMode === 'light'" @click="colorMode = 'light'">
            {{ t("profile.appearence.theme.label.light") }}
          </VButton>
          <VButton variant="primary" :disabled="colorMode === 'dark'" @click="colorMode = 'dark'">
            {{ t("profile.appearence.theme.label.dark") }}
          </VButton>
          <VButton variant="default" :disabled="colorMode === 'auto'" @click="colorMode = 'auto'">
            {{ t("profile.appearence.theme.label.auto") }}
          </VButton>
        </VLayout>
      </VSection>
      <VSection>
        <template #title>{{ t("profile.organization.title") }}</template>
      </VSection>
    </VContainer>
  </PageWrapper>
</template>

<style scoped lang="scss"></style>
