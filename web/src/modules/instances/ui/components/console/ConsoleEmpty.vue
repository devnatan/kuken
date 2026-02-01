<template>
    <div class="console-empty">
        <div class="empty-icon">?</div>
        <p>Waiting logs...</p>
    </div>
</template>

<style lang="scss" scoped>
.console-empty {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #404040;
    pointer-events: none;
}

.empty-icon {
    font-size: 32px;
    margin-bottom: 1.2rem;
    opacity: 0.54;
}
</style>
