export type Blueprint = {
    id: string
    spec: BlueprintSpec
}

export type BlueprintSpec = {
    metadata: BlueprintMetadata
    source: string
    descriptor: BlueprintDescriptor
    build: BlueprintBuild
    inputs: [BlueprintBuildInput]
    assets?: {
        icon?: string
    }
    instanceSettings?: {
        startup?: BlueprintProperty
        commandExecutor?: { type: "rcon" } & BlueprintInstanceRconCommandExecutor
    }
}

export type BlueprintInstanceRconCommandExecutor = {
    readonly type: "rcon"
    port: BlueprintProperty
    password: BlueprintProperty
    template: string
}

export type BlueprintMetadata = {
    name: string
    version: string
    url: string
}

export function resolveBlueprintSource(source?: string): string {
    return source ?? "https://avatars.githubusercontent.com/u/253088926"
}

export type BlueprintDescriptor = {
    name: string
    version: string
}

export type BlueprintBuild = {
    docker: {
        image: BlueprintProperty
    }
    env: { [key: string]: BlueprintProperty }
}

export type BlueprintProperty = {
    type: "null" | "literal" | "input" | "ref" | "env" | "interpolated"
    name?: string
    value?: any // literal
    refPath?: string // ref
    envVarName?: string // env
    template?: string // interpolated
    parts?: [BlueprintProperty] // interpolated
}

export type BlueprintBuildInput = {
    type: "text" | "port" | "checkbox" | "select" | "datasize" | "password"
    name: string
    label: string
    default?: any

    // --- Password ---
    sensitive?: boolean

    // --- Select ---
    items?: [String]
}
