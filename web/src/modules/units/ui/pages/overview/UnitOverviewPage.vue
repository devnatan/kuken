<script lang="ts" setup>
import { useUnitStore } from "@/modules/units/units.store.ts"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"

const unit = useUnitStore().getUnit
</script>

<template>
    {{ unit.name }}
    <VButton
        :to="{
            name: 'instance.console',
            params: { unitId: unit.id, instanceId: unit.instance.id }
        }"
        variant="primary"
        >Console</VButton
    >
</template>

<style lang="scss" scoped></style>
