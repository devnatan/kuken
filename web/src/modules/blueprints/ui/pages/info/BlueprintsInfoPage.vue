<template>
    <Resource :resource="resource" @loaded="(value) => (blueprint = value)">
        <template v-if="blueprint">
            <h4>{{ blueprint.spec.name }}</h4>

            <img :src="blueprint.spec.remote.assets.iconUrl" :alt="`${blueprint.id} icon`" />
        </template>
    </Resource>
</template>
<script setup lang="ts">
import { ref } from "vue"
import type { Blueprint } from "@/modules/blueprints/api/models/blueprint.model"
import blueprintsService from "@/modules/blueprints/api/services/blueprints.service"
import Resource from "@/modules/platform/ui/components/Resource.vue"

const props = defineProps<{ blueprintId: string }>()

const resource = () => blueprintsService.getBlueprint(props.blueprintId)
const blueprint = ref<Blueprint | null>(null)
</script>
