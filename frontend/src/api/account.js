import api from './index'

/**
 * 账户相关 API
 */

// 登录
export function login(data) {
  return api.post('/account/api/login', data)
}

// 注册
export function register(data) {
  return api.post('/account/api/register', data)
}

// 验证 Token
export function verifyToken() {
  return api.post('/account/api/verifyToken')
}

// 获取用户信息
export function getUserInfo() {
  return api.post('/account/api/userinfo')
}

// 修改用户信息
export function modifyUser(data) {
  return api.post('/account/api/modify', data)
}

// 修改密码
export function modifyPassword(data) {
  return api.post('/account/api/modifyPassword', data)
}

// 获取认证信息
export function getAuthInfo() {
  return api.post('/account/api/getAuthenticationInfo')
}

// 修改认证信息
export function modifyAuth(data) {
  return api.post('/account/api/modifyAuthentication', data)
}

// 实名认证
export function identityAuth() {
  return api.post('/account/api/identityAuth')
}

// 获取车辆信息
export function getVehicleInfo() {
  return api.post('/account/api/getVehicleInfo')
}

// 修改车辆信息
export function modifyVehicle(data) {
  return api.post('/account/api/modifyVehicle', data)
}

// 车辆认证
export function vehicleAuth() {
  return api.post('/account/api/vehicleAuth')
}
