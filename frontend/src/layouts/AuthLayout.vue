<template>
  <!-- Auth Layout: full-screen, no sidebar or header -->
  <!-- Used by: LoginView, RegisterView -->
  <div class="auth-layout">
    <RouterView v-slot="{ Component }">
      <Transition name="page" mode="out-in">
        <component :is="Component" />
      </Transition>
    </RouterView>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);

  /* Subtle grid background */
  background-image:
    linear-gradient(rgba(26,122,69,0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(26,122,69,0.04) 1px, transparent 1px);
  background-size: 40px 40px;
}
</style>
