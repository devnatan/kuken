<template>
  <LoadingState v-if="isLoading" />
  <RouterView v-else />
  <ModalsContainer />
</template>

<script lang="ts" setup>
import { ModalsContainer } from "vue-final-modal"
import LoadingState from "@/modules/platform/ui/components/LoadingState.vue"
import { onUnmounted, ref } from "vue"
import backendService from "@/modules/platform/api/services/backend.service.ts"
import { useHead } from "@unhead/vue"
import configService from "@/modules/platform/api/services/config.service.ts"
import { SETUP_ROUTE } from "@/router.ts"
import { useRouter } from "vue-router"
import { usePlatformStore } from "@/modules/platform/platform.store.ts"
import websocketService from "@/modules/platform/api/services/websocket.service.ts"
import { useDark } from "@vueuse/core"

const isLoading = ref(true)

useHead({
  title: configService.appName,
  meta: [
    {
      name: "color-scheme",
      content: "light dark"
    }
  ]
})

useDark({
  selector: "body",
  storageKey: "kk-theme",
  attribute: "color-scheme",
  valueDark: "dark"
})

const platformStore = usePlatformStore()
const router = useRouter()

backendService
  .getInfo()
  .catch(console.error)
  .finally(() => {
    if (!platformStore.hasBackendInfo) {
      router.push({ name: SETUP_ROUTE }).finally(() => (isLoading.value = false))
    } else {
      isLoading.value = false
    }
  })

onUnmounted(() => websocketService.close())
</script>
