<script lang="ts" setup>
import TheHeader from "@/modules/platform/ui/components/TheHeader.vue"
</script>
<template>
    <div :class="$style.root">
        <!-- <TheSidebar /> -->
        <main :class="$style.body">
            <TheHeader />
            <div :class="$style.content">
                <slot :class="$style.content" />
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
    overflow-y: hidden;
    background-color: #d85f34;
    height: 100%;
}

.content {
    background-color: #fff;
    border-radius: 20px;
    margin: 0 16px 16px 16px;
    height: 100%;
    overflow-y: auto;
}
</style>
