import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    // 如果是blob类型响应，直接返回response对象
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const res = response.data
    // 如果返回的是数组，直接包装返回
    if (Array.isArray(res)) {
      return { success: true, data: res }
    }
    // 如果已经是标准格式
    if (res.success !== undefined) {
      if (res.success) {
        return res
      } else {
        // 检查是否是未登录错误
        if (res.code === 401 || res.message === '未登录') {
          handleUnauthorized()
        }
        return Promise.reject(new Error(res.message || '请求失败'))
      }
    }
    // 其他情况直接返回
    return res
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        handleUnauthorized()
      }
      // 处理后端返回的错误消息
      if (data && data.message) {
        return Promise.reject(new Error(data.message))
      }
      return Promise.reject(data)
    }
    return Promise.reject(error)
  }
)

// 处理未登录的统一方法
let isRedirecting = false
function handleUnauthorized() {
  if (isRedirecting) return
  isRedirecting = true
  
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.error('登录已过期，请重新登录')
  
  // 使用setTimeout避免重复跳转
  setTimeout(() => {
    window.location.href = '/login'
    isRedirecting = false
  }, 100)
}

export default service
