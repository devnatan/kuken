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
</script>

<template>
    <Resource
        :resource="() => blueprintsService.getBlueprint(props.blueprintId)"
        @loaded="(result: Blueprint) => (blueprint = result)"
    >
        <template v-if="blueprint">
            <h4>Configure {{ blueprint.spec.descriptor.name }}</h4>
            <VCard>
                <template v-for="(input, key) in blueprint.spec.inputs" :key="key">
                    <BlueprintInputText
                        v-if="input.type == 'text'"
                        :label="input.label"
                        :placeholder="input.placeholder"
                        :sensitive="input.sensitive"
                    />
                    <BlueprintInputPort
                        v-if="input.type == 'port'"
                        :default="input.default"
                        :label="input.label"
                        :placeholder="input.placeholder"
                        :sensitive="input.sensitive"
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
