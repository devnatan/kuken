<template>
    <header>
        <div class="logo">
            <img alt="Logo" src="/img/icon-white-transparent.png" />
        </div>
        <template v-if="$slots.default">
            <slot />
        </template>
    </header>
</template>

<style lang="scss" scoped>
header {
    padding: 0 16px;
    display: flex;
    flex-direction: row;
    align-items: center;
    width: 100%;

    .logo img {
        max-width: 72px;
    }
}
</style>
