import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getCurrentUser } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const permissions = ref([])

  const isLoggedIn = computed(() => !!token.value)

  const userRoles = computed(() => {
    if (!user.value || !user.value.roles) return []
    return user.value.roles.map(r => r.name)
  })

  const hasRole = (role) => {
    return userRoles.value.includes(role)
  }

  const hasPermission = (permission) => {
    return permissions.value.includes(permission)
  }

  async function doLogin(username, password, platform = 'web') {
    try {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      const res = await login({ username, password, platform })
      if (res.code === 200) {
        token.value = res.data.token
        user.value = res.data.user
        localStorage.setItem('token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))
        return { success: true }
      } else {
        return { success: false, message: res.message || '登录失败' }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  async function doLogout(redirect = true) {
    try {
      await logout()
    } catch (error) {
      console.error('Logout error:', error)
    }
    token.value = ''
    user.value = null
    permissions.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    if (redirect) {
      router.push({ name: 'Login' })
    }
  }

  async function fetchCurrentUser() {
    if (!token.value) return
    try {
      const res = await getCurrentUser()
      if (res.code === 200) {
        user.value = res.data
        localStorage.setItem('user', JSON.stringify(user.value))
      }
    } catch (error) {
      console.error('Fetch user error:', error)
      if (error.response?.status === 401) {
        doLogout()
      }
    }
  }

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return {
    token,
    user,
    permissions,
    isLoggedIn,
    userRoles,
    hasRole,
    hasPermission,
    doLogin,
    doLogout,
    fetchCurrentUser,
    setUser
  }
})
