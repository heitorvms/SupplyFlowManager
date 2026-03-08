<template>
  <v-app
    class="app-shell"
    :style="{ '--drawer-offset': drawerOffset, '--topbar-height': '72px' }"
  >
    <Sidebar
      v-model="drawer"
      :mobile="mdAndDown"
      :rail="rail"
      @toggle-rail="toggleRail"
    />

    <v-app-bar class="topbar" flat height="72">
      <v-btn
        icon="mdi-menu"
        variant="text"
        class="topbar-btn menu-toggle-btn"
        @click="toggleDrawer"
      />

      <v-toolbar-title class="topbar-title">
        SupplyFlow Manager
      </v-toolbar-title>

      <v-spacer />

    </v-app-bar>

    <v-main class="main-content">
      <v-container fluid class="page-container">
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { useDisplay } from "vuetify";
import Sidebar from '@/components/Sidebar.vue'

const { mdAndDown } = useDisplay();
const DESKTOP_DRAWER_WIDTH = 280;
const DESKTOP_RAIL_WIDTH = 86;

const drawer = ref(!mdAndDown.value);
const rail = ref(false);
const drawerOffset = computed(() => {
  if (mdAndDown.value) {
    return "0px";
  }

  return `${rail.value ? DESKTOP_RAIL_WIDTH : DESKTOP_DRAWER_WIDTH}px`;
});

watch(mdAndDown, (isMobile) => {
  drawer.value = !isMobile;
  rail.value = false;
});

function toggleDrawer() {
  if (mdAndDown.value) {
    drawer.value = !drawer.value;
    return;
  }

  drawer.value = true;
  rail.value = !rail.value;
}

function toggleRail() {
  if (mdAndDown.value) {
    drawer.value = !drawer.value;
    return;
  }

  drawer.value = true;
  rail.value = !rail.value;
}
</script>

<style scoped>
.app-shell {
  background:
    radial-gradient(90% 100% at 10% 0%, rgba(25, 118, 210, 0.18), transparent 65%),
    radial-gradient(90% 100% at 100% 20%, rgba(14, 165, 136, 0.14), transparent 60%),
    #070b12;
}

.topbar {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(8, 13, 22, 0.92);
  backdrop-filter: blur(10px);
  padding-inline-start: var(--drawer-offset);
  transition: padding-inline-start 220ms ease;
  position: sticky;
  top: 0;
  z-index: 1200;
}

.topbar :deep(.v-toolbar__content) {
  padding-inline: 6px 14px;
}

.topbar-title {
  font-weight: 700;
  letter-spacing: 0.02em;
  margin-inline-start: 4px;
}

.topbar-btn {
  color: rgba(255, 255, 255, 0.85);
}

.menu-toggle-btn {
  margin-inline-start: 0;
  margin-inline-end: 6px;
}

.main-content {
  background: transparent;
  padding-inline-start: var(--drawer-offset);
  padding-block-start: calc(var(--topbar-height) + 8px);
  transition: padding-inline-start 220ms ease;
}

.page-container {
  padding: clamp(16px, 2.4vw, 32px);
  max-width: 1400px;
}
</style>
