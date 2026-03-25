import request from './request'

/**
 * 获取体检报告列表
 */
export function getHealthReportList(params) {
  return request.get('/health-report', { params })
}

/**
 * 生成体检报告
 */
export function generateHealthReport(data) {
  return request.post('/health-report/generate', null, { params: data })
}

/**
 * 获取报告详情
 */
export function getHealthReportById(id) {
  return request.get(`/health-report/${id}`)
}

/**
 * 审核报告
 */
export function auditHealthReport(id, data) {
  return request.put(`/health-report/${id}/audit`, null, { params: data })
}

/**
 * 删除报告
 */
export function deleteHealthReport(id) {
  return request.delete(`/health-report/${id}`)
}

/**
 * 获取健康统计数据
 */
export function getHealthStatistics(elderlyId) {
  return request.get(`/health-report/statistics/${elderlyId}`)
}

/**
 * 导出体检报告为PDF
 * @param {number} id - 报告ID
 * @param {string} fileName - 文件名
 */
export function exportHealthReport(id, fileName) {
  return request({
    url: `/health-report/export/${id}`,
    method: 'get',
    responseType: 'blob'
  }).then(response => {
    // 创建下载链接
    const blob = new Blob([response], { type: 'application/pdf' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = fileName || `体检报告_${id}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(link.href)
    return Promise.resolve()
  })
}
