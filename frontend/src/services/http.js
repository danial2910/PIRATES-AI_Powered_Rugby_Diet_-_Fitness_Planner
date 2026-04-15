/**
 * http.js — Axios instance with JWT Bearer token interceptor
 *
 * Every API call to Spring Boot backend goes through this instance.
 * Automatically attaches the JWT token from localStorage to
 * the Authorization header.
 *
 * UC001 Post-condition: once logged in, all subsequent requests
 * carry the token automatically.
 */
import axios from 'axios'
import router from '@/router'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// ─── Request Interceptor ────────────────────────────────────────────────────
// Attach JWT Bearer token to every outgoing request
http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ─── Response Interceptor ───────────────────────────────────────────────────
// If backend returns 401, token is expired/invalid → redirect to login
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear stale auth state
      localStorage.removeItem('access_token')
      localStorage.removeItem('user')

      // Redirect to login with session-expired message
      router.push({
        name: 'Login',
        query: { sessionExpired: 'true' }
      })
    }
    return Promise.reject(error)
  }
)

export default http
