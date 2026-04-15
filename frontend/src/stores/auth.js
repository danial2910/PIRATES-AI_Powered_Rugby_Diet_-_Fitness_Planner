/**
 * auth.js — Pinia Store for Authentication State
 *
 * UC001: login(), logout()
 * UC002: register()
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService from '@/services/authService'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // ── State ────────────────────────────────────────────────────────────────
  const token   = ref(localStorage.getItem('access_token') || null)
  const user    = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const loading = ref(false)
  const error   = ref(null)

  // ── Getters ──────────────────────────────────────────────────────────────
  const isAuthenticated = computed(() => !!token.value)
  const isAthlete       = computed(() => user.value?.userRole === 'ATHLETE')
  const isTrainer       = computed(() => user.value?.userRole === 'TRAINER')
  const fullName        = computed(() => user.value?.fullName || '')
  const username        = computed(() => user.value?.username || '')
  const userRole        = computed(() => user.value?.userRole || null)

  // ── Actions ───────────────────────────────────────────────────────────────

  /**
   * UC001: Login
   * Normal Flow Steps 4–7 — verify credentials, store JWT, redirect
   */
  async function login(credentials) {
    loading.value = true
    error.value   = null
    try {
      const response = await authService.login(credentials)
      if (response.success) {
        const d = response.data
        token.value = d.accessToken
        user.value  = {
          userId:   d.userId,
          username: d.username,
          fullName: d.fullName,
          userRole: d.userRole,
          email:    d.email
        }
        localStorage.setItem('access_token', d.accessToken)
        localStorage.setItem('user', JSON.stringify(user.value))
        await router.push(d.userRole === 'TRAINER' ? '/trainer' : '/dashboard')
      } else {
        error.value = response.message || 'Login failed.'
      }
    } catch (err) {
      if (err.response?.status === 401) {
        error.value = err.response.data?.message
          || 'Invalid username or password. Please try again.'
      } else if (!err.response) {
        error.value = 'Connection error. Please check your internet connection.'
      } else {
        error.value = 'An unexpected error occurred. Please try again later.'
      }
    } finally {
      loading.value = false
    }
  }

  /**
   * UC002: Register
   * Normal Flow Steps 6–9 — submit form, create account, redirect to login
   *
   * @returns {{ fieldErrors: Object|null }}
   *   fieldErrors: map of { fieldName: errorMessage } from Spring @Valid (AF1)
   *   null if no per-field errors
   */
  async function register(formData) {
    loading.value = true
    error.value   = null

    try {
      const response = await authService.register(formData)
      if (response.success) {
        // UC002 Post-condition: account created → redirect to login with success notice
        await router.push({
          name:  'Login',
          query: { registered: 'true', username: response.data?.username }
        })
        return { fieldErrors: null }
      }
      error.value = response.message || 'Registration failed.'
      return { fieldErrors: null }

    } catch (err) {
      // AF1: Spring @Valid annotation failures (422) — field-level errors
      if (err.response?.status === 422) {
        error.value = err.response.data?.message || 'Please correct the errors below.'
        return { fieldErrors: err.response.data?.data || {} }
      }
      // AF2: Duplicate email or username (409 Conflict)
      if (err.response?.status === 409) {
        error.value = err.response.data?.message
          || 'This email or username is already registered.'
        return { fieldErrors: null }
      }
      // AF1: Custom validation — password mismatch (400)
      if (err.response?.status === 400) {
        error.value = err.response.data?.message || 'Please check your input.'
        return { fieldErrors: null }
      }
      // EF1: No internet connection
      if (!err.response) {
        error.value = 'Connection error. Please check your internet connection.'
        return { fieldErrors: null }
      }
      error.value = 'An unexpected error occurred. Please try again later.'
      return { fieldErrors: null }

    } finally {
      loading.value = false
    }
  }

  /** UC001 Logout */
  async function logout() {
    await authService.logout()
    token.value = null
    user.value  = null
    localStorage.removeItem('access_token')
    localStorage.removeItem('user')
    await router.push({ name: 'Login' })
  }

  function clearError() { error.value = null }

  return {
    // State
    token, user, loading, error,
    // Getters
    isAuthenticated, isAthlete, isTrainer, fullName, username, userRole,
    // Actions
    login, register, logout, clearError
  }
})
