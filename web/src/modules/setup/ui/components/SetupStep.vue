<script setup lang="ts">
const props = defineProps<{
    completed: boolean
    active: boolean
}>()
</script>

<template>
    <div :class="['step', { completed: props.completed }]" :active="props.active">
        <div class="icon">
            <slot name="icon" />
        </div>
        <div class="info">
            <h4 class="title">
                <slot name="title" />
            </h4>
            <p class="description">
                <slot name="description" />
            </p>
        </div>
    </div>
</template>

<style scoped lang="scss">
.step {
    display: flex;
    flex-direction: row;
    gap: 36px;
    user-select: none;

    &[active="true"] .icon {
        background: #ff8f5f;
        color: #ffffff;
    }

    &.completed .icon {
        background: #ff8f5f;
        color: #ffffff;
    }

    .icon {
        display: flex;
        flex-direction: column;
        gap: 12px;
        background-color: #f5f5f5;
        color: rgba(179, 179, 179, 54);
        font-size: 86px;
        font-family: var(--kt-headline-font), serif;
        font-weight: bold;
        align-items: center;
        justify-content: center;
        border-radius: 20px;
        width: 128px;
        height: 128px;
        min-width: 128px;
        min-height: 128px;
    }
}

.info {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 8px;

    .title {
        font-family: var(--kt-headline-font), serif;
        font-weight: bold;
        font-size: 30px;
        color: var(--kt-content-neutral-high);
    }

    .description {
        color: var(--kt-content-neutral);
    }
}
</style>
