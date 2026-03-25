import request from './request'

export function getMedicineList(params) {
  return request.get('/medicines', { params })
}

export function getMedicineById(id) {
  return request.get(`/medicines/${id}`)
}

export function createMedicine(data) {
  return request.post('/medicines', data)
}

export function updateMedicine(id, data) {
  return request.put(`/medicines/${id}`, data)
}

export function deleteMedicine(id) {
  return request.delete(`/medicines/${id}`)
}

export function getMedicineInventory(params) {
  return request.get('/medicine-inventory', { params })
}

export function updateMedicineInventory(id, data) {
  return request.put(`/medicine-inventory/${id}`, data)
}

export function getMedicineRecords(params) {
  return request.get('/medicine-records', { params })
}

export function createMedicineRecord(data) {
  return request.post('/medicine-records', data)
}

export function getMedicineApprovalList(params) {
  return request.get('/medicine-approvals', { params })
}

export function approveMedicine(id, remark) {
  return request.post(`/medicine-approvals/${id}/approve`, { remark })
}

export function rejectMedicine(id, remark) {
  return request.post(`/medicine-approvals/${id}/reject`, { remark })
}
