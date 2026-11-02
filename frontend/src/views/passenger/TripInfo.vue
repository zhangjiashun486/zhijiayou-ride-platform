<template>
  <div class="page-container">
    <AppHeader title="行程信息" show-back @click-left="router.back" />

    <div class="card" v-if="trip">
      <div class="info-row"><span class="info-label">出发时间</span><span class="text-bold">{{ formatTime(trip.departureTime) }}</span></div>
      <div class="info-row"><span class="info-label">出发地</span><span class="text-bold">{{ trip.startAddr }}</span></div>
      <div class="info-row"><span class="info-label">目的地</span><span class="text-bold">{{ trip.endAddr }}</span></div>
      <div class="info-row"><span class="info-label">状态</span><span :style="{ color: statusColor(trip.status) }" class="text-bold">{{ statusLabel(trip.status) }}</span></div>
    </div>

    <div style="padding: 20px 16px"><button class="btn-primary" @click="router.back">知道了</button></div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import * as strokeApi from '@/api/stroke'

const router = useRouter()
const route = useRoute()
const trip = ref(null)

function formatTime(s) { return s ? String(s).substring(0, 16) : '' }
function statusLabel(s) { return ({ 0:'邀请中',1:'已确认',2:'已上车',3:'已下车',4:'已取消' })[s] || '未知' }
function statusColor(s) { return ({ 0:'#F59E0B',1:'#3B82F6',2:'#E5A100',3:'#10B981',4:'#EF4444' })[s] || '#9CA3AF' }

onMounted(async () => {
  try {
    const res = await strokeApi.detail(route.params.id)
    if (res.code === 200) trip.value = res.data?.[0] || {}
  } catch {}
})
</script>

<style scoped>
.info-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid #F0F0F0; }
.info-row:last-child { border-bottom: none; }
.info-label { font-size: 14px; color: #6B7280; }
</style>
