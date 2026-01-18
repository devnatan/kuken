<template>
    <VTitle>Welcome</VTitle>
    <VButton to="blueprints" variant="primary"> Go to blueprints </VButton>

    <div class="header">
        <h4>Your Server List</h4>
        <span class="count" v-text="state.units.length" />
    </div>
    <Resource :resource="unitsService.listUnits" @loaded="(data: Unit[]) => (state.units = data)">
        <div class="serverList">
            <router-link
                v-for="unit in state.units"
                :key="unit.id"
                :to="{ name: 'instance.console', params: { instanceId: unit.instance.id } }"
                class="serverListItem"
            >
                <ProgressiveImage
                    :src="resolveBlueprintSource(unit.instance.blueprint.spec.assets?.icon)"
                    class="image"
                />
                <div class="body">
                    <h5 class="title" v-text="unit.name" />
                    <p class="description">
                        {{ unit.instance.blueprint.spec.metadata.name }}
                        <span class="icon">
                            <VIcon name="Verified" />
                        </span>
                    </p>
                </div>
            </router-link>
        </div>
    </Resource>
</template>

<script lang="ts" setup>
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import { reactive } from "vue"
import type { Unit } from "@/modules/units/api/models/unit.model.ts"
import Resource from "@/modules/platform/ui/components/Resource.vue"
import unitsService from "@/modules/units/api/services/units.service.ts"
import VTitle from "@/modules/platform/ui/components/typography/VTitle.vue"
import { ProgressiveImage } from "vue-progressive-image"
import { resolveBlueprintSource } from "@/modules/blueprints/api/models/blueprint.model.ts"
import VIcon from "@/modules/platform/ui/components/icons/VIcon.vue"

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

.serverList {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-top: 1.2rem;

    .serverListItem {
        display: flex;
        flex-direction: row;
        gap: 16px;
        padding: 12px;
        border: 2px solid var(--kt-border-low);
        transition:
            border ease-in-out 0.15s,
            background-color ease-in-out 0.15s;
        border-radius: 20px;

        &:hover {
            cursor: pointer;
            border-color: var(--kt-border-medium);
        }

        &.selected {
            border-color: var(--kt-content-primary);
        }

        .image {
            width: 72px;
            height: 72px;
            min-width: 72px;
            min-height: 72px;
            border-radius: 20px;
            display: block;
            background-position: center;
            background-size: cover;
            background-repeat: no-repeat;
        }

        .body {
            display: flex;
            flex-direction: column;
            padding: 8px 0;
            justify-content: center;
            gap: 0;
        }

        .description {
            color: var(--kt-content-neutral);
            display: inline-flex;
            flex-direction: row;
            align-items: center;

            .icon {
                min-width: 16px;
                min-height: 16px;
                max-height: 16px;
                margin-left: 2px;
                fill: #4b7bec;
            }
        }
    }
}
</style>
