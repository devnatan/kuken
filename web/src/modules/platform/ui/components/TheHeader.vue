<script lang="ts" setup>
import { useAccount, useAccountsStore } from "@/modules/accounts/accounts.store.ts"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"

const { isLoggedIn } = useAccountsStore()
const userName = !isLoggedIn || useAccount().value.email.charAt(0).toUpperCase()
</script>
<template>
  <header>
    <router-link class="logo" :to="{ path: '/' }">
      <img alt="Logo" src="/img/icon-white-transparent.png" />
    </router-link>
    <template v-if="$slots.default">
      <slot />
    </template>
    <div v-if="isLoggedIn" class="profile">
      <div class="avatar">
        {{ userName }}
      </div>
    </div>
    <div class="create-button">
      <VButton variant="primary">Create new server</VButton>
    </div>
  </header>
</template>

<style lang="scss" scoped>
header {
  padding: 0 16px;
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;

  .logo img {
    max-width: 72px;
    user-select: none;
  }

  .avatar {
    width: 64px;
    height: 64px;
    border-radius: 50%;
  }
}
</style>
