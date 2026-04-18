<template>
  <div class="workout-page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Workout Planner</h1>
        <p class="page-subtitle">Generate a personalised rugby workout plan powered by AI</p>
      </div>
    </div>

    <div class="workout-layout">

      <!-- ══════════════════════════════════════════
           LEFT: Generate Form + Saved Plans
           ══════════════════════════════════════════ -->
      <div class="form-column">
        <div class="card">
          <div class="card-header">
            <span class="card-icon">🏋️</span>
            <h2 class="card-title">Generate New Plan</h2>
          </div>

          <!-- Error banner -->
          <transition name="fade">
            <div v-if="workoutStore.error" class="alert alert-error">
              <span>✕</span> {{ workoutStore.error }}
              <button class="alert-close" @click="workoutStore.clearError()">×</button>
            </div>
          </transition>

          <form @submit.prevent="handleGenerate" class="generate-form">

            <!-- Plan Name -->
            <div class="form-group">
              <label class="form-label">Plan Name <span class="optional-tag">optional</span></label>
              <input v-model="form.planName" type="text" class="form-input"
                placeholder='e.g. "Pre-Season Power Block"' />
            </div>

            <!-- Rugby Position -->
            <div class="form-group" :class="{ 'has-error': errors.rugbyPosition }">
              <label class="form-label">Rugby Position <span class="req">*</span></label>
              <select v-model="form.rugbyPosition" class="form-select" @change="clearError('rugbyPosition')">
                <option value="" disabled>Select your position</option>
                <optgroup label="Forwards">
                  <option>Loosehead Prop</option>
                  <option>Hooker</option>
                  <option>Tighthead Prop</option>
                  <option>Lock (Left)</option>
                  <option>Lock (Right)</option>
                  <option>Blindside Flanker</option>
                  <option>Openside Flanker</option>
                  <option>Number 8</option>
                </optgroup>
                <optgroup label="Backs">
                  <option>Scrumhalf</option>
                  <option>Flyhalf</option>
                  <option>Inside Centre</option>
                  <option>Outside Centre</option>
                  <option>Left Wing</option>
                  <option>Right Wing</option>
                  <option>Fullback</option>
                </optgroup>
              </select>
              <p v-if="errors.rugbyPosition" class="field-error">{{ errors.rugbyPosition }}</p>
            </div>

            <!-- Goal + Training Level -->
            <div class="form-row">
              <div class="form-group" :class="{ 'has-error': errors.goal }">
                <label class="form-label">Primary Goal <span class="req">*</span></label>
                <select v-model="form.goal" class="form-select" @change="clearError('goal')">
                  <option value="" disabled>Select goal</option>
                  <option value="STRENGTH">💪 Strength</option>
                  <option value="POWER">⚡ Power</option>
                  <option value="ENDURANCE">🏃 Endurance</option>
                  <option value="LEAN">🔥 Lean / Cut</option>
                </select>
                <p v-if="errors.goal" class="field-error">{{ errors.goal }}</p>
              </div>

              <div class="form-group" :class="{ 'has-error': errors.trainingLevel }">
                <label class="form-label">Training Level <span class="req">*</span></label>
                <select v-model="form.trainingLevel" class="form-select" @change="clearError('trainingLevel')">
                  <option value="" disabled>Select level</option>
                  <option value="BEGINNER">🌱 Beginner</option>
                  <option value="INTERMEDIATE">🔶 Intermediate</option>
                  <option value="ADVANCED">🔴 Advanced</option>
                </select>
                <p v-if="errors.trainingLevel" class="field-error">{{ errors.trainingLevel }}</p>
              </div>
            </div>

            <!-- Physical Stats -->
            <div class="form-row three-col">
              <div class="form-group" :class="{ 'has-error': errors.weight }">
                <label class="form-label">Weight (kg) <span class="req">*</span></label>
                <input v-model.number="form.weight" type="number" class="form-input"
                  placeholder="e.g. 90" min="40" max="200" @input="clearError('weight')" />
                <p v-if="errors.weight" class="field-error">{{ errors.weight }}</p>
              </div>

              <div class="form-group" :class="{ 'has-error': errors.height }">
                <label class="form-label">Height (cm) <span class="req">*</span></label>
                <input v-model.number="form.height" type="number" class="form-input"
                  placeholder="e.g. 180" min="140" max="220" @input="clearError('height')" />
                <p v-if="errors.height" class="field-error">{{ errors.height }}</p>
              </div>

              <div class="form-group" :class="{ 'has-error': errors.age }">
                <label class="form-label">Age <span class="req">*</span></label>
                <input v-model.number="form.age" type="number" class="form-input"
                  placeholder="e.g. 22" min="15" max="60" @input="clearError('age')" />
                <p v-if="errors.age" class="field-error">{{ errors.age }}</p>
              </div>
            </div>

            <!-- Sessions Per Week -->
            <div class="form-group" :class="{ 'has-error': errors.sessionsPerWeek }">
              <label class="form-label">Sessions Per Week <span class="req">*</span></label>
              <div class="sessions-selector">
                <button
                  v-for="n in [2, 3, 4, 5, 6, 7]" :key="n"
                  type="button"
                  class="session-btn"
                  :class="{ active: form.sessionsPerWeek === n }"
                  @click="form.sessionsPerWeek = n; clearError('sessionsPerWeek')">
                  {{ n }}
                </button>
              </div>
              <p v-if="errors.sessionsPerWeek" class="field-error">{{ errors.sessionsPerWeek }}</p>
            </div>

            <!-- Injury Notes -->
            <div class="form-group">
              <label class="form-label">Injury / Health Notes <span class="optional-tag">optional</span></label>
              <textarea v-model="form.injuryNotes" class="form-textarea" rows="2"
                placeholder='e.g. "Mild left knee soreness, avoid deep squats"'></textarea>
              <p class="field-hint">AI will avoid exercises that aggravate your condition.</p>
            </div>

            <!-- Submit -->
            <button type="submit" class="btn-generate" :disabled="workoutStore.generating">
              <span v-if="workoutStore.generating" class="spinner"></span>
              <span>{{ workoutStore.generating ? 'Generating your plan...' : '⚡ Generate Workout Plan' }}</span>
            </button>

            <p v-if="workoutStore.generating" class="generating-hint">
              AI is building your personalised plan — this may take 20–60 seconds.
            </p>

          </form>
        </div>

        <!-- Saved Plans List -->
        <div class="card saved-plans-card">
          <div class="card-header">
            <span class="card-icon">📋</span>
            <h2 class="card-title">Saved Plans</h2>
            <span class="plan-count" v-if="workoutStore.plans.length">{{ workoutStore.plans.length }}</span>
          </div>

          <div v-if="workoutStore.loadingPlans" class="loading-state">
            <div class="spinner-muted"></div>
            <p>Loading plans...</p>
          </div>

          <div v-else-if="workoutStore.plans.length === 0" class="empty-state">
            <p>No saved plans yet. Generate your first plan above!</p>
          </div>

          <div v-else class="plans-list">
            <div
              v-for="plan in paginatedPlans"
              :key="plan.id"
              class="plan-item"
              :class="{ active: workoutStore.activePlan?.id === plan.id }"
              @click="workoutStore.setActivePlan(plan)">
              <div class="plan-item-info">
                <p class="plan-item-name">{{ plan.planName }}</p>
                <p class="plan-item-meta">
                  {{ plan.rugbyPosition }} · {{ goalLabel(plan.goal) }} · {{ plan.sessionsPerWeek }}×/week
                </p>
                <p class="plan-item-date">{{ formatDate(plan.createdAt) }}</p>
              </div>
              <button class="delete-btn" @click.stop="confirmDelete(plan)" title="Delete plan">🗑️</button>
            </div>

            <!-- Pagination controls -->
            <div v-if="totalPages > 1" class="pagination">
              <button class="page-btn" :disabled="currentPage === 1" @click="prevPage">‹</button>
              <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
              <button class="page-btn" :disabled="currentPage === totalPages" @click="nextPage">›</button>
            </div>
          </div>
        </div>
      </div>

      <!-- ══════════════════════════════════════════
           RIGHT: Plan Display
           ══════════════════════════════════════════ -->
      <div class="plan-column">

        <!-- Empty state -->
        <div v-if="!workoutStore.activePlan && !workoutStore.generating" class="plan-empty">
          <div class="plan-empty-icon">🏉</div>
          <h3>No plan selected</h3>
          <p>Generate a new plan or select a saved plan from the left panel.</p>
        </div>

        <!-- Generating state -->
        <div v-if="workoutStore.generating" class="plan-generating">
          <div class="ai-pulse"><span>🤖</span></div>
          <h3>AI is generating your plan...</h3>
          <p>
            Ollama <strong>llama3.2</strong> is building a personalised
            <strong>{{ form.sessionsPerWeek }}-day</strong> workout plan
            for a <strong>{{ form.rugbyPosition || 'rugby player' }}</strong>.
          </p>
          <div class="progress-dots">
            <span></span><span></span><span></span>
          </div>
        </div>

        <!-- Plan content -->
        <div v-if="workoutStore.activePlan && !workoutStore.generating" class="plan-display card">
          <div class="plan-display-header">
            <div>
              <h2 class="plan-display-title">{{ workoutStore.activePlan.planName }}</h2>
              <div class="plan-tags">
                <span class="tag tag-position">🏉 {{ workoutStore.activePlan.rugbyPosition }}</span>
                <span class="tag tag-goal">{{ goalLabel(workoutStore.activePlan.goal) }}</span>
                <span class="tag tag-level">{{ levelLabel(workoutStore.activePlan.trainingLevel) }}</span>
                <span class="tag tag-sessions">{{ workoutStore.activePlan.sessionsPerWeek }}×/week</span>
              </div>
              <p class="plan-stats-line">
                {{ workoutStore.activePlan.weight }}kg ·
                {{ workoutStore.activePlan.height }}cm ·
                {{ workoutStore.activePlan.age }}yo
                <span v-if="workoutStore.activePlan.injuryNotes">
                  · ⚠️ {{ workoutStore.activePlan.injuryNotes }}
                </span>
              </p>
            </div>
          </div>

          <div class="plan-body">
            <div class="plan-markdown" v-html="renderMarkdown(workoutStore.activePlan.generatedPlan)"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete confirmation modal -->
    <div v-if="deleteTarget" class="modal-overlay" @click.self="deleteTarget = null">
      <div class="modal">
        <h3>Delete Plan</h3>
        <p>Are you sure you want to delete <strong>"{{ deleteTarget.planName }}"</strong>? This cannot be undone.</p>
        <div class="modal-actions">
          <button class="btn btn-ghost" @click="deleteTarget = null">Cancel</button>
          <button class="btn btn-danger" @click="handleDelete">Delete</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useWorkoutStore } from '@/stores/workout'

const workoutStore = useWorkoutStore()

// ── Form state ───────────────────────────────────────────────────────────────
const form = ref({
  planName:        '',
  rugbyPosition:   '',
  goal:            '',
  trainingLevel:   '',
  weight:          null,
  height:          null,
  age:             null,
  injuryNotes:     '',
  sessionsPerWeek: 4
})

const errors       = ref({})
const deleteTarget = ref(null)

// ── Pagination ───────────────────────────────────────────────────────────────
const PAGE_SIZE  = 5
const currentPage = ref(1)

const totalPages = computed(() =>
  Math.max(1, Math.ceil(workoutStore.plans.length / PAGE_SIZE))
)

const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * PAGE_SIZE
  return workoutStore.plans.slice(start, start + PAGE_SIZE)
})

function prevPage() { if (currentPage.value > 1) currentPage.value-- }
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++ }

// ── Lifecycle ────────────────────────────────────────────────────────────────
onMounted(() => {
  workoutStore.fetchPlans()
})

// ── Validation ───────────────────────────────────────────────────────────────
function validate() {
  errors.value = {}
  if (!form.value.rugbyPosition)   errors.value.rugbyPosition   = 'Please select your rugby position.'
  if (!form.value.goal)            errors.value.goal            = 'Please select a goal.'
  if (!form.value.trainingLevel)   errors.value.trainingLevel   = 'Please select your training level.'
  if (!form.value.weight)          errors.value.weight          = 'Weight is required.'
  if (!form.value.height)          errors.value.height          = 'Height is required.'
  if (!form.value.age)             errors.value.age             = 'Age is required.'
  if (!form.value.sessionsPerWeek) errors.value.sessionsPerWeek = 'Please select sessions per week.'
  return Object.keys(errors.value).length === 0
}

function clearError(field) {
  if (errors.value[field]) delete errors.value[field]
  workoutStore.clearError()
}

// ── Generate ─────────────────────────────────────────────────────────────────
async function handleGenerate() {
  if (!validate()) return
  workoutStore.clearError()
  await workoutStore.generatePlan({ ...form.value })
  currentPage.value = 1   // new plan lands at top — reset to page 1
}

// ── Delete ───────────────────────────────────────────────────────────────────
function confirmDelete(plan) { deleteTarget.value = plan }

async function handleDelete() {
  if (!deleteTarget.value) return
  await workoutStore.deletePlan(deleteTarget.value.id)
  deleteTarget.value = null
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function goalLabel(goal) {
  const map = { STRENGTH: '💪 Strength', POWER: '⚡ Power', ENDURANCE: '🏃 Endurance', LEAN: '🔥 Lean' }
  return map[goal] || goal
}

function levelLabel(level) {
  const map = { BEGINNER: '🌱 Beginner', INTERMEDIATE: '🔶 Intermediate', ADVANCED: '🔴 Advanced' }
  return map[level] || level
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-MY', {
    day: 'numeric', month: 'short', year: 'numeric'
  })
}

/**
 * Lightweight markdown → HTML converter for the Ollama-generated plan text.
 * Handles: ## headings, ### sub-headings, **bold**, bullet lists, tables, hr.
 */
function renderMarkdown(text) {
  if (!text) return ''
  return text
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/^## (.+)$/gm,   '<h2 class="md-h2">$1</h2>')
    .replace(/^### (.+)$/gm,  '<h3 class="md-h3">$1</h3>')
    .replace(/^#### (.+)$/gm, '<h4 class="md-h4">$1</h4>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g,    '<em>$1</em>')
    .replace(/^[-*] (.+)$/gm, '<li>$1</li>')
    .replace(/(<li>[\s\S]*?<\/li>\n?)+/g, m => `<ul class="md-ul">${m}</ul>`)
    .replace(/^\|(.+)\|$/gm, (_, cells) =>
      `<tr>${cells.split('|').map(c => `<td>${c.trim()}</td>`).join('')}</tr>`)
    .replace(/(<tr>[\s\S]*?<\/tr>\n?)+/g, m => `<table class="md-table">${m}</table>`)
    .replace(/^---+$/gm, '<hr class="md-hr">')
    .replace(/\n\n/g, '</p><p class="md-p">')
    .replace(/\n/g,   '<br>')
    .replace(/^/, '<p class="md-p">')
    .replace(/$/, '</p>')
}
</script>

<style scoped>
/* ── Page Layout ─────────────────────────────────────────────── */
.workout-page   { display: flex; flex-direction: column; gap: 24px; }
.page-header    { display: flex; align-items: flex-start; }
.page-title     { font-family: 'Barlow Condensed', sans-serif; font-size: 26px; font-weight: 700; }
.page-subtitle  { color: var(--color-muted); font-size: 13.5px; margin-top: 4px; }

.workout-layout {
  display: grid;
  grid-template-columns: 370px 1fr;
  gap: 20px;
  align-items: start;
}

/* ── Card ────────────────────────────────────────────────────── */
.card {
  background: var(--color-bg-2);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 22px;
}
.card-header    { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.card-icon      { font-size: 18px; }
.card-title     { font-family: 'Barlow Condensed', sans-serif; font-size: 18px; font-weight: 700; flex: 1; }
.plan-count     { background: var(--color-green-dim); color: var(--color-green-light); font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 99px; }

/* ── Form ────────────────────────────────────────────────────── */
.generate-form  { display: flex; flex-direction: column; gap: 16px; }
.form-row       { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.form-row.three-col { grid-template-columns: 1fr 1fr 1fr; }
.form-group     { display: flex; flex-direction: column; gap: 5px; }
.form-label     { font-size: 13px; font-weight: 500; }
.req            { color: var(--color-error); }
.optional-tag   { font-size: 11px; color: var(--color-muted); font-weight: 400; }

.form-input, .form-select, .form-textarea {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  color: var(--color-text);
  font-size: 13.5px;
  padding: 9px 12px;
  width: 100%;
  transition: border-color var(--transition);
  font-family: inherit;
  box-sizing: border-box;
}
.form-input:focus, .form-select:focus, .form-textarea:focus {
  outline: none; border-color: var(--color-green);
}
.form-textarea  { resize: vertical; min-height: 60px; }
.form-select    { cursor: pointer; }

.form-group.has-error .form-input,
.form-group.has-error .form-select { border-color: var(--color-error); }
.field-error    { font-size: 12px; color: var(--color-error); }
.field-hint     { font-size: 11.5px; color: var(--color-muted); }

/* Sessions selector */
.sessions-selector { display: flex; gap: 8px; flex-wrap: wrap; }
.session-btn {
  width: 42px; height: 42px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  color: var(--color-text-dim);
  font-size: 14px; font-weight: 600;
  cursor: pointer;
  transition: all var(--transition);
  font-family: inherit;
}
.session-btn:hover  { border-color: var(--color-green); color: var(--color-green-light); }
.session-btn.active { background: var(--color-green-dim); border-color: var(--color-green); color: var(--color-green-light); }

/* Generate button */
.btn-generate {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  width: 100%; padding: 12px;
  background: var(--color-green); color: #fff;
  font-size: 14px; font-weight: 600;
  border: none; border-radius: var(--radius-sm);
  cursor: pointer; transition: opacity var(--transition);
  font-family: inherit;
}
.btn-generate:disabled         { opacity: 0.65; cursor: not-allowed; }
.btn-generate:not(:disabled):hover { opacity: 0.9; }

.generating-hint { font-size: 12px; color: var(--color-muted); text-align: center; }

/* ── Saved Plans ─────────────────────────────────────────────── */
.saved-plans-card   { margin-top: 16px; }
.plans-list         { display: flex; flex-direction: column; gap: 8px; }

.plan-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: border-color var(--transition), background var(--transition);
}
.plan-item:hover  { border-color: var(--color-green); }
.plan-item.active { border-color: var(--color-green); background: var(--color-green-dim); }
.plan-item-info   { flex: 1; min-width: 0; }
.plan-item-name   { font-size: 13.5px; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.plan-item-meta   { font-size: 11.5px; color: var(--color-muted); margin-top: 2px; }
.plan-item-date   { font-size: 11px; color: var(--color-muted); margin-top: 2px; }

.delete-btn {
  background: none; border: none; cursor: pointer;
  font-size: 14px; padding: 4px 6px;
  border-radius: var(--radius-sm);
  opacity: 0.45; transition: opacity var(--transition); flex-shrink: 0;
}
.delete-btn:hover { opacity: 1; }

/* ── Pagination ──────────────────────────────────────────────── */
.pagination {
  display: flex; align-items: center; justify-content: center;
  gap: 10px; padding-top: 10px; border-top: 1px solid var(--color-border); margin-top: 6px;
}
.page-btn {
  width: 30px; height: 30px; border-radius: var(--radius-sm);
  border: 1px solid var(--color-border); background: var(--color-surface);
  color: var(--color-text); font-size: 16px; font-weight: 600;
  cursor: pointer; transition: all var(--transition);
  display: flex; align-items: center; justify-content: center;
}
.page-btn:hover:not(:disabled) { border-color: var(--color-green); color: var(--color-green-light); }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.page-info { font-size: 12.5px; color: var(--color-muted); min-width: 44px; text-align: center; }

/* ── States ──────────────────────────────────────────────────── */
.loading-state, .empty-state {
  display: flex; flex-direction: column; align-items: center;
  padding: 24px; gap: 10px;
  color: var(--color-muted); font-size: 13.5px; text-align: center;
}
.spinner-muted {
  width: 20px; height: 20px;
  border: 2px solid var(--color-border);
  border-top-color: var(--color-green);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

/* ── Plan Column ─────────────────────────────────────────────── */
.plan-column { min-width: 0; }

.plan-empty {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  min-height: 360px;
  background: var(--color-bg-2);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-lg);
  color: var(--color-muted); text-align: center; gap: 10px; padding: 40px;
}
.plan-empty-icon { font-size: 48px; }
.plan-empty h3   { font-family: 'Barlow Condensed', sans-serif; font-size: 20px; color: var(--color-text-dim); }
.plan-empty p    { font-size: 13.5px; }

.plan-generating {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  min-height: 380px;
  background: var(--color-bg-2);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  text-align: center; gap: 16px; padding: 40px;
}
.plan-generating h3 { font-family: 'Barlow Condensed', sans-serif; font-size: 22px; }
.plan-generating p  { font-size: 14px; color: var(--color-muted); max-width: 340px; line-height: 1.6; }

.ai-pulse {
  width: 64px; height: 64px; border-radius: 50%;
  background: var(--color-green-dim);
  display: flex; align-items: center; justify-content: center;
  font-size: 30px;
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse { 0%,100%{transform:scale(1);opacity:1} 50%{transform:scale(1.1);opacity:0.7} }

.progress-dots { display: flex; gap: 6px; }
.progress-dots span {
  width: 8px; height: 8px; border-radius: 50%;
  background: var(--color-green);
  animation: dot-bounce 1.2s ease-in-out infinite;
}
.progress-dots span:nth-child(2) { animation-delay: 0.2s; }
.progress-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes dot-bounce { 0%,100%{opacity:0.3;transform:translateY(0)} 50%{opacity:1;transform:translateY(-4px)} }

/* ── Plan Display ────────────────────────────────────────────── */
.plan-display { padding: 28px; }

.plan-display-header {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-bottom: 20px; padding-bottom: 18px;
  border-bottom: 1px solid var(--color-border);
}
.plan-display-title {
  font-family: 'Barlow Condensed', sans-serif;
  font-size: 22px; font-weight: 700; margin-bottom: 10px;
}
.plan-tags     { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.tag           { font-size: 11.5px; font-weight: 500; padding: 3px 10px; border-radius: 99px; }
.tag-position  { background: var(--color-green-dim); color: var(--color-green-light); }
.tag-goal      { background: #1e3a5f; color: #60a5fa; }
.tag-level     { background: #3a2e1e; color: #f59e0b; }
.tag-sessions  { background: var(--color-surface); color: var(--color-text-dim); border: 1px solid var(--color-border); }
.plan-stats-line { font-size: 12px; color: var(--color-muted); }

/* ── Markdown ────────────────────────────────────────────────── */
.plan-body { overflow: hidden; }

.plan-markdown :deep(.md-h2) {
  font-family: 'Barlow Condensed', sans-serif; font-size: 20px; font-weight: 700;
  color: var(--color-green-light); margin: 28px 0 12px;
  padding-bottom: 6px; border-bottom: 1px solid var(--color-border);
}
.plan-markdown :deep(.md-h3) { font-size: 15px; font-weight: 600; color: var(--color-text); margin: 18px 0 8px; }
.plan-markdown :deep(.md-h4) { font-size: 13.5px; font-weight: 600; color: var(--color-text-dim); margin: 12px 0 6px; }
.plan-markdown :deep(.md-p)  { font-size: 14px; line-height: 1.7; color: var(--color-text-dim); margin: 8px 0; }
.plan-markdown :deep(.md-ul) { padding-left: 18px; margin: 8px 0; display: flex; flex-direction: column; gap: 5px; }
.plan-markdown :deep(li)     { font-size: 14px; line-height: 1.6; color: var(--color-text-dim); }
.plan-markdown :deep(.md-table) { width: 100%; border-collapse: collapse; margin: 16px 0; font-size: 13.5px; }
.plan-markdown :deep(td)        { padding: 8px 12px; border: 1px solid var(--color-border); color: var(--color-text-dim); }
.plan-markdown :deep(tr:first-child td) { background: var(--color-surface); font-weight: 600; color: var(--color-text); }
.plan-markdown :deep(.md-hr)    { border: none; border-top: 1px solid var(--color-border); margin: 20px 0; }
.plan-markdown :deep(strong)    { color: var(--color-text); }

/* ── Alerts ──────────────────────────────────────────────────── */
.alert         { display: flex; align-items: center; gap: 8px; padding: 10px 14px; border-radius: var(--radius-sm); font-size: 13.5px; margin-bottom: 16px; }
.alert-error   { background: var(--color-error-bg); color: var(--color-error); border: 1px solid var(--color-error-border); }
.alert-close   { margin-left: auto; background: none; border: none; cursor: pointer; font-size: 16px; color: inherit; opacity: 0.6; }
.alert-close:hover { opacity: 1; }

/* ── Spinner ─────────────────────────────────────────────────── */
.spinner {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite; flex-shrink: 0;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Modal ───────────────────────────────────────────────────── */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal         { background: var(--color-bg-2); border: 1px solid var(--color-border); border-radius: var(--radius-lg); padding: 28px; max-width: 400px; width: 90%; display: flex; flex-direction: column; gap: 14px; }
.modal h3      { font-family: 'Barlow Condensed', sans-serif; font-size: 20px; font-weight: 700; }
.modal p       { font-size: 14px; color: var(--color-text-dim); line-height: 1.6; }
.modal-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 4px; }

.btn           { display: inline-flex; align-items: center; gap: 6px; padding: 8px 16px; border-radius: var(--radius-sm); font-size: 13.5px; font-weight: 500; cursor: pointer; border: none; font-family: inherit; }
.btn-ghost     { background: var(--color-surface); color: var(--color-text); border: 1px solid var(--color-border); }
.btn-ghost:hover { background: var(--color-surface-2); }
.btn-danger    { background: var(--color-error); color: #fff; }
.btn-danger:hover { opacity: 0.85; }

/* ── Transitions ─────────────────────────────────────────────── */
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s; }
.fade-enter-from, .fade-leave-to       { opacity: 0; }

/* ── Responsive ──────────────────────────────────────────────── */
@media (max-width: 900px) {
  .workout-layout { grid-template-columns: 1fr; }
}
</style>
