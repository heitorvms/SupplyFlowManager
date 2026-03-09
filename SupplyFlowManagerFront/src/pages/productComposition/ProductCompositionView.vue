<template>
  <section class="product-composition-page">
    <v-sheet class="hero" rounded="xl">
      <div class="hero-content">
        <div>
          <h1>Product Composition</h1>
          <p>Manage raw materials required to produce one unit of each product.</p>
        </div>

        <v-autocomplete
          v-model="selectedProductId"
          v-model:search="productSearch"
          :items="productOptions"
          item-title="title"
          item-value="value"
          class="product-select"
          label="Select Product"
          variant="outlined"
          density="comfortable"
          hide-details
          :loading="loadingProducts"
          no-data-text="Nenhum produto encontrado"
          auto-select-first
          :menu-props="{ maxHeight: 320 }"
        />
      </div>
    </v-sheet>

  <ProductCompositionTable
    :items="compositionItems"
    :loading="loadingComposition"
    :selected-product-id="selectedProductId"
    @add="openCreateDialog"
    @delete="openDeleteDialog"
  />
</section>

  <ProductCompositionDialog
    v-model="formDialog"
    :product-id="selectedProductId"
    :raw-materials="rawMaterials"
    :saving="savingComposition"
    @submit="handleSaveComposition"
    @error="showError"
  />

  <ProductCompositionDeleteDialog
    v-model="deleteDialog"
    :item="selectedCompositionItem"
    @confirm="confirmDeleteComposition"
  />

  <AppFeedbackSnackbar
    v-model="snackbar.show"
    :text="snackbar.text"
    :color="snackbar.color"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import type { Product } from "@/types/Product";
import type { RawMaterial } from "@/types/RawMaterial";
import type {
  ProductCompositionItem,
  SaveProductCompositionPayload,
} from "@/types/ProductComposition";
import ProductCompositionTable from "@/components/productComposition/ProductCompositionTable.vue";
import ProductCompositionDialog from "@/components/productComposition/ProductCompositionDialog.vue";
import ProductCompositionDeleteDialog from "@/components/productComposition/ProductCompositionDeleteDialog.vue";
import AppFeedbackSnackbar from "@/components/AppFeedbackSnackbar.vue";
import { getProducts } from "@/services/productService";
import { getRawMaterials } from "@/services/rawMaterialService";
import {
  deleteProductComposition,
  getProductComposition,
  postProductComposition,
} from "@/services/productCompositionService";

const products = ref<Product[]>([]);
const rawMaterials = ref<RawMaterial[]>([]);
const compositionItems = ref<ProductCompositionItem[]>([]);

const loadingProducts = ref(false);
const loadingComposition = ref(false);
const savingComposition = ref(false);

const selectedProductId = ref<number | null>(null);
const productSearch = ref("");
const formDialog = ref(false);
const deleteDialog = ref(false);
const selectedCompositionItem = ref<ProductCompositionItem | null>(null);

const snackbar = ref({
  show: false,
  text: "",
  color: "success",
});

const productOptions = computed(() =>
  products.value.map((product) => ({
    title: product.name,
    value: product.code as number,
  })),
);

onMounted(async () => {
  await Promise.all([loadProducts(), loadRawMaterials()]);
});

watch(selectedProductId, async (value) => {
  if (!value) {
    compositionItems.value = [];
    return;
  }

  await loadComposition(value);
});

async function loadProducts() {
  loadingProducts.value = true;
  try {
    const response = await getProducts(0, 300);
    products.value = response.content;

    const firstProductCode = response.content[0]?.code;
    if (firstProductCode && !selectedProductId.value) {
      selectedProductId.value = firstProductCode;
    }
  } catch (error) {
    console.error("Error loading products:", error);
    showError("Could not load products.");
  } finally {
    loadingProducts.value = false;
  }
}

async function loadRawMaterials() {
  try {
    const response = await getRawMaterials(0, 400);
    rawMaterials.value = response.content;
  } catch (error) {
    console.error("Error loading raw materials:", error);
    showError("Could not load raw materials.");
  }
}

async function loadComposition(productId: number) {
  loadingComposition.value = true;
  try {
    const items = await getProductComposition(productId);

    compositionItems.value = items.map((item) => {
      const matchedRawMaterial = rawMaterials.value.find(
        (rawMaterial) =>
          rawMaterial.code === item.rawMaterialId || rawMaterial.name === item.rawMaterialName,
      );

      const hasGenericName = item.rawMaterialName === "Raw Material";
      const hasGenericUnit = item.unitOfMeasure === "UNIT";

      return {
        ...item,
        rawMaterialName: hasGenericName
          ? (matchedRawMaterial?.name ?? item.rawMaterialName)
          : item.rawMaterialName,
        unitOfMeasure:
          hasGenericUnit && matchedRawMaterial?.unitOfMeasure && matchedRawMaterial.unitOfMeasure !== "UNIT"
            ? matchedRawMaterial.unitOfMeasure
            : item.unitOfMeasure,
      };
    });
  } catch (error) {
    console.error("Error loading composition:", error);
    showError("Could not load product composition.");
  } finally {
    loadingComposition.value = false;
  }
}

function openCreateDialog() {
  if (!selectedProductId.value) {
    showError("Select a product before adding a raw material.");
    return;
  }

  formDialog.value = true;
}

function openDeleteDialog(item: ProductCompositionItem) {
  selectedCompositionItem.value = item;
  deleteDialog.value = true;
}

async function handleSaveComposition(payload: SaveProductCompositionPayload) {
  if (!selectedProductId.value) {
    showError("Select a product before saving composition.");
    return;
  }

  savingComposition.value = true;
  try {
    await postProductComposition(payload);
    showSuccess("Raw material added to composition successfully.");

    formDialog.value = false;
    await loadComposition(selectedProductId.value);
  } catch (error) {
    console.error("Error saving composition:", error);
    showError("Could not save composition. Please try again.");
  } finally {
    savingComposition.value = false;
  }
}

async function confirmDeleteComposition(item: ProductCompositionItem) {
  if (!item.id || !selectedProductId.value) {
    showError("Invalid composition item for deletion.");
    return;
  }

  try {
    await deleteProductComposition(item.id);
    showSuccess("Composition item removed successfully.");
    selectedCompositionItem.value = null;
    await loadComposition(selectedProductId.value);
  } catch (error) {
    console.error("Error deleting composition item:", error);
    showError("Could not remove composition item.");
  }
}

function showSuccess(text: string) {
  snackbar.value = { show: true, text, color: "success" };
}

function showError(text: string) {
  snackbar.value = { show: true, text, color: "error" };
}
</script>

<style scoped>
.product-composition-page {
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

.hero h1 {
  margin: 0;
  font-size: clamp(1.7rem, 2vw, 2rem);
}

.hero p {
  margin: 6px 0 0;
  opacity: 0.8;
}

.product-select {
  width: min(420px, 100%);
  min-width: min(420px, 100%);
}

@media (max-width: 600px) {
  .product-select {
    width: 100%;
    min-width: 100%;
  }
}
</style>
