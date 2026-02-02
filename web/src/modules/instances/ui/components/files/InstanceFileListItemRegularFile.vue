<script setup lang="ts">
import type { VirtualFile } from "@/modules/instances/api/models/file.model.ts"
import { filesize } from "filesize"
import dayjs from "dayjs"
import VIcon from "@/modules/platform/ui/components/icons/VIcon.vue"
import {
  ContextMenu,
  ContextMenuGroup,
  ContextMenuItem,
  ContextMenuSeparator,
  type MenuOptions
} from "@imengyu/vue3-context-menu"
import { ref } from "vue"
import instancesService from "@/modules/instances/api/services/instances.service.ts"
import { useInstanceFilesStore } from "@/modules/instances/instance-files.store.ts"
import { useInstanceStore } from "@/modules/instances/instances.store.ts"

const { file } = defineProps<{ file: VirtualFile }>()
const deleted = ref(false)

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

async function onDeleteFile() {
  const instanceId = useInstanceStore().getInstance.id

  await instancesService.deleteFile(instanceId, file.relativePath)
  deleted.value = true
}
</script>

<template>
  <router-link
    v-if="!deleted"
    :to="{
      name: 'instance.file.editor',
      query: { filePath: file.relativePath }
    }"
    class="file"
    @contextmenu="onContextMenu"
  >
    <div class="icon-wrapper">
      <VIcon name="File" />
    </div>
    <div class="name">{{ file.name }}</div>
    <div class="size">
      {{ filesize(file.size, { standard: "jedec" }) }}
    </div>
    <div class="createdAt">
      {{ dayjs(file.createdAt).format("DD/MMM/YYYY h:mm A") }}
    </div>
  </router-link>

  <context-menu v-model:show="showContextMenu" :options="options">
    <context-menu-item label="Rename">
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
    <context-menu-item label="Delete" @click="onDeleteFile">
      <template #icon>
        <VIcon name="DeleteForever" />
      </template>
    </context-menu-item>
  </context-menu>
</template>

<style scoped lang="scss">
.file {
  display: flex;
  align-items: center;
  flex-direction: row;
  padding: 8px;
  border-radius: 8px;
  gap: 16px;
  text-decoration: none;

  .size,
  .createdAt {
    color: var(--kt-content-neutral);
  }

  &:hover {
    background-color: var(--kt-background-surface);
  }

  .icon-wrapper {
    fill: var(--kt-content-neutral-low);
    width: 24px;
    height: 24px;
  }

  .createdAt {
    margin-left: auto;
  }
}
</style>
