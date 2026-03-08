<template>
  <v-card class="raw-material-table-card" rounded="xl">
    <div class="table-toolbar">
      <div>
        <h2>Raw Material Catalog</h2>
      </div>

      <v-text-field
        v-model="search"
        class="search-field"
        prepend-inner-icon="mdi-magnify"
        label="Buscar materia-prima"
        variant="outlined"
        density="comfortable"
        hide-details
        clearable
      />
    </div>

    <div class="table-wrapper">
      <v-data-table
        :headers="headers"
        :items="filteredRawMaterials"
        :loading="loading"
        :mobile="smAndDown"
        :density="smAndDown ? 'comfortable' : 'default'"
        item-value="code"
        hover
        hide-default-footer
        class="raw-material-table"
      >
        <template #item.stockQuantity="{ item }">
          {{ formatQuantity(item.stockQuantity) }}
        </template>

        <template #item.actions="{ item }">
          <div class="actions-cell">
            <v-btn
              icon="mdi-pencil"
              variant="text"
              color="primary"
              @click="$emit('edit', item)"
            />

            <v-btn
              icon="mdi-delete"
              variant="text"
              color="error"
              @click="$emit('delete', item)"
            />
          </div>
        </template>
      </v-data-table>
    </div>

    <div class="table-footer">
      <p class="footer-range">
        Mostrando {{ startItem }}-{{ endItem }} de {{ totalItems }} itens
      </p>

      <div class="footer-controls">
        <span class="page-size-label">Itens</span>
        <v-menu v-model="pageSizeMenu" location="top">
          <template #activator="{ props: menuProps }">
            <v-btn
              v-bind="menuProps"
              class="page-size-btn"
              variant="outlined"
              size="small"
              append-icon="mdi-chevron-down"
            >
              {{ itemsPerPage }}
            </v-btn>
          </template>

          <v-list density="compact" class="page-size-list">
            <v-list-item
              v-for="option in itemsPerPageOptions"
              :key="option.value"
              :title="option.title"
              @click="onSelectItemsPerPage(option.value)"
            />
          </v-list>
        </v-menu>

        <v-pagination
          :model-value="page"
          :length="totalPages"
          density="comfortable"
          rounded="circle"
          active-color="primary"
          total-visible="6"
          @update:model-value="$emit('update:page', $event)"
        />
      </div>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useDisplay } from "vuetify";
import type { RawMaterial } from "@/types/RawMaterial";

const props = defineProps<{
  rawMaterials: RawMaterial[]
  loading: boolean
  totalItems: number
  page: number
  itemsPerPage: number
}>();

const emit = defineEmits<{
  (e: "edit", rawMaterial: RawMaterial): void
  (e: "delete", rawMaterial: RawMaterial): void
  (e: "update:page", value: number): void
  (e: "update:itemsPerPage", value: number): void
}>();

const search = ref("");
const pageSizeMenu = ref(false);

const filteredRawMaterials = computed(() => {
  const value = search.value.trim().toLowerCase();

  if (!value) {
    return props.rawMaterials;
  }

  return props.rawMaterials.filter((rawMaterial) =>
    rawMaterial.name.toLowerCase().includes(value)
  );
});

const { smAndDown } = useDisplay();

const totalPages = computed(() =>
  Math.max(1, Math.ceil(props.totalItems / props.itemsPerPage))
);

const startItem = computed(() => {
  if (!props.totalItems) {
    return 0;
  }

  return (props.page - 1) * props.itemsPerPage + 1;
});

const endItem = computed(() =>
  Math.min(props.totalItems, props.page * props.itemsPerPage)
);

const headers = [
  { title: "Code", key: "code", width: 100 },
  { title: "Name", key: "name" },
  { title: "Stock Quantity", key: "stockQuantity", align: "end" as const, width: 180 },
  { title: "Unit", key: "unitOfMeasure", align: "center" as const, width: 140 },
  { title: "Actions", key: "actions", sortable: false, align: "end" as const, width: 120 },
];

const itemsPerPageOptions = [
  { title: "5", value: 5 },
  { title: "10", value: 10 },
  { title: "20", value: 20 },
  { title: "50", value: 50 },
];

function onChangeItemsPerPage(value: number | string | null) {
  const parsed = Number(value);

  if (!Number.isFinite(parsed) || parsed <= 0) {
    return;
  }

  emit("update:itemsPerPage", parsed);
  emit("update:page", 1);
}

function onSelectItemsPerPage(value: number) {
  onChangeItemsPerPage(value);
  pageSizeMenu.value = false;
}

function formatQuantity(value: number) {
  return new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3,
  }).format(value);
}
</script>

<style scoped>
.raw-material-table-card {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(11, 18, 30, 0.72);
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  padding: 16px 16px 8px;
}

.table-toolbar h2 {
  margin: 0;
  font-size: 1.05rem;
}

.search-field {
  max-width: 320px;
  min-width: min(320px, 100%);
}

.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

.raw-material-table {
  min-width: 820px;
}

.raw-material-table :deep(.v-data-table__wrapper > table) {
  width: 100%;
}

.raw-material-table :deep(thead th) {
  padding-top: 14px !important;
  padding-bottom: 14px !important;
}

.raw-material-table :deep(thead th:first-child),
.raw-material-table :deep(tbody td:first-child) {
  padding-left: 20px !important;
}

.raw-material-table :deep(thead th:last-child),
.raw-material-table :deep(tbody td:last-child) {
  padding-right: 20px !important;
}

.raw-material-table :deep(tbody td) {
  padding-top: 12px !important;
  padding-bottom: 12px !important;
}

.actions-cell {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  padding: 10px 16px 16px;
}

.footer-range {
  margin: 0;
  font-size: 13px;
  opacity: 0.75;
}

.footer-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.page-size-label {
  font-size: 13px;
  opacity: 0.8;
}

.page-size-btn {
  min-width: 92px;
  justify-content: space-between;
}

.page-size-list {
  min-width: 92px;
}

@media (max-width: 600px) {
  .table-toolbar {
    padding: 14px 14px 6px;
  }

  .search-field {
    max-width: 100%;
    min-width: 100%;
  }

  .raw-material-table {
    min-width: 100%;
  }

  .raw-material-table :deep(thead th:first-child),
  .raw-material-table :deep(tbody td:first-child) {
    padding-left: 12px !important;
  }

  .raw-material-table :deep(thead th:last-child),
  .raw-material-table :deep(tbody td:last-child) {
    padding-right: 12px !important;
  }

  .table-footer {
    padding: 8px 12px 14px;
  }
}
</style>
