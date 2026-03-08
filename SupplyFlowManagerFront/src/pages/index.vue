<template>
  <div class="index-page">
    <v-progress-circular indeterminate color="primary" />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

onMounted(() => {
  router.replace("/products");
});
</script>

<style scoped>
.index-page {
  min-height: 40vh;
  display: grid;
  place-items: center;
}
</style>
