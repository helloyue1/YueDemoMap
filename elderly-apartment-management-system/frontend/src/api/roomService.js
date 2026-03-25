import request from './request'

// 获取服务申请列表
export function getRoomServiceRequests(params) {
  return request({
    url: '/room-service/requests/all',
    method: 'get',
    params
  })
}

// 获取服务申请详情
export function getRoomServiceRequest(id) {
  return request({
    url: `/room-service/requests/${id}`,
    method: 'get'
  })
}

// 创建服务申请
export function createRoomServiceRequest(data) {
  return request({
    url: '/room-service/requests',
    method: 'post',
    data
  })
}

// 更新服务申请
export function updateRoomServiceRequest(id, data) {
  return request({
    url: `/room-service/requests/${id}`,
    method: 'put',
    data
  })
}

// 删除服务申请
export function deleteRoomServiceRequest(id) {
  return request({
    url: `/room-service/requests/${id}`,
    method: 'delete'
  })
}

// 分配处理人
export function assignHandler(id, handlerId, handlerName) {
  return request({
    url: `/room-service/requests/${id}/assign`,
    method: 'post',
    params: { handlerId, handlerName }
  })
}

// 开始处理
export function startHandle(id, notes) {
  return request({
    url: `/room-service/requests/${id}/start`,
    method: 'post',
    params: { notes }
  })
}

// 完成服务
export function completeRequest(id, notes) {
  return request({
    url: `/room-service/requests/${id}/complete`,
    method: 'post',
    params: { notes }
  })
}

// 取消服务
export function cancelRequest(id, reason) {
  return request({
    url: `/room-service/requests/${id}/cancel`,
    method: 'post',
    params: { reason }
  })
}

// 获取房间最近的服务记录
export function getRecentRoomServices(roomId, limit = 5) {
  return request({
    url: `/room-service/requests/room/${roomId}/recent`,
    method: 'get',
    params: { limit }
  })
}

// 获取评价列表
export function getRoomServiceEvaluations(params) {
  return request({
    url: '/room-service/evaluations',
    method: 'get',
    params
  })
}

// 创建评价
export function createRoomServiceEvaluation(data) {
  return request({
    url: '/room-service/evaluations',
    method: 'post',
    data
  })
}

// 获取房间评价统计
export function getRoomEvaluationStats(roomId) {
  return request({
    url: `/room-service/evaluations/room/${roomId}/stats`,
    method: 'get'
  })
}

// 获取统计数据
export function getRoomServiceStats() {
  return request({
    url: '/room-service/stats',
    method: 'get'
  })
}

// 服务类型选项
export const SERVICE_TYPES = [
  { value: 'CLEANING', label: '保洁服务', color: '#67C23A' },
  { value: 'REPAIR', label: '维修服务', color: '#E6A23C' },
  { value: 'MAINTENANCE', label: '设备维护', color: '#409EFF' },
  { value: 'OTHER', label: '其他服务', color: '#909399' }
]

// 服务子类型
export const SERVICE_SUBTYPES = {
  CLEANING: [
    { value: '日常保洁', label: '日常保洁' },
    { value: '深度清洁', label: '深度清洁' },
    { value: '专项保洁', label: '专项保洁' }
  ],
  REPAIR: [
    { value: '水电维修', label: '水电维修' },
    { value: '电器维修', label: '电器维修' },
    { value: '家具维修', label: '家具维修' },
    { value: '其他维修', label: '其他维修' }
  ],
  MAINTENANCE: [
    { value: '空调维护', label: '空调维护' },
    { value: '暖气维护', label: '暖气维护' },
    { value: '门窗维护', label: '门窗维护' },
    { value: '设备检查', label: '设备检查' }
  ],
  OTHER: [
    { value: '其他服务', label: '其他服务' }
  ]
}

// 紧急程度
export const URGENCY_LEVELS = [
  { value: 'URGENT', label: '紧急', color: '#F56C6C', type: 'danger' },
  { value: 'NORMAL', label: '一般', color: '#E6A23C', type: 'warning' },
  { value: 'LOW', label: '常规', color: '#909399', type: 'info' }
]

// 服务状态
export const SERVICE_STATUSES = [
  { value: 'PENDING', label: '待处理', color: '#909399', type: 'info' },
  { value: 'ASSIGNED', label: '已分配', color: '#409EFF', type: 'primary' },
  { value: 'IN_PROGRESS', label: '处理中', color: '#E6A23C', type: 'warning' },
  { value: 'COMPLETED', label: '已完成', color: '#67C23A', type: 'success' },
  { value: 'CANCELLED', label: '已取消', color: '#F56C6C', type: 'danger' }
]

// 获取标签文本
export function getServiceTypeLabel(value) {
  const item = SERVICE_TYPES.find(t => t.value === value)
  return item ? item.label : value
}

export function getServiceTypeColor(value) {
  const item = SERVICE_TYPES.find(t => t.value === value)
  return item ? item.color : '#909399'
}

export function getUrgencyLabel(value) {
  const item = URGENCY_LEVELS.find(u => u.value === value)
  return item ? item.label : value
}

export function getUrgencyType(value) {
  const item = URGENCY_LEVELS.find(u => u.value === value)
  return item ? item.type : 'info'
}

export function getStatusLabel(value) {
  const item = SERVICE_STATUSES.find(s => s.value === value)
  return item ? item.label : value
}

export function getStatusType(value) {
  const item = SERVICE_STATUSES.find(s => s.value === value)
  return item ? item.type : 'info'
}
