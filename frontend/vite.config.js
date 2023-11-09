import { defineConfig } from 'vite';
import { resolve } from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  base: '',
  build: {
    outDir: "../backend/src/main/resources/static",
    rollupOptions: {
      input: {
        index: resolve(__dirname, 'index.html'),
        room: resolve(__dirname, 'room.html'),
        chat: resolve(__dirname, 'chat.html'),
      }
    }
  }, // 빌드 결과물이 생성되는 경로
  server: {
    proxy: {
      "/api": "http://localhost:8083",
    }, // proxy 설정
  },
})
