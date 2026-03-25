import request from './request'

// ==================== 护理记录相关接口 ====================
export function getCareRecordList(params) {
  return request.get('/care-record', { params })
}

export function getCareRecordById(id) {
  return request.get(`/care-record/${id}`)
}

export function createCareRecord(data) {
  return request.post('/care-record', data)
}

export function updateCareRecord(id, data) {
  return request.put(`/care-record/${id}`, data)
}

export function deleteCareRecord(id) {
  return request.delete(`/care-record/${id}`)
}

export function getCareRecordsByElderly(elderlyId) {
  return request.get(`/care-record/elderly/${elderlyId}`)
}

export function getCareRecordsByCaregiver(caregiverId) {
  return request.get(`/care-record/caregiver/${caregiverId}`)
}

export function getTodayCareRecords() {
  return request.get('/care-record/today')
}

export function getCareRecordsByCarePlan(carePlanId) {
  return request.get(`/care-record/care-plan/${carePlanId}`)
}

export function updateCareRecordStatus(id, status) {
  return request.put(`/care-record/${id}/status`, null, { params: { status } })
}

export function getCareStatistics(caregiverId) {
  return request.get(`/care-record/statistics/${caregiverId}`)
}

// ==================== 护理评估相关接口 ====================
export function getCareAssessmentList(params) {
  return request.get('/care-assessment', { params })
}

export function getCareAssessmentById(id) {
  return request.get(`/care-assessment/${id}`)
}

export function createCareAssessment(data) {
  return request.post('/care-assessment', data)
}

export function updateCareAssessment(id, data) {
  return request.put(`/care-assessment/${id}`, data)
}

export function deleteCareAssessment(id) {
  return request.delete(`/care-assessment/${id}`)
}

export function getCareAssessmentsByElderly(elderlyId) {
  return request.get(`/care-assessment/elderly/${elderlyId}`)
}

export function getLatestAssessmentByElderly(elderlyId) {
  return request.get(`/care-assessment/elderly/${elderlyId}/latest`)
}

export function getCareAssessmentsByAssessor(assessorId) {
  return request.get(`/care-assessment/assessor/${assessorId}`)
}

export function getCareAssessmentsByType(type) {
  return request.get(`/care-assessment/type/${type}`)
}

export function getCareAssessmentsByCareLevel(careLevel) {
  return request.get(`/care-assessment/care-level/${careLevel}`)
}

export function getDueForAssessment() {
  return request.get('/care-assessment/due-for-assessment')
}

export function getAssessmentStatistics() {
  return request.get('/care-assessment/statistics')
}

export function calculateAdlScore(data) {
  return request.post('/care-assessment/calculate', data)
}

// ==================== 呼叫护工相关接口 ====================
export function getCallCaregiverList(params) {
  return request.get('/call-caregiver/page', { params })
}

export function getCallCaregiverById(id) {
  return request.get(`/call-caregiver/${id}`)
}

export function getCallCaregiversByElderly(elderlyId) {
  return request.get(`/call-caregiver/elderly/${elderlyId}`)
}

export function getCallCaregiversByStatus(status) {
  return request.get(`/call-caregiver/status/${status}`)
}

export function getCallCaregiversByNurse(nurseId) {
  return request.get(`/call-caregiver/nurse/${nurseId}`)
}

export function getCallCaregiverStats() {
  return request.get('/call-caregiver/stats')
}

export function createCallCaregiver(data) {
  return request.post('/call-caregiver', data)
}

export function updateCallCaregiver(id, data) {
  return request.put(`/call-caregiver/${id}`, data)
}

export function deleteCallCaregiver(id) {
  return request.delete(`/call-caregiver/${id}`)
}

export function assignNurseToCall(id, data) {
  return request.put(`/call-caregiver/${id}/assign`, data)
}

export function respondToCall(id) {
  return request.put(`/call-caregiver/${id}/respond`)
}

export function completeCall(id, data) {
  return request.put(`/call-caregiver/${id}/complete`, data)
}

export function cancelCall(id) {
  return request.put(`/call-caregiver/${id}/cancel`)
}

// ==================== 护工/护士相关接口 ====================
export function getNurseList(params) {
  return request.get('/nurse', { params })
}

// ==================== 护理预约相关接口 ====================
export function getCareBookingList(params) {
  return request.get('/care-booking', { params })
}

export function getCareBookingById(id) {
  return request.get(`/care-booking/${id}`)
}

export function createCareBooking(data) {
  return request.post('/care-booking', data)
}

export function updateCareBooking(id, data) {
  return request.put(`/care-booking/${id}`, data)
}

export function deleteCareBooking(id) {
  return request.delete(`/care-booking/${id}`)
}

export function confirmCareBooking(id) {
  return request.put(`/care-booking/${id}/confirm`)
}

export function completeCareBooking(id) {
  return request.put(`/care-booking/${id}/complete`)
}

export function cancelCareBooking(id) {
  return request.put(`/care-booking/${id}/cancel`)
}

export function assignNurseToBooking(id, nurseId) {
  return request.put(`/care-booking/${id}/assign`, { nurseId })
}

// ==================== 紧急联系人相关接口 ====================
export function getEmergencyContactList(params) {
  return request.get('/emergency-contact/page', { params })
}

export function getEmergencyContactActive() {
  return request.get('/emergency-contact/active')
}

export function getEmergencyContactById(id) {
  return request.get(`/emergency-contact/${id}`)
}

export function createEmergencyContact(data) {
  return request.post('/emergency-contact', data)
}

export function updateEmergencyContact(id, data) {
  return request.put(`/emergency-contact/${id}`, data)
}

export function deleteEmergencyContact(id) {
  return request.delete(`/emergency-contact/${id}`)
}

export function toggleEmergencyContactStatus(id) {
  return request.put(`/emergency-contact/${id}/toggle`)
}
