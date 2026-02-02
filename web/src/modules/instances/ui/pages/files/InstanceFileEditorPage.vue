<script setup lang="ts">
import Resource from "@/modules/platform/ui/components/Resource.vue"
import instancesService from "@/modules/instances/api/services/instances.service.ts"
import { useInstanceStore } from "@/modules/instances/instances.store.ts"
import { computed, h, nextTick, ref, useTemplateRef } from "vue"
import CodeMirror from "vue-codemirror6"
import { usePreferredDark } from "@vueuse/core"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import { useRoute } from "vue-router"
import { useInstanceFilesStore } from "@/modules/instances/instance-files.store.ts"
import router from "@/router.ts"
import VRow from "@/modules/platform/ui/components/grid/VRow.vue"
import VFlexRow from "@/modules/platform/ui/components/grid/VFlexRow.vue"
import VLayout from "@/modules/platform/ui/components/grid/VLayout.vue"
import { cybrh3, isUndefined } from "@/utils"
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"

const instance = useInstanceStore().getInstance
const filePath = useInstanceFilesStore().getCurrentFilePath
const fileContents = ref<string>("")
const fileHash = ref<number>()
const darkMode = usePreferredDark()

function onContentsLoaded(contents: string) {
  if (isUndefined(fileHash.value)) {
    fileHash.value = cybrh3(contents)
  }

  fileContents.value = contents
}

const canSave = computed(
  () => !isUndefined(fileHash.value) && fileHash.value !== cybrh3(fileContents.value)
)

function onSave() {
  instancesService
    .replaceFileContents(instance.id, filePath, fileContents.value)
    .then(() => {
      window.location.reload()
    })
    .catch((e) => console.error("deu pau na hr de salvar", e))
}
</script>

<template>
  <VContainer>
    <VLayout gap="sm" direction="horizontal">
      <VButton variant="default" @click="router.back()">Go back</VButton>
      <VButton variant="primary" :disabled="!canSave" @click.prevent="onSave">Save</VButton>
      {{ filePath }}
    </VLayout>
  </VContainer>
  <div class="editor">
    <Resource
      :resource="() => instancesService.getFileContents(instance.id, filePath)"
      @loaded="onContentsLoaded"
    >
      <div class="contents">
        <CodeMirror v-model="fileContents" :dark="darkMode" />
      </div>
    </Resource>
  </div>
</template>

<style scoped lang="scss">
.editor {
  margin-top: 2.4rem;
  position: relative;
}

.contents {
  background-color: var(--kt-background-surface);
  margin: 1.2rem 0 0;

  :deep(.cm-content) {
    padding: 12px;
  }
}
</style>
