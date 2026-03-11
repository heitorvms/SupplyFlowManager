<template>
  <v-card class="optimization-table-card" rounded="xl">
    <div class="table-toolbar">
      <div>
        <h2>{{ t("optimization.resultTitle") }}</h2>
        <p>{{ t("optimization.resultSubtitle") }}</p>
      </div>
    </div>

    <v-data-table
      :headers="headers"
      :items="items"
      :loading="loading"
      class="optimization-table"
      item-value="productName"
      hover
      hide-default-footer
      :no-data-text="t('optimization.noData')"
    >
      <template #item.rank="{ item }">
        <div class="rank-badge" :class="getRankClass(item.rank)">
          <v-icon :icon="getRankIcon(item.rank)" size="16" />
          <span>#{{ item.rank }}</span>
        </div>
      </template>

      <template #item.units="{ item }">
        {{ formatUnits(item.units) }}
      </template>

      <template #item.totalValue="{ item }">
        {{ formatCurrency(item.totalValue) }}
      </template>
    </v-data-table>

    <div class="table-total">
      <span>{{ t("optimization.totalTopSalesValue") }}</span>
      <strong>{{ formatCurrency(totalValue) }}</strong>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import type { ProductionOptimizationItem } from "@/types/ProductionOptimization";
import { formatCurrency as formatCurrencyByLocale, formatNumber } from "@/utils/numberFormat";
import type { AppLocale } from "@/plugins/i18n";

defineProps<{
  items: Array<ProductionOptimizationItem & { rank: number }>
  loading: boolean
  totalValue: number
}>();

const { t, locale } = useI18n();

const headers = computed(() => [
  { title: t("optimization.rank"), key: "rank", align: "center" as const, width: 90 },
  { title: t("optimization.product"), key: "productName" },
  { title: t("optimization.producibleQuantity"), key: "units", align: "end" as const, width: 260 },
  { title: t("optimization.totalTopSalesValue"), key: "totalValue", align: "end" as const, width: 200 },
]);

function getRankIcon(rank: number) {
  if (rank <= 3) {
    return "mdi-medal";
  }

  return "mdi-pound";
}

function getRankClass(rank: number) {
  if (rank === 1) {
    return "rank-gold";
  }

  if (rank === 2) {
    return "rank-silver";
  }

  if (rank === 3) {
    return "rank-bronze";
  }

  return "rank-default";
}

function formatUnits(value: number) {
  return formatNumber(value, locale.value as AppLocale, {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3,
  });
}

function formatCurrency(value: number) {
  return formatCurrencyByLocale(value, locale.value as AppLocale);
}
</script>

<style scoped>
.optimization-table-card {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(11, 18, 30, 0.72);
}

.table-toolbar {
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

.optimization-table :deep(thead th:first-child),
.optimization-table :deep(tbody td:first-child) {
  padding-left: 20px !important;
}

.optimization-table :deep(thead th:last-child),
.optimization-table :deep(tbody td:last-child) {
  padding-right: 20px !important;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 999px;
  padding: 5px 10px;
  font-weight: 700;
  font-size: 12px;
  letter-spacing: 0.02em;
}

.rank-gold {
  color: #1d1400;
  background: linear-gradient(140deg, #facc15, #f59e0b);
  border: 1px solid rgba(255, 255, 255, 0.35);
}

.rank-silver {
  color: #0f172a;
  background: linear-gradient(140deg, #e2e8f0, #94a3b8);
  border: 1px solid rgba(255, 255, 255, 0.35);
}

.rank-bronze {
  color: #2a1100;
  background: linear-gradient(140deg, #fb923c, #c2410c);
  border: 1px solid rgba(255, 255, 255, 0.35);
}

.rank-default {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.table-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 14px 20px 16px;
}

.table-total span {
  opacity: 0.82;
}

.table-total strong {
  font-size: 1.05rem;
}

@media (max-width: 600px) {
  .table-toolbar {
    padding: 14px;
  }

  .optimization-table :deep(thead th:first-child),
  .optimization-table :deep(tbody td:first-child) {
    padding-left: 12px !important;
  }

  .optimization-table :deep(thead th:last-child),
  .optimization-table :deep(tbody td:last-child) {
    padding-right: 12px !important;
  }

  .table-total {
    padding: 12px 14px 14px;
  }
}
</style>
