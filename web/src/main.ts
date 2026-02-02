import "@/assets/styles/main.scss"
import { createApp } from "vue"
import App from "@/App.vue"
import { createVfm } from "vue-final-modal"
import router from "@/router"
import { createPinia } from "pinia"
import VueProgressiveImage from "vue-progressive-image"
import logService from "@/modules/platform/api/services/log.service"
import configService from "@/modules/platform/api/services/config.service"
import { createHead } from "@unhead/vue"
import "highlight.js/styles/atom-one-light.css"
import hljs from "highlight.js/lib/core"
import json from "highlight.js/lib/languages/json"
import hljsVuePlugin from "@highlightjs/vue-plugin"
import VueVirtualScroller from "vue-virtual-scroller"
import "vue-virtual-scroller/dist/vue-virtual-scroller.css"
import localizedFormat from "dayjs/plugin/localizedFormat"
import dayjs from "dayjs"
import "@imengyu/vue3-context-menu/lib/vue3-context-menu.css"
import ContextMenu from "@imengyu/vue3-context-menu"

dayjs.extend(localizedFormat)

hljs.registerLanguage("json", json)

createApp(App)
  .use(createVfm())
  .use(createPinia())
  .use(VueProgressiveImage)
  .use(VueVirtualScroller)
  .use(router)
  .use(createHead())
  .use(hljsVuePlugin)
  .use(ContextMenu)
  .mount("#app")

logService.info(configService.toVersionInfoString())
