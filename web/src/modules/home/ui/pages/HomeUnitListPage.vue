<template>
    <VContainer class="container">
        <Resource :resource="unitsService.listUnits" @loaded="(data) => (state.units = data)">
            <ul>
                <router-link
                    :to="{ name: 'instance.console', params: { instanceId: unit.instance } }"
                    v-for="unit in state.units"
                    :key="unit.id"
                >
                    Unit: {{ unit.name }}
                </router-link>
            </ul>
        </Resource>
        <VButton variant="primary" to="blueprints"> Go to blueprints </VButton>
    </VContainer>
</template>

<script setup lang="ts">
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import { reactive } from "vue"
import type { Unit } from "@/modules/units/api/models/unit.model.ts"
import Resource from "@/modules/platform/ui/components/Resource.vue"
import unitsService from "@/modules/units/api/services/units.service.ts"

let state = reactive({ units: [] as Unit[] })
</script>

<style scoped lang="scss">
.container {
    padding: 48px;
    display: flex;
    flex-direction: column;
}
</style>
