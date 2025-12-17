<template>
    <h2 class="root">
        <slot />
    </h2>
</template>
<style scoped lang="scss">
.root {
    position: relative;
    font-weight: bold;
    width: fit-content;
    margin-bottom: 48px;

    &:after {
        content: "";
        display: inline;
        border-top: 2px solid var(--kt-content-primary);
        width: 23%;
        bottom: 3px;
        position: absolute;
        right: 15%;
    }
}
</style>
