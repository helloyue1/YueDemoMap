import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const device = ref('desktop')
  const theme = ref('light')

  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  const closeSidebar = () => {
    sidebarCollapsed.value = true
  }

  const openSidebar = () => {
    sidebarCollapsed.value = false
  }

  const toggleDevice = (newDevice) => {
    device.value = newDevice
  }

  const toggleTheme = (newTheme) => {
    theme.value = newTheme
  }

  return {
    sidebarCollapsed,
    device,
    theme,
    toggleSidebar,
    closeSidebar,
    openSidebar,
    toggleDevice,
    toggleTheme
  }
})
