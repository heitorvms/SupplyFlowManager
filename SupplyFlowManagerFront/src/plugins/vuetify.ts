/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com
 */

import { createVuetify } from 'vuetify'
import '@mdi/font/css/materialdesignicons.css'
import '../styles/layers.css'
import 'vuetify/styles'

export default createVuetify({
  defaults: {
    VCard: {
      rounded: 'xl',
      elevation: 0,
    },
    VBtn: {
      rounded: 'lg',
      style: 'text-transform: none; letter-spacing: 0.01em; font-weight: 600;',
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
      hideDetails: 'auto',
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable',
      hideDetails: 'auto',
    },
  },
  theme: {
    defaultTheme: 'supplyFlowDark',
    utilities: false,
    themes: {
      supplyFlowDark: {
        dark: true,
        colors: {
          primary: '#2D8CFF',
          secondary: '#14B8A6',
          accent: '#7DD3FC',
          background: '#070B12',
          surface: '#0D1524',
          'surface-variant': '#132037',
          error: '#FF5D73',
          warning: '#F59E0B',
          info: '#38BDF8',
          success: '#10B981',
          'on-primary': '#EAF4FF',
          'on-secondary': '#E9FFF9',
        },
      },
    },
  },
  display: {
    mobileBreakpoint: 'md',
    thresholds: {
      xs: 0,
      sm: 600,
      md: 840,
      lg: 1145,
      xl: 1545,
      xxl: 2138,
    },
  },
})
