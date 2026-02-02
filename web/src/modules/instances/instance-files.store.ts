import { defineStore } from "pinia"
import { isNull, isUndefined } from "@/utils"
import { useRoute } from "vue-router"

type File = {
  type: "file" | "directory"
  name: string
  children?: File[]
}

type InstanceFilesStore = {
  files: File[]
}

export const useInstanceFilesStore = defineStore("instanceFiles", {
  state: (): InstanceFilesStore => ({ files: [] }),
  getters: {
    getCurrentFilePath(): string {
      return useRoute().query.filePath as string
    }
  },
  actions: {}
})
