<script lang="ts" setup>
import websocketService from "@/modules/platform/api/services/websocket.service.ts"
import { WebSocketOpCodes } from "@/modules/platform/api/models/websocket.response.ts"
import { computed, nextTick, onMounted, onUnmounted, ref, unref, useTemplateRef, watch } from "vue"
import { useElementSize, useScroll } from "@vueuse/core"
import VInput from "@/modules/platform/ui/components/form/VInput.vue"
import VForm from "@/modules/platform/ui/components/form/VForm.vue"
import instancesService from "@/modules/instances/api/services/instances.service.ts"

interface Frame {
    value: string
    length: number
    stream: {
        code: number
        name: "STDOUT" | "STDERR"
    }
    timestamp: string
}

const props = defineProps<{ instanceId: string }>()
const frames = ref<Frame[]>([])
const maxFrames = 10000
const isPaused = ref(false)
const command = ref("")

const scrollerRef = useTemplateRef("scrollerRef")
const autoScroll = ref(true)

const { y: scrollTop, arrivedState } = useScroll(scrollerRef, {
    behavior: "smooth"
})

const { height: containerHeight } = useElementSize(scrollerRef)

const ITEM_HEIGHT = 24
const BUFFER_SIZE = 10

const visibleRange = computed(() => {
    const start = Math.floor(scrollTop.value / ITEM_HEIGHT)
    const visibleCount = Math.ceil(containerHeight.value / ITEM_HEIGHT)
    const end = start + visibleCount

    return {
        start: Math.max(0, start - BUFFER_SIZE),
        end: Math.min(searchResults.value.length, end + BUFFER_SIZE)
    }
})

const visibleItems = computed(() => {
    return searchResults.value
        .slice(visibleRange.value.start, visibleRange.value.end)
        .map((item) => {
            return { ...item, value: ansiToHtml(item.value) }
        })
})

const totalHeight = computed(() => {
    return searchResults.value.length * ITEM_HEIGHT
})

const offsetY = computed(() => {
    return visibleRange.value.start * ITEM_HEIGHT
})

const scrollToBottom = () => {
    if (scrollerRef.value) {
        scrollerRef.value.scrollTop = scrollerRef.value.scrollHeight
    }
}

watch(
    () => arrivedState.bottom,
    (isAtBottom) => {
        autoScroll.value = isAtBottom
    }
)

// --- Frames ---
const addFrame = (frame: Frame) => {
    if (isPaused.value) return

    frames.value.push(frame)

    // Trim old frames to prevent memory issues
    if (frames.value.length > maxFrames) {
        frames.value = frames.value.slice(-maxFrames)
    }

    // Auto-scroll to bottom if enabled
    if (autoScroll.value) {
        nextTick(() => {
            scrollToBottom()
        })
    }
}

const downloadLogs = () => {
    const content = frames.value.map((f) => f.value).join("\n")
    const blob = new Blob([content], { type: "text/plain" })
    const url = URL.createObjectURL(blob)
    const a = document.createElement("a")
    a.href = url
    a.download = `instance-${props.instanceId}-logs.txt`
    a.click()
    URL.revokeObjectURL(url)
}

const clearConsole = () => {
    frames.value = []
    logsEnded.value = false
    scrollTop.value = 0
}

const filterStream = ref<"ALL" | "STDOUT" | "STDERR">("ALL")
const filteredFrames = computed(() => {
    if (filterStream.value === "ALL") return frames.value
    return frames.value.filter((f) => f.stream.name === filterStream.value)
})

const searchQuery = ref("")
const searchResults = computed(() => {
    if (!searchQuery.value.trim()) return filteredFrames.value

    const query = searchQuery.value.toLowerCase()
    return filteredFrames.value.filter((f) => f.value.toLowerCase().includes(query))
})

const highlightSearch = (text: string) => {
    const query = searchQuery.value.trim()
    if (!query) return text

    const regex = new RegExp(`(${query.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")})`, "gi")
    return text.replace(regex, "<mark>$1</mark>")
}

async function sendCommand() {
    const input = unref(command.value)
    command.value = ""

    const { exitCode } = await instancesService.runInstanceCommand(props.instanceId, input)

    console.log(`Exit code for ${input}: ${exitCode}`)
}

const isConnected = ref(false)
const logsEnded = ref(false)
const fetechedWhileStopped = ref(false)

let unsubscribeStart: (() => void) | null = null
let unsubscribeFrames: (() => void) | null = null
let unsubscribeEnd: (() => void) | null = null
let unsubscribeInstanceStart: (() => void) | null = null

const handleLogsEnd = () => {
    logsEnded.value = true
    isConnected.value = false
}

const setupListeners = () => {
    unsubscribeStart = websocketService.listen(
        WebSocketOpCodes.InstanceLogsRequestStarted,
        (payload?: { running: boolean }) => {
            isConnected.value = true

            if (payload?.running) {
                console.log("Fetching messages: is server running?", payload.running)
                fetechedWhileStopped.value = !payload.running
            }
        }
    )

    unsubscribeInstanceStart = websocketService.listen(WebSocketOpCodes.InstanceStarted, () => {
        isConnected.value = true
    })

    unsubscribeFrames = websocketService.listen(WebSocketOpCodes.InstanceLogsRequestFrame, addFrame)

    unsubscribeEnd = websocketService.listen(WebSocketOpCodes.InstanceLogsRequestFinished, () => {
        if (!fetechedWhileStopped.value) {
            handleLogsEnd()
        }
    })

    unsubscribeFrames = websocketService.listen(WebSocketOpCodes.InstanceStarted, () => {
        isConnected.value = true
    })
}

onMounted(() => {
    websocketService.send(WebSocketOpCodes.InstanceLogsRequest, {
        iid: props.instanceId
    })

    setupListeners()
})

onUnmounted(() => {
    unsubscribeStart?.()
    unsubscribeFrames?.()
    unsubscribeEnd?.()
    unsubscribeInstanceStart?.()
})

watch([searchQuery, filterStream], () => {
    if (searchQuery.value.trim() === "") {
        return
    }

    scrollTop.value = 0
    if (scrollerRef.value) {
        scrollerRef.value.scrollTop = 0
    }
})

const ansiToHtml = (text: string) => {
    const ansiCodes: { [key: string]: string } = {
        "0": "reset",
        "39": "color: inherit",
        "1": "font-weight: bold",
        "2": "opacity: 0.7",
        "3": "font-style: italic",
        "4": "text-decoration: underline",
        "30": "color: #6b7280",
        "31": "color: #fca5a5",
        "32": "color: #86efac",
        "33": "color: #fde68a",
        "34": "color: #93c5fd",
        "35": "color: #f0abfc",
        "36": "color: #a5f3fc",
        "37": "color: #e5e7eb",
        "40": "background-color: #374151; color: #e5e7eb",
        "41": "background-color: #fecaca; color: #7f1d1d",
        "42": "background-color: #bbf7d0; color: #14532d",
        "43": "background-color: #fef3c7; color: #713f12",
        "44": "background-color: #bfdbfe; color: #1e3a8a",
        "45": "background-color: #f5d0fe; color: #701a75",
        "46": "background-color: #cffafe; color: #164e63",
        "47": "background-color: #f3f4f6; color: #1f2937",
        "49": "background-color: inherit",
        "90": "color: #9ca3af",
        "91": "color: #fecaca",
        "92": "color: #bbf7d0",
        "93": "color: #fef3c7",
        "94": "color: #bfdbfe",
        "95": "color: #f5d0fe",
        "96": "color: #cffafe",
        "97": "color: #f3f4f6",
        "100": "background-color: #6b7280; color: #f9fafb",
        "101": "background-color: #fca5a5; color: #7f1d1d",
        "102": "background-color: #86efac; color: #14532d",
        "103": "background-color: #fde68a; color: #713f12",
        "104": "background-color: #93c5fd; color: #1e3a8a",
        "105": "background-color: #f0abfc; color: #701a75",
        "106": "background-color: #a5f3fc; color: #164e63",
        "107": "background-color: #e5e7eb; color: #1f2937"
    }

    let html = text
    let openSpans = 0

    html = html.replace(/\x1b\[(\d+(?:;\d+)*)m/g, (_, codes) => {
        const codeList = codes.split(";")
        let styles = []
        let result = ""

        for (const code of codeList) {
            if (code === "0") {
                result += "</span>".repeat(openSpans)
                openSpans = 0
            } else if (ansiCodes[code] && ansiCodes[code] !== "reset") {
                styles.push(ansiCodes[code])
            }
        }

        if (styles.length > 0) {
            result += `<span style="${styles.join("; ")}">`
            openSpans++
        }

        return result
    })

    if (openSpans > 0) {
        html += "</span>".repeat(openSpans)
    }

    html = html.replace(/&(?!#?\w+;)/g, "&amp;")

    return html
}
</script>

<template>
    <div class="console-container">
        <div class="console-toolbar">
            <div class="toolbar-left">
                <input
                    v-model="searchQuery"
                    class="search-input"
                    placeholder="Search..."
                    type="text"
                />
                <span class="frame-count">
                    {{ searchResults.length }} / {{ frames.length }} lines
                </span>
            </div>

            <div class="toolbar-right">
                <div
                    :class="{
                        disconnected: fetechedWhileStopped || !isConnected,
                        ended: logsEnded
                    }"
                    class="connection-status"
                >
                    <span class="status-dot"></span>
                    <template v-if="logsEnded"> Logs Ended </template>
                    <template v-else-if="fetechedWhileStopped"> Server closed </template>
                    <template v-else>
                        {{ isConnected ? "Connected" : "Disconnected" }}
                    </template>
                </div>
            </div>
        </div>

        <div class="console-wrapper">
            <div class="fade-overlay fade-top"></div>

            <div v-if="searchResults.length > 0" ref="scrollerRef" class="console-output">
                <div :style="{ height: `${totalHeight}px` }" class="virtual-scroller-spacer">
                    <div
                        :style="{ transform: `translateY(${offsetY}px)` }"
                        class="virtual-scroller-content"
                    >
                        <TransitionGroup name="slide-up">
                            <div
                                v-for="(item, idx) in visibleItems"
                                :key="visibleRange.start + idx"
                                :class="[
                                    'console-line',
                                    `stream-${item.stream.name.toLowerCase()}`
                                ]"
                            >
                                <span class="line-number">{{ visibleRange.start + idx + 1 }}</span>
                                <span
                                    class="line-content"
                                    v-html="highlightSearch(item.value)"
                                ></span>
                            </div>
                        </TransitionGroup>
                    </div>
                </div>
            </div>
            <div ref="dummy" />

            <div v-if="searchResults.length === 0" class="console-empty">
                <template v-if="logsEnded"> Log stream completed </template>
                <template v-else-if="searchQuery"> No matching logs found... </template>
                <template v-else> Waiting for logs... </template>
            </div>

            <div class="fade-overlay fade-bottom"></div>
        </div>

        <VForm class="command" @submit.prevent="sendCommand">
            <VInput v-model="command" placeholder="Type something..." />
        </VForm>
    </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=DM+Mono&display=swap");
@import url("https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;700&display=swap");

.console-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    color: #d4d4d4;
    font-family: "JetBrains Mono", monospace;
    font-size: 14px;
}

.console-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 32px;
    background: transparent;
    border-bottom: 1px solid transparent;
    gap: 12px;
    flex-wrap: wrap;
    flex-shrink: 0;
}

.toolbar-left,
.toolbar-right {
    display: flex;
    gap: 8px;
    align-items: center;
    flex-wrap: wrap;
}

.console-toolbar button,
.console-toolbar select {
    padding: 6px 12px;
    background: #3e3e42;
    border: 1px solid #555;
    color: #d4d4d4;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
    transition: all 0.2s;
}

.console-toolbar button:hover:not(:disabled),
.console-toolbar select:hover {
    background: #505050;
}

.console-toolbar button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

@keyframes pulse {
    0%,
    100% {
        opacity: 1;
    }
    50% {
        opacity: 0.7;
    }
}

.search-input {
    padding: 6px 12px;
    background: rgba(0, 0, 0, 0.12);
    font-family: "JetBrains Mono", "DM Mono", "Consolas", "Monaco", "Courier New", monospace;
    color: #d1d5db;
    border-radius: 8px;
    min-width: 240px;
    transition: border-color 0.2s;
    height: 38px;
}

.search-input:focus {
    outline: none;
    border-color: #0e639c;
}

.frame-count {
    white-space: nowrap;
    margin-left: 12px;
    color: #d1d5db;
}

.connection-status {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 6px 16px;
    background: #2d4a2d;
    border: 1px solid #4a7c4a;
    border-radius: 8px;
    font-size: 12px;
    color: #8cd98c;
    font-weight: 500;
    transition: all 0.3s;
}

.connection-status.disconnected {
    background: #4a2d2d;
    border-color: #7c4a4a;
    color: #f48771;
}

.connection-status.ended {
    background: #3d3d2d;
    border-color: #7c7c4a;
    color: #f9a825;
}

.status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #8cd98c;
    animation: blink 2s infinite;
}

.connection-status.disconnected .status-dot {
    background: #f48771;
}

.connection-status.ended .status-dot {
    background: #f9a825;
    animation: none;
}

@keyframes blink {
    0%,
    100% {
        opacity: 1;
    }
    50% {
        opacity: 0.3;
    }
}

.console-wrapper {
    flex: 1;
    position: relative;
    overflow: hidden;
}

.fade-overlay {
    position: absolute;
    left: 0;
    right: 10px;
    height: 24px;
    pointer-events: none;
    z-index: 10;
    transition: opacity 0.3s;
}

.fade-top {
    top: 0;
    background: linear-gradient(
        to bottom,
        rgb(30, 39, 46) 0%,
        rgba(30, 39, 46, 0.93) 20%,
        rgba(30, 39, 46, 0.8) 40%,
        rgba(30, 39, 46, 0.6) 60%,
        rgba(30, 39, 46, 0.4) 80%,
        transparent 100%
    );
}

.fade-bottom {
    bottom: 0;
    background: linear-gradient(
        to top,
        rgb(30, 39, 46) 0%,
        rgba(30, 39, 46, 0.93) 20%,
        rgba(30, 39, 46, 0.8) 40%,
        rgba(30, 39, 46, 0.6) 60%,
        rgba(30, 39, 46, 0.4) 80%,
        transparent 100%
    );
}

.console-output {
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    position: relative;
    scroll-behavior: smooth;
    scroll-snap-type: y proximity;
}

.virtual-scroller-spacer {
    position: relative;
    width: 100%;
}

.virtual-scroller-content {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    will-change: transform;
}

.console-line {
    display: flex;
    padding: 2px 8px;
    white-space: pre-wrap;
    word-break: break-all;
    min-height: 24px;
    align-items: center;
    scroll-snap-align: start;

    &:first-child {
        margin-top: 16px;
    }

    &:last-child {
        margin-bottom: 16px;
    }
}

.console-line:hover {
    background-color: rgba(0, 0, 0, 0.2);
}

/*noinspection CssUnusedSymbol*/
.slide-up-enter-active,
.slide-up-leave-active {
    transition: all 0.15s ease-out;
}

/*noinspection CssUnusedSymbol*/
.slide-up-enter-from {
    opacity: 0;
    transform: translateY(-30px);
}

/*noinspection CssUnusedSymbol*/
.slide-up-leave-to {
    opacity: 0;
    transform: translateY(30px);
}

.line-number {
    min-width: 50px;
    color: rgba(255, 255, 255, 0.18);
    text-align: right;
    padding-right: 12px;
    user-select: none;
    flex-shrink: 0;
    align-self: baseline;
}

.line-content {
    flex: 1;
    color: #cbd5e1;
}

.line-content :deep(mark) {
    background-color: #fbbf24;
    color: #1f2937;
    border-radius: 2px;
    padding: 1px 2px;
}

.console-empty {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: #cbd5e1;
    opacity: 0.54;
    padding: 40px;
    font-size: 30px;
    pointer-events: none;
    z-index: 5;
}

.console-output::-webkit-scrollbar {
    width: 10px;
}

.console-output::-webkit-scrollbar-track {
    background: #1e1e1e;
}

.console-output::-webkit-scrollbar-thumb {
    background: #424242;
    border-radius: 5px;
}

.console-output::-webkit-scrollbar-thumb:hover {
    background: #4e4e4e;
}

.console-output {
    scrollbar-width: thin;
    scrollbar-color: #424242 #1e1e1e;
}

.command input {
    padding: 6px 12px;
    background: rgba(0, 0, 0, 0.12);
    font-family: "JetBrains Mono", "DM Mono", "Consolas", "Monaco", "Courier New", monospace;
    color: #d1d5db;
    border-radius: 0;
    transition: border-color 0.2s;
    height: 48px;

    &::placeholder {
        font-family: "JetBrains Mono", "DM Mono", "Consolas", "Monaco", "Courier New", monospace;
        font-style: normal;
        color: inherit;
    }
}
</style>
