import request from './request'

// 菜品管理
export function getMealMenuList(params) {
  return request.get('/meal/menu/list', { params })
}

export function getAllMealMenu(type) {
  return request.get('/meal/menu/all', { params: { type } })
}

export function getMealMenuById(id) {
  return request.get(`/meal/menu/${id}`)
}

export function createMealMenu(data) {
  return request.post('/meal/menu', data)
}

export function updateMealMenu(id, data) {
  return request.put(`/meal/menu/${id}`, data)
}

export function deleteMealMenu(id) {
  return request.delete(`/meal/menu/${id}`)
}

// 住客订餐
export function getMealOrderList(params) {
  return request.get('/meal/order/list', { params })
}

export function getMealOrderByDate(date, mealType) {
  return request.get('/meal/order/date', { params: { date, mealType } })
}

export function getMealOrderByElderly(elderlyId) {
  return request.get(`/meal/order/elderly/${elderlyId}`)
}

export function getMealOrderById(id) {
  return request.get(`/meal/order/${id}`)
}

export function createMealOrder(data) {
  return request.post('/meal/order', data)
}

export function updateMealOrder(id, data) {
  return request.put(`/meal/order/${id}`, data)
}

export function deliverMealOrder(id) {
  return request.put(`/meal/order/${id}/deliver`)
}

export function completeMealOrder(id) {
  return request.put(`/meal/order/${id}/complete`)
}

export function cancelMealOrder(id) {
  return request.put(`/meal/order/${id}/cancel`)
}

export function deleteMealOrder(id) {
  return request.delete(`/meal/order/${id}`)
}
