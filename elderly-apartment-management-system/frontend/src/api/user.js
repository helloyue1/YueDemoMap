import request from './request'

export function getUserList(params) {
  return request.get('/user', { params })
}

export function getUserById(id) {
  return request.get(`/user/${id}`)
}

export function createUser(data) {
  return request.post('/user', data)
}

export function updateUser(id, data) {
  return request.put(`/user/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

export function getUserByUsername(username) {
  return request.get(`/user/username/${username}`)
}

export function resetUserPassword(id, newPassword) {
  return request.post(`/user/${id}/reset-password`, newPassword)
}

export function getUsersByPage(page, size) {
  return request.get('/user/page', { params: { page, size } })
}

export function updatePassword(data) {
  return request.put('/user/password', data)
}
