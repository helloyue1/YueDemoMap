import request from './request'

/**
 * 获取健康档案列表
 */
export function getHealthRecordList(params) {
  return request.get('/health-record', { params })
}

/**
 * 根据ID获取健康档案
 */
export function getHealthRecordById(id) {
  return request.get(`/health-record/${id}`)
}

/**
 * 获取住客的健康档案列表
 */
export function getHealthRecordsByElderlyId(elderlyId) {
  return request.get(`/health-record/elderly/${elderlyId}`)
}

/**
 * 创建健康档案
 */
export function createHealthRecord(data) {
  return request.post('/health-record', data)
}

/**
 * 更新健康档案
 */
export function updateHealthRecord(id, data) {
  return request.put(`/health-record/${id}`, data)
}

/**
 * 删除健康档案
 */
export function deleteHealthRecord(id) {
  return request.delete(`/health-record/${id}`)
}

/**
 * 获取健康档案统计
 */
export function getHealthStatistics() {
  return request.get('/health-record/statistics')
}
