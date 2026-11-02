import api from './index'

/**
 * 支付相关 API
 */

// 创建支付
export function createPayment(data) {
  return api.post('/payment/api/payment', data)
}

// 查询支付状态
export function queryPayment(data) {
  return api.post('/payment/api/query', data)
}

// 确认支付
export function confirmPay(id) {
  return api.post(`/payment/api/confirmPay/${id}`)
}
