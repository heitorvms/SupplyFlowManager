<template>
  <v-snackbar
    :model-value="modelValue"
    :color="color"
    class="feedback-snackbar"
    variant="elevated"
    rounded="lg"
    elevation="24"
    location="top right"
    :timeout="timeout"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <div class="snackbar-content">
      <v-icon
        :icon="color === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle'"
        size="20"
      />
      <span>{{ text }}</span>
    </div>

    <template #actions>
      <v-btn
        icon="mdi-close"
        variant="text"
        density="comfortable"
        @click="emit('update:modelValue', false)"
      />
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    modelValue: boolean;
    text: string;
    color?: string;
    timeout?: number;
  }>(),
  {
    color: "success",
    timeout: 3200,
  },
);

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
}>();
</script>

<style scoped>
.feedback-snackbar :deep(.v-snackbar__wrapper) {
  margin: 14px;
  min-width: min(420px, calc(100vw - 28px));
  max-width: min(520px, calc(100vw - 28px));
  padding: 4px 6px;
  border: 1px solid rgba(255, 255, 255, 0.35);
  box-shadow: 0 18px 45px rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(6px);
}

.snackbar-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 6px;
  font-weight: 600;
  line-height: 1.35;
  letter-spacing: 0.01em;
  color: #fff;
}

.feedback-snackbar :deep(.v-snackbar__actions) {
  margin-inline-start: 2px;
  padding-inline-start: 2px;
}
</style>
