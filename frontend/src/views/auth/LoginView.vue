<template>
  <div class="login-page">
    <div class="login-brand">
      <div class="brand-inner">
        <div class="brand-logo">🏉</div>
        <h1 class="brand-title">Rugby AI<br>Planner</h1>
        <p class="brand-tagline">Personalized nutrition &amp; fitness<br>for UTM Pirates athletes</p>
        <ul class="brand-features">
          <li><span class="feature-icon">🥗</span><span>AI-powered meal plans</span></li>
          <li><span class="feature-icon">🏋️</span><span>Position-specific workout programs</span></li>
          <li><span class="feature-icon">💬</span><span>24/7 AI nutrition chatbot</span></li>
          <li><span class="feature-icon">📅</span><span>Trainer appointment booking</span></li>
        </ul>
        <p class="brand-credit">Universiti Teknologi Malaysia</p>
      </div>
    </div>

    <div class="login-form-panel">
      <div class="login-card">
        <div class="card-header">
          <h2 class="card-title">Sign in</h2>
          <p class="card-subtitle">Enter your credentials to access your dashboard</p>
        </div>

        <div v-if="justRegistered" class="alert alert-success">
          <span>✓</span> Account created successfully! Sign in to get started.
        </div>

        <div v-if="sessionExpired" class="alert alert-warning">
          <span>⚠</span> Your session has expired. Please sign in again.
        </div>

        <div v-if="errorMsg" class="alert alert-error">
          <span>✕</span> {{ errorMsg }}
        </div>

        <div class="login-form">
          <div class="form-group" :class="{ 'has-error': errors.username }">
            <label class="form-label">Username</label>
            <div class="input-wrapper">
              <span class="input-icon">👤</span>
              <input v-model.trim="form.username" type="text" class="form-input" placeholder="Enter your username" autocomplete="username" autocapitalize="none" @input="clearError('username')" />
            </div>
            <p v-if="errors.username" class="field-error">{{ errors.username }}</p>
          </div>

          <div class="form-group" :class="{ 'has-error': errors.password }">
            <label class="form-label">Password</label>
            <div class="input-wrapper">
              <span class="input-icon">🔒</span>
              <input v-model="form.password" :type="showPassword ? 'text' : 'password'" class="form-input" placeholder="Enter your password" autocomplete="current-password" @input="clearError('password')" />
              <button type="button" class="input-toggle" @click="showPassword = !showPassword">{{ showPassword ? '🙈' : '👁️' }}</button>
            </div>
            <p v-if="errors.password" class="field-error">{{ errors.password }}</p>
          </div>

          <button type="button" class="btn-login" :disabled="loading" @click="handleLogin">
            <span v-if="loading" class="btn-spinner"></span>
            <span>{{ loading ? 'Signing in...' : 'Sign in' }}</span>
          </button>
        </div>

        <div class="form-divider"><span>New to Rugby AI Planner?</span></div>
        <RouterLink to="/register" class="btn-register">Create an account</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const route = useRoute()

const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })
const showPassword = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const sessionExpired = ref(false)
const justRegistered = ref(false)

onMounted(() => {
  if (route.query.sessionExpired === 'true') sessionExpired.value = true
  if (route.query.registered === 'true') {
    justRegistered.value = true
    if (route.query.username) form.username = route.query.username
  }
  authStore.clearError()
})

function clearError(field) {
  errors[field] = ''
  errorMsg.value = ''
}

function validate() {
  let valid = true
  errors.username = ''
  errors.password = ''
  if (!form.username.trim()) { errors.username = 'Username is required'; valid = false }
  else if (form.username.length < 3) { errors.username = 'Username must be at least 3 characters'; valid = false }
  if (!form.password) { errors.password = 'Password is required'; valid = false }
  else if (form.password.length < 6) { errors.password = 'Password must be at least 6 characters'; valid = false }
  return valid
}

async function handleLogin() {
  if (!validate()) return
  loading.value = true
  errorMsg.value = ''
  await authStore.login({ username: form.username, password: form.password })
  loading.value = false
  if (authStore.error) errorMsg.value = authStore.error
}
</script>

<style scoped>
.login-page { display: grid; grid-template-columns: 1fr 1fr; min-height: 100vh; width: 100%; max-width: 1100px; margin: 0 auto; border-radius: 20px; overflow: hidden; box-shadow: 0 8px 32px rgba(0,0,0,0.6); }
.login-brand { background: #0f4d2b; display: flex; align-items: center; justify-content: center; padding: 48px 40px; }
.brand-inner { max-width: 320px; }
.brand-logo { font-size: 52px; margin-bottom: 16px; display: block; }
.brand-title { font-family: 'Barlow Condensed', sans-serif; font-size: 44px; font-weight: 900; line-height: 1.05; color: #e8f0ea; margin-bottom: 12px; }
.brand-tagline { font-size: 15px; color: #a8c0ae; line-height: 1.6; margin-bottom: 36px; }
.brand-features { list-style: none; display: flex; flex-direction: column; gap: 14px; margin-bottom: 40px; }
.brand-features li { display: flex; align-items: center; gap: 12px; font-size: 14px; color: #a8c0ae; }
.feature-icon { width: 32px; height: 32px; background: rgba(255,255,255,0.07); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px; flex-shrink: 0; }
.brand-credit { font-size: 12px; color: #6b8574; }
.login-form-panel { background: #141a16; display: flex; align-items: center; justify-content: center; padding: 48px 40px; }
.login-card { width: 100%; max-width: 400px; }
.card-header { margin-bottom: 28px; }
.card-title { font-family: 'Barlow Condensed', sans-serif; font-size: 32px; font-weight: 700; color: #e8f0ea; margin-bottom: 6px; }
.card-subtitle { font-size: 14px; color: #6b8574; }
.alert { display: flex; align-items: flex-start; gap: 10px; padding: 12px 14px; border-radius: 10px; font-size: 13.5px; line-height: 1.5; margin-bottom: 20px; }
.alert-error { background: rgba(200,50,50,0.12); border: 1px solid rgba(200,50,50,0.25); color: #e07070; }
.alert-success { background: rgba(26,122,69,0.12); border: 1px solid rgba(26,122,69,0.3); color: #22a85c; }
.alert-warning { background: rgba(212,160,23,0.1); border: 1px solid rgba(212,160,23,0.25); color: #f0be35; }
.login-form { display: flex; flex-direction: column; gap: 18px; margin-bottom: 20px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 12.5px; font-weight: 500; color: #a8c0ae; }
.input-wrapper { position: relative; display: flex; align-items: center; }
.input-icon { position: absolute; left: 12px; font-size: 14px; pointer-events: none; color: #6b8574; }
.form-input { width: 100%; padding: 11px 14px 11px 38px; background: #212c24; border: 1px solid rgba(255,255,255,0.14); border-radius: 10px; color: #e8f0ea; font-size: 14.5px; font-family: inherit; outline: none; transition: border-color 0.18s; }
.form-input::placeholder { color: #6b8574; }
.form-input:focus { border-color: #1a7a45; box-shadow: 0 0 0 3px rgba(26,122,69,0.12); }
.has-error .form-input { border-color: #e07070; }
.input-toggle { position: absolute; right: 10px; background: none; border: none; cursor: pointer; font-size: 15px; padding: 4px; }
.field-error { font-size: 12px; color: #e07070; padding-left: 2px; }
.btn-login { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 13px; background: #1a7a45; color: white; border: none; border-radius: 10px; font-size: 15px; font-weight: 600; font-family: inherit; cursor: pointer; transition: background 0.18s; margin-top: 4px; }
.btn-login:hover:not(:disabled) { background: #22a85c; }
.btn-login:disabled { opacity: 0.65; cursor: not-allowed; }
.btn-spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.7s linear infinite; flex-shrink: 0; }
@keyframes spin { to { transform: rotate(360deg); } }
.form-divider { display: flex; align-items: center; gap: 12px; margin: 20px 0 14px; color: #6b8574; font-size: 13px; }
.form-divider::before, .form-divider::after { content: ''; flex: 1; height: 1px; background: rgba(255,255,255,0.08); }
.btn-register { display: block; text-align: center; padding: 11px; background: transparent; border: 1px solid rgba(255,255,255,0.14); border-radius: 10px; color: #a8c0ae; text-decoration: none; font-size: 14px; font-weight: 500; transition: all 0.18s; }
.btn-register:hover { background: #212c24; color: #e8f0ea; border-color: #1a7a45; }
@media (max-width: 768px) { .login-page { grid-template-columns: 1fr; border-radius: 0; } .login-brand { padding: 32px 24px; } .brand-title { font-size: 36px; } .brand-features { display: none; } .login-form-panel { padding: 32px 24px; } }
</style>
