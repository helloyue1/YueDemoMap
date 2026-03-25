import request from './request'

// 获取家属列表
export function getFamilyList(params) {
  return request({
    url: '/family/list',
    method: 'get',
    params
  })
}

// 根据ID获取家属详情
export function getFamilyById(id) {
  return request({
    url: `/family/${id}`,
    method: 'get'
  })
}

// 获取指定老人的所有家属
export function getFamilyByElderlyId(elderlyId) {
  return request({
    url: `/family/elderly/${elderlyId}`,
    method: 'get'
  })
}

// 添加家属
export function createFamily(data) {
  return request({
    url: '/family',
    method: 'post',
    data
  })
}

// 更新家属信息
export function updateFamily(id, data) {
  return request({
    url: `/family/${id}`,
    method: 'put',
    data
  })
}

// 删除家属
export function deleteFamily(id) {
  return request({
    url: `/family/${id}`,
    method: 'delete'
  })
}

// 获取紧急联系人
export function getEmergencyContacts(elderlyId) {
  return request({
    url: `/family/elderly/${elderlyId}/emergency`,
    method: 'get'
  })
}

// 获取主要联系人
export function getPrimaryContacts(elderlyId) {
  return request({
    url: `/family/elderly/${elderlyId}/primary`,
    method: 'get'
  })
}

// 设置紧急联系人
export function setEmergencyContact(id, isEmergency) {
  return request({
    url: `/family/${id}/emergency`,
    method: 'put',
    params: { isEmergency }
  })
}

// 设置主要联系人
export function setPrimaryContact(id, isPrimary) {
  return request({
    url: `/family/${id}/primary`,
    method: 'put',
    params: { isPrimary }
  })
}

// 获取关系选项
export function getRelationshipOptions() {
  return request({
    url: '/family/relationships',
    method: 'get'
  })
}
