<template>
  <!-- App Layout: sidebar + header + main content -->
  <!-- Used by all authenticated routes -->
  <div class="app-layout">

    <!-- ── Sidebar ──────────────────────────────────── -->
    <aside class="sidebar">
      <!-- Logo -->
      <div class="sidebar-logo">
        <div class="logo-mark">🏉</div>
        <div class="logo-text">
          <span class="logo-title">Rugby AI</span>
          <span class="logo-sub">UTM Pirates</span>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="sidebar-nav">
        <!-- Athlete nav -->
        <template v-if="authStore.isAthlete">
          <p class="nav-section-label">Main</p>
          <RouterLink to="/dashboard"    class="nav-item"><span class="nav-icon">📊</span>Dashboard</RouterLink>
          <RouterLink to="/profile"      class="nav-item"><span class="nav-icon">👤</span>My Profile</RouterLink>

          <p class="nav-section-label">AI Features</p>
          <RouterLink to="/chatbot"      class="nav-item"><span class="nav-icon">💬</span>AI Chatbot</RouterLink>
          <RouterLink to="/meal-planner" class="nav-item"><span class="nav-icon">🥗</span>Meal Planner</RouterLink>
          <RouterLink to="/workout"      class="nav-item"><span class="nav-icon">🏋️</span>Workout Planner</RouterLink>

          <p class="nav-section-label">Manage</p>
          <RouterLink to="/appointments" class="nav-item"><span class="nav-icon">📅</span>Appointments</RouterLink>
        </template>

        <!-- Trainer nav -->
        <template v-if="authStore.isTrainer">
          <p class="nav-section-label">Trainer</p>
          <RouterLink to="/trainer"      class="nav-item"><span class="nav-icon">📊</span>Dashboard</RouterLink>
          <RouterLink to="/profile"      class="nav-item"><span class="nav-icon">👤</span>My Profile</RouterLink>
          <RouterLink to="/appointments" class="nav-item"><span class="nav-icon">📅</span>Appointments</RouterLink>
        </template>
      </nav>

      <!-- User chip at bottom -->
      <div class="sidebar-footer">
        <div class="user-chip">
          <div class="avatar">
            <img v-if="authStore.profilePicture" :src="authStore.profilePicture" alt="Profile" class="avatar-img" />
            <span v-else>{{ initials }}</span>
          </div>
          <div class="user-info">
            <p class="user-name">{{ authStore.fullName }}</p>
            <p class="user-role">{{ authStore.userRole }}</p>
          </div>
        </div>
      </div>
    </aside>

    <!-- ── Main Area ─────────────────────────────────── -->
    <div class="main-area">
      <!-- Header -->
      <header class="app-header">
        <h1 class="page-title">{{ pageTitle }}</h1>
        <div class="header-actions">
          <RouterLink to="/chatbot" class="btn btn-ghost">💬 Ask AI</RouterLink>
          <button class="btn btn-danger-ghost" @click="handleLogout">
            Sign Out
          </button>
        </div>
      </header>

      <!-- Page content -->
      <main class="page-content">
        <RouterView v-slot="{ Component }">
          <Transition name="page" mode="out-in">
            <component :is="Component" />
          </Transition>
        </RouterView>
      </main>
    </div>

  </div>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const route     = useRoute()

const initials = computed(() => {
  const name = authStore.fullName || authStore.username || '?'
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
})

const pageTitles = {
  Dashboard:       'Dashboard',
  TrainerDashboard:'Trainer Dashboard',
  MealPlanner:     'Meal Planner',
  Workout:         'Workout Planner',
  Chatbot:         'AI Chatbot',
  Appointments:    'Appointments',
  Profile:         'My Profile'
}

const pageTitle = computed(() => pageTitles[route.name] || 'Rugby AI Planner')

async function handleLogout() {
  await authStore.logout()
}
</script>

<style scoped>
.app-layout {
  display: grid;
  grid-template-columns: var(--sidebar-width) 1fr;
  min-height: 100vh;
}

/* ── Sidebar ─────────────────────────────────────────── */
.sidebar {
  background: var(--color-bg-2);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px;
  border-bottom: 1px solid var(--color-border);
}

.logo-mark {
  width: 36px; height: 36px;
  background: var(--color-green);
  border-radius: 9px;
  display: flex; align-items: center; justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-title {
  font-family: 'Barlow Condensed', sans-serif;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.4px;
  color: var(--color-text);
  line-height: 1.1;
}

.logo-sub {
  font-size: 11px;
  color: var(--color-muted);
}

.sidebar-nav {
  flex: 1;
  padding: 14px 10px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-section-label {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 1.4px;
  text-transform: uppercase;
  color: var(--color-muted);
  padding: 14px 8px 5px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 9px 12px;
  border-radius: var(--radius-sm);
  font-size: 13.5px;
  color: var(--color-text-dim);
  text-decoration: none;
  transition: background var(--transition), color var(--transition);
}

.nav-item:hover {
  background: var(--color-surface);
  color: var(--color-text);
}

.nav-item.router-link-active {
  background: var(--color-surface-2);
  color: var(--color-green-light);
  font-weight: 500;
}

.nav-icon {
  font-size: 15px;
  width: 18px;
  text-align: center;
}

.sidebar-footer {
  padding: 14px;
  border-top: 1px solid var(--color-border);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  background: var(--color-surface);
  border-radius: var(--radius-sm);
}

.avatar {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: var(--color-green-dim);
  color: var(--color-green-light);
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600;
  flex-shrink: 0;
  overflow: hidden;
}

.avatar-img {
  width: 100%; height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.user-info { min-width: 0; }

.user-name {
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 11px;
  color: var(--color-muted);
}

/* ── Main Area ───────────────────────────────────────── */
.main-area {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  overflow: hidden;
}

.app-header {
  height: var(--header-height);
  background: var(--color-bg-2);
  border-bottom: 1px solid var(--color-border);
  padding: 0 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-shrink: 0;
}

.page-title {
  font-family: 'Barlow Condensed', sans-serif;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.4px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-content {
  flex: 1;
  padding: 28px;
  overflow-y: auto;
}

/* ── Buttons (global in app) ─────────────────────────── */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  border: none;
  text-decoration: none;
  transition: background var(--transition);
}

.btn-ghost {
  background: var(--color-surface);
  color: var(--color-text);
}

.btn-ghost:hover { background: var(--color-surface-2); }

.btn-danger-ghost {
  background: transparent;
  color: var(--color-muted);
  border: 1px solid var(--color-border);
}

.btn-danger-ghost:hover {
  background: var(--color-error-bg);
  color: var(--color-error);
  border-color: var(--color-error-border);
}
</style>