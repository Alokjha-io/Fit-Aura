import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import {defineConfig} from 'vite';

export default defineConfig(() => {
  return {
    plugins: [react(), tailwindcss()],
    define: {
      'import.meta.env.FITAURA_ADMIN_EMAIL': JSON.stringify(
        process.env.FITAURA_ADMIN_EMAIL || process.env.VITE_FITAURA_ADMIN_EMAIL || process.env.ADMIN_EMAIL || process.env.VITE_ADMIN_EMAIL || ''
      ),
      'import.meta.env.FITAURA_ADMIN_PASSWORD': JSON.stringify(
        process.env.FITAURA_ADMIN_PASSWORD || process.env.VITE_FITAURA_ADMIN_PASSWORD || process.env.ADMIN_PASSWORD || process.env.VITE_ADMIN_PASSWORD || ''
      ),
      'import.meta.env.FITAURA_ADMIN_NAME': JSON.stringify(
        process.env.FITAURA_ADMIN_NAME || process.env.VITE_FITAURA_ADMIN_NAME || process.env.ADMIN_NAME || process.env.VITE_ADMIN_NAME || 'System Administrator'
      ),
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '.'),
      },
    },
    server: {
      // HMR is disabled in AI Studio via DISABLE_HMR env var.
      // Do not modify—file watching is disabled to prevent flickering during agent edits.
      hmr: process.env.DISABLE_HMR !== 'true',
      // Disable file watching when DISABLE_HMR is true to save CPU during agent edits.
      watch: process.env.DISABLE_HMR === 'true' ? null : {},
    },
  };
});
