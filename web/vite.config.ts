import vue from "@vitejs/plugin-vue"
import { fileURLToPath, URL } from "node:url"
import { defineConfig } from "vite"

// @ts-expect-error JSON Module import
import pkg from "./package.json"

process.env.VITE_APP_VERSION = pkg.version

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url))
    }
  },
  build: {
    rollupOptions: {
      output: {
        compact: true,
        assetFileNames: "[ext]/[hash][extname]",
        chunkFileNames: "js/[hash].js",
        manualChunks: (id) => {
          if (id.includes("codemirror")) {
            return `codeeditor`
          }

          if (id.includes("/locales/")) {
            return `lang-${id}`
          }

          if (id.includes("/modules/")) {
            return "app"
          }

          if (id.includes("/@vue/")) {
            return `core`
          }

          if (id.includes("/node_modules/")) {
            return `vendor`
          }

          return null
        }
      }
    }
  }
})
