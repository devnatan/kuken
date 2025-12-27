<script setup lang="ts">
import TheHeader from "@/modules/platform/ui/components/TheHeader.vue"
</script>
<template>
    <div :class="$style.root">
        <main :class="$style.body">
            <TheHeader>
                <h4>Console</h4>
            </TheHeader>
            <div :class="$style.content">
                <slot />
            </div>
        </main>
    </div>
</template>
<style lang="scss" module>
.root {
    display: flex;
    height: 100%;
    overflow: hidden;
}

.body {
    display: flex;
    flex-grow: 1;
    flex-direction: column;
    overflow: auto;
    background-color: rgb(30, 39, 46);
    color: #fff;
}

.content {
    background-color: rgb(30, 39, 46);
    height: 100%;
    overflow-y: auto;
}
</style>
