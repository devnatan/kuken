<script lang="ts" setup>
import blueprintsService from "@/modules/blueprints/api/services/blueprints.service.ts"
import Resource from "@/modules/platform/ui/components/Resource.vue"
import { ref } from "vue"
import BlueprintInputPort from "@/modules/blueprints/ui/components/BlueprintInputPort.vue"
import BlueprintInputText from "@/modules/blueprints/ui/components/BlueprintInputText.vue"
import VCard from "@/modules/platform/ui/components/card/VCard.vue"
import BlueprintInputDataSize from "@/modules/blueprints/ui/components/BlueprintInputDataSize.vue"
import BlueprintInputPassword from "@/modules/blueprints/ui/components/BlueprintInputPassword.vue"
import BlueprintInputSelect from "@/modules/blueprints/ui/components/BlueprintInputSelect.vue"
import BlueprintInputCheckbox from "@/modules/blueprints/ui/components/BlueprintInputCheckbox.vue"
import type {
    InterpolatedBlueprintProperty,
    ResolveBlueprintResponse
} from "@/modules/blueprints/api/models/blueprint.spec.model.ts"
import { isUndefined } from "@/utils"

const props = defineProps<{ blueprintId: string }>()
const resolution = ref<ResolveBlueprintResponse>()
const startup = ref<InterpolatedBlueprintProperty>()

const model = defineModel<{ [name: string]: string }>({ default: {} })

function updateInput(name: string, value: any): void {
    if (isUndefined(value)) return
    model.value[name] = value.toString()
}

function onSpecLoaded(result: ResolveBlueprintResponse) {
    resolution.value = result
    startup.value = resolution.value.startup as InterpolatedBlueprintProperty
}
</script>

<template>
    <Resource
        :resource="() => blueprintsService.resolveBlueprint(props.blueprintId)"
        @loaded="onSpecLoaded"
    >
        <template v-if="resolution">
            <section>
                <VCard class="inputs">
                    <template v-for="input in resolution.inputs" :key="input.name">
                        <BlueprintInputText
                            v-if="input.type == 'text'"
                            :modelValue="model[input.name]"
                            v-bind="input"
                            @update:modelValue="
                                (value: string) => updateInput(input.name as string, value)
                            "
                        />
                        <BlueprintInputPort
                            v-if="input.type == 'port'"
                            :modelValue="model[input.name]"
                            v-bind="input"
                            @update:modelValue="
                                (value: string) => updateInput(input.name as string, value)
                            "
                        />
                        <BlueprintInputDataSize
                            v-if="input.type == 'datasize'"
                            :modelValue="model[input.name]"
                            v-bind="input"
                            @update:modelValue="
                                (value: string) => updateInput(input.name as string, value)
                            "
                        />
                        <BlueprintInputPassword
                            v-if="input.type == 'password'"
                            :modelValue="model[input.name]"
                            v-bind="input"
                            @update:modelValue="
                                (value: string) => updateInput(input.name as string, value)
                            "
                        />
                        <BlueprintInputSelect
                            v-if="input.type == 'select'"
                            :modelValue="model[input.name]"
                            v-bind="input"
                            @update:modelValue="
                                (value: string) => updateInput(input.name as string, value)
                            "
                        />
                        <BlueprintInputCheckbox
                            v-if="input.type == 'checkbox'"
                            :modelValue="Boolean(model[input.name] ?? false)"
                            v-bind="input"
                            @update:modelValue="
                                (value: boolean) =>
                                    updateInput(input.name as string, value.toString())
                            "
                        />
                    </template>
                </VCard>
                <!--                <p class="description">Startup command</p>-->
                <!--                <BlueprintPropertyInterpolated-->
                <!--                    :input-values="model"-->
                <!--                    :property="startup!"-->
                <!--                    @update:property="(prop) => (startup = prop)"-->
                <!--                    @removed:property=""-->
                <!--                />-->
            </section>
        </template>
    </Resource>
</template>

<style lang="scss" scoped>
.description {
    color: var(--kt-content-neutral);
    margin-bottom: 0.4rem;
}

.card.startup {
    margin-top: 0.8rem;
    padding: 0;
}

fieldset:not(:last-child) {
    margin-bottom: 3.2rem;
}
</style>
