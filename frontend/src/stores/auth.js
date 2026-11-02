import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const SESSION_TOKEN_KEY = 'SESSION_TOKEN_KEY'

export const useAuthStore = defineStore('auth', () => {
  // ---- state ----
  const token = ref('')
  const currentUser = ref(null)
  const currentRole = ref(0) // 0=乘客, 1=司机

  // ---- getters ----
  const isLoggedIn = computed(() => !!token.value)

  const isDriver = computed(() => currentRole.value === 1)

  const isPassenger = computed(() => currentRole.value === 0)

  // ---- actions ----
  function setToken(val) {
    token.value = val
    localStorage.setItem(SESSION_TOKEN_KEY, val)
  }

  function setUser(user) {
    currentUser.value = user
    localStorage.setItem('currentUser', JSON.stringify(user))
  }

  function setRole(role) {
    // role: number (0=乘客, 1=司机) or string 'passenger'/'driver'
    const num = typeof role === 'string' ? (role === 'driver' ? 1 : 0) : role
    currentRole.value = num
    localStorage.setItem('currentRole', String(num))
  }

  function logout() {
    token.value = ''
    currentUser.value = null
    currentRole.value = 0
    localStorage.removeItem(SESSION_TOKEN_KEY)
    localStorage.removeItem('currentUser')
    localStorage.removeItem('currentRole')
  }

  function init() {
    const savedToken = localStorage.getItem(SESSION_TOKEN_KEY)
    if (savedToken) {
      token.value = savedToken
    }
    const savedUser = localStorage.getItem('currentUser')
    if (savedUser) {
      try {
        currentUser.value = JSON.parse(savedUser)
      } catch {
        currentUser.value = null
      }
    }
    const savedRole = localStorage.getItem('currentRole')
    if (savedRole !== null) {
      currentRole.value = Number(savedRole)
    }
  }

  return {
    token,
    currentUser,
    currentRole,
    isLoggedIn,
    isDriver,
    isPassenger,
    setToken,
    setUser,
    setRole,
    logout,
    init,
  }
})
