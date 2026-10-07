import { defineConfig } from 'vite';

// Port 5173 matches allowedOrigins in ../pointfix.config.json; strictPort fails instead of drifting to another port.
export default defineConfig({
  server: { port: 5173, strictPort: true },
  build: { target: 'es2022' } // main.js uses top-level await
});
