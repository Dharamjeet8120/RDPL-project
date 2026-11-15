import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// The Spring Boot backend runs on port 8085. The dev server forwards every
// /api request to it, so the browser never has to deal with CORS.
const backend = process.env.VITE_BACKEND_URL || 'http://localhost:8085'
const proxy = { '/api': { target: backend, changeOrigin: true } }

export default defineConfig({
  plugins: [react()],
  server: { port: 5173, proxy },
  preview: { port: 4173, proxy },
})
