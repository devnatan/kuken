<script lang="ts" setup>
import { useAccount } from "@/modules/accounts/accounts.store.ts"

const userName = useAccount().value.email.charAt(0).toUpperCase()
</script>
<template>
  <header>
    <router-link class="logo" :to="{ path: '/' }">
      <img alt="Logo" src="/img/icon-white-transparent.png" />
    </router-link>
    <template v-if="$slots.default">
      <slot />
    </template>
    <div class="profile">
      <div class="avatar">
        {{ userName }}
      </div>
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
