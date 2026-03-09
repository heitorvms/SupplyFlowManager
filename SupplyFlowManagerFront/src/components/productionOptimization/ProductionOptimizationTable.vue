<template>
  <v-card class="optimization-table-card" rounded="xl">
    <div class="table-toolbar">
      <div>
        <h2>Optimization Result</h2>
        <p>Estimated production capacity based on current stock levels.</p>
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
      no-data-text="Click 'Analyze Production' to load optimization data"
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
      <span>Total Top Sales Value</span>
      <strong>{{ formatCurrency(totalValue) }}</strong>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import type { ProductionOptimizationItem } from "@/types/ProductionOptimization";

defineProps<{
  items: Array<ProductionOptimizationItem & { rank: number }>
  loading: boolean
  totalValue: number
}>();

const headers = [
  { title: "Rank", key: "rank", align: "center" as const, width: 90 },
  { title: "Product", key: "productName" },
  { title: "Producible Quantity", key: "units", align: "end" as const, width: 260 },
  { title: "Total Top Sales Value", key: "totalValue", align: "end" as const, width: 200 },
];

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
  const safeValue = Number.isFinite(value) ? value : 0;
  return new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3,
  }).format(safeValue);
}

function formatCurrency(value: number) {
  const safeValue = Number.isFinite(value) ? value : 0;
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(safeValue);
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
