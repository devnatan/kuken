<template>
    <div class="root">
        <div class="form">
            <img src="/img/icon-color.svg" class="logo" alt="Icon" />
            <div class="content">
                <slot />
            </div>
        </div>
        <div class="background" />
    </div>
</template>
<style lang="scss" scoped>
// TODO remove it from here
@import "@/assets/styles/abstracts/variables";

.root {
    display: flex;
    align-items: center;
    height: 100%;
}

.container {
    display: flex;
    align-items: center;
    height: 100%;
}

.form {
    align-items: normal;
    z-index: 1;
    background: #fff;
    position: relative;
    left: 0;
    height: 100%;
    margin: 0 2.4% 128px 10%;
    min-width: 30%;
    max-width: 30%;
    padding: 0 7.2rem 0 7.2rem;
    border-bottom-left-radius: 20px;
    border-bottom-right-radius: 20px;
    box-shadow: rgba(0, 0, 0, 0.08) 3px 3px 2px 2px;
    display: flex;
    flex-direction: column;
    justify-content: center;

    @media (max-width: map-get($breakpoints, "medium")) {
        & {
            width: 100%;
            max-width: 100%;
            padding: 24px;
        }
    }

    .logo {
        position: relative;
        height: auto;
        max-width: 128px;
        left: -32px;
    }

    .content {
        margin-top: 96px;
    }
}

.background {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    mask-size: 40px;
    mask-repeat: repeat;
    background-color: rgba(255, 101, 35, 0.12);
    mask-image: url("/img/icon-color.svg");
}
</style>
