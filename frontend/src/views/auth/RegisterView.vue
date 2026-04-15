<template>
  <div class="register-page">
    <div class="register-brand">
      <div class="brand-inner">
        <div class="brand-logo">🏉</div>
        <h1 class="brand-title">Join the<br>Pirates</h1>
        <p class="brand-tagline">Create your account and start your AI-powered training journey.</p>
        <div class="step-progress">
          <div v-for="(step, i) in steps" :key="i" class="step-indicator" :class="{ 'is-active': currentStep === i + 1, 'is-complete': currentStep > i + 1 }">
            <div class="step-dot">
              <span v-if="currentStep > i + 1">✓</span>
              <span v-else>{{ i + 1 }}</span>
            </div>
            <span class="step-label">{{ step }}</span>
          </div>
        </div>
        <p class="brand-credit">Universiti Teknologi Malaysia</p>
      </div>
    </div>

    <div class="register-form-panel">
      <div class="register-card">
        <RouterLink to="/login" class="back-link">← Back to sign in</RouterLink>

        <div class="card-header">
          <h2 class="card-title">Create account</h2>
          <p class="card-subtitle">Step {{ currentStep }} of {{ steps.length }} — {{ steps[currentStep - 1] }}</p>
        </div>

        <div v-if="errorMsg" class="alert alert-error">
          <span>✕</span> {{ errorMsg }}
        </div>

        <!-- STEP 1: Personal Info -->
        <div v-if="currentStep === 1" class="step-body">
          <div class="form-group" :class="{ 'has-error': errors.fullName }">
            <label class="form-label">Full name</label>
            <div class="input-wrapper">
              <span class="input-icon">👤</span>
              <input v-model="form.fullName" type="text" class="form-input" placeholder="e.g. Muhammad Danial Syafiq" @input="clearError('fullName')" />
            </div>
            <p v-if="errors.fullName" class="field-error">{{ errors.fullName }}</p>
          </div>

          <div class="form-group" :class="{ 'has-error': errors.email }">
            <label class="form-label">Email address</label>
            <div class="input-wrapper">
              <span class="input-icon">✉</span>
              <input v-model="form.email" type="email" class="form-input" placeholder="you@utm.my" @input="clearError('email')" />
            </div>
            <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
          </div>

          <div class="form-group">
            <label class="form-label">Phone number <span class="optional-tag">optional</span></label>
            <div class="input-wrapper">
              <span class="input-icon">📱</span>
              <input v-model="form.phoneNumber" type="tel" class="form-input" placeholder="+601X XXXX XXXX" />
            </div>
          </div>

          <button type="button" class="btn-next" @click="nextStep">Continue →</button>
        </div>

        <!-- STEP 2: Credentials -->
        <div v-if="currentStep === 2" class="step-body">
          <div class="form-group" :class="{ 'has-error': errors.username }">
            <label class="form-label">Username</label>
            <div class="input-wrapper">
              <span class="input-icon">@</span>
              <input v-model="form.username" type="text" class="form-input" placeholder="letters, numbers, underscores" autocapitalize="none" @input="clearError('username')" />
            </div>
            <p v-if="errors.username" class="field-error">{{ errors.username }}</p>
          </div>

          <div class="form-group" :class="{ 'has-error': errors.password }">
            <label class="form-label">Password</label>
            <div class="input-wrapper">
              <span class="input-icon">🔒</span>
              <input v-model="form.password" :type="showPassword ? 'text' : 'password'" class="form-input" placeholder="Min. 8 chars, uppercase + number" @input="clearError('password')" />
              <button type="button" class="input-toggle" @click="showPassword = !showPassword">{{ showPassword ? '🙈' : '👁️' }}</button>
            </div>
            <div v-if="form.password" class="strength-bar-wrap">
              <div class="strength-bar"><div class="strength-fill" :class="passwordStrength.cls" :style="{ width: passwordStrength.pct + '%' }"></div></div>
              <span class="strength-label" :class="passwordStrength.cls">{{ passwordStrength.label }}</span>
            </div>
            <p v-if="errors.password" class="field-error">{{ errors.password }}</p>
          </div>

          <div class="form-group" :class="{ 'has-error': errors.confirmPassword }">
            <label class="form-label">Confirm password</label>
            <div class="input-wrapper">
              <span class="input-icon">🔒</span>
              <input v-model="form.confirmPassword" :type="showPassword ? 'text' : 'password'" class="form-input" placeholder="Re-enter your password" @input="clearError('confirmPassword')" />
            </div>
            <p v-if="errors.confirmPassword" class="field-error">{{ errors.confirmPassword }}</p>
          </div>

          <ul class="password-rules">
            <li :class="{ met: form.password.length >= 8 }">{{ form.password.length >= 8 ? '✓' : '○' }} At least 8 characters</li>
            <li :class="{ met: /[A-Z]/.test(form.password) }">{{ /[A-Z]/.test(form.password) ? '✓' : '○' }} One uppercase letter</li>
            <li :class="{ met: /[a-z]/.test(form.password) }">{{ /[a-z]/.test(form.password) ? '✓' : '○' }} One lowercase letter</li>
            <li :class="{ met: /\d/.test(form.password) }">{{ /\d/.test(form.password) ? '✓' : '○' }} One number</li>
          </ul>

          <div class="form-actions actions-split">
            <button type="button" class="btn-back" @click="currentStep = 1">← Back</button>
            <button type="button" class="btn-next" @click="nextStep">Continue →</button>
          </div>
        </div>

        <!-- STEP 3: Role -->
        <div v-if="currentStep === 3" class="step-body">
          <p class="role-prompt">Select your role in the UTM Pirates system:</p>

          <div class="role-grid">
            <button type="button" class="role-card" :class="{ selected: form.userRole === 'ATHLETE' }" @click="form.userRole = 'ATHLETE'; errorMsg = ''">
              <div class="role-icon">🏉</div>
              <div class="role-name">Athlete</div>
              <div class="role-desc">Access AI meal plans, workout programs, chatbot, and trainer booking.</div>
              <div v-if="form.userRole === 'ATHLETE'" class="role-check">✓</div>
            </button>
            <button type="button" class="role-card" :class="{ selected: form.userRole === 'TRAINER' }" @click="form.userRole = 'TRAINER'; errorMsg = ''">
              <div class="role-icon">🏋️</div>
              <div class="role-name">Trainer</div>
              <div class="role-desc">Manage athlete appointments, review training plans, and provide guidance.</div>
              <div v-if="form.userRole === 'TRAINER'" class="role-check">✓</div>
            </button>
          </div>

          <p v-if="errors.userRole" class="field-error role-error">{{ errors.userRole }}</p>

          <div class="summary-box">
            <p class="summary-title">Account summary</p>
            <div class="summary-row"><span class="summary-key">Name</span><span class="summary-val">{{ form.fullName || '—' }}</span></div>
            <div class="summary-row"><span class="summary-key">Email</span><span class="summary-val">{{ form.email || '—' }}</span></div>
            <div class="summary-row"><span class="summary-key">Username</span><span class="summary-val">{{ form.username || '—' }}</span></div>
          </div>

          <div class="form-actions actions-split">
            <button type="button" class="btn-back" @click="currentStep = 2">← Back</button>
            <button type="button" class="btn-next" :disabled="loading" @click="handleRegister">
              <span v-if="loading">Creating...</span>
              <span v-else>Create account</span>
            </button>
          </div>
        </div>

        <p class="signin-link">Already have an account? <RouterLink to="/login" class="signin-anchor">Sign in</RouterLink></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const steps = ['Personal Info', 'Credentials', 'Role & Review']
const currentStep = ref(1)
const showPassword = ref(false)
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  fullName: '',
  email: '',
  phoneNumber: '',
  username: '',
  password: '',
  confirmPassword: '',
  userRole: null
})

const errors = reactive({
  fullName: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  userRole: ''
})

function clearError(field) {
  errors[field] = ''
  errorMsg.value = ''
}

const passwordStrength = computed(() => {
  const p = form.password
  if (!p) return { pct: 0, label: '', cls: '' }
  let score = 0
  if (p.length >= 8) score++
  if (p.length >= 12) score++
  if (/[A-Z]/.test(p)) score++
  if (/[a-z]/.test(p)) score++
  if (/\d/.test(p)) score++
  if (/[^a-zA-Z0-9]/.test(p)) score++
  if (score <= 2) return { pct: 33, label: 'Weak', cls: 'strength-weak' }
  if (score <= 4) return { pct: 66, label: 'Fair', cls: 'strength-fair' }
  return { pct: 100, label: 'Strong', cls: 'strength-strong' }
})

function validateStep1() {
  let valid = true
  errors.fullName = ''
  errors.email = ''
  if (!form.fullName.trim()) { errors.fullName = 'Full name is required'; valid = false }
  else if (form.fullName.trim().length < 2) { errors.fullName = 'Full name must be at least 2 characters'; valid = false }
  if (!form.email.trim()) { errors.email = 'Email is required'; valid = false }
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) { errors.email = 'Please enter a valid email address'; valid = false }
  return valid
}

function validateStep2() {
  let valid = true
  errors.username = ''
  errors.password = ''
  errors.confirmPassword = ''
  if (!form.username.trim()) { errors.username = 'Username is required'; valid = false }
  else if (form.username.length < 3) { errors.username = 'Username must be at least 3 characters'; valid = false }
  else if (!/^[a-zA-Z0-9_]+$/.test(form.username)) { errors.username = 'Username can only contain letters, numbers and underscores'; valid = false }
  if (!form.password) { errors.password = 'Password is required'; valid = false }
  else if (form.password.length < 8) { errors.password = 'Password must be at least 8 characters'; valid = false }
  else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(form.password)) { errors.password = 'Password must contain uppercase, lowercase, and a number'; valid = false }
  if (!form.confirmPassword) { errors.confirmPassword = 'Please confirm your password'; valid = false }
  else if (form.password !== form.confirmPassword) { errors.confirmPassword = 'Passwords do not match'; valid = false }
  return valid
}

function nextStep() {
  if (currentStep.value === 1 && validateStep1()) currentStep.value = 2
  else if (currentStep.value === 2 && validateStep2()) currentStep.value = 3
}

async function handleRegister() {
  errors.userRole = ''
  if (!form.userRole) { errors.userRole = 'Please select a role to continue.'; return }
  loading.value = true
  errorMsg.value = ''
  const { fieldErrors } = await authStore.register({
    fullName: form.fullName,
    email: form.email,
    phoneNumber: form.phoneNumber || null,
    username: form.username,
    password: form.password,
    confirmPassword: form.confirmPassword,
    userRole: form.userRole
  })
  loading.value = false
  if (authStore.error) errorMsg.value = authStore.error
  if (fieldErrors) {
    Object.keys(fieldErrors).forEach(k => { if (errors[k] !== undefined) errors[k] = fieldErrors[k] })
  }
}
</script>

<style scoped>
.register-page { display: grid; grid-template-columns: 1fr 1fr; min-height: 100vh; width: 100%; max-width: 1100px; margin: 0 auto; border-radius: 20px; overflow: hidden; box-shadow: 0 8px 32px rgba(0,0,0,0.6); }
.register-brand { background: #0f4d2b; display: flex; align-items: center; justify-content: center; padding: 48px 40px; }
.brand-inner { max-width: 300px; }
.brand-logo { font-size: 52px; display: block; margin-bottom: 16px; }
.brand-title { font-family: 'Barlow Condensed', sans-serif; font-size: 42px; font-weight: 900; line-height: 1.05; color: #e8f0ea; margin-bottom: 12px; }
.brand-tagline { font-size: 14px; color: #a8c0ae; line-height: 1.65; margin-bottom: 40px; }
.step-progress { display: flex; flex-direction: column; gap: 20px; margin-bottom: 44px; }
.step-indicator { display: flex; align-items: center; gap: 14px; }
.step-dot { width: 32px; height: 32px; border-radius: 50%; background: rgba(255,255,255,0.1); border: 1.5px solid rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; color: #6b8574; flex-shrink: 0; transition: all 0.25s; }
.step-indicator.is-active .step-dot { background: #1a7a45; border-color: #22a85c; color: white; }
.step-indicator.is-complete .step-dot { background: #0f4d2b; border-color: #1a7a45; color: #22a85c; }
.step-label { font-size: 13px; color: #6b8574; }
.step-indicator.is-active .step-label { color: #e8f0ea; font-weight: 500; }
.step-indicator.is-complete .step-label { color: #22a85c; }
.brand-credit { font-size: 12px; color: #6b8574; }
.register-form-panel { background: #141a16; display: flex; align-items: center; justify-content: center; padding: 40px; overflow-y: auto; }
.register-card { width: 100%; max-width: 420px; }
.back-link { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; color: #6b8574; text-decoration: none; margin-bottom: 24px; }
.back-link:hover { color: #e8f0ea; }
.card-header { margin-bottom: 24px; }
.card-title { font-family: 'Barlow Condensed', sans-serif; font-size: 30px; font-weight: 700; color: #e8f0ea; margin-bottom: 4px; }
.card-subtitle { font-size: 13.5px; color: #6b8574; }
.alert { display: flex; align-items: flex-start; gap: 10px; padding: 12px 14px; border-radius: 10px; font-size: 13.5px; line-height: 1.5; margin-bottom: 20px; }
.alert-error { background: rgba(200,50,50,0.12); border: 1px solid rgba(200,50,50,0.25); color: #e07070; }
.step-body { display: flex; flex-direction: column; gap: 16px; margin-bottom: 24px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 12.5px; font-weight: 500; color: #a8c0ae; }
.optional-tag { font-size: 11px; color: #6b8574; font-weight: 400; margin-left: 4px; }
.input-wrapper { position: relative; display: flex; align-items: center; }
.input-icon { position: absolute; left: 12px; font-size: 14px; pointer-events: none; color: #6b8574; }
.form-input { width: 100%; padding: 11px 14px 11px 38px; background: #212c24; border: 1px solid rgba(255,255,255,0.14); border-radius: 10px; color: #e8f0ea; font-size: 14.5px; font-family: inherit; outline: none; transition: border-color 0.18s; }
.form-input::placeholder { color: #6b8574; }
.form-input:focus { border-color: #1a7a45; box-shadow: 0 0 0 3px rgba(26,122,69,0.12); }
.has-error .form-input { border-color: #e07070; }
.input-toggle { position: absolute; right: 10px; background: none; border: none; cursor: pointer; font-size: 15px; padding: 4px; }
.strength-bar-wrap { display: flex; align-items: center; gap: 10px; }
.strength-bar { flex: 1; height: 4px; background: #2a3830; border-radius: 99px; overflow: hidden; }
.strength-fill { height: 100%; border-radius: 99px; transition: width 0.3s; }
.strength-label { font-size: 11px; font-weight: 500; white-space: nowrap; }
.strength-weak { background: #e07070; color: #e07070; }
.strength-fair { background: #f0be35; color: #f0be35; }
.strength-strong { background: #22a85c; color: #22a85c; }
.field-error { font-size: 12px; color: #e07070; padding-left: 2px; }
.password-rules { list-style: none; display: grid; grid-template-columns: 1fr 1fr; gap: 6px 12px; padding: 12px 14px; background: #212c24; border-radius: 10px; font-size: 12px; color: #6b8574; }
.password-rules li { display: flex; align-items: center; gap: 6px; transition: color 0.2s; }
.password-rules li.met { color: #22a85c; }
.role-prompt { font-size: 13.5px; color: #a8c0ae; }
.role-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.role-card { position: relative; background: #212c24; border: 1.5px solid rgba(255,255,255,0.14); border-radius: 14px; padding: 20px 16px; text-align: center; cursor: pointer; transition: border-color 0.18s; font-family: inherit; width: 100%; }
.role-card:hover { border-color: #1a7a45; }
.role-card.selected { border-color: #22a85c; background: rgba(26,122,69,0.12); }
.role-icon { font-size: 28px; display: block; margin-bottom: 10px; }
.role-name { font-size: 15px; font-weight: 600; color: #e8f0ea; margin-bottom: 8px; }
.role-desc { font-size: 12px; color: #6b8574; line-height: 1.6; }
.role-check { position: absolute; top: 10px; right: 12px; font-size: 14px; color: #22a85c; font-weight: 700; }
.role-error { text-align: center; }
.summary-box { background: #212c24; border: 1px solid rgba(255,255,255,0.08); border-radius: 10px; padding: 14px 16px; }
.summary-title { font-size: 11px; font-weight: 500; text-transform: uppercase; letter-spacing: 1px; color: #6b8574; margin-bottom: 10px; }
.summary-row { display: flex; justify-content: space-between; padding: 5px 0; font-size: 13px; }
.summary-key { color: #6b8574; }
.summary-val { color: #e8f0ea; font-weight: 500; }
.form-actions { display: flex; justify-content: flex-end; }
.actions-split { justify-content: space-between; }
.btn-next { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 12px 28px; background: #1a7a45; color: white; border: none; border-radius: 10px; font-size: 15px; font-weight: 600; font-family: inherit; cursor: pointer; transition: background 0.18s; min-width: 160px; }
.btn-next:hover:not(:disabled) { background: #22a85c; }
.btn-next:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-back { padding: 12px 20px; background: transparent; border: 1px solid rgba(255,255,255,0.14); border-radius: 10px; color: #a8c0ae; font-size: 14px; font-weight: 500; font-family: inherit; cursor: pointer; }
.btn-back:hover { background: #212c24; color: #e8f0ea; }
.signin-link { text-align: center; font-size: 13.5px; color: #6b8574; margin-top: 20px; }
.signin-anchor { color: #22a85c; text-decoration: none; font-weight: 500; }
@media (max-width: 768px) { .register-page { grid-template-columns: 1fr; } .register-brand { padding: 28px 24px; } .brand-title { font-size: 34px; } .step-progress { flex-direction: row; } .step-label { display: none; } .register-form-panel { padding: 32px 20px; } .role-grid { grid-template-columns: 1fr; } .password-rules { grid-template-columns: 1fr; } }
</style>
