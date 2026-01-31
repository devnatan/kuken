import { fileURLToPath, URL } from "node:url"
import { defineConfig, splitVendorChunkPlugin } from "vite"
import vue from "@vitejs/plugin-vue"
// @ts-ignore
import pkg from "./package.json"

process.env.VITE_APP_VERSION = pkg.version

export default defineConfig({
    plugins: [splitVendorChunkPlugin(), vue()],
    resolve: {
        alias: {
            "@": fileURLToPath(new URL("./src", import.meta.url))
        }
    }
})
