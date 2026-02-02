<script setup lang="ts">
import Resource from "@/modules/platform/ui/components/Resource.vue"
import type { VirtualFile } from "@/modules/instances/api/models/file.model.ts"
import instancesService from "@/modules/instances/api/services/instances.service.ts"
import { computed, nextTick, ref } from "vue"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import { useRouter } from "vue-router"
import VIcon from "@/modules/platform/ui/components/icons/VIcon.vue"
import dayjs from "dayjs"
import VCol from "@/modules/platform/ui/components/grid/VCol.vue"
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"
import { filesize } from "filesize"
import ContextMenu from "@imengyu/vue3-context-menu"
import InstanceFileListItemRegularFile from "@/modules/instances/ui/components/files/InstanceFileListItemRegularFile.vue"
import InstanceFileListItemDirectory from "@/modules/instances/ui/components/files/InstanceFileListItemDirectory.vue"

const { instanceId, filePath } = defineProps<{
  instanceId: string
  filePath: string
}>()

const fileList = ref<VirtualFile[]>()
const refreshing = ref(false)

function refresh() {
  refreshing.value = true
  nextTick(() => (refreshing.value = false))
}

const router = useRouter()
// const links = computed(() =>
//   filePath.split("/").map((query) => {
//     return {
//       path: query,
//       route: router.resolve({
//         name: "instance.files",
//         params: { instanceId },
//         query: { filePath: query }
//       })
//     }
//   })
// )
</script>

<template>
  <VContainer>
    <div>
      <VButton variant="default" @click="router.back()">Go back</VButton>
      {{ filePath }}
    </div>
    <Resource
      v-if="!refreshing"
      :resource="() => instancesService.listFiles(instanceId, filePath)"
      @loaded="(value: VirtualFile[]) => (fileList = value)"
    >
      <div class="file-list">
        <template v-for="file in fileList" :key="file.name">
          <InstanceFileListItemRegularFile v-if="file.type === 'FILE'" :file="file" />
          <InstanceFileListItemDirectory v-if="file.type === 'DIRECTORY'" :file="file" />
        </template>
      </div>
    </Resource>
  </VContainer>
</template>
<style scoped lang="scss">
.file-list {
  display: flex;
  flex-direction: column;
  padding: 3.6rem 0;
}

.file-list-container {
}
</style>
