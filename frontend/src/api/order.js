import api from './index'

/**
 * 订单相关 API
 */

// 订单列表
export function list(role) {
  return api.post('/order/api/list', { role })
}

// 已支付订单列表
export function paidList() {
  return api.post('/order/api/paidList')
}

// 查看订单详情
export function viewOrder(id) {
  return api.post(`/order/api/view/order/${id}`)
}

// 同行人列表
export function fellows(data) {
  return api.post('/order/api/fellows', data)
}
