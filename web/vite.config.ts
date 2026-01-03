import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// @ts-ignore
import pkg from "./package.json"
import { splitVendorChunkPlugin } from 'vite'

process.env.VITE_APP_VERSION = pkg.version

export default defineConfig({
    plugins: [
        splitVendorChunkPlugin(),
        vue()
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
            '@icons': fileURLToPath(new URL('./node_modules/vue-material-design-icons', import.meta.url)),
        }
    }
})
