import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },

  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/,'')
      },
      // 2. 【新增】图片资源的代理
      '/images': {
        target: 'http://localhost:8080', // 转发给后端
        changeOrigin: true
        // 注意：这里不需要 rewrite 去掉 /images，因为后端的映射路径就是 /images/**
      }
    }
  }
})
