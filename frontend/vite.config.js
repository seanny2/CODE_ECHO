import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
  base: '',
  build: {
    outDir: "../backend/src/main/resources/static",
  }, // 빌드 결과물이 생성되는 경로
  server: {
    proxy: {
      "/api": "http://localhost:8083",
    }, // proxy 설정
  },
})
