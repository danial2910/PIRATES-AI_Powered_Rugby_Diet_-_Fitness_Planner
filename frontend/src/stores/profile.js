/**
 * profile.js — Pinia store for UC003: Update Profile
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import profileService from '@/services/profileService'
import { useAuthStore } from '@/stores/auth'

export const useProfileStore = defineStore('profile', () => {

  const profile     = ref(null)
  const loading     = ref(false)
  const saveSuccess = ref(false)
  const error       = ref(null)

  async function fetchProfile() {
    loading.value = true
    error.value   = null
    try {
      const response = await profileService.getProfile()
      if (response.success) profile.value = response.data
    } catch {
      error.value = 'Failed to load profile. Please refresh the page.'
    } finally {
      loading.value = false
    }
  }

  async function _save(apiCall) {
    loading.value     = true
    error.value       = null
    saveSuccess.value = false
    try {
      const response = await apiCall()
      if (response.success) {
        profile.value     = response.data
        saveSuccess.value = true
        // Keep sidebar avatar / fullName in sync
        const authStore = useAuthStore()
        authStore.syncUserFromProfile(response.data)
        setTimeout(() => { saveSuccess.value = false }, 3000)
        return { fieldErrors: null }
      }
      error.value = response.message || 'Update failed.'
      return { fieldErrors: null }
    } catch (err) {
      if (err.response?.status === 422) {
        error.value = err.response.data?.message || 'Please correct the errors below.'
        return { fieldErrors: err.response.data?.data || {} }
      }
      if (err.response?.status === 409) {
        error.value = err.response.data?.message || 'This email is already in use.'
        return { fieldErrors: null }
      }
      error.value = 'An unexpected error occurred. Please try again.'
      return { fieldErrors: null }
    } finally {
      loading.value = false
    }
  }

  async function saveUserInfo(payload)       { return _save(() => profileService.updateUserInfo(payload)) }
  async function saveAthleteProfile(payload) { return _save(() => profileService.updateAthleteProfile(payload)) }
  async function saveTrainerProfile(payload) { return _save(() => profileService.updateTrainerProfile(payload)) }

  function clearError() { error.value = null }

  return {
    profile, loading, saveSuccess, error,
    fetchProfile, saveUserInfo, saveAthleteProfile, saveTrainerProfile, clearError
  }
})