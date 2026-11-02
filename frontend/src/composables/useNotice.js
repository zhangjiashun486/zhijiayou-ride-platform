import { ref } from 'vue'
import { showToast } from 'vant'

// ---- 单例状态 ----
const messages = ref([])
const unreadCount = ref(0)
let ws = null
let reconnectTimer = null

// ---- 内部 ----
function persist() {
  try {
    localStorage.setItem('_msg', JSON.stringify(messages.value))
  } catch { /* ignore */ }
}

function isOnChatPage() {
  return window.location.hash.startsWith('#/message')
}

// ---- 对外 API ----

/**
 * 添加一条消息到全局列表
 */
export function addNoticeMsg(data) {
  if (!data) return

  // 用发送者+内容+时间组合去重
  const key = `${data.senderId || ''}_${data.tripId || ''}_${data.message || data.content || ''}_${Date.now()}`
  const exists = messages.value.some(m =>
    m.senderId === (data.senderId || '') &&
    (m.message || '') === (data.message || data.content || '') &&
    m.tripId === (data.tripId || '')
  )
  if (exists) return

  messages.value.push({
    _key: key,
    senderId: data.senderId || '',
    senderUseralias: data.senderUseralias || '',
    message: data.message || data.content || '',
    tripId: data.tripId || '',
    time: data.timestamp || new Date().toISOString(),
  })
  unreadCount.value = messages.value.length
  persist()

  // 不在聊天页时弹出提示
  if (!isOnChatPage()) {
    showToast({ message: `${data.senderUseralias || '有人'}发来新消息`, duration: 2000, position: 'top' })
  }
}

/**
 * 初始化全局 WebSocket（登录后调用）
 */
export function initNoticeSocket() {
  const token = localStorage.getItem('SESSION_TOKEN_KEY')
  if (!token) return

  try {
    const raw = localStorage.getItem('_msg')
    if (raw) {
      messages.value = JSON.parse(raw)
      unreadCount.value = messages.value.length
    }
  } catch {
    messages.value = []
  }

  let host = window.location.host
  if (host.includes(':3000') || host === 'localhost:3000') {
    host = window.location.hostname + ':9999'
  }
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const url = `${protocol}//${host}/notice/ws/socket?SESSION_TOKEN_KEY=${token}`

  ws = new WebSocket(url)

  ws.onopen = () => console.log('[notice] WebSocket connected')

  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      addNoticeMsg(data)
    } catch {
      // 非 JSON 消息忽略
    }
  }

  ws.onclose = () => {
    console.log('[notice] WebSocket closed, reconnecting in 5s...')
    reconnectTimer = setTimeout(() => initNoticeSocket(), 5000)
  }

  ws.onerror = (err) => console.error('[notice] WebSocket error:', err)
}

/**
 * 关闭 WebSocket（登出时调用）
 */
export function closeNoticeSocket() {
  if (reconnectTimer) clearTimeout(reconnectTimer)
  if (ws) {
    ws.onclose = null
    ws.close()
    ws = null
  }
}

/**
 * 删除一条消息
 */
export function removeNoticeMsg(index) {
  messages.value.splice(index, 1)
  unreadCount.value = messages.value.length
  persist()
}

/** 清空所有消息 */
export function clearNoticeMsg() {
  messages.value = []
  unreadCount.value = 0
  localStorage.removeItem('_msg')
}

/** 响应式消息列表 */
export function useNoticeMessages() {
  return { messages, unreadCount }
}
