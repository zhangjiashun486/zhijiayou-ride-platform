<template>
  <div class="page-container">
    <AppHeader title="行程详情" show-back @click-left="onBack" />

    <div class="trip-title">搭乘中，待匹配……</div>
    <div class="info-card" v-if="trip">
      <div class="info-row"><van-icon name="clock-o" /><span>{{ formatTime(trip.departureTime) }}</span></div>
      <div class="info-row"><van-icon name="location-o" color="#4caf50" /><span>{{ trip.startAddr || '' }}</span></div>
      <div class="info-row"><van-icon name="flag-o" color="#f44336" /><span>{{ trip.endAddr || '' }}</span></div>
      <div class="info-row"><van-icon name="info-o" color="#F59E0B" /><span style="color:#F59E0B;font-weight:500">邀请中</span></div>
    </div>

    <!-- 邀请我的 -->
    <div style="padding: 0 16px; margin-top: 12px"><h3 class="section-title">邀请我的</h3></div>
    <div
      v-for="inv in inviteList"
      :key="inv.id"
      class="card invite-card"
    >
      <div class="flex" style="align-items: center">
        <van-image round width="48" height="48" :src="inv.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'" />
        <div class="flex-1" style="margin-left: 12px">
          <div class="flex-between">
            <span class="text-bold">{{ inv.nickName || inv.useralias || inv.driverUseralias || '司机' }}</span>
            <span :style="{ color: invStatusColor(inv.status) }" class="text-sm text-bold">
              {{ invStatusLabel(inv.status) }}
            </span>
          </div>
          <div class="text-sm text-secondary mt-sm">
            起: {{ inv.startAddr || '' }} ({{ inv.startDistance || 0 }}km) → 终: {{ inv.endAddr || '' }} ({{ inv.endDistance || 0 }}km)
          </div>
          <div class="text-sm text-hint mt-sm">
            时间差: {{ diffDate(trip?.departureTime, inv.departureTime || inv._departureTime) }} | 剩{{ inv.quantity || 0 }}座
          </div>
        </div>
      </div>

      <div v-if="inv.status === 0" class="invite-actions mt-md">
        <span v-if="(inv.quantity || 1) === 0" class="full-tag">已满员</span>
        <template v-else>
          <button class="btn-accept" :disabled="acting[inv.id]" @click="onAccept(inv)">
            {{ acting[inv.id] ? '处理中...' : '确认同行' }}
          </button>
          <button class="btn-reject" :disabled="acting[inv.id]" @click="onReject(inv)">不合适</button>
        </template>
      </div>
    </div>
    <van-empty v-if="inviteList.length === 0" description="暂无邀请" />

    <div style="padding: 0 16px; margin-top: 12px"><h3 class="section-title">顺路车主</h3></div>

    <div v-for="d in driverList" :key="d.inviteeTripId || d.id" class="card driver-card" @click="goChat(d)">
      <div class="flex" style="align-items: center">
        <van-image round width="48" height="48" :src="d.avatar || '/img/default-header.jpg'" />
        <div class="flex-1" style="margin-left: 12px">
          <div class="flex-between">
            <span class="text-bold">{{ d.useralias || '车主' }}</span>
            <span class="text-primary-color text-bold">顺路 {{ Number(d.suitability || 0).toFixed(2) }}%</span>
          </div>
          <div class="text-sm text-secondary mt-sm">起: {{ d.startAddr }} ({{ d.startDistance }}km) → 终: {{ d.endAddr }} ({{ d.endDistance }}km)</div>
          <div class="text-sm text-hint mt-sm">时间差: {{ d._diffDate }} | 剩{{ d.quantity }}座</div>
        </div>
      </div>
    </div>

    <van-empty v-if="driverList.length === 0" description="暂无顺路车主" />

    <TabBar />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import * as strokeApi from '@/api/stroke'

const router = useRouter()
const route = useRoute()
const trip = ref(null)
const driverList = ref([])
const inviteList = ref([])
const inviteLoading = ref(false)
const acting = reactive({})

function goChat(d) { router.push(`/message?t=${d.inviteeTripId}`) }

function onBack() {
  if (trip.value && trip.value.status === 0) {
    router.push('/passenger/trip')
  } else {
    router.back()
  }
}

function formatTime(t) {
  if (!t) return ''
  return String(t).substring(0, 16)
}

function diffDate(d1, d2) { var diff = Math.abs(new Date(d1) - new Date(d2)) / 1000; var hours = Math.floor(diff / 3600); var minutes = Math.floor(diff % 3600 / 60); if (hours > 0) return hours + "小时" + minutes + "分"; return minutes + "分" }

function invStatusLabel(status) {
  const map = { 0: '未确认', 1: '已确认', 2: '已拒绝', 3: '已超时' }
  return map[status] || '未知'
}

function invStatusColor(status) {
  const map = { 0: '#F59E0B', 1: '#3B82F6', 2: '#EF4444', 3: '#9CA3AF' }
  return map[status] || '#9CA3AF'
}

async function loadInvites() {
  inviteLoading.value = true
  try {
    const res = await strokeApi.inviteList(route.params.id)
    if (res.code === 200) {
      const raw = res.data || []
      // 按 ID 倒序排列，保证顺序稳定
      raw.sort((a, b) => (b.id || 0) - (a.id || 0))
      inviteList.value = raw
    }
  } catch { /* ignore */ }
  inviteLoading.value = false
}

async function onAccept(inv) {
  try {
    await showConfirmDialog({
      title: '确认同行',
      message: `确定要接受 ${inv.nickName || inv.useralias || '该司机'} 的邀请吗？`,
    })
  } catch { return }

  acting[inv.id] = true
  try {
    const res = await strokeApi.acceptInvite({
      inviterTripId: inv.inviteeTripId,
      inviteeTripId: inv.inviterTripId,
      status: 1,
    })
    if (res.code === 200) {
      showToast('已确认同行')
      router.push(`/passenger/trip-confirmed/${route.params.id}`)
    } else if (res.code === -5002) {
      showToast('行程已发车')
      await loadInvites()
    } else {
      showToast(res.msg || '操作失败')
    }
  } catch { showToast('操作失败') }
  finally { acting[inv.id] = false }
}

async function onReject(inv) {
  try {
    await showConfirmDialog({
      title: '不合适',
      message: `确定要拒绝 ${inv.nickName || inv.useralias || '该司机'} 的邀请吗？`,
    })
  } catch { return }

  acting[inv.id] = true
  try {
    const res = await strokeApi.acceptInvite({
      inviterTripId: inv.inviteeTripId,
      inviteeTripId: inv.inviterTripId,
      status: 2,
    })
    if (res.code === 200) {
      showToast('已拒绝')
      loadInvites()
    } else if (res.code === -5002) {
      showToast('行程已发车')
      await loadInvites()
    } else {
      showToast(res.msg || '操作失败')
    }
  } catch { showToast('操作失败') }
  finally { acting[inv.id] = false }
}

let pollTimer = null

async function loadAll(silent) {
  const id = route.params.id
  try {
    const r = await strokeApi.detail(id)
    if (r && r.data && r.data.length > 0) {
      const t = r.data[0]
      // 仅状态变化时更新，避免无意义重渲染
      if (!trip.value || trip.value.status !== t.status) trip.value = t
    }
  } catch (e) {}
  try {
    const r = await strokeApi.itineraryList(id)
    if (r && r.data && Array.isArray(r.data)) {
      const tripTime = trip.value ? trip.value.departureTime : null
      const list = r.data.map(function(d) {
        d._diffDate = tripTime && d.departureTime ? diffDate(tripTime, d.departureTime) : ''
        return d
      })
      // 轮询时仅在列表内容变化时替换，避免闪动
      if (!silent || !arraysEqual(driverList.value, list)) driverList.value = list
    }
  } catch (e) {}
  loadInvites()
}

function arraysEqual(a, b) {
  if (!a || !b || a.length !== b.length) return false
  return a.every((item, i) => item.id === b[i].id)
}

onMounted(() => {
  loadAll()
  pollTimer = setInterval(() => loadAll(true), 3000)
})

onBeforeUnmount(() => {
  clearInterval(pollTimer)
})
</script>

<style scoped>
/* 行程信息卡片 */
.trip-title { font-size: 18px; font-weight: 600; color: #333; text-align: center; margin: 12px 0; }
.info-card { background: #fff; border-radius: 10px; padding: 14px; margin: 0 12px 14px; }
.info-row { display: flex; align-items: center; gap: 8px; padding: 6px 0; font-size: 14px; color: #333; }

.section-title { font-size: 15px; font-weight: 700; color: #1A1A2E; margin-bottom: 8px; }
.driver-card { cursor: pointer; }
.driver-card:active { background: #f9f9f9; }
.invite-card { /* base */ }
.invite-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
.btn-accept {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}
.btn-accept:active { opacity: 0.85; }
.btn-accept:disabled { opacity: 0.5; }
.btn-reject {
  padding: 8px 20px;
  border: 1.5px solid #ccc;
  border-radius: 20px;
  background: #fff;
  color: #999;
  font-size: 14px;
  cursor: pointer;
}
.btn-reject:active { background: #f5f5f5; }
.full-tag {
  font-size: 13px;
  color: #EF4444;
  font-weight: 600;
}
</style>
