<template>
  <v-dialog
    v-model="dialog"
    :fullscreen="smAndDown"
    :max-width="smAndDown ? undefined : 560"
    scrim="rgba(3, 8, 16, 0.86)"
    scrollable
  >
    <v-card class="raw-material-form-card">
      <v-card-title class="form-title">
        <div>
          <p class="form-kicker">Raw Material</p>
          <h3>{{ props.rawMaterial ? "Edit Raw Material" : "New Raw Material" }}</h3>
        </div>
      </v-card-title>

      <v-card-text class="form-content">
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="form.name"
              label="Name"
              variant="outlined"
              density="comfortable"
              hide-details="auto"
              :rules="[requiredRule]"
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              v-model.number="form.stockQuantity"
              label="Stock Quantity"
              type="number"
              min="0"
              step="0.001"
              variant="outlined"
              density="comfortable"
              hide-details="auto"
              :rules="[quantityRule]"
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-autocomplete
              v-model="form.unitOfMeasure"
              v-model:search="unitSearch"
              :items="unitOptions"
              item-title="title"
              item-value="value"
              label="Unit Type"
              variant="outlined"
              density="comfortable"
              hide-details="auto"
              :disabled="unitOptions.length === 0"
              no-data-text="Nenhuma unidade carregada"
              :rules="[requiredRule]"
              auto-select-first
              @keydown="onUnitInputKeydown"
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions class="form-actions">
        <v-spacer class="action-spacer" />

        <v-btn class="action-btn" variant="text" @click="close">
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
import { onMounted, ref, watch } from "vue";
import { useDisplay } from "vuetify";
import type { RawMaterial, UnitOfMeasure } from "@/types/RawMaterial";
import { postRawMaterial, putRawMaterial } from "@/services/rawMaterialService";
import { getUnitOfMeasureEnumsCached } from "@/services/enumService";

const props = defineProps<{
  modelValue: boolean
  rawMaterial?: RawMaterial | null
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void
  (e: "save", rawMaterial: RawMaterial): void
  (e: "error", message: string): void
}>();

const dialog = ref(false);
const saving = ref(false);
const { smAndDown } = useDisplay();

const form = ref<RawMaterial>({
  name: "",
  stockQuantity: 0,
  unitOfMeasure: "UNIT",
});
const unitSearch = ref("");
const unitOptions = ref<Array<{ title: string; value: UnitOfMeasure }>>([]);

function formatUnitLabel(unit: UnitOfMeasure) {
  return unit.replace(/_/g, " ").toLowerCase().replace(/\b\w/g, (letter: string) => letter.toUpperCase());
}

function onUnitInputKeydown(event: KeyboardEvent) {
  const allowedKeys = new Set(["ArrowDown", "ArrowUp", "Enter", "Escape", "Tab"]);

  if (!allowedKeys.has(event.key)) {
    event.preventDefault();
  }
}

onMounted(async () => {
  try {
    const units = await getUnitOfMeasureEnumsCached();
    unitOptions.value = units.map((unit) => ({
      title: formatUnitLabel(unit),
      value: unit,
    }));

    const firstUnit = unitOptions.value[0];
    if (firstUnit && !unitOptions.value.some((option) => option.value === form.value.unitOfMeasure)) {
      form.value.unitOfMeasure = firstUnit.value;
    }
  } catch (error) {
    console.error("Error loading unit enums:", error);
    emit("error", "Nao foi possivel carregar os enums de unidade.");
  }
});

watch(
  () => props.modelValue,
  (val) => {
    dialog.value = val;

    if (props.rawMaterial) {
      form.value = { ...props.rawMaterial };
    } else {
      form.value = { name: "", stockQuantity: 0, unitOfMeasure: "UNIT" };
    }
  },
);

watch(dialog, (val) => {
  emit("update:modelValue", val);
});

function close() {
  dialog.value = false;
}

const requiredRule = (value: string) => !!value || "Field is required";
const quantityRule = (value: number) => value >= 0 || "Quantity must be non-negative";

async function save() {
  if (!form.value.name?.trim() || !form.value.unitOfMeasure?.trim() || form.value.stockQuantity < 0) {
    emit("error", "Preencha os campos obrigatorios corretamente.");
    return;
  }

  saving.value = true;
  try {
    const savedRawMaterial = props.rawMaterial?.code
      ? await putRawMaterial(form.value)
      : await postRawMaterial(form.value);

    emit("save", savedRawMaterial);
    close();
  } catch (error) {
    console.error("Error saving raw material:", error);
    emit(
      "error",
      props.rawMaterial?.code
        ? "Nao foi possivel atualizar a materia-prima. Tente novamente."
        : "Nao foi possivel salvar a materia-prima. Tente novamente.",
    );
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.raw-material-form-card {
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
