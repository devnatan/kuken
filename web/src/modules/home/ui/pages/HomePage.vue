<template>
  <div class="container">
    <Breadcrumb />
    <h2 class="title">
      Hello {{ account.email }}
    </h2>
  </div>
</template>

<script setup lang="ts">
import Breadcrumb from "@/modules/platform/ui/components/Breadcrumb.vue"
import { useAccount } from "@/modules/accounts/accounts.store.ts"

const account = useAccount()
</script>

<style scoped lang="scss">
.container {
    padding: 48px;
}

.title {
    position: relative;
    font-weight: bold;
    width: fit-content;

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
</style>
