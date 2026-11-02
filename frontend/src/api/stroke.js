import api from './index'

/**
 * 行程相关 API
 */

// 发布行程
export function publish(data) {
  return api.post('/stroke/api/publish', data)
}

// 行程列表
export function list(role) {
  return api.post('/stroke/api/list', { role })
}

// 行程详情
export function detail(id) {
  return api.post(`/stroke/api/detail/${id}`)
}

// 行程路线列表
export function itineraryList(id) {
  return api.post(`/stroke/api/itinerary/list`, { id })
}

// 邀请同行
export function invite(data) {
  return api.post('/stroke/api/invite', data)
}

// 邀请列表
export function inviteList(id) {
  return api.post(`/stroke/api/invite/list/${id}`)
}

// 接受邀请
export function acceptInvite(data) {
  return api.post('/stroke/api/invite/accept', data)
}

// 出发
export function departure(id) {
  return api.post(`/stroke/api/departure/${id}`)
}

// 搭车人列表
export function hitchhiker(id) {
  return api.post(`/stroke/api/hitchhiker/${id}`)
}

// 免单
export function freeride(id) {
  return api.post(`/stroke/api/freeride/${id}`)
}

// 送达
export function delivery(id) {
  return api.post(`/stroke/api/delivery/${id}`)
}

// 当前位置
export function currentLocation(id) {
  return api.post(`/stroke/api/currentLocation/${id}`)
}
