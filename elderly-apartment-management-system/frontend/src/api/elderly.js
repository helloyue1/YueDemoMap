import request from './request'

// 老人管理接口 - 已合并到 UserController，使用 /user/elderly 前缀

export function getElderlyList(params) {
  return request.get('/user/elderly', { params })
}

export function getElderlyById(id) {
  return request.get(`/user/elderly/${id}`)
}

export function createElderly(data) {
  return request.post('/user/elderly', data)
}

export function updateElderly(id, data) {
  return request.put(`/user/elderly/${id}`, data)
}

export function deleteElderly(id) {
  return request.delete(`/user/elderly/${id}`)
}

export function checkInElderly(data) {
  // 入住功能通过创建老人时设置状态实现
  return request.post('/user/elderly', { ...data, status: 1 })
}

export function checkOutElderly(id) {
  return request.post(`/user/elderly/${id}/check-out`)
}

export function updateHealthStatus(id, healthStatus) {
  return request.put(`/user/elderly/${id}/health-status`, healthStatus)
}

export function getAllStaying() {
  return request.get('/user/elderly/staying')
}

export function getAllCheckedOut() {
  return request.get('/user/elderly/checked-out')
}

export function getAllOnLeave() {
  // 请假状态的老人列表 - 通过查询参数实现
  return request.get('/user/elderly', { params: { status: 3 } })
}

export function searchElderly(name) {
  return request.get('/user/elderly/search', { params: { name } })
}

// 健康档案相关接口 - 这些接口保持不变，由 HealthRecordController 处理
export function getHealthRecords(elderlyId) {
  return request.get(`/health/records`, { params: { userId: elderlyId } })
}

export function createHealthRecord(elderlyId, data) {
  return request.post('/health/records', { ...data, userId: elderlyId })
}

export function updateHealthRecord(recordId, data) {
  return request.put(`/health/records/${recordId}`, data)
}

export function deleteHealthRecord(recordId) {
  return request.delete(`/health/records/${recordId}`)
}

export function getHealthArchiveList() {
  return request.get('/health/archives')
}

export function createHealthArchive(data) {
  return request.post('/health/archives', data)
}

export function updateHealthArchive(id, data) {
  return request.put(`/health/archives/${id}`, data)
}

export function getHealthRecordList(params) {
  return request.get('/health/records', { params })
}

export function getCheckupReportList(params) {
  return request.get('/health/checkup-reports', { params })
}

// 余额管理接口
export function getBalance(userId) {
  return request.get('/user/elderly/balance', { params: { userId } })
}

export function recharge(data) {
  return request.post('/user/elderly/balance/recharge', data)
}

export function withdraw(data) {
  return request.post('/user/elderly/balance/withdraw', data)
}

export function getBalanceRecords(userId) {
  return request.get('/user/elderly/balance/records', { params: { userId } })
}
