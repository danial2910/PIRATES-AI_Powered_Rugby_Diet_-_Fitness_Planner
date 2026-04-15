/**
 * auth.js — Pinia Store for Authentication State
 * UC001: login(), logout()
 * UC002: register()
 * UC003: updateProfilePicture() — keeps avatar in sync after profile save
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService from '@/services/authService'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token   = ref(localStorage.getItem('access_token') || null)
  const user    = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const loading = ref(false)
  const error   = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAthlete       = computed(() => user.value?.userRole === 'ATHLETE')
  const isTrainer       = computed(() => user.value?.userRole === 'TRAINER')
  const fullName        = computed(() => user.value?.fullName || '')
  const username        = computed(() => user.value?.username || '')
  const userRole        = computed(() => user.value?.userRole || null)
  const profilePicture  = computed(() => user.value?.profilePicture || null)

  async function login(credentials) {
    loading.value = true
    error.value   = null
    try {
      const response = await authService.login(credentials)
      if (response.success) {
        const d = response.data
        token.value = d.accessToken
        user.value  = {
          userId: d.userId, username: d.username,
          fullName: d.fullName, userRole: d.userRole,
          email: d.email, profilePicture: d.profilePicture || null
        }
        localStorage.setItem('access_token', d.accessToken)
        localStorage.setItem('user', JSON.stringify(user.value))
        await router.push(d.userRole === 'TRAINER' ? '/trainer' : '/dashboard')
      } else {
        error.value = response.message || 'Login failed.'
      }
    } catch (err) {
      if (err.response?.status === 401) {
        error.value = err.response.data?.message || 'Invalid username or password. Please try again.'
      } else if (!err.response) {
        error.value = 'Connection error. Please check your internet connection.'
      } else {
        error.value = 'An unexpected error occurred. Please try again later.'
      }
    } finally {
      loading.value = false
    }
  }

  async function register(formData) {
    loading.value = true
    error.value   = null
    try {
      const response = await authService.register(formData)
      if (response.success) {
        await router.push({ name: 'Login', query: { registered: 'true', username: response.data?.username } })
        return { fieldErrors: null }
      }
      error.value = response.message || 'Registration failed.'
      return { fieldErrors: null }
    } catch (err) {
      if (err.response?.status === 422) {
        error.value = err.response.data?.message || 'Please correct the errors below.'
        return { fieldErrors: err.response.data?.data || {} }
      }
      if (err.response?.status === 409) {
        error.value = err.response.data?.message || 'This email or username is already registered.'
        return { fieldErrors: null }
      }
      if (err.response?.status === 400) {
        error.value = err.response.data?.message || 'Please check your input.'
        return { fieldErrors: null }
      }
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

  async function logout() {
    await authService.logout()
    token.value = null
    user.value  = null
    localStorage.removeItem('access_token')
    localStorage.removeItem('user')
    await router.push({ name: 'Login' })
  }

  /**
   * Called by ProfileView after a successful profile save
   * so the sidebar avatar and AppLayout stay in sync.
   */
  function syncUserFromProfile(profileData) {
    if (!user.value || !profileData) return
    user.value = {
      ...user.value,
      fullName:       profileData.fullName,
      email:          profileData.email,
      profilePicture: profileData.profilePicture || null
    }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function clearError() { error.value = null }

  return {
    token, user, loading, error,
    isAuthenticated, isAthlete, isTrainer,
    fullName, username, userRole, profilePicture,
    login, register, logout, clearError, syncUserFromProfile
  }
})