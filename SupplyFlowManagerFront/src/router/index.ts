import { createRouter, createWebHistory } from 'vue-router'


import ProductsView from '@/pages/products/ProductsView.vue'
import RawMaterialView from '@/pages/rawMaterial/RawMaterialView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/products"
    },

    {
      path: "/products",
      name: "products",
      component: ProductsView
    },
    {
      path: "/raw-materials",
      name: "raw-materials",
      component: RawMaterialView
    },

  ],
})

export default router
