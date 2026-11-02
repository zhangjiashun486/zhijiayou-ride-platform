<template>
  <div class="ai-page">
    <header class="ai-header">
      <button class="icon-btn" aria-label="返回" @click="router.back()">
        <van-icon name="arrow-left" size="20" />
      </button>
      <div class="ai-header__main">
        <div class="ai-avatar"><van-icon name="chat-o" size="18" /></div>
        <div class="ai-header__text">
          <h1>小驾 · AI 出行助手</h1>
          <p><i class="online-dot"></i>{{ sending ? '正在思考…' : '在线，随时解答出行问题' }}</p>
        </div>
      </div>
      <div class="ai-header__actions">
        <button class="icon-btn" aria-label="新建对话" @click="createNewConversation">
          <van-icon name="plus" size="19" />
        </button>
        <button class="icon-btn" aria-label="历史会话" @click="showHistory = true">
          <van-icon name="comment-o" size="19" />
        </button>
      </div>
    </header>

    <main ref="chatList" class="chat-list">
      <section v-if="!messages.length && !loading" class="welcome">
        <div class="welcome-avatar"><van-icon name="chat-o" size="30" /></div>
        <h2>你好，我是小驾</h2>
        <p>可以问我路线规划、景点攻略、旅行社推荐，还能查实时天气</p>
        <div class="suggestions">
          <button
            v-for="text in suggestions"
            :key="text"
            class="suggestion"
            @click="pickSuggestion(text)"
          >
            {{ text }}
          </button>
        </div>
      </section>

      <div v-if="loading" class="loading-hint"><van-loading size="22" /></div>

      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        class="msg-row"
        :class="isUser(msg.role) ? 'is-user' : 'is-ai'"
      >
        <div v-if="!isUser(msg.role)" class="avatar avatar--ai">
          <van-icon name="chat-o" size="17" />
        </div>

        <div class="bubble" :class="isUser(msg.role) ? 'bubble--user' : 'bubble--ai'">
          <div v-if="isUser(msg.role)" class="bubble-text">{{ msg.content }}</div>
          <template v-else>
            <div v-if="msg.content" class="markdown" v-html="renderMd(msg.content)"></div>
            <div v-else-if="sending && idx === messages.length - 1" class="typing">
              <i></i><i></i><i></i>
            </div>
            <div v-if="msg.content" class="bubble-actions">
              <button class="bubble-action" @click="copyText(msg.content)">
                <van-icon name="records-o" size="13" /> 复制
              </button>
            </div>
          </template>
        </div>

        <div v-if="isUser(msg.role)" class="avatar avatar--user">{{ userInitial }}</div>
      </div>
    </main>

    <footer class="composer">
      <textarea
        ref="inputRef"
        v-model="inputText"
        class="composer__input"
        rows="1"
        placeholder="问问小驾，比如：青岛怎么玩？"
        @input="autoGrow"
        @keydown.enter.exact.prevent="send"
      ></textarea>
      <button
        v-if="sending"
        class="send-btn send-btn--stop"
        aria-label="停止生成"
        @click="stopStream"
      >
        <van-icon name="cross" size="18" />
      </button>
      <button
        v-else
        class="send-btn"
        :disabled="!inputText.trim()"
        aria-label="发送"
        @click="send"
      >
        <van-icon name="guide-o" size="20" />
      </button>
    </footer>

    <van-popup
      v-model:show="showHistory"
      position="bottom"
      round
      teleport="body"
      :style="{ maxHeight: '72vh' }"
    >
      <div class="history-sheet">
        <div class="history-head">
          <h3>历史会话</h3>
          <button class="new-chat-btn" @click="createNewConversation">
            <van-icon name="plus" size="14" /> 新建对话
          </button>
        </div>

        <div class="history-list">
          <button
            v-for="conv in convList"
            :key="conv.cid"
            class="history-item"
            :class="{ active: conv.cid === conversationId }"
            @click="selectConversation(conv.cid)"
          >
            <div class="history-main">
              <p class="history-title">{{ conv.title }}</p>
              <p class="history-preview">{{ conv.preview }}</p>
            </div>
            <div class="history-meta">
              <span>{{ formatTime(conv.lastTime) }}</span>
              <van-icon name="delete-o" size="16" @click.stop="removeConversation(conv)" />
            </div>
          </button>
          <van-empty v-if="!convList.length" description="还没有历史会话" />
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import * as aiApi from '@/api/ai'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const CONVERSATION_ID_KEY = `AI_CURRENT_CONVERSATION_ID:${auth.currentUser?.id || 'unknown'}`
const conversationId = ref(sessionStorage.getItem(CONVERSATION_ID_KEY) || '')
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const loading = ref(false)
const chatList = ref(null)
const inputRef = ref(null)
const showHistory = ref(false)
const convList = ref([])
let cancelStream = null

const suggestions = [
  '青岛三日游怎么安排？',
  '推荐一家靠谱的旅行社',
  '爬泰山需要准备什么？',
  '北京今天天气怎么样？',
]

const userInitial = computed(() => {
  const name = auth.currentUser?.useralias || auth.currentUser?.username || '我'
  return String(name).slice(0, 1)
})

function setConversationId(cid) {
  conversationId.value = cid
  if (cid) {
    sessionStorage.setItem(CONVERSATION_ID_KEY, cid)
  } else {
    sessionStorage.removeItem(CONVERSATION_ID_KEY)
  }
}

function createNewConversation() {
  cancelStream?.()
  sending.value = false
  setConversationId('')
  messages.value = []
  showHistory.value = false
}

function isUser(role) {
  return role === 'user'
}

function renderMd(text) {
  if (!text) return ''
  let html = String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>')
  html = html.replace(/^### (.+)$/gm, '<h4>$1</h4>')
  html = html.replace(/^## (.+)$/gm, '<h3>$1</h3>')
  html = html.replace(/^- (.+)$/gm, '<li>$1</li>')
  html = html.replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
  html = html.replace(/((?:<li>.*?<\/li>\n?)+)/g, '<ul>$1</ul>')
  html = html.replace(/\n\n/g, '<br><br>')
  html = html.replace(/\n/g, '<br>')
  return html
}

function formatTime(value) {
  if (!value) return ''
  const text = String(value)
  return text.length > 16 ? text.slice(5, 16) : text
}

function scrollToBottom() {
  nextTick(() => {
    if (chatList.value) {
      chatList.value.scrollTop = chatList.value.scrollHeight
    }
  })
}

function autoGrow() {
  const el = inputRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = `${Math.min(el.scrollHeight, 96)}px`
}

function normalizeConv(raw) {
  return {
    cid: raw.conversationId || raw.conversation_id || '',
    title: raw.title || '未命名会话',
    preview: raw.preview || '暂无消息',
    lastTime: raw.lastTime || raw.last_time || '',
    messageCount: raw.messageCount || raw.message_count || 0,
  }
}

async function loadConvList() {
  try {
    const res = await aiApi.getConversations()
    if (res && res.code === 200 && Array.isArray(res.data)) {
      convList.value = res.data.map(normalizeConv).filter((item) => item.cid)
    }
  } catch {
    /* 会话列表加载失败不影响聊天 */
  }
}

async function loadHistory(cid) {
  if (!cid) return
  loading.value = true
  try {
    const res = await aiApi.getHistory(cid)
    if (res && res.code === 200 && Array.isArray(res.data)) {
      messages.value = res.data.map((item) => ({
        ...item,
        role: item.role === 'user' ? 'user' : 'assistant',
      }))
      scrollToBottom()
    }
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

function selectConversation(cid) {
  showHistory.value = false
  cancelStream?.()
  sending.value = false
  setConversationId(cid)
  loadHistory(cid)
}

async function removeConversation(conv) {
  try {
    await showConfirmDialog({
      title: '删除会话',
      message: `删除「${conv.title}」的所有聊天记录？`,
      confirmButtonText: '删除',
    })
  } catch {
    return
  }
  try {
    await aiApi.deleteConversation(conv.cid)
    showToast('已删除')
    if (conv.cid === conversationId.value) {
      createNewConversation()
    }
    await loadConvList()
  } catch {
    showToast('删除失败，请重试')
  }
}

function pickSuggestion(text) {
  inputText.value = text
  send()
}

function copyText(text) {
  if (navigator.clipboard?.writeText) {
    navigator.clipboard.writeText(text).then(() => showToast('已复制')).catch(() => {})
  }
}

function stopStream() {
  cancelStream?.()
  cancelStream = null
  sending.value = false
  const last = messages.value[messages.value.length - 1]
  if (last && !isUser(last.role) && !last.content) {
    last.content = '（已停止生成）'
  }
}

async function send() {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  messages.value.push({ role: 'user', content: text, conversationId: conversationId.value })
  inputText.value = ''
  nextTick(autoGrow)
  scrollToBottom()

  messages.value.push({ role: 'assistant', content: '', conversationId: conversationId.value })
  const aiIndex = messages.value.length - 1
  sending.value = true

  cancelStream = aiApi.sendMessageStream(
    { conversationId: conversationId.value, message: text },
    (token) => {
      messages.value[aiIndex].content += token
      scrollToBottom()
    },
    () => {
      if (!messages.value[aiIndex].content) {
        messages.value[aiIndex].content = '(空回复)'
      }
      sending.value = false
      cancelStream = null
      scrollToBottom()
      loadConvList()
    },
    (error) => {
      if (!messages.value[aiIndex].content) {
        messages.value[aiIndex].content = error.message === 'HTTP 403'
          ? '无法访问该会话，请新建会话。'
          : error.message === 'HTTP 404'
            ? '会话不存在，请新建会话。'
            : error.message === 'HTTP 401'
              ? '登录已过期，请重新登录。'
              : '请求失败，请稍后重试。'
      }
      sending.value = false
      cancelStream = null
      scrollToBottom()
    },
    (cid) => {
      setConversationId(cid)
      messages.value[aiIndex - 1].conversationId = cid
      messages.value[aiIndex].conversationId = cid
    },
  )
}

onMounted(async () => {
  await loadConvList()

  const preset = route.query.q
  if (preset) {
    inputText.value = String(preset)
    return
  }

  if (conversationId.value) {
    await loadHistory(conversationId.value)
  } else if (convList.value.length) {
    setConversationId(convList.value[0].cid)
    await loadHistory(convList.value[0].cid)
  }
})
</script>

<style scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-page);
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--divider-color);
}
.icon-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 12px;
  background: var(--bg-soft);
  color: var(--text-primary);
  cursor: pointer;
}
.icon-btn:active {
  transform: scale(0.94);
}
.ai-header__main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}
.ai-avatar {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 13px;
  background: var(--gradient-primary);
  color: #fff;
  flex-shrink: 0;
}
.ai-header__text {
  min-width: 0;
}
.ai-header__text h1 {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.ai-header__text p {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 2px;
  font-size: 11px;
  color: var(--text-hint);
}
.online-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-secondary);
}
.ai-header__actions {
  display: flex;
  gap: 8px;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 14px 8px;
  -webkit-overflow-scrolling: touch;
}

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 36px 12px 8px;
}
.welcome-avatar {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 22px;
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-button);
}
.welcome h2 {
  margin-top: 16px;
  font-size: 19px;
  font-weight: 700;
  color: var(--text-primary);
}
.welcome p {
  margin-top: 8px;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-hint);
  max-width: 260px;
}
.suggestions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  margin-top: 22px;
}
.suggestion {
  padding: 10px 14px;
  border: 1px solid var(--border-color);
  border-radius: 14px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 13px;
  cursor: pointer;
}
.suggestion:active {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.loading-hint {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 16px;
}
.msg-row.is-user {
  justify-content: flex-end;
}

.avatar {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}
.avatar--ai {
  background: var(--gradient-primary);
  color: #fff;
}
.avatar--user {
  background: var(--bg-card);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.bubble {
  max-width: 76%;
  padding: 11px 14px;
  border-radius: 16px;
  font-size: 14.5px;
  line-height: 1.65;
  word-break: break-word;
}
.bubble--ai {
  background: var(--bg-card);
  color: var(--text-primary);
  border-top-left-radius: 5px;
  box-shadow: var(--shadow-card);
}
.bubble--user {
  background: var(--gradient-primary);
  color: #fff;
  border-top-right-radius: 5px;
  box-shadow: var(--shadow-button);
}
.bubble-text {
  white-space: pre-wrap;
}

.markdown :deep(strong) { font-weight: 700; }
.markdown :deep(h3) { font-size: 15px; font-weight: 700; margin: 8px 0 4px; }
.markdown :deep(h4) { font-size: 14px; font-weight: 700; margin: 6px 0 2px; }
.markdown :deep(ul) { padding-left: 16px; margin: 4px 0; }
.markdown :deep(li) { margin: 2px 0; }
.markdown :deep(code) {
  background: color-mix(in srgb, var(--color-primary) 10%, transparent);
  color: var(--color-primary);
  padding: 1px 5px;
  border-radius: 5px;
  font-size: 13px;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
}

.bubble-actions {
  margin-top: 8px;
  padding-top: 6px;
  border-top: 1px dashed var(--divider-color);
}
.bubble-action {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: transparent;
  color: var(--text-hint);
  font-size: 11px;
  cursor: pointer;
}

.typing {
  display: flex;
  gap: 4px;
  padding: 4px 2px;
}
.typing i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-hint);
  animation: blink 1.2s infinite ease-in-out;
}
.typing i:nth-child(2) { animation-delay: 0.2s; }
.typing i:nth-child(3) { animation-delay: 0.4s; }
@keyframes blink {
  0%, 60%, 100% { opacity: 0.25; transform: translateY(0); }
  30% { opacity: 1; transform: translateY(-3px); }
}

.composer {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 10px 12px calc(10px + env(safe-area-inset-bottom, 0px));
  background: var(--bg-card);
  border-top: 1px solid var(--divider-color);
}
.composer__input {
  flex: 1;
  min-height: 42px;
  max-height: 96px;
  padding: 11px 16px;
  border: 1px solid var(--border-color);
  border-radius: 21px;
  background: var(--bg-soft);
  color: var(--text-primary);
  font-family: inherit;
  font-size: 14px;
  line-height: 1.4;
  resize: none;
  outline: none;
}
.composer__input:focus {
  border-color: var(--color-primary);
  background: var(--bg-card);
}
.send-btn {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-button);
  cursor: pointer;
  flex-shrink: 0;
}
.send-btn:disabled {
  opacity: 0.4;
  box-shadow: none;
}
.send-btn--stop {
  background: var(--text-secondary);
  box-shadow: none;
}

.history-sheet {
  padding: 18px 16px calc(20px + env(safe-area-inset-bottom, 0px));
}
.history-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.history-head h3 {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}
.new-chat-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 7px 12px;
  border: none;
  border-radius: 999px;
  background: color-mix(in srgb, var(--color-primary) 12%, transparent);
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}
.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 54vh;
  overflow-y: auto;
}
.history-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border-color);
  border-radius: 14px;
  background: var(--bg-soft);
  text-align: left;
  cursor: pointer;
}
.history-item.active {
  border-color: var(--color-primary);
  background: color-mix(in srgb, var(--color-primary) 8%, var(--bg-card));
}
.history-main {
  flex: 1;
  min-width: 0;
}
.history-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.history-preview {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-hint);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.history-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  font-size: 11px;
  color: var(--text-hint);
}
</style>
