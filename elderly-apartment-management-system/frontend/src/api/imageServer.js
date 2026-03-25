import axios from 'axios'
import { ElMessage } from 'element-plus'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://8.152.163.213:8080'
const IMAGE_SERVER_URL = import.meta.env.VITE_IMAGE_SERVER_URL || 'http://8.152.163.213:3001'

const imageApi = axios.create({
  baseURL: IMAGE_SERVER_URL,
  timeout: 60000
})

imageApi.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.error('API请求错误:', error)
    const message = error.response?.data?.message || error.message || '网络请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export const uploadImageToServer = (file, directory = 'temp', onProgress = null) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('directory', directory)

  const config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }

  if (onProgress) {
    config.onUploadProgress = (event) => {
      const percent = Math.round((event.loaded * 100) / event.total)
      onProgress({
        percentage: Math.min(percent, 100),
        loaded: event.loaded,
        total: event.total
      })
    }
  }

  return imageApi.post('/upload', formData, config)
}

export const uploadImagesToServer = (files, directory = 'temp', onProgress = null) => {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  formData.append('directory', directory)

  const config = {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }

  if (onProgress) {
    config.onUploadProgress = (event) => {
      const percent = Math.round((event.loaded * 100) / event.total)
      onProgress({
        percentage: Math.min(percent, 100),
        loaded: event.loaded,
        total: event.total
      })
    }
  }

  return imageApi.post('/upload/batch', formData, config)
}

export const deleteImageFromServer = (imageUrl) => {
  return imageApi.delete('/delete', {
    params: { imageUrl }
  })
}

export const getImageUrlFromServer = (relativePath) => {
  return imageApi.get('/url', {
    params: { path: relativePath }
  })
}

/**
 * 构建图片完整URL
 * @param {string} path - 图片路径（可能是相对路径、完整URL或带/uploads/前缀的路径）
 * @returns {string} 完整URL
 */
export const buildImageUrl = (path) => {
  console.log('buildImageUrl input:', path)
  
  if (!path) {
    console.log('buildImageUrl: empty path')
    return ''
  }
  
  // 如果已经是完整URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    // 如果是旧的后端地址(8080端口)，替换为新的图片服务器地址(3001端口)
    if (path.includes(':8080')) {
      const newUrl = path.replace(/http:\/\/[^:]+:8080/, IMAGE_SERVER_URL)
      console.log('buildImageUrl: replaced 8080 url:', newUrl)
      return newUrl
    }
    console.log('buildImageUrl: already full url:', path)
    return path
  }
  
  // 如果以/uploads/开头，直接拼接
  if (path.startsWith('/uploads/')) {
    const url = `${IMAGE_SERVER_URL}${path}`
    console.log('buildImageUrl: with /uploads/ prefix:', url)
    return url
  }
  
  // 处理相对路径（如 rooms/20260208/test.jpg）
  // 移除开头的斜杠避免双斜杠
  const cleanPath = path.startsWith('/') ? path.substring(1) : path
  const url = `${IMAGE_SERVER_URL}/uploads/${cleanPath}`
  console.log('buildImageUrl: relative path:', { cleanPath, url })
  
  return url
}

/**
 * 从URL中提取相对路径
 * @param {string} url - 图片URL
 * @returns {string} 相对路径
 */
export const extractRelativePath = (url) => {
  if (!url) return ''
  
  // 处理 blob URL（浏览器临时URL）
  if (url.startsWith('blob:')) {
    return ''
  }
  
  // 如果已经是相对路径（不包含/uploads前缀和http），直接返回
  if (!url.startsWith('http') && !url.startsWith('/uploads')) {
    if (url.includes('/uploads/')) {
      const match = url.match(/\/uploads\/(.+)$/)
      return match ? match[1] : ''
    }
    return url
  }
  
  // 处理错误格式的路径 /uploads/blob:http://...
  if (url.includes('blob:')) {
    return ''
  }
  
  // 从完整URL或/upload路径中提取相对路径
  // 匹配 /uploads/后面的内容 或 http://.../uploads/后面的内容
  const match = url.match(/\/uploads\/(.+)$/)
  return match ? match[1] : ''
}

/**
 * 解析图片列表字符串
 * @param {string} imagesString - 逗号分隔的图片路径字符串
 * @returns {Array} 图片对象数组
 */
export const parseImageList = (imagesString) => {
  if (!imagesString) return []
  
  return imagesString.split(',')
    .map(url => url.trim())
    .filter(Boolean)
    .map(url => {
      const relativePath = extractRelativePath(url)
      return {
        name: relativePath.split('/').pop() || 'image',
        relativePath,
        url: buildImageUrl(relativePath),
        originalUrl: url
      }
    })
    .filter(item => item.relativePath)
}

export { IMAGE_SERVER_URL }
export default {
  buildImageUrl,
  extractRelativePath,
  parseImageList,
  IMAGE_SERVER_URL
}
