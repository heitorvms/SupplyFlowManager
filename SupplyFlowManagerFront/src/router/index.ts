import { createRouter, createWebHistory } from 'vue-router'


import ProductsView from '@/pages/products/ProductsView.vue'
import RawMaterialView from '@/pages/rawMaterial/RawMaterialView.vue'
import ProductCompositionView from '@/pages/productComposition/ProductCompositionView.vue'
import ProductionOptimizationView from '@/pages/productionOptimization/ProductionOptimizationView.vue'

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
    {
      path: "/product-composition",
      name: "product-composition",
      component: ProductCompositionView
    },
    {
      path: "/production-optimization",
      name: "production-optimization",
      component: ProductionOptimizationView
    },

  ],
})

export default router
