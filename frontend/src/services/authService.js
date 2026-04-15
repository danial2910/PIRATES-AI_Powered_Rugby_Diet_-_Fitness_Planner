import http from './http'
const authService = {
  async login(credentials) { const { data } = await http.post('/auth/login', credentials); return data },
  async register(userData) { const { data } = await http.post('/auth/register', userData); return data },
  async verifyToken() { const { data } = await http.get('/auth/verify'); return data },
  async logout() { try { await http.post('/auth/logout') } catch {} }
}
export default authService
