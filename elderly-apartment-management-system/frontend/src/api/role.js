import request from './request'

export function getRoleList(params) {
  return request.get('/role', { params })
}

export function getRoleById(id) {
  return request.get(`/role/${id}`)
}

export function createRole(data) {
  return request.post('/role', data)
}

export function updateRole(id, data) {
  return request.put(`/role/${id}`, data)
}

export function deleteRole(id) {
  return request.delete(`/role/${id}`)
}

export function getAllRoles() {
  return request.get('/role/all')
}

export function getRolesByUserId(userId) {
  return request.get(`/role/user/${userId}`)
}

export function assignRolesToUser(userId, roleIds) {
  return request.post(`/role/user/${userId}/assign`, roleIds)
}
