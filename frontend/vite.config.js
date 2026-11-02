import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'

export default defineConfig({
  plugins: [
    vue(),
    Components({ resolvers: [VantResolver()] }),
  ],
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      '/account':  { target: 'http://localhost:9999', changeOrigin: true },
      '/stroke':   { target: 'http://localhost:9999', changeOrigin: true },
      '/order':    { target: 'http://localhost:9999', changeOrigin: true },
      '/notice':   { target: 'http://localhost:9999', changeOrigin: true },
      '/storage':  { target: 'http://localhost:9999', changeOrigin: true },
      '/ai-msg':   { target: 'http://localhost:9999', changeOrigin: true },
      '/payment':  { target: 'http://localhost:9999', changeOrigin: true },
      '/ai':       { target: 'http://localhost:9999', changeOrigin: true },
    },
  },
  resolve: {
    alias: { '@': '/src' },
  },
})
