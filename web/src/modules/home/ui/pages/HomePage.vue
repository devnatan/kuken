<template>
    <div class="container">
        <Breadcrumb />
        <VTitle> Hello {{ account.email }} </VTitle>
        <VButton to="setup"> Go to setup </VButton>
    </div>
</template>

<script setup lang="ts">
import Breadcrumb from "@/modules/platform/ui/components/Breadcrumb.vue"
import { useAccount } from "@/modules/accounts/accounts.store.ts"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import VTitle from "@/modules/platform/ui/components/typography/VTitle.vue"

const account = useAccount()
</script>

<style scoped lang="scss">
.container {
    padding: 48px;
}
</style>
