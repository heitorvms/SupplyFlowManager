<template>
  <v-navigation-drawer
    v-model="drawerState"
    :temporary="mobile"
    :permanent="!mobile"
    :rail="!mobile && rail"
    :expand-on-hover="false"
    :width="280"
    :rail-width="86"
    :class="['sidebar', { 'sidebar-rail': !mobile && rail }]"
  >
    <div class="logo-wrap">
      <div class="logo-mark">
        <v-icon :icon="rail && !mobile ? 'mdi-view-dashboard-outline' : 'mdi-factory'" />
      </div>
      <div v-if="!rail || mobile" class="logo-text">
        <h2>SupplyFlow</h2>
      </div>
    </div>

    <div class="menu-section">
      <v-list nav density="comfortable" class="menu-list">
        <v-list-item
          prepend-icon="mdi-cube-outline"
          :title="rail && !mobile ? undefined : 'Products'"
          to="/products"
          rounded="xl"
          @click="handleItemClick"
        />

        <v-list-item
          prepend-icon="mdi-package-variant-closed"
          :title="rail && !mobile ? undefined : 'Raw Materials'"
          to="/raw-materials"
          rounded="xl"
          @click="handleItemClick"
        />
      </v-list>
    </div>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  modelValue: boolean
  mobile: boolean
  rail: boolean
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void
  (e: "toggle-rail"): void
}>();

const drawerState = computed({
  get: () => (props.mobile ? props.modelValue : true),
  set: (value: boolean) => {
    if (props.mobile) {
      emit("update:modelValue", value);
    }
  },
});

function handleItemClick() {
  if (props.mobile) {
    emit("update:modelValue", false);
  }
}
</script>

<style scoped>
.sidebar {
  border-right: 1px solid rgba(255, 255, 255, 0.08) !important;
  background: linear-gradient(180deg, rgba(9, 17, 30, 0.96), rgba(8, 12, 20, 0.96));
  backdrop-filter: blur(10px);
}

.logo-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px 14px;
}

.logo-mark {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  color: #dff5ff;
  background: linear-gradient(135deg, #1976d2, #00b894);
}

.logo-text {
  min-width: 0;
}

.logo-text h2 {
  margin: 0;
  font-size: 16px;
  line-height: 1.2;
}

.logo-text span {
  display: block;
  font-size: 11px;
  opacity: 0.68;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.menu-section {
  padding: 8px 10px;
}

.menu-list :deep(.v-list-item) {
  margin-bottom: 6px;
}

.menu-list :deep(.v-list-item--active) {
  background: linear-gradient(120deg, rgba(25, 118, 210, 0.28), rgba(14, 165, 136, 0.22));
}

.sidebar-footer {
  padding: 12px;
}

.sidebar-rail .logo-wrap {
  justify-content: center;
  padding-inline: 0;
}

.sidebar-rail .menu-section {
  padding-inline: 8px;
}

.sidebar-rail .menu-list :deep(.v-list-item__prepend) {
  width: 100%;
  justify-content: center;
}
</style>
