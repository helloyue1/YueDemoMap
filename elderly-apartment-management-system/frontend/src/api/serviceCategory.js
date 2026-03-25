import request from './request'

// ==================== 服务分类相关接口 ====================
export function getServiceCategoryList() {
  return request.get('/service-categories')
}

export function getActiveServiceCategories() {
  return request.get('/service-categories/active')
}

export function getServiceCategoryById(id) {
  return request.get(`/service-categories/${id}`)
}

export function createServiceCategory(data) {
  return request.post('/service-categories', data)
}

export function updateServiceCategory(id, data) {
  return request.put(`/service-categories/${id}`, data)
}

export function deleteServiceCategory(id) {
  return request.delete(`/service-categories/${id}`)
}

// ==================== 服务项相关接口 ====================
export function getServiceItemList(params) {
  return request.get('/service-items', { params })
}

export function getAllServiceItems() {
  return request.get('/service-items/all')
}

export function getServiceItemsByCategory(categoryId) {
  return request.get(`/service-items/category/${categoryId}`)
}

export function getServiceItemById(id) {
  return request.get(`/service-items/${id}`)
}

export function createServiceItem(data) {
  return request.post('/service-items', data)
}

export function updateServiceItem(id, data) {
  return request.put(`/service-items/${id}`, data)
}

export function deleteServiceItem(id) {
  return request.delete(`/service-items/${id}`)
}
