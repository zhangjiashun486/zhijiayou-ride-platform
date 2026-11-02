<template>
  <div class="page-container">
    <AppHeader title="我的消息" :show-back="true" />

    <div v-if="messages.length === 0" class="empty-wrap">
      <van-empty description="暂无消息" />
    </div>

    <div v-else class="msg-list">
      <div
        v-for="(msg, idx) in reversedMessages"
        :key="idx"
        class="msg-item"
        @click="goChat(msg)"
      >
        <div class="msg-content">
          <div class="msg-sender">{{ msg.senderUseralias || msg.senderId || '系统' }}</div>
          <div class="msg-text">{{ msg.message || msg.content || '' }}</div>
        </div>
        <van-icon
          name="delete-o"
          size="18"
          color="#999"
          @click.stop="handleDelete(msg)"
          class="msg-delete"
        />
      </div>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import { useNoticeMessages, removeNoticeMsg } from '@/composables/useNotice'

const router = useRouter()

const { messages, unreadCount } = useNoticeMessages()
const reversedMessages = computed(() => [...messages.value].reverse())

function goChat(msg) {
  const senderId = msg.senderId || ''
  if (senderId) {
    const t = msg.tripId ? `&t=${msg.tripId}` : ''
    router.push(`/message?r=${senderId}${t}`)
  }
}

function handleDelete(msg) {
  const idx = messages.value.findIndex(m => m._key === msg._key)
  if (idx >= 0) removeNoticeMsg(idx)
}
</script>

<style scoped>
.empty-wrap {
  padding-top: 60px;
}

.msg-list {
  padding: 8px 16px;
}

.msg-item {
  display: flex;
  align-items: center;
  padding: 14px 12px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.msg-content {
  flex: 1;
  min-width: 0;
}

.msg-sender {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.msg-text {
  font-size: 13px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-delete {
  margin-left: 12px;
  flex-shrink: 0;
}
</style>
