import request from './request'

export function login(data) {
  return request.post('/auth/login', data, {
    headers: {
      'Authorization': undefined
    }
  })
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUser() {
  return request.get('/auth/me')
}

export function register(data) {
  return request.post('/auth/register', data, {
    headers: {
      'Authorization': undefined
    }
  })
}
