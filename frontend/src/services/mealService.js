/**
 * mealService.js — API calls for UC006/UC007
 *
 * POST   /api/meal/generate      → generate a new AI 7-day meal plan
 * GET    /api/meal/plans         → list saved plans
 * GET    /api/meal/plans/:id     → get a single plan
 * DELETE /api/meal/plans/:id     → delete a plan
 */
import http from './http'

const mealService = {
  async generatePlan(payload) {
    // 7-day meal generation can take 2-4 minutes on llama3.2 — use extended timeout
    const { data } = await http.post('/meal/generate', payload, { timeout: 250000 })
    return data
  },

  async getPlans() {
    const { data } = await http.get('/meal/plans')
    return data
  },

  async getPlan(id) {
    const { data } = await http.get(`/meal/plans/${id}`)
    return data
  },

  async deletePlan(id) {
    const { data } = await http.delete(`/meal/plans/${id}`)
    return data
  }
}

export default mealService
