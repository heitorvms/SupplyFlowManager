<template>
  <section class="raw-material-page">
    <v-sheet class="hero" rounded="xl">
      <div class="hero-content">
        <div>
          <h1>{{ t("rawMaterials.title") }}</h1>
        </div>

        <v-btn
          class="new-raw-material-btn"
          color="primary"
          size="large"
          prepend-icon="mdi-plus"
          @click="openCreate"
        >
          {{ t("rawMaterials.new") }}
        </v-btn>
      </div>
    </v-sheet>

    <RawMaterialTable
      :raw-materials="rawMaterials"
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

  <RawMaterialForm
    v-model="formDialog"
    :raw-material="selectedRawMaterial"
    @save="handleRawMaterialSaved"
    @error="handleRawMaterialError"
  />

  <RawMaterialDeleteDialog
    v-model="deleteDialog"
    :raw-material="selectedRawMaterial ?? undefined"
    @confirm="confirmDeleteRawMaterial"
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
import RawMaterialTable from "@/components/rawMaterial/RawMaterialTable.vue";
import RawMaterialForm from "@/components/rawMaterial/RawMaterialForm.vue";
import RawMaterialDeleteDialog from "@/components/rawMaterial/RawMaterialDeleteDialog.vue";
import type { RawMaterial } from "@/types/RawMaterial";
import {
  deleteRawMaterial,
  getRawMaterials,
} from "@/services/rawMaterialService";
import { resolveApiErrorMessage } from "@/utils/apiError";

const { t } = useI18n();

const rawMaterials = ref<RawMaterial[]>([]);
const totalItems = ref(0);
const page = ref(1);
const itemsPerPage = ref(10);
const search = ref("");
const debouncedSearch = ref("");
const loading = ref(false);
const deleteDialog = ref(false);
const formDialog = ref(false);
const searchPool = ref<RawMaterial[]>([]);
const searchCacheTerm = ref("");
let searchDebounceTimer: ReturnType<typeof setTimeout> | null = null;

const snackbar = ref({
  show: false,
  text: "",
  color: "success",
});

const selectedRawMaterial = ref<RawMaterial | null>(null);

async function loadRawMaterials() {
  loading.value = true;
  try {
    const normalizedSearch = debouncedSearch.value.trim().toLowerCase();

    if (!normalizedSearch || normalizedSearch.length < 2) {
      const data = await getRawMaterials(page.value - 1, itemsPerPage.value);
      rawMaterials.value = data.content;
      totalItems.value = data.totalElements;
      searchPool.value = [];
      searchCacheTerm.value = "";
      return;
    }

    if (searchCacheTerm.value !== normalizedSearch || searchPool.value.length === 0) {
      searchPool.value = await loadAllRawMaterials();
      searchCacheTerm.value = normalizedSearch;
    }

    const filtered = searchPool.value.filter((rawMaterial) =>
      rawMaterial.name.toLowerCase().includes(normalizedSearch),
    );

    totalItems.value = filtered.length;

    const start = (page.value - 1) * itemsPerPage.value;
    const end = start + itemsPerPage.value;
    rawMaterials.value = filtered.slice(start, end);
  } finally {
    loading.value = false;
  }
}

watch([page, itemsPerPage], loadRawMaterials, { immediate: true });

watch(search, () => {
  page.value = 1;

  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer);
  }

  searchDebounceTimer = setTimeout(() => {
    debouncedSearch.value = search.value;
    loadRawMaterials();
  }, 450);
});

onBeforeUnmount(() => {
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer);
  }
});

async function loadAllRawMaterials() {
  const firstPageSize = 100;
  const firstPage = await getRawMaterials(0, firstPageSize);

  if (firstPage.totalPages <= 1) {
    return firstPage.content;
  }

  const pageRequests: Array<Promise<Awaited<ReturnType<typeof getRawMaterials>>>> = [];

  for (let currentPage = 1; currentPage < firstPage.totalPages; currentPage += 1) {
    pageRequests.push(getRawMaterials(currentPage, firstPageSize));
  }

  const remainingPages = await Promise.all(pageRequests);

  return [
    ...firstPage.content,
    ...remainingPages.flatMap((pageData) => pageData.content),
  ];
}

function openCreate() {
  selectedRawMaterial.value = null;
  formDialog.value = true;
}

function openEdit(rawMaterial: RawMaterial) {
  selectedRawMaterial.value = rawMaterial;
  formDialog.value = true;
}

function openDelete(rawMaterial: RawMaterial) {
  selectedRawMaterial.value = rawMaterial;
  deleteDialog.value = true;
}

function handleRawMaterialSaved() {
  snackbar.value = {
    show: true,
    text: t("rawMaterials.savedSuccess"),
    color: "success",
  };

  loadRawMaterials();
}

function handleRawMaterialError(message: string) {
  snackbar.value = {
    show: true,
    text: message,
    color: "error",
  };
}

async function confirmDeleteRawMaterial(rawMaterial: RawMaterial) {
  if (!rawMaterial.code) {
    handleRawMaterialError(t("rawMaterials.invalidCodeDelete"));
    return;
  }

  try {
    await deleteRawMaterial(rawMaterial.code);
    snackbar.value = {
      show: true,
      text: t("rawMaterials.deletedSuccess"),
      color: "success",
    };

    await loadRawMaterials();
  } catch (error) {
    console.error("Error deleting raw material:", error);
    handleRawMaterialError(resolveApiErrorMessage(error, t, { defaultKey: "rawMaterials.couldNotDelete" }));
  }
}
</script>

<style scoped>
.raw-material-page {
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

.new-raw-material-btn {
  min-width: 230px;
}

@media (max-width: 600px) {
  .new-raw-material-btn {
    width: 100%;
    min-width: 100%;
  }
}
</style>
