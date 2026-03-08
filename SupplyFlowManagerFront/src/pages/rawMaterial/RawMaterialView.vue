<template>
  <section class="raw-material-page">
    <v-sheet class="hero" rounded="xl">
      <div class="hero-content">
        <div>
          <h1>Raw Materials</h1>
        </div>

        <v-btn
          class="new-raw-material-btn"
          color="primary"
          size="large"
          prepend-icon="mdi-plus"
          @click="openCreate"
        >
          New Raw Material
        </v-btn>
      </div>
    </v-sheet>

    <RawMaterialTable
      :raw-materials="rawMaterials"
      :loading="loading"
      :total-items="totalItems"
      :page="page"
      :items-per-page="itemsPerPage"
      @edit="openEdit"
      @delete="openDelete"
      @update:page="page = $event"
      @update:items-per-page="itemsPerPage = $event"
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

  <v-snackbar
    v-model="snackbar.show"
    :color="snackbar.color"
    location="top right"
    timeout="3200"
  >
    {{ snackbar.text }}
  </v-snackbar>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import RawMaterialTable from "@/components/rawMaterial/RawMaterialTable.vue";
import RawMaterialForm from "@/components/rawMaterial/RawMaterialForm.vue";
import RawMaterialDeleteDialog from "@/components/rawMaterial/RawMaterialDeleteDialog.vue";
import type { RawMaterial } from "@/types/RawMaterial";
import {
  deleteRawMaterial,
  getRawMaterials,
} from "@/services/rawMaterialService";

const rawMaterials = ref<RawMaterial[]>([]);
const totalItems = ref(0);
const page = ref(1);
const itemsPerPage = ref(10);
const loading = ref(false);
const deleteDialog = ref(false);
const formDialog = ref(false);

const snackbar = ref({
  show: false,
  text: "",
  color: "success",
});

const selectedRawMaterial = ref<RawMaterial | null>(null);

async function loadRawMaterials() {
  loading.value = true;
  try {
    const data = await getRawMaterials(page.value - 1, itemsPerPage.value);

    rawMaterials.value = data.content;
    totalItems.value = data.totalElements;
  } finally {
    loading.value = false;
  }
}

watch([page, itemsPerPage], loadRawMaterials, { immediate: true });

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
    text: "Materia-prima salva com sucesso.",
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
    handleRawMaterialError("Codigo da materia-prima invalido para exclusao.");
    return;
  }

  try {
    await deleteRawMaterial(rawMaterial.code);
    snackbar.value = {
      show: true,
      text: "Materia-prima excluida com sucesso.",
      color: "success",
    };

    await loadRawMaterials();
  } catch (error) {
    console.error("Error deleting raw material:", error);
    handleRawMaterialError("Nao foi possivel excluir a materia-prima. Tente novamente.");
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
