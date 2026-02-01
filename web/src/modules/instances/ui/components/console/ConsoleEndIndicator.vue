<script lang="ts" setup>
defineProps<{
    position: "top" | "bottom"
    clickable?: boolean
}>()

const emit = defineEmits<{
    click: []
}>()
</script>

<template>
    <div
        :class="[position, { clickable }]"
        class="end-indicator"
        @click="clickable && emit('click')"
    >
        <template v-if="position === 'top'">
            <div class="end-icon">TODO</div>
            <span>Inicio dos logs aq rsrs</span>
            <div class="end-line"></div>
        </template>
        <template v-else>
            <div class="end-line"></div>
            <span>Most recent logs</span>
            <div class="end-icon"></div>
        </template>
    </div>
</template>

<style lang="scss" scoped>
.end-indicator {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 1.6rem;
    font-size: 12px;
    color: #606060;

    &.clickable {
        cursor: pointer;
        transition: color 0.15s ease;
    }
}

.end-icon {
    font-size: 10px;
    opacity: 0.54;
}

.end-line {
    flex: 1;
    max-width: 100px;
    height: 1px;
    background: linear-gradient(to right, transparent, #303030, transparent);
}
</style>
