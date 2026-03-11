<template>
  <v-card class="composition-table-card" rounded="xl">
    <div class="table-toolbar">
      <div>
        <h2>{{ t("composition.title") }}</h2>
        <p>{{ t("composition.tableSubtitle") }}</p>
      </div>

      <v-btn
        class="add-raw-material-btn"
        color="primary"
        size="large"
        prepend-icon="mdi-plus"
        :disabled="!selectedProductId"
        @click="$emit('add')"
      >
        {{ t("composition.addRawMaterial") }}
      </v-btn>
    </div>

    <v-data-table
      :headers="headers"
      :items="items"
      :loading="loading"
      item-value="id"
      class="composition-table"
      :no-data-text="t('composition.noDataSelectProduct')"
      hover
      hide-default-footer
    >
      <template #item.quantityRequired="{ item }">
        {{ formatQuantity(item.quantityRequired) }}
      </template>

      <template #item.actions="{ item }">
        <div class="actions-cell">
          <v-btn
            icon="mdi-delete"
            color="error"
            variant="text"
            @click="$emit('delete', item)"
          />
        </div>
      </template>
    </v-data-table>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import type { ProductCompositionItem } from "@/types/ProductComposition";
import { formatNumber } from "@/utils/numberFormat";
import type { AppLocale } from "@/plugins/i18n";

defineProps<{
  items: ProductCompositionItem[]
  loading: boolean
  selectedProductId: number | null
}>();

defineEmits<{
  (e: "add"): void
  (e: "delete", item: ProductCompositionItem): void
}>();

const { t, locale } = useI18n();

const headers = computed(() => [
  { title: t("composition.fieldRawMaterial"), key: "rawMaterialName" },
  { title: t("composition.fieldQuantityRequired"), key: "quantityRequired", align: "end" as const, width: 180 },
  { title: t("rawMaterials.fieldUnit"), key: "unitOfMeasure", align: "center" as const, width: 130 },
  { title: t("common.actions"), key: "actions", sortable: false, align: "end" as const, width: 120 },
]);

function formatQuantity(value: number) {
  return formatNumber(value, locale.value as AppLocale, {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3,
  });
}
</script>

<style scoped>
.composition-table-card {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(11, 18, 30, 0.72);
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  padding: 16px;
}

.table-toolbar h2 {
  margin: 0;
  font-size: 1.05rem;
}

.table-toolbar p {
  margin: 2px 0 0;
  opacity: 0.72;
  font-size: 13px;
}

.add-raw-material-btn {
  min-width: 210px;
}

.composition-table :deep(thead th:first-child),
.composition-table :deep(tbody td:first-child) {
  padding-left: 20px !important;
}

.composition-table :deep(thead th:last-child),
.composition-table :deep(tbody td:last-child) {
  padding-right: 20px !important;
}

.actions-cell {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 600px) {
  .table-toolbar {
    padding: 14px;
  }

  .add-raw-material-btn {
    width: 100%;
    min-width: 100%;
  }

  .composition-table :deep(thead th:first-child),
  .composition-table :deep(tbody td:first-child) {
    padding-left: 12px !important;
  }

  .composition-table :deep(thead th:last-child),
  .composition-table :deep(tbody td:last-child) {
    padding-right: 12px !important;
  }
}
</style>
