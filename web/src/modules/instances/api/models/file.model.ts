export type VirtualFile = {
  name: string
  relativePath: string
  size: number
  type: "FILE" | "DIRECTORY"
  createdAt: Date
  accessedAt: Date
  modifiedAt: Date
  isExecutable: boolean
  isReadable: boolean
  isWritable: boolean
}
