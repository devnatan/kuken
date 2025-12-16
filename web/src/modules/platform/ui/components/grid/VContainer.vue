<template>
  <div :class="$style.container">
    <slot />
  </div>
</template>
<style lang="scss" module>
.root {
    position: relative;
    width: 100%;
    max-width: calc(1170px * 1.6);
    margin: 0 auto;
    box-sizing: border-box;

    &:after {
        content: "";
        display: table;
        clear: both;
    }
}
</style>
