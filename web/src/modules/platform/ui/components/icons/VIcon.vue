<template>
    <Component :is="component" :aria-labelledby="`icon-${name}`" class="icon" />
</template>
<script lang="ts" setup>
import { defineAsyncComponent } from "vue"

const props = defineProps<{ name: string }>()
const component = defineAsyncComponent(() => import(`./_${props.name}.vue`))
</script>
