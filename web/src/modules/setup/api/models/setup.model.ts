export type Setup = {
    completed: boolean
    remainingSteps: [
        {
            type: string
        }
    ]
}

export const SetupStepType = {
    CREATE_ACCOUNT: "create-account",
    ORGANIZATION_NAME: "organization-name"
} as const

export type SetupStepType = (typeof SetupStepType)[keyof typeof SetupStepType]

export type SetupRequest = {
    account: {
        email: string
        password: string
    }
    organizationName: string
}
