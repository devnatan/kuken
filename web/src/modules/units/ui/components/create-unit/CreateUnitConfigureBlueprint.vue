<script lang="ts" setup>
import blueprintsService from "@/modules/blueprints/api/services/blueprints.service.ts"
import Resource from "@/modules/platform/ui/components/Resource.vue"
import { ref } from "vue"
import type { Blueprint } from "@/modules/blueprints/api/models/blueprint.model.ts"
import BlueprintInputPort from "@/modules/blueprints/ui/components/BlueprintInputPort.vue"
import BlueprintInputText from "@/modules/blueprints/ui/components/BlueprintInputText.vue"
import VCard from "@/modules/platform/ui/components/card/VCard.vue"

const props = defineProps<{ blueprintId: string }>()
const blueprint = ref<Blueprint>()

const model = defineModel<{ [name: string]: string }>({ default: {} })

function updateInput(name: string, value: any): void {
    model.value[name] = value.toString()
}
</script>

<template>
    <Resource
        :resource="() => blueprintsService.getBlueprint(props.blueprintId)"
        @loaded="(result: Blueprint) => (blueprint = result)"
    >
        <template v-if="blueprint">
            <h4>Configure {{ blueprint.spec.descriptor.name }}</h4>
            <VCard>
                <template v-for="(input, name) in blueprint.spec.inputs" :key="name">
                    <BlueprintInputText
                        v-if="input.type == 'text'"
                        :label="input.label"
                        :modelValue="model[name]"
                        :placeholder="input.placeholder"
                        :sensitive="input.sensitive"
                        @update:modelValue="(value) => updateInput(name as string, value)"
                    />
                    <BlueprintInputPort
                        v-if="input.type == 'port'"
                        :default="input.default"
                        :label="input.label"
                        :modelValue="model[name]"
                        :placeholder="input.placeholder"
                        :sensitive="input.sensitive"
                        @update:modelValue="(value) => updateInput(name as string, value)"
                    />
                </template>
            </VCard>
        </template>
    </Resource>
</template>

<style lang="scss" scoped>
.description {
    margin-bottom: 0.8rem;
}

.box {
}
</style>
