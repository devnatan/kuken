<template>
    <VTitle>Welcome</VTitle>
    <div class="header">
        <h4>Your Server List</h4>
        <span class="count" v-text="state.units.length" />
    </div>
    <VContainer class="container">
        <Resource
            :resource="unitsService.listUnits"
            @loaded="(data: Unit[]) => (state.units = data)"
        >
            <div class="server-list">
                <router-link
                    v-for="unit in state.units"
                    :key="unit.id"
                    :to="{ name: 'instance.console', params: { instanceId: unit.instance.id } }"
                    class="server-list-item"
                >
                    <ProgressiveImage :src="unit.instance.blueprint.iconUrl!" />
                    Unit: {{ unit.name }}
                </router-link>
            </div>
        </Resource>
        <VButton to="blueprints" variant="primary"> Go to blueprints </VButton>
    </VContainer>
</template>

<script lang="ts" setup>
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import { reactive } from "vue"
import type { Unit } from "@/modules/units/api/models/unit.model.ts"
import Resource from "@/modules/platform/ui/components/Resource.vue"
import unitsService from "@/modules/units/api/services/units.service.ts"
import VTitle from "@/modules/platform/ui/components/typography/VTitle.vue"
import { ProgressiveImage } from "vue-progressive-image"

let state = reactive({ units: [] as Unit[] })
</script>

<style lang="scss" scoped>
.container {
    padding: 48px;
    display: flex;
    flex-direction: column;
}

.header {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 12px;

    h4 {
        margin: 0;
    }

    .count {
        background-color: var(--kt-content-primary);
        width: 20px;
        height: 20px;
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
        user-select: none;
        color: #fff;
        font-family: var(--kt-headline-font), serif;
        font-size: 14px;
        font-weight: bold;
        border-radius: 4px;
    }
}

.server-list {
    display: flex;
    flex-direction: column;

    .server-list-item {
    }
}
</style>
