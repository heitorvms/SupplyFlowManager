<template>
  <section class="optimization-page">
    <v-sheet class="hero" rounded="xl">
      <div class="hero-content">
        <div>
          <h1>{{ t("optimization.title") }}</h1>
          <p>{{ t("optimization.subtitle") }}</p>
        </div>

        <v-btn
          class="analyze-btn"
          color="primary"
          size="large"
          prepend-icon="mdi-chart-line"
          :loading="loading"
          @click="analyzeProduction"
        >
          {{ t("optimization.analyze") }}
        </v-btn>
      </div>
    </v-sheet>

    <ProductionOptimizationTable
      :items="rankedItems"
      :loading="loading"
      :total-value="totalValue"
    />
  </section>

  <AppFeedbackSnackbar
    v-model="snackbar.show"
    :text="snackbar.text"
    :color="snackbar.color"
  />
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import AppFeedbackSnackbar from "@/components/AppFeedbackSnackbar.vue";
import ProductionOptimizationTable from "@/components/productionOptimization/ProductionOptimizationTable.vue";
import type { ProductionOptimizationItem } from "@/types/ProductionOptimization";
import { getProductionOptimization } from "@/services/productionService";
import { resolveApiErrorMessage } from "@/utils/apiError";

const { t } = useI18n();

const items = ref<ProductionOptimizationItem[]>([]);
const loading = ref(false);

const snackbar = ref({
  show: false,
  text: "",
  color: "success",
});

const rankedItems = computed(() => {
  return [...items.value]
    .sort((a, b) => b.totalValue - a.totalValue)
    .map((item, index) => ({
      ...item,
      rank: index + 1,
    }));
});

const totalValue = computed(() => {
  return rankedItems.value.reduce((accumulator, item) => {
    const value = Number.isFinite(item.totalValue) ? item.totalValue : 0;
    return accumulator + value;
  }, 0);
});

async function analyzeProduction() {
  loading.value = true;
  try {
    items.value = await getProductionOptimization();
    snackbar.value = {
      show: true,
      text: t("optimization.analysisSuccess"),
      color: "success",
    };
  } catch (error) {
    console.error("Error optimizing production:", error);
    snackbar.value = {
      show: true,
      text: resolveApiErrorMessage(error, t, { defaultKey: "optimization.analysisError" }),
      color: "error",
    };
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.optimization-page {
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

.analyze-btn {
  min-width: 210px;
}

@media (max-width: 600px) {
  .analyze-btn {
    width: 100%;
    min-width: 100%;
  }
}
</style>
