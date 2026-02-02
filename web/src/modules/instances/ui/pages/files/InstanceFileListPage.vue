<script lang="ts" setup>
import { useInstanceStore } from "@/modules/instances/instances.store.ts"
import InstanceFileList from "@/modules/instances/ui/components/files/InstanceFileList.vue"
import { useInstanceFilesStore } from "@/modules/instances/instance-files.store.ts"

const filePath = useInstanceFilesStore().getCurrentFilePath
const instance = useInstanceStore().getInstance
</script>

<template>
  <InstanceFileList :instance-id="instance.id" :file-path="filePath ?? ''" />
</template>
