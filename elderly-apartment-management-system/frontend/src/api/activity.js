import request from './request'

// 活动管理API
export function getActivityList(params) {
  return request.get('/activities', { params })
}

export function getActivityById(id) {
  return request.get(`/activities/${id}`)
}

export function createActivity(data) {
  return request.post('/activities', data)
}

export function updateActivity(id, data) {
  return request.put(`/activities/${id}`, data)
}

export function deleteActivity(id) {
  return request.delete(`/activities/${id}`)
}

export function publishActivity(id) {
  return request.post(`/activities/${id}/publish`)
}

export function cancelActivity(id) {
  return request.post(`/activities/${id}/cancel`)
}

export function updateActivityStatus(id, status) {
  return request.put(`/activities/${id}/status`, { status })
}

export function getUpcomingActivities(limit = 5) {
  return request.get('/activities/upcoming', { params: { limit } })
}

export function getRecommendedActivities(limit = 5) {
  return request.get('/activities/recommended', { params: { limit } })
}

export function getActivityStatistics() {
  return request.get('/activities/statistics/overview')
}

export function getActivityCategories(params) {
  return request.get('/activities/categories', { params })
}

export const getCategoryList = getActivityCategories

export function createCategory(data) {
  return request.post('/activities/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/activities/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/activities/categories/${id}`)
}

export function updateCategoryStatus(id, status) {
  return request.put(`/activities/categories/${id}/status`, { status })
}

// 活动报名API
export function getActivitySignupList(params) {
  return request.get('/activity-signups', { params })
}

export function getSignupById(id) {
  return request.get(`/activity-signups/${id}`)
}

export function createSignup(data) {
  return request.post('/activity-signups', data)
}

export function updateSignup(id, data) {
  return request.put(`/activity-signups/${id}`, data)
}

export function deleteSignup(id) {
  return request.delete(`/activity-signups/${id}`)
}

export function checkInSignup(id, data) {
  return request.post(`/activity-signups/${id}/check-in`, data)
}

export function cancelSignup(id, data) {
  return request.post(`/activity-signups/${id}/cancel`, data)
}

export function rejectSignup(id, data) {
  return request.post(`/activity-signups/${id}/reject`, data)
}

export function approveSignup(id) {
  return request.post(`/activity-signups/${id}/approve`)
}

export function getSignupStatistics(activityId) {
  return request.get(`/activity-signups/statistics/${activityId}`)
}

export function getMyActivities(elderlyId) {
  return request.get(`/activity-signups/my-activities/${elderlyId}`)
}

export function batchCheckIn(data) {
  return request.post('/activity-signups/batch-check-in', data)
}

// 二维码签到API
export function generateCheckInQrCode(signupId) {
  return request.get(`/activity-signups/${signupId}/qrcode`)
}

export function generateActivityQrCode(activityId) {
  return request.get(`/activity-signups/activity/${activityId}/qrcode`)
}

export function scanCheckIn(data) {
  return request.post('/activity-signups/scan-checkin', data)
}

export function scanActivityCheckIn(data) {
  return request.post('/activity-signups/scan-activity-checkin', data)
}

export function verifyQrCode(data) {
  return request.post('/activity-signups/verify-qrcode', data)
}

export function verifyActivityQrCode(data) {
  return request.post('/activity-signups/verify-activity-qrcode', data)
}

// 活动反馈API
export function getFeedbackList(params) {
  return request.get('/activity-feedbacks', { params })
}

export function getFeedbackById(id) {
  return request.get(`/activity-feedbacks/${id}`)
}

export function createFeedback(data) {
  return request.post('/activity-feedbacks', data)
}

export function updateFeedback(id, data) {
  return request.put(`/activity-feedbacks/${id}`, data)
}

export function deleteFeedback(id) {
  return request.delete(`/activity-feedbacks/${id}`)
}

export function replyFeedback(id, data) {
  return request.post(`/activity-feedbacks/${id}/reply`, data)
}

export function handleFeedback(id, data) {
  return request.post(`/activity-feedbacks/${id}/handle`, data)
}

export function getFeedbackStatistics(activityId) {
  return request.get(`/activity-feedbacks/statistics/${activityId}`)
}

// 导出API
export function exportSignup(params) {
  return request.get('/activity-signups/export', { params, responseType: 'blob' })
}
