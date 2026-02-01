export type ResolveBlueprintResponse = {
    inputs: [BlueprintBuildInput]
    startup: BlueprintProperty
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

export type BlueprintProperty =
    | LiteralBlueprintProperty
    | InterpolatedBlueprintProperty
    | ConditionalBlueprintProperty
    | EnvVarBlueprintProperty
    | RefBlueprintProperty
    | {
          type: "null" | "input"
          name: string
      }

export type InterpolatedBlueprintProperty = {
    type: "interpolated"
    template: string
    parts: BlueprintProperty[]
}

export type LiteralBlueprintProperty = {
    type: "literal"
    value: string
}

export type ConditionalBlueprintProperty = {
    type: "conditional"
    inputName: string
    value: string
}

export type EnvVarBlueprintProperty = {
    type: "env"
    envVarName: string
}

export type RefBlueprintProperty = {
    type: "ref"
    refPath: string
}

export type BlueprintBuildInput =
    | BlueprintBuildInputPortNumber
    | BlueprintBuildInputCheckbox
    | BlueprintBuildInputText
    | BlueprintBuildInputPassword
    | BlueprintBuildInputDataSize
    | BlueprintBuildInputSelect

export type BlueprintBuildInputBase = {
    label: string
    name: string
    description: string
}

export type BlueprintBuildInputText = BlueprintBuildInputBase & {
    type: "text"
    default?: string
}

export type BlueprintBuildInputPassword = BlueprintBuildInputBase & {
    type: "password"
}

export type BlueprintBuildInputDataSize = BlueprintBuildInputBase & {
    type: "datasize"
    default?: number
}

export type BlueprintBuildInputSelect = BlueprintBuildInputBase & {
    type: "select"
    items: { [key: string]: string }
}

export type BlueprintBuildInputCheckbox = BlueprintBuildInputBase & {
    type: "checkbox"
    default: boolean
}

export type BlueprintBuildInputPortNumber = BlueprintBuildInputBase & {
    type: "port"
    default?: number
}
