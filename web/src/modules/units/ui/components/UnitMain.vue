<script lang="ts" setup>
import { computed, onUnmounted } from "vue"
import Resource from "@/modules/platform/ui/components/Resource.vue"
import unitsService from "@/modules/units/api/services/units.service.ts"
import { useUnitStore } from "@/modules/units/units.store.ts"
import type { Unit } from "@/modules/units/api/models/unit.model.ts"

defineProps<{ unitId: string }>()

const unitStore = useUnitStore()
const unit = computed(() => unitStore.unit)

function onUnitLoaded(unit: Unit) {
    unitStore.updateUnit(unit)
}

onUnmounted(unitStore.resetUnit)
</script>

<template>
    <Resource :resource="() => unitsService.getUnit(unitId)" @loaded="onUnitLoaded">
        <template v-if="unit">
            <router-view />
        </template>
    </Resource>
</template>

<style lang="scss" scoped></style>
