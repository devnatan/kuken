export type Blueprint = {
  id: string
  header: BlueprintHeader
  official: boolean
}

export type BlueprintHeader = {
  name: string
  version: string
  author: string
  url: string
  assets: {
    icon: string
  }
}

export function resolveBlueprintSource(source?: string): string {
  return source ?? "https://avatars.githubusercontent.com/u/253088926"
}
