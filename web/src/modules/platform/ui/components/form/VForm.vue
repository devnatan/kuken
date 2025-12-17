<template>
    <form :class="$style.root">
        <slot />
    </form>
</template>
<style lang="scss" module>
.root {
    display: flex;
    flex-direction: column;
}
</style>
