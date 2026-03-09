<template>
  <v-dialog v-model="dialog" max-width="560" scrim="rgba(3, 8, 16, 0.86)">
    <v-card class="delete-card" rounded="xl">
      <v-card-title class="delete-title">
        <div class="title-wrap">
          <v-avatar color="error" variant="tonal" size="40">
            <v-icon icon="mdi-alert-outline" />
          </v-avatar>
          <div>
            <p class="title-kicker">Confirmation</p>
            <h3>Delete Composition Item</h3>
          </div>
        </div>
      </v-card-title>

      <v-card-text class="delete-content">
        <p>
          Are you sure you want to remove
          <strong>{{ item?.rawMaterialName }}</strong
          > from product composition?
        </p>
        <p class="delete-warning">This action cannot be undone.</p>
      </v-card-text>

      <v-card-actions class="delete-actions">
        <v-spacer />

        <v-btn class="action-btn" variant="text" @click="close">Cancel</v-btn>

        <v-btn class="action-btn" color="error" @click="confirm">Delete</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import type { ProductCompositionItem } from "@/types/ProductComposition";

const props = defineProps<{
  modelValue: boolean
  item?: ProductCompositionItem | null
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void
  (e: "confirm", item: ProductCompositionItem): void
}>();

const dialog = ref(false);

watch(
  () => props.modelValue,
  (value) => {
    dialog.value = value;
  },
);

watch(dialog, (value) => {
  emit("update:modelValue", value);
});

function close() {
  dialog.value = false;
}

function confirm() {
  if (props.item) {
    emit("confirm", props.item);
  }

  close();
}
</script>

<style scoped>
.delete-card {
  border: 1px solid rgba(255, 255, 255, 0.12);
  background:
    linear-gradient(160deg, rgba(220, 38, 38, 0.16), rgba(120, 53, 15, 0.08)),
    rgba(11, 18, 30, 0.94);
}

.delete-title {
  padding: 24px 28px 12px;
}

.title-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-kicker {
  margin: 0;
  font-size: 11px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.7;
}

.title-wrap h3 {
  margin: 4px 0 0;
  font-size: 1.25rem;
}

.delete-content {
  padding: 10px 28px 18px;
  font-size: 1rem;
  line-height: 1.55;
}

.delete-content p {
  margin: 0;
}

.delete-warning {
  margin-top: 10px !important;
  opacity: 0.78;
}

.delete-actions {
  padding: 0 28px 24px;
  gap: 8px;
}

.action-btn {
  min-width: 112px;
}
</style>
