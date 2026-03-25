import request from './request'

export function getRoomList(params) {
  return request.get('/room', { params })
}

export function getRoomById(id) {
  return request.get(`/room/${id}`)
}

export function createRoom(data) {
  return request.post('/room', data)
}

export function updateRoom(id, data) {
  return request.put(`/room/${id}`, data)
}

export function deleteRoom(id) {
  return request.delete(`/room/${id}`)
}

export function getRoomsByType(type) {
  return request.get(`/room/type/${type}`)
}

export function getRoomsByStatus(status) {
  return request.get(`/room/status/${status}`)
}

export function getAvailableRooms() {
  return request.get('/room/available')
}

export function getOccupiedRooms() {
  return request.get('/room/occupied')
}

export function updateRoomStatus(id, status) {
  return request.put(`/room/${id}/status`, null, { params: { status } })
}

export function isRoomAvailable(id) {
  return request.get(`/room/${id}/available`)
}

export function increaseOccupancy(id) {
  return request.put(`/room/${id}/increase-occupancy`)
}

export function decreaseOccupancy(id) {
  return request.put(`/room/${id}/decrease-occupancy`)
}

// 获取房间统计数据
export function getRoomStats() {
  return request.get('/room/stats')
}

// 获取房间入住住客列表
export function getRoomElders(roomId) {
  return request.get(`/room/${roomId}/elders`)
}
