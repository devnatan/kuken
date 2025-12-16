<script setup lang="ts">
import { useRoute } from "vue-router"
import { isNull, isUndefined } from "@/utils"
import { computed } from "vue"

const children = useRoute().matched
const links = children
    .filter(
        (path) =>
            !isUndefined(path.meta.title) &&
            !isNull(path.meta.title) &&
            path.meta.title.toString().length > 0
    )
    .map((path) => {
        return {
            title: path.meta.title as string,
            href: path.name
        }
    })
const isVisible = links.length > 1

const inactiveLinks = computed(() => links.slice(0, links.length - 1))
const activeLink = computed(() => links[links.length - 1]!)
</script>

<template>
  <div
    v-if="isVisible"
    class="root"
  >
    <router-link
      v-for="link in inactiveLinks"
      :key="link.title"
      class="link"
      :to="{ name: link.href }"
    >
      {{ link.title }}
    </router-link>
    <span
      key="active"
      class="link"
      v-text="activeLink.title"
    />
  </div>
</template>

<style scoped lang="scss">
.root {
    margin-bottom: 2rem;

    .link {
        font-size: 16px;
        color: var(--kt-content-neutral-high);
        user-select: none;
    }

    a.link {
        text-decoration: none;
        color: var(--kt-content-neutral);

        &::after {
            content: "/";
            font-weight: bold;
            display: inline-block;
            padding: 0 12px;
            font-size: 12px;
            opacity: 0.38;
        }
    }
}
</style>
