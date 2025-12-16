import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: true,
    port: 5173
  },
  preview: {
    port: 5173
  },
  // 新增这部分配置
  define: {
    'global': 'window' // 让global指向浏览器的window对象
  }
})