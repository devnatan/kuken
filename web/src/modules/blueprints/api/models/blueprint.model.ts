export type Blueprint = {
    id: string
    spec: BlueprintSpec
}

export type BlueprintSpec = {
    source: string
    descriptor: BlueprintDescriptor
    build: BlueprintBuild
    inputs: { [name: string]: BlueprintBuildInput }
    assets: {
        icon?: BlueprintAsset
    }
}

export type BlueprintAsset = {
    type: "image"
    source: string
}

export function resolveBlueprintSource(source: string): string {
    return source.substring(source.indexOf("://") + 3, source.length)
}

export type BlueprintDescriptor = {
    name: string
    version: string
}

export type BlueprintBuild = {
    env: { [key: string]: BlueprintProperty }
}

export type BlueprintProperty = {
    type: "placeholder" | "input" | "env"
    expr?: string
    name?: string
}

export type BlueprintBuildInput = {
    type: "text" | "port"
    label: string
    placeholder?: string
    sensitive?: boolean
    default?: any
}
