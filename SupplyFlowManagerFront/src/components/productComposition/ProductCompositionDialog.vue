<template>
  <v-dialog
    v-model="dialog"
    :fullscreen="smAndDown"
    :max-width="smAndDown ? undefined : 560"
    scrim="rgba(3, 8, 16, 0.86)"
    scrollable
  >
    <v-card class="composition-form-card">
      <v-card-title class="form-title">
        <div>
          <p class="form-kicker">Composition</p>
          <h3>Add Raw Material</h3>
        </div>
      </v-card-title>

      <v-card-text class="form-content">
        <v-row>
          <v-col cols="12">
            <v-select
              v-model="form.rawMaterialId"
              :items="rawMaterialOptions"
              item-title="title"
              item-value="value"
              label="Raw Material"
              variant="outlined"
              density="comfortable"
              hide-details="auto"
              :rules="[requiredNumberRule]"
              :disabled="saving"
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              v-model.number="form.quantityRequired"
              label="Quantity Required"
              type="number"
              min="0.001"
              step="0.001"
              variant="outlined"
              density="comfortable"
              hide-details="auto"
              :rules="[quantityRule]"
              :disabled="saving"
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              :model-value="form.unitOfMeasure"
              label="Unit Of Measure"
              variant="outlined"
              density="comfortable"
              readonly
              hide-details="auto"
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions class="form-actions">
        <v-spacer class="action-spacer" />

        <v-btn class="action-btn" variant="text" :disabled="saving" @click="close">
          Cancel
        </v-btn>

        <v-btn class="action-btn" color="primary" :loading="saving" @click="save">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { useDisplay } from "vuetify";
import type { RawMaterial, UnitOfMeasure } from "@/types/RawMaterial";
import type { SaveProductCompositionPayload } from "@/types/ProductComposition";

type CompositionFormState = {
  rawMaterialId: number | null
  quantityRequired: number
  unitOfMeasure: UnitOfMeasure
};

const props = defineProps<{
  modelValue: boolean
  productId: number | null
  rawMaterials: RawMaterial[]
  saving: boolean
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void
  (e: "submit", payload: SaveProductCompositionPayload): void
  (e: "error", message: string): void
}>();

const dialog = ref(false);
const { smAndDown } = useDisplay();

const form = ref<CompositionFormState>({
  rawMaterialId: null,
  quantityRequired: 1,
  unitOfMeasure: "UNIT",
});

const rawMaterialOptions = computed(() =>
  props.rawMaterials.map((rawMaterial) => ({
    title: rawMaterial.name,
    value: rawMaterial.code as number,
  })),
);

const selectedRawMaterial = computed(() =>
  props.rawMaterials.find((rawMaterial) => rawMaterial.code === form.value.rawMaterialId),
);

watch(
  () => props.modelValue,
  (value) => {
    dialog.value = value;

    if (value) {
      resetForm();
    }
  },
);

watch(dialog, (value) => {
  emit("update:modelValue", value);
});

watch(selectedRawMaterial, (value) => {
  if (value?.unitOfMeasure) {
    form.value.unitOfMeasure = value.unitOfMeasure;
  }
});

function resetForm() {
  form.value = {
    rawMaterialId: null,
    quantityRequired: 1,
    unitOfMeasure: "UNIT",
  };
}

function close() {
  dialog.value = false;
}

const requiredNumberRule = (value: number | null) =>
  Boolean(value) || "Raw material is required";
const quantityRule = (value: number) => value > 0 || "Quantity must be greater than zero";

function save() {
  if (!props.productId || !form.value.rawMaterialId || form.value.quantityRequired <= 0) {
    emit("error", "Please fill in the required fields correctly.");
    return;
  }

  emit("submit", {
    productId: props.productId,
    rawMaterialId: form.value.rawMaterialId,
    quantityRequired: form.value.quantityRequired,
  });
}
</script>

<style scoped>
.composition-form-card {
  border: 1px solid rgba(255, 255, 255, 0.12);
  background:
    linear-gradient(150deg, rgba(25, 118, 210, 0.18), rgba(14, 165, 136, 0.1)),
    rgba(11, 18, 30, 0.9);
  margin: 10px;
}

.form-title {
  padding: 24px 28px 10px;
}

.form-kicker {
  margin: 0;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  opacity: 0.7;
}

.form-title h3 {
  margin: 4px 0 0;
  font-size: 1.25rem;
}

.form-content {
  padding: 14px 28px 20px;
}

.form-actions {
  padding: 0 28px 24px;
}

@media (max-width: 600px) {
  .form-actions {
    display: grid;
    grid-template-columns: 1fr;
    gap: 8px;
    padding: 0 20px 22px;
  }

  .action-spacer {
    display: none;
  }

  .action-btn {
    width: 100%;
  }
}
</style>
