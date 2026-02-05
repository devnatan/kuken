<script setup lang="ts">
import VIcon from "@/modules/platform/ui/components/icons/VIcon.vue"
import {
  ContextMenu,
  ContextMenuItem,
  ContextMenuSeparator,
  type MenuOptions
} from "@imengyu/vue3-context-menu"
import { ref } from "vue"

const emit = defineEmits(["rename", "delete"])
defineExpose({ onContextMenu })

const showContextMenu = ref(false)
const options = ref<MenuOptions>({
  zIndex: 3,
  minWidth: 230,
  x: 500,
  y: 200
})

function onContextMenu(e: MouseEvent) {
  e.preventDefault()
  showContextMenu.value = true
  options.value.x = e.x
  options.value.y = e.y
}
</script>

<template>
  <context-menu v-model:show="showContextMenu" :options="options">
    <context-menu-item label="Rename" @click="emit('rename')">
      <template #icon>
        <VIcon name="Rename" />
      </template>
    </context-menu-item>
    <context-menu-item label="Download">
      <template #icon>
        <VIcon name="Download" />
      </template>
    </context-menu-item>
    <context-menu-separator />
    <context-menu-item label="Delete" @click="emit('delete')">
      <template #icon>
        <VIcon name="DeleteForever" />
      </template>
    </context-menu-item>
  </context-menu>
</template>
