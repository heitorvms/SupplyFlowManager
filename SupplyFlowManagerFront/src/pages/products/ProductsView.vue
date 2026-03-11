<template>
  <section class="products-page">
    <v-sheet class="hero" rounded="xl">
      <div class="hero-content">
        <div>
          <h1>{{ t("products.title") }}</h1>
        </div>

        <v-btn
          class="new-product-btn"
          color="primary"
          size="large"
          prepend-icon="mdi-plus"
          @click="openCreate"
        >
          {{ t("products.new") }}
        </v-btn>
      </div>
    </v-sheet>

    <ProductTable
      :products="products"
      :loading="loading"
      :total-items="totalItems"
      :page="page"
      :items-per-page="itemsPerPage"
      :search="search"
      @edit="openEdit"
      @delete="openDelete"
      @update:page="page = $event"
      @update:items-per-page="itemsPerPage = $event"
      @update:search="search = $event"
    />
  </section>

  <ProductForm
    v-model="formDialog"
    :product="selectedProduct"
    @save="handleProductSaved"
    @error="handleProductError"
  />

  <ProductDeleteDialog
    v-model="deleteDialog"
    :product="selectedProduct ?? undefined"
    @confirm="confirmDeleteProduct"
  />

  <AppFeedbackSnackbar
    v-model="snackbar.show"
    :text="snackbar.text"
    :color="snackbar.color"
  />
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import AppFeedbackSnackbar from "@/components/AppFeedbackSnackbar.vue";
import ProductTable from "@/components/products/ProductTable.vue";
import ProductForm from "@/components/products/ProductForm.vue";
import ProductDeleteDialog from "@/components/products/ProductDeleteDialog.vue";
import type { Product } from "@/types/Product";
import { deleteProduct, getProducts } from "@/services/productService";
import { resolveApiErrorMessage } from "@/utils/apiError";

const { t } = useI18n();

const products = ref<Product[]>([]);
const totalItems = ref(0);
const page = ref(1);
const itemsPerPage = ref(10);
const search = ref("");
const debouncedSearch = ref("");
const loading = ref(false);
const deleteDialog = ref(false);
const searchPool = ref<Product[]>([]);
const searchCacheTerm = ref("");
let searchDebounceTimer: ReturnType<typeof setTimeout> | null = null;
const snackbar = ref({
  show: false,
  text: "",
  color: "success",
});

async function loadProducts() {
  loading.value = true;
  try {
    const normalizedSearch = debouncedSearch.value.trim().toLowerCase();

    if (!normalizedSearch || normalizedSearch.length < 2) {
      const data = await getProducts(page.value - 1, itemsPerPage.value);
      products.value = data.content;
      totalItems.value = data.totalElements;
      searchPool.value = [];
      searchCacheTerm.value = "";
      return;
    }

    if (searchCacheTerm.value !== normalizedSearch || searchPool.value.length === 0) {
      searchPool.value = await loadAllProducts();
      searchCacheTerm.value = normalizedSearch;
    }

    const filtered = searchPool.value.filter((product) =>
      product.name.toLowerCase().includes(normalizedSearch),
    );

    totalItems.value = filtered.length;
    const start = (page.value - 1) * itemsPerPage.value;
    const end = start + itemsPerPage.value;
    products.value = filtered.slice(start, end);
  } finally {
    loading.value = false;
  }
}

watch([page, itemsPerPage], loadProducts, { immediate: true });

watch(search, () => {
  page.value = 1;

  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer);
  }

  searchDebounceTimer = setTimeout(() => {
    debouncedSearch.value = search.value;
    loadProducts();
  }, 450);
});

onBeforeUnmount(() => {
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer);
  }
});

async function loadAllProducts() {
  const firstPageSize = 100;
  const firstPage = await getProducts(0, firstPageSize);

  if (firstPage.totalPages <= 1) {
    return firstPage.content;
  }

  const pageRequests: Array<Promise<Awaited<ReturnType<typeof getProducts>>>> = [];

  for (let currentPage = 1; currentPage < firstPage.totalPages; currentPage += 1) {
    pageRequests.push(getProducts(currentPage, firstPageSize));
  }

  const remainingPages = await Promise.all(pageRequests);

  return [
    ...firstPage.content,
    ...remainingPages.flatMap((pageData) => pageData.content),
  ];
}

const formDialog = ref(false);
const selectedProduct = ref<Product | null>(null);

function openCreate() {
  selectedProduct.value = null;
  formDialog.value = true;
}

function openEdit(product: Product) {
  selectedProduct.value = product;
  formDialog.value = true;
}

function openDelete(product: Product) {
  selectedProduct.value = product;
  deleteDialog.value = true;
}

function handleProductSaved() {
  snackbar.value = {
    show: true,
    text: t("products.savedSuccess"),
    color: "success",
  };

  loadProducts();
}

function handleProductError(message: string) {
  snackbar.value = {
    show: true,
    text: message,
    color: "error",
  };
}

async function confirmDeleteProduct(product: Product) {
  if (!product.code) {
    handleProductError(t("products.invalidCodeDelete"));
    return;
  }

  try {
    await deleteProduct(product.code);
    snackbar.value = {
      show: true,
      text: t("products.deletedSuccess"),
      color: "success",
    };

    await loadProducts();
  } catch (error) {
    console.error("Error deleting product:", error);
    handleProductError(resolveApiErrorMessage(error, t, { defaultKey: "products.couldNotDelete" }));
  }
}
</script>

<style scoped>
.products-page {
  width: 100%;
  display: grid;
  gap: 18px;
}

.hero {
  padding: clamp(16px, 2vw, 24px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  background:
    linear-gradient(130deg, rgba(25, 118, 210, 0.2), rgba(14, 165, 136, 0.16)),
    rgba(255, 255, 255, 0.03);
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-kicker {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.72;
}

.hero h1 {
  margin: 0;
  font-size: clamp(1.7rem, 2vw, 2rem);
}

.hero-subtitle {
  margin: 4px 0 0;
  opacity: 0.8;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.stat-card {
  padding: 14px 16px;
}

.stat-label {
  margin: 0;
  opacity: 0.7;
  font-size: 13px;
}

.stat-card h3 {
  margin: 6px 0 0;
  font-size: 1.4rem;
}

.new-product-btn {
  min-width: 210px;
}

@media (max-width: 600px) {
  .new-product-btn {
    width: 100%;
    min-width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
