import api from './index'

/**
 * 消息通知 API
 */

// 消息列表
export function list(data) {
  return api.post('/notice/api/list', data)
}
