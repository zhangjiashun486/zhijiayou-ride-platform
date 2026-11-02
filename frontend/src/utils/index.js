import { showToast as vantShowToast } from 'vant'

/**
 * 统一 Toast 提示
 */
export function showToast(msg) {
  vantShowToast(msg)
}

/**
 * ISO 时间字符串 → yyyy-MM-dd HH:mm
 */
export function formatDate(dateStr) {
  if (!dateStr) return ''
  var s = String(dateStr)
  // 直接截取字符串 "2026-07-03T14:04" → "2026-07-03 14:04"
  if (s.length >= 16) return s.substring(0, 10) + ' ' + s.substring(11, 16)
  return s
}

/**
 * 计算两个日期/时间戳的差值，返回 "X天X小时X分"
 */
export function diffDate(d1, d2) {
  const t1 = new Date(d1).getTime()
  const t2 = new Date(d2).getTime()
  let diff = Math.abs(t1 - t2)

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  diff -= days * 1000 * 60 * 60 * 24
  const hours = Math.floor(diff / (1000 * 60 * 60))
  diff -= hours * 1000 * 60 * 60
  const minutes = Math.floor(diff / (1000 * 60))

  const parts = []
  if (days > 0) parts.push(`${days}天`)
  if (hours > 0) parts.push(`${hours}小时`)
  if (minutes > 0 || parts.length === 0) parts.push(`${minutes}分`)
  return parts.join('')
}

/**
 * 读取 URL 查询参数
 */
export function getParam(name) {
  const params = new URLSearchParams(window.location.search)
  return params.get(name)
}

/**
 * 行程状态常量映射
 */
export const tripStatus = {
  0: '待出发',
  1: '行程中',
  2: '已完成',
  3: '已取消',
}

/**
 * 司机状态常量映射
 */
export const driverStatus = {
  0: '空闲',
  1: '行程中',
  2: '已下线',
}

/**
 * 订单状态常量映射
 */
export const orderStatus = {
  1: '待支付',
  2: '已支付',
}

/**
 * 根据状态值返回对应 CSS class 名
 */
export function getStatusClass(status) {
  const map = {
    0: 'status-pending',
    1: 'status-active',
    2: 'status-done',
    3: 'status-cancel',
    4: 'status-refund',
    5: 'status-refunded',
  }
  return map[status] || 'status-default'
}
