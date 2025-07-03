import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: 'localhost',
    port: 3000,
    // 配置代理解决跨域问题
    proxy: {
      // 代理所有以 /api 开头的请求到后端服务器
      '/api': {
        target: '',
        changeOrigin: true,
      },
    }
  },
  build: {
    // 构建优化
    rollupOptions: {
      external: [
        '#minpath',
        '#minproc',
        '#minurl'
      ],
      output: {
        // 手动分块，减少单个文件大小
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          echarts: ['echarts'],
          axios: ['axios']
        }
      }
    },
    // 调整chunk大小警告限制
    chunkSizeWarningLimit: 1000,
    // 忽略某些警告
    target: 'esnext',
    commonjsOptions: {
      transformMixedEsModules: true
    }
  },
  define: {
    global: 'globalThis',
  },
  optimizeDeps: {
    include: ['react-markdown', 'remark-gfm']
  }
})
