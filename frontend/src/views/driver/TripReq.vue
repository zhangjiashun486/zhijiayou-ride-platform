<template>
  <div class="page">
    <AppHeader title="我的邀请记录" show-back @click-left="router.push('/driver/trip-detail/' + id)" />
    <div class="content">
      <div v-if="list.length === 0" class="empty-tip">暂无邀请记录</div>
      <div v-for="item in list" :key="item.id" class="req-card" @click="goChat(item.inviteeTripId)">
        <van-image round width="44" height="44" :src="item.avatar || defaultAvatar" fit="cover" />
        <div class="req-info">
          <div class="req-name">
            {{ item.useralias || '用户' }}
            <span class="status-tag" :class="statusClass(item.status)">{{ statusLabel(item.status) }}</span>
          </div>
          <div class="req-route">{{ item.startAddr }} → {{ item.endAddr }}</div>
        </div>
        <van-icon name="arrow" color="#ccc" />
      </div>
    </div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as strokeApi from '@/api/stroke'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const defaultAvatar = '/img/default-header.jpg'
const route = useRoute()
const router = useRouter()
const list = ref([])
const id = route.params.id
const statusMap = { 0: '未确认', 1: '已确认', 2: '已拒绝', 3: '已超时' }
const statusClassMap = { 0: 's-pending', 1: 's-confirmed', 2: 's-rejected', 3: 's-timeout' }
const statusLabel = (s) => statusMap[s] || '未知'
const statusClass = (s) => statusClassMap[s] || ''

onMounted(async () => {
  try {
    const res = await strokeApi.inviteList(id)
    list.value = (res && res.data && Array.isArray(res.data)) ? res.data : (Array.isArray(res) ? res : [])
  } catch {}
})

const goChat = (inviteeTripId) => { router.push({ path: '/message', query: { t: inviteeTripId } }) }
</script>

<style scoped>
.page { min-height: 100vh; background: #f7f8fa; padding-bottom: 60px; }
.content { padding: 12px; }
.empty-tip { text-align: center; color: #999; font-size: 13px; padding: 20px; }
.req-card { background: #fff; border-radius: 10px; padding: 12px; margin-bottom: 10px; display: flex; align-items: center; gap: 10px; cursor: pointer; }
.req-info { flex: 1; min-width: 0; }
.req-name { font-size: 14px; font-weight: 500; margin-bottom: 4px; display: flex; align-items: center; gap: 6px; }
.req-route { font-size: 12px; color: #666; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.status-tag { font-size: 10px; padding: 1px 5px; border-radius: 3px; white-space: nowrap; }
.s-pending { background: #fff3e0; color: #ff9800; }
.s-confirmed { background: #e8f5e9; color: #4caf50; }
.s-rejected { background: #fce4ec; color: #f44336; }
.s-timeout { background: #f5f5f5; color: #999; }
</style>
