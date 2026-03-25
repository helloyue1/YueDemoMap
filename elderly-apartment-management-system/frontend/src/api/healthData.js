import request from './request'

/**
 * 健康数据API（日常健康监测记录）
 */

/**
 * 获取健康数据列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 * @param {string} params.elderlyName - 住客姓名
 * @param {number} params.recordType - 记录类型
 * @param {string} params.startDate - 开始日期
 * @param {string} params.endDate - 结束日期
 */
export function getHealthDataList(params) {
  return request({
    url: '/health-data',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取健康数据
 * @param {number} id - 记录ID
 */
export function getHealthDataById(id) {
  return request({
    url: `/health-data/${id}`,
    method: 'get'
  })
}

/**
 * 获取住客的健康数据列表
 * @param {number} elderlyId - 住客ID
 */
export function getHealthDataByElderlyId(elderlyId) {
  return request({
    url: `/health-data/elderly/${elderlyId}`,
    method: 'get'
  })
}

/**
 * 创建健康数据记录
 * @param {Object} data - 健康数据
 */
export function createHealthData(data) {
  return request({
    url: '/health-data',
    method: 'post',
    data
  })
}

/**
 * 更新健康数据记录
 * @param {number} id - 记录ID
 * @param {Object} data - 健康数据
 */
export function updateHealthData(id, data) {
  return request({
    url: `/health-data/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除健康数据记录
 * @param {number} id - 记录ID
 */
export function deleteHealthData(id) {
  return request({
    url: `/health-data/${id}`,
    method: 'delete'
  })
}

/**
 * 获取健康数据统计
 */
export function getHealthDataStatistics() {
  return request({
    url: '/health-data/statistics',
    method: 'get'
  })
}
