/**
 * workoutService.js — API calls for UC004/UC005
 *
 * POST /api/workout/generate   → generate a new AI workout plan
 * GET  /api/workout/plans      → list saved plans
 * GET  /api/workout/plans/:id  → get a single plan
 * DELETE /api/workout/plans/:id → delete a plan
 */
import http from './http'

const workoutService = {
  async generatePlan(payload) {
    // Ollama generation can take 30–90 seconds — override the default 10s timeout
    const { data } = await http.post('/workout/generate', payload, { timeout: 120000 })
    return data
  },

  async getPlans() {
    const { data } = await http.get('/workout/plans')
    return data
  },

  async getPlan(id) {
    const { data } = await http.get(`/workout/plans/${id}`)
    return data
  },

  async deletePlan(id) {
    const { data } = await http.delete(`/workout/plans/${id}`)
    return data
  }
}

export default workoutService
