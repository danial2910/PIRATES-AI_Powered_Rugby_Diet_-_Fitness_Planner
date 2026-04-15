/**
 * profileService.js — API calls for UC003: Update Profile
 *
 * GET  /api/profile          → load current profile (Step 2)
 * PUT  /api/profile/user     → update name/email/phone
 * PUT  /api/profile/athlete  → update athlete data
 * PUT  /api/profile/trainer  → update trainer data
 *
 * All calls include the JWT Bearer token automatically
 * via the Axios request interceptor in http.js.
 */
import http from './http'

const profileService = {

  /**
   * UC003 Step 2 — fetch current profile data for the logged-in user.
   * Called when ProfileView.vue mounts.
   */
  async getProfile() {
    const { data } = await http.get('/profile')
    return data
  },

  /**
   * UC003 Step 6 — update personal info (shared by both roles).
   * @param {{ fullName, email, phoneNumber }} payload
   */
  async updateUserInfo(payload) {
    const { data } = await http.put('/profile/user', payload)
    return data
  },

  /**
   * UC003 Step 6 — update athlete-specific profile.
   * @param {{ weight, height, age, rugbyPosition, goal, activityLevel,
   *           targetWeight, location, dietaryRestrictions,
   *           injuryNotes, trainingLevel }} payload
   */
  async updateAthleteProfile(payload) {
    const { data } = await http.put('/profile/athlete', payload)
    return data
  },

  /**
   * UC003 Step 6 — update trainer-specific profile.
   * @param {{ availability, expertise, experience, certifications }} payload
   */
  async updateTrainerProfile(payload) {
    const { data } = await http.put('/profile/trainer', payload)
    return data
  }
}

export default profileService