<template>
  <v-dialog
    v-model="dialog"
    :fullscreen="smAndDown"
    :max-width="smAndDown ? undefined : 560"
    scrim="rgba(3, 8, 16, 0.86)"
    scrollable
  >
    <v-card class="product-form-card">
      <v-card-title class="form-title">
        <div>
          <p class="form-kicker">{{ t("products.formKicker") }}</p>
          <h3>{{ props.product ? t("products.formEditTitle") : t("products.formNewTitle") }}</h3>
        </div>
      </v-card-title>

      <v-card-text class="form-content">
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="form.name"
              :label="t('products.fieldName')"
              variant="outlined"
              density="comfortable"
              hide-details="auto"
              :rules="[requiredRule]"
            />
          </v-col>

          <v-col cols="12">
            <v-text-field
              v-model.number="form.price"
              :label="t('products.fieldPrice')"
              type="number"
              min="0"
              step="0.01"
              variant="outlined"
              density="comfortable"
              prefix="R$"
              hide-details="auto"
              :rules="[priceRule]"
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions class="form-actions">
        <v-spacer class="action-spacer" />

        <v-btn class="action-btn" variant="text" @click="close">
          {{ t("common.cancel") }}
        </v-btn>

        <v-btn class="action-btn" color="primary" :loading="saving" @click="save">
          {{ t("common.save") }}
        </v-btn>

      </v-card-actions>

    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">

import { watch, ref } from "vue"
import type { Product } from "@/types/Product"
import { postProduct, putProduct } from "@/services/productService";
import { useDisplay } from "vuetify";
import { useI18n } from "vue-i18n";
import { resolveApiErrorMessage } from "@/utils/apiError";

const props = defineProps<{
  modelValue: boolean
  product?: Product | null
}>()

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void
  (e: "save", product: Product): void
  (e: "error", message: string): void
}>()

const dialog = ref(false)
const { smAndDown } = useDisplay();
const { t } = useI18n();
const saving = ref(false);

const form = ref<Product>({
  name: "",
  price: 0
})

watch(
  () => props.modelValue,
  (val) => {
    dialog.value = val

    if (props.product) {
      form.value = { ...props.product }
    } else {
      form.value = { name: "", price: 0 }
    }
  }
)

watch(dialog, (val) => {
  emit("update:modelValue", val)
})

function close() {
  dialog.value = false
}

const requiredRule = (value: string) => !!value || t("products.nameRequired");
const priceRule = (value: number) => value >= 0 || t("products.priceMustBeNonNegative");

async function save() {
  if (!form.value.name?.trim() || form.value.price < 0) {
    emit("error", t("common.fillRequiredFields"));
    return;
  }

  saving.value = true;
  try {
    const savedProduct = props.product?.code
      ? await putProduct(form.value)
      : await postProduct(form.value);

    emit("save", savedProduct);
    close();
  } catch (error) {
    console.error("Error saving product:", error);
    emit("error", resolveApiErrorMessage(error, t, {
      defaultKey: props.product?.code ? "products.couldNotUpdate" : "products.couldNotSave",
    }));
  } finally {
    saving.value = false;
  }
}

</script>

<style scoped>
.product-form-card {
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
