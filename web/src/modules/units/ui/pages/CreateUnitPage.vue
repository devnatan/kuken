<script setup lang="ts">
import unitsService from "@/modules/units/api/services/units.service.ts"
import { useAsyncState } from "@vueuse/core"
import VButton from "@/modules/platform/ui/components/button/VButton.vue"
import { effect, reactive } from "vue"
import VForm from "@/modules/platform/ui/components/form/VForm.vue"
import VFieldSet from "@/modules/platform/ui/components/form/VFieldSet.vue"
import VLabel from "@/modules/platform/ui/components/form/VLabel.vue"
import VInput from "@/modules/platform/ui/components/form/VInput.vue"
import type { CreateUnitRequest } from "@/modules/units/api/models/create-unit.model.ts"
import { useRoute } from "vue-router"
import VContainer from "@/modules/platform/ui/components/grid/VContainer.vue"
import VTitle from "@/modules/platform/ui/components/typography/VTitle.vue"
import Breadcrumb from "@/modules/platform/ui/components/Breadcrumb.vue"

const form = reactive({
    name: "",
    blueprint: "",
    image: ""
})
const { state, isLoading, execute } = useAsyncState(unitsService.createUnit, null, {
    immediate: false
})

const createUnit = () =>
    execute(0, {
        name: form.name,
        blueprint: form.blueprint,
        image: form.image
    } as CreateUnitRequest)

effect(() => {
    form.blueprint = useRoute().query.blueprint as string
})
</script>

<template>
    <VContainer class="container">
        <Breadcrumb />
        <VTitle>Create new server</VTitle>
        <VForm @submit.prevent="createUnit">
            <VFieldSet>
                <VLabel>Name</VLabel>
                <VInput v-model="form.name" type="text" required="true" />
            </VFieldSet>
            <VFieldSet>
                <VLabel>Blueprint</VLabel>
                <VInput v-model="form.blueprint" type="text" required="true" />
            </VFieldSet>
            <VFieldSet>
                <VLabel>Image</VLabel>
                <VInput v-model="form.image" type="text" required="true" />
            </VFieldSet>
            <VButton :disabled="isLoading" variant="primary" type="submit"> Create </VButton>
        </VForm>
    </VContainer>
</template>

<style scoped lang="scss">
.container {
    padding: 48px;
    display: flex;
    flex-direction: column;
}
</style>
