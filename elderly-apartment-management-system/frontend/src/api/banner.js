import request from './request'

// 获取轮播图列表
export function getBannerList(params) {
  return request({
    url: '/banners',
    method: 'get',
    params
  })
}

// 获取启用的轮播图列表
export function getActiveBanners(position = 'home') {
  return request({
    url: '/banners/active',
    method: 'get',
    params: { position }
  })
}

// 获取轮播图详情
export function getBannerById(id) {
  return request({
    url: `/banners/${id}`,
    method: 'get'
  })
}

// 创建轮播图
export function createBanner(data) {
  return request({
    url: '/banners',
    method: 'post',
    data
  })
}

// 更新轮播图
export function updateBanner(id, data) {
  return request({
    url: `/banners/${id}`,
    method: 'put',
    data
  })
}

// 删除轮播图
export function deleteBanner(id) {
  return request({
    url: `/banners/${id}`,
    method: 'delete'
  })
}

// 更新轮播图状态
export function updateBannerStatus(id, status) {
  return request({
    url: `/banners/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新轮播图排序
export function updateBannerSort(id, sortOrder) {
  return request({
    url: `/banners/${id}/sort`,
    method: 'put',
    data: { sortOrder }
  })
}

// 获取轮播图统计
export function getBannerStatistics() {
  return request({
    url: '/banners/statistics',
    method: 'get'
  })
}
