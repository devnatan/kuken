<template>
    <div class="container">
        <slot />
    </div>
</template>
<style lang="scss" scoped>
.container {
    position: relative;
    width: 100%;
    margin: 0 auto;
    max-width: calc(1170px * 1.4);
    box-sizing: border-box;

    &:after {
        content: "";
        display: table;
        clear: both;
    }
}
</style>
