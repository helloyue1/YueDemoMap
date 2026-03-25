import { ElMessage } from 'element-plus'

const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
const MAX_SIZE_MB = 5
const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024

export const validateImageFile = (file) => {
  const errors = []

  if (!file) {
    errors.push('请选择文件')
    return { valid: false, errors }
  }

  if (!ALLOWED_TYPES.includes(file.type)) {
    errors.push(`不支持的图片格式 ${file.name}，仅支持 JPEG、PNG、GIF、WebP`)
  }

  if (file.size > MAX_SIZE_BYTES) {
    errors.push(`文件 ${file.name} 大小超过 ${MAX_SIZE_MB}MB 限制`)
  }

  if (errors.length > 0) {
    return { valid: false, errors }
  }

  return { valid: true, errors: [] }
}

export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export const getFileExtension = (filename) => {
  if (!filename || filename.lastIndexOf('.') === -1) {
    return '.jpg'
  }
  return filename.substring(filename.lastIndexOf('.')).toLowerCase()
}

export const isImageFile = (file) => {
  return ALLOWED_TYPES.includes(file.type)
}

export const createImageUploadHandler = (uploadFunction, options = {}) => {
  const {
    onProgress = () => {},
    onSuccess = () => {},
    onError = () => {},
    directory = 'temp'
  } = options

  return async (file) => {
    const validation = validateImageFile(file)
    
    if (!validation.valid) {
      const errorMsg = validation.errors.join('; ')
      ElMessage.error(errorMsg)
      onError(new Error(errorMsg))
      return null
    }

    try {
      const result = await uploadFunction(file, directory, onProgress)
      onSuccess(result)
      return result
    } catch (error) {
      const errorMsg = error.message || '上传失败'
      ElMessage.error(errorMsg)
      onError(error)
      return null
    }
  }
}

export const extractRelativePath = (url) => {
  if (!url) return ''
  
  if (url.startsWith('blob:')) {
    return ''
  }
  
  if (!url.startsWith('http') && !url.startsWith('/uploads')) {
    if (url.includes('/uploads/')) {
      const match = url.match(/\/uploads\/(.+)$/)
      return match ? match[1] : ''
    }
    return url
  }
  
  if (url.includes('blob:')) {
    return ''
  }
  
  const match = url.match(/\/uploads\/(.+)$/)
  return match ? match[1] : ''
}

export const buildImageUrl = (relativePath, baseUrl = import.meta.env.VITE_IMAGE_SERVER_URL || 'http://localhost:3001') => {
  if (!relativePath) return ''
  
  if (relativePath.startsWith('http')) {
    return relativePath
  }
  
  if (relativePath.startsWith('/uploads/')) {
    return `${baseUrl}${relativePath}`
  }
  
  return `${baseUrl}/uploads/${relativePath}`
}

export const parseImageList = (imagesString) => {
  if (!imagesString) return []
  
  return imagesString.split(',')
    .map(url => url.trim())
    .filter(Boolean)
    .map(url => ({
      relativePath: extractRelativePath(url),
      fullUrl: buildImageUrl(extractRelativePath(url)),
      originalUrl: url
    }))
    .filter(item => item.relativePath)
}

export const createProgressHandler = (onProgress) => {
  return (event) => {
    if (event.total > 0) {
      const percent = Math.round((event.loaded * 100) / event.total)
      onProgress({
        percentage: percent,
        loaded: event.loaded,
        total: event.total
      })
    }
  }
}

export const convertFileToBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}

export const getImageDimensions = (file) => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      resolve({
        width: img.width,
        height: img.height,
        aspectRatio: img.width / img.height
      })
      URL.revokeObjectURL(img.src)
    }
    img.onerror = reject
    img.src = URL.createObjectURL(file)
  })
}

export const compressImage = (file, maxWidth = 1920, quality = 0.8) => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      const canvas = document.createElement('canvas')
      let { width, height } = img
      
      if (width > maxWidth) {
        height = Math.round((height * maxWidth) / width)
        width = maxWidth
      }
      
      canvas.width = width
      canvas.height = height
      
      const ctx = canvas.getContext('2d')
      ctx.drawImage(img, 0, 0, width, height)
      
      canvas.toBlob(
        (blob) => {
          if (blob) {
            resolve(new File([blob], file.name, {
              type: file.type,
              lastModified: Date.now()
            }))
          } else {
            reject(new Error('图片压缩失败'))
          }
        },
        file.type,
        quality
      )
      
      URL.revokeObjectURL(img.src)
    }
    img.onerror = reject
    img.src = URL.createObjectURL(file)
  })
}

export { ALLOWED_TYPES, MAX_SIZE_MB, MAX_SIZE_BYTES }
