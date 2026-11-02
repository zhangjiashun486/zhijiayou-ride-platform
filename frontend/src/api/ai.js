import api from './index'

/**
 * AI 聊天 API
 */

// 发送消息
export function sendMessage(data) {
  return api.post('/ai/chat', data)
}

// 获取会话历史（分页，默认最近 50 条）
export function getHistory(conversationId, page = 1, size = 50) {
  return api.get(`/ai/chat/history/${conversationId}`, { params: { page, size } })
}

// 获取用户会话列表（含标题、最后一条消息、消息数）
export function getConversations() {
  return api.get('/ai/chat/conversations')
}

// 删除会话
export function deleteConversation(conversationId) {
  return api.delete(`/ai/chat/conversation/${conversationId}`)
}

// 获取用户全部聊天记录（跨会话，时间倒序）
export function getAllMessages() {
  return api.get('/ai/chat/messages')
}

/**
 * 流式发送消息 (SSE)
 * @param {Object} data - { conversationId, message }
 * @param {Function} onToken - 每个 token 回调
 * @param {Function} onDone  - 流结束回调
 * @param {Function} onError - 错误回调
 * @param {Function} onConversation - 接收后端分配的会话 ID
 * @returns {Function} cancel - 取消请求
 */
export function sendMessageStream(data, onToken, onDone, onError, onConversation) {
  const token = localStorage.getItem('SESSION_TOKEN_KEY')
  const controller = new AbortController()

  fetch('/ai/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'SESSION_TOKEN_KEY': token || '',
    },
    body: JSON.stringify(data),
    signal: controller.signal,
  }).then(async (response) => {
    if (!response.ok) {
      if (response.status === 401) {
        localStorage.removeItem('SESSION_TOKEN_KEY')
        localStorage.removeItem('currentUser')
        localStorage.removeItem('currentRole')
        window.location.hash = '#/login'
      }
      onError(new Error('HTTP ' + response.status))
      return
    }
    const conversationId = response.headers.get('X-Conversation-Id')
    if (conversationId) onConversation?.(conversationId)
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        break
      }

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:')) {
          onToken(line.slice(5))
        }
      }
    }
    onDone('')
  }).catch((err) => {
    if (err.name !== 'AbortError') {
      onError(err)
    }
  })

  return () => controller.abort()
}

/**
 * 获取资讯列表
 */
export function getAiMsgList(data = {}) {
  return api.post('/ai-msg/list', data)
}

// 获取资讯详情
export function getAiMsgDetail(id) {
  return api.get(`/ai-msg/${id}`)
}
