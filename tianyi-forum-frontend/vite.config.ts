import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  server: {
    host: '0.0.0.0',
    port: 5173,
    // 允许通过 ngrok 域名访问开发服务器
    allowedHosts: ['09dd68b1aa4e.ngrok-free.app'],
    proxy: {
      '/api': { target: 'http://localhost:9090', changeOrigin: true },
      '/portal': { target: 'http://localhost:9090', changeOrigin: true },
      '/admin/auth': { target: 'http://localhost:9090', changeOrigin: true },
      '/user': { target: 'http://localhost:9090', changeOrigin: true },
      '/admin': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        // Return the SPA index for HTML requests; proxy everything else to the backend.
        bypass(req) {
          if (req.headers.accept?.includes('text/html')) return '/index.html'
        },
      },
    },
  },
  plugins: [
    vue(),
    // 添加以下配置
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
