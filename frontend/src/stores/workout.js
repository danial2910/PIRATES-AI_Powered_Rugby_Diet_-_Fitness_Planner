/**
 * workout.js — Pinia store for UC004/UC005: Workout Planner
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import workoutService from '@/services/workoutService'

export const useWorkoutStore = defineStore('workout', () => {
  const plans          = ref([])
  const activePlan     = ref(null)
  const generating     = ref(false)
  const loadingPlans   = ref(false)
  const error          = ref(null)
  const generateSuccess = ref(false)

  /** UC004: Generate a new workout plan via Ollama */
  async function generatePlan(payload) {
    generating.value     = true
    error.value          = null
    generateSuccess.value = false
    try {
      const response = await workoutService.generatePlan(payload)
      if (response.success) {
        activePlan.value     = response.data
        generateSuccess.value = true
        // Prepend to saved plans list
        plans.value = [response.data, ...plans.value]
        return response.data
      }
      error.value = response.message || 'Failed to generate plan.'
      return null
    } catch (err) {
      if (err.response?.status === 503) {
        error.value = err.response.data?.message ||
          'AI engine is not available. Please make sure Ollama is running.'
      } else if (err.response?.status === 400) {
        error.value = err.response.data?.message || 'Please check your inputs.'
      } else if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
        error.value = 'Request timed out. Ollama is taking too long — try again or use a shorter prompt.'
      } else if (!err.response) {
        error.value = 'Connection error. Please check that the backend server is running.'
      } else {
        error.value = 'Failed to generate plan. Please try again.'
      }
      return null
    } finally {
      generating.value = false
    }
  }

  /** UC005: Load all saved plans */
  async function fetchPlans() {
    loadingPlans.value = true
    error.value        = null
    try {
      const response = await workoutService.getPlans()
      if (response.success) plans.value = response.data
    } catch {
      error.value = 'Failed to load saved plans.'
    } finally {
      loadingPlans.value = false
    }
  }

  /** UC005: Delete a plan */
  async function deletePlan(id) {
    error.value = null
    try {
      const response = await workoutService.deletePlan(id)
      if (response.success) {
        plans.value = plans.value.filter(p => p.id !== id)
        if (activePlan.value?.id === id) activePlan.value = null
      }
    } catch {
      error.value = 'Failed to delete plan. Please try again.'
    }
  }

  function setActivePlan(plan) { activePlan.value = plan }
  function clearError()        { error.value = null }

  return {
    plans, activePlan, generating, loadingPlans, error, generateSuccess,
    generatePlan, fetchPlans, deletePlan, setActivePlan, clearError
  }
})
