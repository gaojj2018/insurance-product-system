import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]')
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo.username || '',
    role: (state) => state.userInfo.role || '',
    orgId: (state) => state.userInfo.orgId || null
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    setPermissions(permissions) {
      this.permissions = permissions
      localStorage.setItem('permissions', JSON.stringify(permissions))
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      this.permissions = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('permissions')
    },
    hasPermission(permission) {
      return this.permissions.includes(permission)
    }
  }
})
