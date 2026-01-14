<script lang="ts" setup>
import unitsService from "@/modules/units/api/services/units.service.ts"
import { useAsyncState } from "@vueuse/core"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import { computed, reactive } from "vue"
import VForm from "@/modules/platform/ui/components/form/VForm.vue"
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"
import VTitle from "@/modules/platform/ui/components/typography/VTitle.vue"
import Breadcrumb from "@/modules/platform/ui/components/Breadcrumb.vue"
import CreateUnitNameInput from "@/modules/units/ui/components/create-unit/CreateUnitNameInput.vue"
import CreateUnitBlueprintSelector from "@/modules/units/ui/components/create-unit/CreateUnitBlueprintSelector.vue"
import { onBeforeRouteLeave, useRouter } from "vue-router"
import type { Unit } from "@/modules/units/api/models/unit.model.ts"
import CreateUnitConfigureBlueprint from "@/modules/units/ui/components/create-unit/CreateUnitConfigureBlueprint.vue"
import { isNull } from "@/utils"

const form = reactive({
    name: "",
    blueprint: "",
    inputs: {}
})

const router = useRouter()
const { isLoading, execute } = useAsyncState(unitsService.createUnit, null as unknown as Unit, {
    immediate: false,
    onSuccess: (payload: Unit) => {
        window.location.href = router.resolve({
            name: "instance.console",
            params: { instanceId: payload.instance.id }
        }).href
    }
})

enum Steps {
    Name,
    SelectBlueprint,
    ConfigureBlueprint
}

const steps = reactive({
    current: Steps.Name,
    all: [Steps.Name, Steps.SelectBlueprint, Steps.ConfigureBlueprint]
})

const firstStep = computed(() => steps.all[0])
const lastStep = computed(() => steps.all[steps.all.length - 1])

const goNextButtonLabel = computed(() => {
    return steps.current === lastStep.value ? "Create" : "Next"
})
const goBackButtonLabel = computed(() => {
    return steps.current === firstStep.value ? null : "Back"
})

const isFormCompleted = computed(() => {
    return !isNull(form.name) && !isNull(form.blueprint)
})

const canProceed = computed(() => {
    if (isLoading.value) return false

    if (steps.current == Steps.SelectBlueprint) {
        return form.blueprint.length > 0
    }

    return true
})

function goBack() {
    steps.current = steps.all[steps.all.indexOf(steps.current) - 1]!
}

function goNext() {
    steps.current = steps.all[steps.all.indexOf(steps.current) + 1]!
}

function proceed() {
    if (steps.current === lastStep.value) {
        execute(0, form)
    } else {
        goNext()
    }
}

onBeforeRouteLeave((_, __, next) => {
    if (steps.current !== Steps.Name) {
        goBack()
        return
    }

    next()
})
</script>

<template>
    <VContainer class="container">
        <Breadcrumb />
        <VTitle :centered="true">Create new server</VTitle>
        <div class="content">
            <VForm @submit.prevent="proceed">
                <CreateUnitNameInput
                    v-if="steps.current == Steps.Name"
                    :key="Steps.Name"
                    v-model="form.name"
                />
                <CreateUnitBlueprintSelector
                    v-else-if="steps.current == Steps.SelectBlueprint"
                    :key="Steps.SelectBlueprint"
                    v-model="form.blueprint"
                />
                <CreateUnitConfigureBlueprint
                    v-else-if="steps.current == Steps.ConfigureBlueprint"
                    :key="Steps.ConfigureBlueprint"
                    v-model="form.inputs"
                    :blueprint-id="form.blueprint"
                />
                <div class="buttons">
                    <VButton
                        v-if="goBackButtonLabel"
                        class="back"
                        variant="default"
                        @click="goBack"
                        v-text="goBackButtonLabel"
                    />
                    <VButton
                        :disabled="!canProceed"
                        class="next"
                        type="submit"
                        variant="primary"
                        v-text="goNextButtonLabel"
                    />
                </div>
            </VForm>
        </div>
    </VContainer>
</template>

<style>
h4 {
    margin-bottom: 1.6rem;
}
</style>
<style lang="scss" scoped>
.container {
    padding: 48px;
    display: flex;
    flex-direction: column;
}

.content {
    display: flex;
    flex-direction: column;
    max-width: 40%;
    position: relative;
    left: 50%;
    transform: translateX(-50%);
}

.buttons {
    display: flex;
    flex-direction: row;
}

.button {
    margin-top: 3.2rem;
    width: 15%;

    &.back {
        margin-right: auto;
    }
    &.next {
        margin-left: auto;
    }
}
</style>
