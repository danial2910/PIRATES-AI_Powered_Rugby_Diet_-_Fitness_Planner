<template>
  <div class="profile-page">

    <!-- Page header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">My Profile</h1>
        <p class="page-subtitle">
          {{ isAthlete ? 'Manage your personal info and rugby details' : 'Manage your personal info and trainer details' }}
        </p>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="profileStore.loading && !profileStore.profile" class="loading-state">
      <div class="spinner"></div>
      <p>Loading your profile...</p>
    </div>

    <!-- Load error -->
    <div v-else-if="profileStore.error && !profileStore.profile" class="alert alert-error">
      <span>✕</span> {{ profileStore.error }}
    </div>

    <template v-else-if="profileStore.profile">

      <!-- Success banner -->
      <transition name="fade">
        <div v-if="profileStore.saveSuccess" class="alert alert-success">
          <span>✓</span> Profile updated successfully.
        </div>
      </transition>

      <!-- Save error banner -->
      <transition name="fade">
        <div v-if="profileStore.error" class="alert alert-error">
          <span>✕</span> {{ profileStore.error }}
        </div>
      </transition>

      <!-- ═══════════════════════════════════════════
           SECTION 1 — Profile Picture + Personal Info
           ═══════════════════════════════════════════ -->
      <div class="profile-card">
        <div class="card-header">
          <div class="card-title-row">
            <span class="card-icon">👤</span>
            <h2 class="card-title">Personal Information</h2>
          </div>
          <span class="role-badge" :class="isAthlete ? 'badge-athlete' : 'badge-trainer'">
            {{ isAthlete ? '🏉 Athlete' : '🏋️ Trainer' }}
          </span>
        </div>

        <!-- Profile picture row -->
        <div class="avatar-row">
          <!-- Avatar preview -->
          <div class="avatar-preview" @click="triggerAvatarInput">
            <img v-if="avatarPreview" :src="avatarPreview" alt="Profile picture" class="avatar-img" />
            <div v-else class="avatar-initials">{{ initials }}</div>
            <!-- Hover overlay -->
            <div class="avatar-overlay">
              <span class="avatar-overlay-icon">📷</span>
              <span class="avatar-overlay-text">Change</span>
            </div>
          </div>

          <input
            ref="avatarInput"
            type="file"
            accept="image/jpeg,image/png,image/webp"
            style="display:none"
            @change="handleAvatarSelect"
          />

          <div class="avatar-info">
            <p class="avatar-name">{{ userForm.fullName || profileStore.profile.fullName }}</p>
            <p class="avatar-role">{{ isAthlete ? 'UTM Pirates · Athlete' : 'UTM Pirates · Trainer' }}</p>
            <div class="avatar-actions">
              <button class="btn-avatar-upload" @click="triggerAvatarInput">Upload photo</button>
              <button
                v-if="avatarPreview"
                class="btn-avatar-remove"
                @click="removeAvatar"
              >Remove</button>
            </div>
            <p class="avatar-hint">JPG, PNG or WebP · Max 2 MB · Square photos work best</p>
            <p v-if="avatarError" class="field-error">{{ avatarError }}</p>
          </div>
        </div>

        <!-- Divider -->
        <div class="section-divider"></div>

        <!-- Personal info form -->
        <div class="form-grid">
          <div class="form-group" :class="{ 'has-error': userErrors.fullName }">
            <label class="form-label">Full name</label>
            <input v-model="userForm.fullName" type="text" class="form-input"
              placeholder="Your full name" @input="clearUserError('fullName')" />
            <p v-if="userErrors.fullName" class="field-error">{{ userErrors.fullName }}</p>
          </div>

          <div class="form-group" :class="{ 'has-error': userErrors.email }">
            <label class="form-label">Email address</label>
            <input v-model="userForm.email" type="email" class="form-input"
              placeholder="you@utm.my" @input="clearUserError('email')" />
            <p v-if="userErrors.email" class="field-error">{{ userErrors.email }}</p>
          </div>

          <div class="form-group" :class="{ 'has-error': userErrors.phoneNumber }">
            <label class="form-label">Phone number <span class="optional">optional</span></label>
            <input v-model="userForm.phoneNumber" type="tel" class="form-input"
              placeholder="+601X XXXX XXXX" @input="clearUserError('phoneNumber')" />
            <p v-if="userErrors.phoneNumber" class="field-error">{{ userErrors.phoneNumber }}</p>
          </div>

          <div class="form-group">
            <label class="form-label">Username <span class="optional">read-only</span></label>
            <input :value="profileStore.profile.username" type="text"
              class="form-input readonly-field" readonly />
          </div>
        </div>

        <div class="card-footer">
          <button class="btn-save" :disabled="profileStore.loading" @click="handleSaveUser">
            <span v-if="profileStore.loading" class="btn-spinner"></span>
            {{ profileStore.loading ? 'Saving...' : 'Save personal info' }}
          </button>
        </div>
      </div>

      <!-- ═══════════════════════════════════════════
           SECTION 2A — Athlete Rugby Profile
           ═══════════════════════════════════════════ -->
      <div v-if="isAthlete" class="profile-card">
        <div class="card-header">
          <div class="card-title-row">
            <span class="card-icon">🏉</span>
            <h2 class="card-title">Rugby & Fitness Details</h2>
          </div>
        </div>

        <p class="section-subtitle">Body stats</p>
        <div class="form-grid-4">
          <div class="form-group" :class="{ 'has-error': athleteErrors.weight }">
            <label class="form-label">Weight (kg)</label>
            <input v-model.number="athleteForm.weight" type="number" class="form-input"
              placeholder="85" min="30" max="250" @input="clearAthleteError('weight')" />
            <p v-if="athleteErrors.weight" class="field-error">{{ athleteErrors.weight }}</p>
          </div>
          <div class="form-group" :class="{ 'has-error': athleteErrors.targetWeight }">
            <label class="form-label">Target weight (kg)</label>
            <input v-model.number="athleteForm.targetWeight" type="number" class="form-input"
              placeholder="80" min="30" max="250" @input="clearAthleteError('targetWeight')" />
            <p v-if="athleteErrors.targetWeight" class="field-error">{{ athleteErrors.targetWeight }}</p>
          </div>
          <div class="form-group" :class="{ 'has-error': athleteErrors.height }">
            <label class="form-label">Height (cm)</label>
            <input v-model.number="athleteForm.height" type="number" class="form-input"
              placeholder="178" min="100" max="250" @input="clearAthleteError('height')" />
            <p v-if="athleteErrors.height" class="field-error">{{ athleteErrors.height }}</p>
          </div>
          <div class="form-group" :class="{ 'has-error': athleteErrors.age }">
            <label class="form-label">Age</label>
            <input v-model.number="athleteForm.age" type="number" class="form-input"
              placeholder="22" min="10" max="80" @input="clearAthleteError('age')" />
            <p v-if="athleteErrors.age" class="field-error">{{ athleteErrors.age }}</p>
          </div>
        </div>

        <p class="section-subtitle" style="margin-top:20px;">Rugby details</p>
        <div class="form-grid">
          <div class="form-group">
            <label class="form-label">Playing position</label>
            <select v-model="athleteForm.rugbyPosition" class="form-input">
              <option value="">Select position</option>
              <optgroup label="Forwards">
                <option>Prop (Loosehead)</option>
                <option>Prop (Tighthead)</option>
                <option>Hooker</option>
                <option>Lock</option>
                <option>Flanker (Openside)</option>
                <option>Flanker (Blindside)</option>
                <option>Number 8</option>
              </optgroup>
              <optgroup label="Backs">
                <option>Scrum Half</option>
                <option>Fly Half</option>
                <option>Inside Centre</option>
                <option>Outside Centre</option>
                <option>Wing</option>
                <option>Fullback</option>
              </optgroup>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Training level</label>
            <select v-model="athleteForm.trainingLevel" class="form-input">
              <option value="">Select level</option>
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
              <option value="ELITE">Elite</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Primary goal</label>
            <select v-model="athleteForm.goal" class="form-input">
              <option value="">Select goal</option>
              <option value="STRENGTH">Strength & Power</option>
              <option value="ENDURANCE">Endurance</option>
              <option value="LEAN">Lean & Athletic</option>
              <option value="BULK">Muscle Gain</option>
              <option value="RECOVERY">Recovery Optimization</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Activity level</label>
            <select v-model="athleteForm.activityLevel" class="form-input">
              <option value="">Select level</option>
              <option value="MODERATE">Moderately Active</option>
              <option value="ACTIVE">Very Active</option>
              <option value="EXTREME">Extremely Active</option>
            </select>
          </div>
        </div>

        <p class="section-subtitle" style="margin-top:20px;">Health & dietary notes</p>
        <div class="form-grid-1">
          <div class="form-group" :class="{ 'has-error': athleteErrors.dietaryRestrictions }">
            <label class="form-label">Dietary restrictions / allergies</label>
            <textarea v-model="athleteForm.dietaryRestrictions" class="form-input form-textarea"
              placeholder="e.g. Halal diet only, no peanuts, lactose intolerant..."
              rows="2" @input="clearAthleteError('dietaryRestrictions')"></textarea>
            <p v-if="athleteErrors.dietaryRestrictions" class="field-error">{{ athleteErrors.dietaryRestrictions }}</p>
            <p class="field-hint">Used by the AI to tailor your meal plans.</p>
          </div>
          <div class="form-group" :class="{ 'has-error': athleteErrors.injuryNotes }">
            <label class="form-label">Current injuries / health notes</label>
            <textarea v-model="athleteForm.injuryNotes" class="form-input form-textarea"
              placeholder="e.g. Mild left knee soreness, lower back strain..."
              rows="2" @input="clearAthleteError('injuryNotes')"></textarea>
            <p v-if="athleteErrors.injuryNotes" class="field-error">{{ athleteErrors.injuryNotes }}</p>
            <p class="field-hint">The AI avoids exercises that may worsen injuries.</p>
          </div>
        </div>

        <div class="card-footer">
          <button class="btn-save" :disabled="profileStore.loading" @click="handleSaveAthlete">
            <span v-if="profileStore.loading" class="btn-spinner"></span>
            {{ profileStore.loading ? 'Saving...' : 'Save rugby profile' }}
          </button>
        </div>
      </div>

      <!-- ═══════════════════════════════════════════
           SECTION 2B — Trainer Profile
           ═══════════════════════════════════════════ -->
      <div v-if="!isAthlete" class="profile-card">
        <div class="card-header">
          <div class="card-title-row">
            <span class="card-icon">🏋️</span>
            <h2 class="card-title">Trainer Details</h2>
          </div>
        </div>

        <!-- Expertise & Experience -->
        <div class="form-grid-1" style="margin-bottom:24px;">
          <div class="form-group" :class="{ 'has-error': trainerErrors.expertise }">
            <label class="form-label">Areas of expertise</label>
            <input v-model="trainerForm.expertise" type="text" class="form-input"
              placeholder="e.g. Strength & Conditioning, Forwards, Rugby Fitness"
              @input="clearTrainerError('expertise')" />
            <p v-if="trainerErrors.expertise" class="field-error">{{ trainerErrors.expertise }}</p>
          </div>
          <div class="form-group" :class="{ 'has-error': trainerErrors.experience }">
            <label class="form-label">Professional experience</label>
            <textarea v-model="trainerForm.experience" class="form-input form-textarea"
              placeholder="e.g. 5 years as UTM Pirates head coach, rugby development background..."
              rows="3" @input="clearTrainerError('experience')"></textarea>
            <p v-if="trainerErrors.experience" class="field-error">{{ trainerErrors.experience }}</p>
          </div>
        </div>

        <!-- Weekly schedule -->
        <p class="section-subtitle">Weekly availability schedule</p>
        <p class="field-hint" style="margin-bottom:14px;">Click a day to add a time slot. You can add multiple slots per day.</p>

        <div class="schedule-grid">
          <div v-for="day in DAYS" :key="day" class="day-column">
            <div class="day-header" :class="{ 'day-active': hasSlotsForDay(day) }">
              {{ day.slice(0,3) }}
            </div>
            <div v-for="(slot, idx) in getSlotsForDay(day)" :key="idx" class="time-slot">
              <div class="slot-times">
                <input v-model="slot.startTime" type="time" class="time-input" title="Start time" />
                <span class="slot-sep">–</span>
                <input v-model="slot.endTime" type="time" class="time-input" title="End time" />
              </div>
              <button class="slot-remove" title="Remove slot" @click="removeSlot(day, idx)">✕</button>
            </div>
            <button class="slot-add" @click="addSlot(day)">+ Add</button>
          </div>
        </div>

        <div v-if="trainerForm.availabilitySlots.length > 0" class="schedule-summary">
          <p class="summary-title">Your schedule</p>
          <div v-for="day in DAYS" :key="day">
            <div v-if="hasSlotsForDay(day)" class="summary-row">
              <span class="summary-day">{{ day.charAt(0) + day.slice(1).toLowerCase() }}</span>
              <div class="summary-slots">
                <span v-for="(slot, i) in getSlotsForDay(day)" :key="i" class="summary-pill">
                  {{ formatTime(slot.startTime) }} – {{ formatTime(slot.endTime) }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <p v-else class="field-hint" style="margin-top:8px;">No availability set yet.</p>

        <!-- Certifications -->
        <p class="section-subtitle" style="margin-top:28px;">Certifications</p>
        <p class="field-hint" style="margin-bottom:14px;">
          Upload your coaching certificates and qualifications (PDF, JPG, PNG — max 5 MB each).
        </p>

        <div class="upload-zone"
          :class="{ 'drag-over': isDragging }"
          @click="triggerFileInput"
          @dragover.prevent="isDragging = true"
          @dragleave="isDragging = false"
          @drop.prevent="handleDrop"
        >
          <div class="upload-icon">📎</div>
          <p class="upload-text">Click to upload or drag & drop</p>
          <p class="upload-hint">PDF, JPG, PNG — max 5 MB each</p>
          <input ref="fileInput" type="file" multiple accept=".pdf,.jpg,.jpeg,.png"
            style="display:none" @change="handleFileSelect" />
        </div>

        <div v-if="trainerForm.certificationFiles.length > 0" class="cert-list">
          <div v-for="(cert, idx) in trainerForm.certificationFiles" :key="idx" class="cert-item">
            <div class="cert-icon">{{ getFileIcon(cert.fileName) }}</div>
            <div class="cert-info">
              <input v-model="cert.name" type="text" class="cert-name-input"
                placeholder="Enter certification name (e.g. World Rugby Level 2)" />
              <p class="cert-filename">{{ cert.fileName }}</p>
            </div>
            <div class="cert-meta">
              <span class="cert-date">{{ formatDate(cert.uploadedAt) }}</span>
              <a v-if="cert.fileUrl" :href="cert.fileUrl" :download="cert.fileName"
                class="cert-action cert-download" title="Download">⬇</a>
              <button class="cert-action cert-delete" title="Remove" @click="removeCert(idx)">✕</button>
            </div>
          </div>
        </div>
        <p v-else class="field-hint" style="margin-top:8px;">No certificates uploaded yet.</p>
        <p v-if="fileError" class="field-error" style="margin-top:8px;">{{ fileError }}</p>

        <div class="card-footer">
          <button class="btn-save" :disabled="profileStore.loading" @click="handleSaveTrainer">
            <span v-if="profileStore.loading" class="btn-spinner"></span>
            {{ profileStore.loading ? 'Saving...' : 'Save trainer profile' }}
          </button>
        </div>
      </div>

    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useAuthStore }    from '@/stores/auth'
import { useProfileStore } from '@/stores/profile'

const authStore    = useAuthStore()
const profileStore = useProfileStore()

const isAthlete = computed(() => authStore.userRole === 'ATHLETE')

// ── Initials computed from current name in form (live) ────────────────────────
const initials = computed(() => {
  const name = userForm.fullName || profileStore.profile?.fullName || authStore.fullName || '?'
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
})

// ── Days of the week ──────────────────────────────────────────────────────────
const DAYS = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY']

// ── Form state ────────────────────────────────────────────────────────────────
const userForm = reactive({ fullName: '', email: '', phoneNumber: '' })

const athleteForm = reactive({
  weight: null, targetWeight: null, height: null, age: null,
  location: '', goal: '', activityLevel: '',
  rugbyPosition: '', trainingLevel: '',
  dietaryRestrictions: '', injuryNotes: ''
})

const trainerForm = reactive({
  availabilitySlots:  [],
  expertise:          '',
  experience:         '',
  certificationFiles: []
})

// ── Avatar state ──────────────────────────────────────────────────────────────
const avatarInput   = ref(null)
const avatarPreview = ref(null)   // the current displayed image (base64 or existing URL)
const avatarChanged = ref(false)  // true = user picked a new image or removed existing
const avatarError   = ref('')
const MAX_AVATAR_SIZE = 2 * 1024 * 1024   // 2 MB

// ── Certification file upload state ──────────────────────────────────────────
const fileInput  = ref(null)
const isDragging = ref(false)
const fileError  = ref('')
const MAX_FILE_SIZE = 5 * 1024 * 1024   // 5 MB

// ── Error state ───────────────────────────────────────────────────────────────
const userErrors    = reactive({})
const athleteErrors = reactive({})
const trainerErrors = reactive({})

function clearUserError(f)    { delete userErrors[f];    profileStore.clearError() }
function clearAthleteError(f) { delete athleteErrors[f]; profileStore.clearError() }
function clearTrainerError(f) { delete trainerErrors[f]; profileStore.clearError() }

// ── Avatar handlers ───────────────────────────────────────────────────────────
function triggerAvatarInput() { avatarInput.value?.click() }

function handleAvatarSelect(e) {
  const file = e.target.files[0]
  if (!file) return
  avatarError.value = ''

  if (file.size > MAX_AVATAR_SIZE) {
    avatarError.value = 'Image must be smaller than 2 MB.'
    e.target.value = ''
    return
  }
  if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
    avatarError.value = 'Only JPG, PNG, or WebP images are supported.'
    e.target.value = ''
    return
  }

  const reader = new FileReader()
  reader.onload = (ev) => {
    avatarPreview.value = ev.target.result   // base64 data URL
    avatarChanged.value = true
  }
  reader.readAsDataURL(file)
  e.target.value = ''
}

function removeAvatar() {
  avatarPreview.value = null
  avatarChanged.value = true   // signal: send empty string to backend to remove
  avatarError.value   = ''
}

// ── Schedule helpers ──────────────────────────────────────────────────────────
function getSlotsForDay(day) { return trainerForm.availabilitySlots.filter(s => s.day === day) }
function hasSlotsForDay(day) { return getSlotsForDay(day).length > 0 }
function addSlot(day)        { trainerForm.availabilitySlots.push({ day, startTime: '08:00', endTime: '17:00' }) }

function removeSlot(day, indexWithinDay) {
  let count = -1
  const globalIdx = trainerForm.availabilitySlots.findIndex(s => {
    if (s.day === day) { count++; return count === indexWithinDay }
    return false
  })
  if (globalIdx !== -1) trainerForm.availabilitySlots.splice(globalIdx, 1)
}

function formatTime(t) {
  if (!t) return ''
  const [h, m] = t.split(':')
  const hour = parseInt(h)
  return `${hour % 12 || 12}:${m}${hour >= 12 ? 'pm' : 'am'}`
}

// ── Cert file helpers ─────────────────────────────────────────────────────────
function triggerFileInput() { fileInput.value?.click() }

function handleFileSelect(e) {
  processFiles(Array.from(e.target.files))
  e.target.value = ''
}

function handleDrop(e) {
  isDragging.value = false
  processFiles(Array.from(e.dataTransfer.files))
}

function processFiles(files) {
  fileError.value = ''
  for (const file of files) {
    if (file.size > MAX_FILE_SIZE) { fileError.value = `"${file.name}" exceeds 5 MB limit.`; continue }
    if (!['application/pdf','image/jpeg','image/png'].includes(file.type)) { fileError.value = `"${file.name}" is not a supported file type.`; continue }
    const reader = new FileReader()
    reader.onload = (e) => {
      trainerForm.certificationFiles.push({ name: '', fileName: file.name, fileUrl: e.target.result, uploadedAt: new Date().toISOString() })
    }
    reader.readAsDataURL(file)
  }
}

function removeCert(idx) { trainerForm.certificationFiles.splice(idx, 1) }
function getFileIcon(n) { if (!n) return '📄'; const e = n.split('.').pop()?.toLowerCase(); return e === 'pdf' ? '📕' : ['jpg','jpeg','png'].includes(e) ? '🖼️' : '📄' }
function formatDate(iso) { if (!iso) return ''; try { return new Date(iso).toLocaleDateString('en-MY', { day: 'numeric', month: 'short', year: 'numeric' }) } catch { return '' } }

// ── Populate forms ────────────────────────────────────────────────────────────
function populateForms(p) {
  if (!p) return
  userForm.fullName    = p.fullName    || ''
  userForm.email       = p.email       || ''
  userForm.phoneNumber = p.phoneNumber || ''

  // Set avatar preview from existing profile picture
  avatarPreview.value = p.profilePicture || null
  avatarChanged.value = false

  if (isAthlete.value) {
    athleteForm.weight              = p.weight              ?? null
    athleteForm.targetWeight        = p.targetWeight        ?? null
    athleteForm.height              = p.height              ?? null
    athleteForm.age                 = p.age                 ?? null
    athleteForm.location            = p.location            || ''
    athleteForm.goal                = p.goal                || ''
    athleteForm.activityLevel       = p.activityLevel       || ''
    athleteForm.rugbyPosition       = p.rugbyPosition       || ''
    athleteForm.trainingLevel       = p.trainingLevel       || ''
    athleteForm.dietaryRestrictions = p.dietaryRestrictions || ''
    athleteForm.injuryNotes         = p.injuryNotes         || ''
  } else {
    trainerForm.expertise           = p.expertise           || ''
    trainerForm.experience          = p.experience          || ''
    trainerForm.availabilitySlots   = p.availabilitySlots   ? p.availabilitySlots.map(s => ({ ...s })) : []
    trainerForm.certificationFiles  = p.certificationFiles  ? p.certificationFiles.map(c => ({ ...c })) : []
  }
}

// ── Validation ────────────────────────────────────────────────────────────────
function validateUser() {
  let valid = true
  Object.keys(userErrors).forEach(k => delete userErrors[k])
  if (!userForm.fullName.trim())                                   { userErrors.fullName = 'Full name is required'; valid = false }
  if (!userForm.email.trim())                                      { userErrors.email = 'Email is required'; valid = false }
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(userForm.email))   { userErrors.email = 'Enter a valid email address'; valid = false }
  if (userForm.phoneNumber && !/^\+?[0-9]{10,15}$/.test(userForm.phoneNumber)) { userErrors.phoneNumber = 'Enter a valid phone number'; valid = false }
  return valid
}

function validateAthlete() {
  let valid = true
  Object.keys(athleteErrors).forEach(k => delete athleteErrors[k])
  if (athleteForm.weight       && (athleteForm.weight < 30 || athleteForm.weight > 250))             { athleteErrors.weight = 'Weight must be 30–250 kg'; valid = false }
  if (athleteForm.targetWeight && (athleteForm.targetWeight < 30 || athleteForm.targetWeight > 250)) { athleteErrors.targetWeight = 'Target weight must be 30–250 kg'; valid = false }
  if (athleteForm.height       && (athleteForm.height < 100 || athleteForm.height > 250))            { athleteErrors.height = 'Height must be 100–250 cm'; valid = false }
  if (athleteForm.age          && (athleteForm.age < 10 || athleteForm.age > 80))                    { athleteErrors.age = 'Age must be 10–80'; valid = false }
  return valid
}

// ── Save handlers ─────────────────────────────────────────────────────────────
async function handleSaveUser() {
  if (!validateUser()) return

  // Build profilePicture value for the request:
  //   null           → no change (user didn't touch the avatar)
  //   base64 string  → new image selected
  //   ""             → user clicked Remove (clears the picture)
  let profilePicture = null
  if (avatarChanged.value) {
    profilePicture = avatarPreview.value || ''   // empty string = remove
  }

  const { fieldErrors } = await profileStore.saveUserInfo({
    ...userForm,
    profilePicture
  })
  if (fieldErrors) Object.assign(userErrors, fieldErrors)

  // Reset change flag after save
  if (!profileStore.error) avatarChanged.value = false
}

async function handleSaveAthlete() {
  if (!validateAthlete()) return
  const payload = { ...athleteForm }
  if (!payload.weight)       payload.weight       = null
  if (!payload.targetWeight) payload.targetWeight = null
  if (!payload.height)       payload.height       = null
  if (!payload.age)          payload.age          = null
  const { fieldErrors } = await profileStore.saveAthleteProfile(payload)
  if (fieldErrors) Object.assign(athleteErrors, fieldErrors)
}

async function handleSaveTrainer() {
  const { fieldErrors } = await profileStore.saveTrainerProfile({
    availabilitySlots:  trainerForm.availabilitySlots,
    expertise:          trainerForm.expertise,
    experience:         trainerForm.experience,
    certificationFiles: trainerForm.certificationFiles
  })
  if (fieldErrors) Object.assign(trainerErrors, fieldErrors)
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────
onMounted(async () => {
  await profileStore.fetchProfile()
  populateForms(profileStore.profile)
})

watch(() => profileStore.profile, (p) => populateForms(p))
</script>

<style scoped>
/* ── Page ─────────────────────────────────────────── */
.profile-page { max-width: 900px; }
.page-header  { margin-bottom: 28px; }
.page-title   { font-family: 'Barlow Condensed', sans-serif; font-size: 28px; font-weight: 700; color: #e8f0ea; margin-bottom: 4px; }
.page-subtitle { font-size: 14px; color: #6b8574; }

/* ── Loading ──────────────────────────────────────── */
.loading-state { display: flex; flex-direction: column; align-items: center; gap: 14px; padding: 60px; color: #6b8574; font-size: 14px; }
.spinner { width: 32px; height: 32px; border: 3px solid #212c24; border-top-color: #22a85c; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Alerts ───────────────────────────────────────── */
.alert { display: flex; align-items: flex-start; gap: 10px; padding: 12px 14px; border-radius: 10px; font-size: 13.5px; line-height: 1.5; margin-bottom: 20px; }
.alert-error   { background: rgba(200,50,50,0.12); border: 1px solid rgba(200,50,50,0.25); color: #e07070; }
.alert-success { background: rgba(26,122,69,0.12); border: 1px solid rgba(26,122,69,0.3);  color: #22a85c; }

/* ── Card ─────────────────────────────────────────── */
.profile-card { background: #141a16; border: 1px solid rgba(255,255,255,0.08); border-radius: 14px; padding: 24px; margin-bottom: 20px; }
.card-header  { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid rgba(255,255,255,0.07); }
.card-title-row { display: flex; align-items: center; gap: 10px; }
.card-icon    { font-size: 20px; }
.card-title   { font-family: 'Barlow Condensed', sans-serif; font-size: 18px; font-weight: 700; color: #e8f0ea; }
.role-badge   { font-size: 12px; font-weight: 500; padding: 4px 12px; border-radius: 20px; }
.badge-athlete { background: rgba(26,122,69,0.2); color: #22a85c; border: 1px solid rgba(26,122,69,0.3); }
.badge-trainer { background: rgba(127,119,221,0.2); color: #afa9ec; border: 1px solid rgba(127,119,221,0.3); }
.section-subtitle { font-size: 11px; font-weight: 500; letter-spacing: 1px; text-transform: uppercase; color: #6b8574; margin-bottom: 12px; }
.section-divider  { height: 1px; background: rgba(255,255,255,0.07); margin: 20px 0; }

/* ── Avatar row ───────────────────────────────────── */
.avatar-row {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 4px;
}

.avatar-preview {
  position: relative;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  flex-shrink: 0;
  cursor: pointer;
  overflow: hidden;
  border: 2px solid rgba(255,255,255,0.12);
  transition: border-color 0.18s;
}

.avatar-preview:hover { border-color: #1a7a45; }

.avatar-img {
  width: 100%; height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-initials {
  width: 100%; height: 100%;
  background: #0f4d2b;
  display: flex; align-items: center; justify-content: center;
  font-family: 'Barlow Condensed', sans-serif;
  font-size: 28px; font-weight: 700;
  color: #22a85c;
}

/* Hover overlay on avatar */
.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.55);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.18s;
  border-radius: 50%;
}

.avatar-preview:hover .avatar-overlay { opacity: 1; }

.avatar-overlay-icon { font-size: 20px; }
.avatar-overlay-text { font-size: 11px; font-weight: 500; color: white; }

.avatar-info { display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.avatar-name { font-size: 17px; font-weight: 600; color: #e8f0ea; }
.avatar-role { font-size: 13px; color: #6b8574; margin-bottom: 4px; }

.avatar-actions { display: flex; align-items: center; gap: 10px; margin-bottom: 4px; }

.btn-avatar-upload {
  padding: 7px 16px;
  background: #1a7a45; color: white;
  border: none; border-radius: 7px;
  font-size: 13px; font-weight: 500; font-family: inherit;
  cursor: pointer; transition: background 0.18s;
}
.btn-avatar-upload:hover { background: #22a85c; }

.btn-avatar-remove {
  padding: 7px 14px;
  background: transparent;
  border: 1px solid rgba(200,50,50,0.35);
  border-radius: 7px;
  color: #e07070;
  font-size: 13px; font-weight: 500; font-family: inherit;
  cursor: pointer; transition: all 0.18s;
}
.btn-avatar-remove:hover { background: rgba(200,50,50,0.12); }

.avatar-hint { font-size: 11px; color: #6b8574; }

/* ── Form grids ───────────────────────────────────── */
.form-grid   { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 8px; }
.form-grid-4 { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; margin-bottom: 8px; }
.form-grid-1 { display: grid; grid-template-columns: 1fr; gap: 16px; margin-bottom: 8px; }
.form-group  { display: flex; flex-direction: column; gap: 6px; }
.form-label  { font-size: 12px; font-weight: 500; color: #a8c0ae; }
.optional    { font-size: 11px; color: #6b8574; font-weight: 400; margin-left: 4px; }
.form-input  { width: 100%; padding: 10px 14px; background: #212c24; border: 1px solid rgba(255,255,255,0.12); border-radius: 8px; color: #e8f0ea; font-size: 14px; font-family: inherit; outline: none; transition: border-color 0.18s; }
.form-input:focus     { border-color: #1a7a45; box-shadow: 0 0 0 3px rgba(26,122,69,0.12); }
.has-error .form-input { border-color: #e07070; }
.readonly-field { opacity: 0.5; cursor: not-allowed; }
.form-textarea { resize: vertical; min-height: 70px; }
select.form-input option { background: #141a16; }
.field-error { font-size: 12px; color: #e07070; }
.field-hint  { font-size: 11px; color: #6b8574; }

/* ── Weekly schedule ──────────────────────────────── */
.schedule-grid { display: grid; grid-template-columns: repeat(7,1fr); gap: 8px; margin-bottom: 16px; }
.day-column    { display: flex; flex-direction: column; gap: 6px; }
.day-header    { text-align: center; font-size: 11px; font-weight: 600; letter-spacing: 0.5px; text-transform: uppercase; padding: 6px 4px; border-radius: 6px; background: #1c2420; color: #6b8574; border: 1px solid rgba(255,255,255,0.06); transition: all 0.18s; }
.day-header.day-active { background: rgba(26,122,69,0.18); color: #22a85c; border-color: rgba(26,122,69,0.3); }
.time-slot  { background: #1c2420; border: 1px solid rgba(255,255,255,0.08); border-radius: 6px; padding: 6px; display: flex; flex-direction: column; gap: 4px; }
.slot-times { display: flex; align-items: center; gap: 3px; }
.time-input { flex: 1; min-width: 0; background: #212c24; border: 1px solid rgba(255,255,255,0.1); border-radius: 4px; color: #e8f0ea; font-size: 11px; font-family: inherit; padding: 4px 3px; text-align: center; outline: none; width: 100%; cursor: pointer; }
.time-input::-webkit-calendar-picker-indicator { display: none; }
.time-input:focus { border-color: #1a7a45; }
.slot-sep    { font-size: 10px; color: #6b8574; flex-shrink: 0; }
.slot-remove { align-self: flex-end; background: none; border: none; color: #6b8574; font-size: 10px; cursor: pointer; padding: 0; line-height: 1; transition: color 0.15s; }
.slot-remove:hover { color: #e07070; }
.slot-add    { background: none; border: 1px dashed rgba(255,255,255,0.1); border-radius: 6px; color: #6b8574; font-size: 11px; font-family: inherit; padding: 5px 4px; cursor: pointer; text-align: center; transition: all 0.18s; }
.slot-add:hover { border-color: #1a7a45; color: #22a85c; background: rgba(26,122,69,0.08); }

/* ── Schedule summary ─────────────────────────────── */
.schedule-summary { background: #1c2420; border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; padding: 14px 16px; margin-top: 4px; }
.summary-title    { font-size: 11px; font-weight: 500; letter-spacing: 0.8px; text-transform: uppercase; color: #6b8574; margin-bottom: 10px; }
.summary-row  { display: flex; align-items: center; gap: 12px; padding: 5px 0; border-bottom: 1px solid rgba(255,255,255,0.04); }
.summary-row:last-child { border-bottom: none; }
.summary-day  { font-size: 12px; font-weight: 500; color: #a8c0ae; width: 90px; flex-shrink: 0; }
.summary-slots { display: flex; flex-wrap: wrap; gap: 6px; }
.summary-pill  { background: rgba(26,122,69,0.15); border: 1px solid rgba(26,122,69,0.25); border-radius: 20px; padding: 2px 10px; font-size: 12px; color: #22a85c; }

/* ── File upload ──────────────────────────────────── */
.upload-zone  { border: 2px dashed rgba(255,255,255,0.12); border-radius: 10px; padding: 28px 20px; text-align: center; cursor: pointer; transition: all 0.18s; background: #1c2420; }
.upload-zone:hover, .upload-zone.drag-over { border-color: #1a7a45; background: rgba(26,122,69,0.06); }
.upload-icon  { font-size: 28px; margin-bottom: 8px; }
.upload-text  { font-size: 14px; font-weight: 500; color: #a8c0ae; margin-bottom: 4px; }
.upload-hint  { font-size: 12px; color: #6b8574; }
.cert-list    { display: flex; flex-direction: column; gap: 10px; margin-top: 14px; }
.cert-item    { display: flex; align-items: center; gap: 12px; background: #1c2420; border: 1px solid rgba(255,255,255,0.07); border-radius: 10px; padding: 12px 14px; }
.cert-icon    { font-size: 24px; flex-shrink: 0; }
.cert-info    { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.cert-name-input { background: #212c24; border: 1px solid rgba(255,255,255,0.1); border-radius: 6px; color: #e8f0ea; font-size: 13px; font-weight: 500; font-family: inherit; padding: 5px 9px; outline: none; width: 100%; }
.cert-name-input:focus { border-color: #1a7a45; }
.cert-name-input::placeholder { color: #6b8574; font-weight: 400; }
.cert-filename { font-size: 11px; color: #6b8574; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cert-meta    { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
.cert-date    { font-size: 11px; color: #6b8574; white-space: nowrap; }
.cert-action  { width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; border-radius: 6px; border: none; font-size: 13px; cursor: pointer; transition: all 0.15s; text-decoration: none; }
.cert-download { background: rgba(55,138,221,0.12); color: #85b7eb; }
.cert-download:hover { background: rgba(55,138,221,0.22); }
.cert-delete  { background: rgba(200,50,50,0.1); color: #e07070; }
.cert-delete:hover { background: rgba(200,50,50,0.2); }

/* ── Footer ───────────────────────────────────────── */
.card-footer { margin-top: 24px; padding-top: 16px; border-top: 1px solid rgba(255,255,255,0.07); display: flex; justify-content: flex-end; }
.btn-save    { display: flex; align-items: center; gap: 8px; padding: 10px 24px; background: #1a7a45; color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; font-family: inherit; cursor: pointer; transition: background 0.18s; }
.btn-save:hover:not(:disabled) { background: #22a85c; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.7s linear infinite; flex-shrink: 0; }

/* ── Transitions ──────────────────────────────────── */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ── Responsive ───────────────────────────────────── */
@media (max-width: 700px) {
  .form-grid   { grid-template-columns: 1fr; }
  .form-grid-4 { grid-template-columns: 1fr 1fr; }
  .schedule-grid { grid-template-columns: repeat(4,1fr); }
  .avatar-row  { flex-direction: column; align-items: flex-start; }
}
@media (max-width: 480px) {
  .schedule-grid { grid-template-columns: repeat(2,1fr); }
}
</style>