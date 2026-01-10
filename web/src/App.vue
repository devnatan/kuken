<template>
    <LoadingState v-if="isLoading" :class="$style.loading" />
    <router-view v-else />
    <ModalsContainer />
</template>

<script lang="ts" setup>
import { ModalsContainer } from "vue-final-modal"
import LoadingState from "@/modules/platform/ui/components/LoadingState.vue"
import { ref } from "vue"
import backendService from "@/modules/platform/api/services/backend.service.ts"
import { useHead } from "@unhead/vue"

useHead({
    title: `Küken`
})

const isLoading = ref(true)

backendService.getInfo().finally(() => (isLoading.value = false))
</script>
<style lang="scss">
#app {
    display: flex;
    display: -ms-flexbox;
    flex-direction: column;
    height: 100%;
}
</style>
<style lang="scss" module>
.loading {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}
</style>
