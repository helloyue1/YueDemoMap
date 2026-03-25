import request from './request'
import { uploadImageToServer, uploadImagesToServer, deleteImageFromServer, buildImageUrl, extractRelativePath, parseImageList, IMAGE_SERVER_URL } from './imageServer'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://8.152.163.213:8080'

export const uploadImage = async (file, directory = 'temp', onProgress = null) => {
  try {
    const response = await uploadImageToServer(file, directory, onProgress)
    return response.data
  } catch (error) {
    console.error('上传图片失败:', error)
    throw error
  }
}

export const uploadImages = async (files, directory = 'temp', onProgress = null) => {
  try {
    const response = await uploadImagesToServer(files, directory, onProgress)
    return response.data
  } catch (error) {
    console.error('批量上传图片失败:', error)
    throw error
  }
}

export const deleteImage = async (imageUrl) => {
  try {
    const response = await deleteImageFromServer(imageUrl)
    return response.data
  } catch (error) {
    console.error('删除图片失败:', error)
    throw error
  }
}

export const getFullImageUrl = (relativePath) => {
  return buildImageUrl(relativePath)
}

export const getImageUrl = buildImageUrl

export const extractPath = extractRelativePath

export const parseImages = parseImageList

export { IMAGE_SERVER_URL }

export const getBackendImageUrl = (path) => {
  if (!path) return ''

  if (path.startsWith('http')) {
    return path
  }

  if (path.startsWith('/uploads/')) {
    return `${API_BASE_URL}${path}`
  }

  const cleanPath = path.startsWith('/') ? path.substring(1) : path
  return `${API_BASE_URL}/uploads/${cleanPath}`
}
