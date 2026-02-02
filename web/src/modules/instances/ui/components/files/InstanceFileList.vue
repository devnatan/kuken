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
          <router-link
            v-if="file.type === 'FILE'"
            :to="{
              name: 'instance.file.editor',
              params: { instanceId },
              query: { filePath: file.relativePath }
            }"
            class="file"
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
          <router-link
            v-else
            :to="{
              name: 'instance.files',
              params: { instanceId },
              query: { filePath: file.relativePath }
            }"
            class="file"
          >
            <div class="icon-wrapper">
              <VIcon name="Folder" />
            </div>
            <div class="name">{{ file.name }}</div>
            <div class="createdAt">
              {{ dayjs(file.createdAt).format("DD/MMM/YYYY h:mm A") }}
            </div>
          </router-link>
        </template>
      </div>
    </Resource>
  </VContainer>
</template>
<style scoped lang="scss">
.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 3.6rem 0;
}

.file-list-container {
}

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
