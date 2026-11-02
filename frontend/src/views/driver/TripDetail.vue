<template>
  <div class="page">
    <AppHeader title="行程详情" show-back @click-left="$router.replace('/driver/trip')" />
    <div class="content" v-if="trip">
      <div class="trip-title">未出发，拼单中……</div>
      <div class="info-card">
        <div class="info-row"><van-icon name="clock-o" /><span>{{ formatDate(trip.departureTime) }}</span></div>
        <div class="info-row"><van-icon name="location-o" color="#4caf50" /><span>{{ trip.startAddr }}</span></div>
        <div class="info-row"><van-icon name="flag-o" color="#f44336" /><span>{{ trip.endAddr }}</span></div>
      </div>

      <!-- 我邀请的 -->
      <div class="section-title">同行乘客</div>
      <div v-if="confirmedInvites.length === 0" class="empty-tip">目前还没有乘客同意与您同行</div>
      <div v-for="item in confirmedInvites" :key="item.id" class="invite-card" @click="goChat(item.inviteeTripId)">
        <van-image round width="44" height="44" :src="item.avatar || defaultAvatar" fit="cover" />
        <div class="invite-info">
          <div class="invite-name">{{ item.useralias || '用户' }} <span class="status-tag s-confirmed">已确认</span></div>
          <div class="invite-route">起: {{ item.startAddr || '' }} ({{ item.startDistance || 0 }}km) → 终: {{ item.endAddr || '' }} ({{ item.endDistance || 0 }}km)</div>
          <div class="invite-meta" v-if="item._diffDate">时间差: {{ item._diffDate }}</div>
        </div>
        <van-icon name="arrow" color="#ccc" />
      </div>

      <!-- 顺路乘客 -->
      <div class="section-title">顺路乘客</div>
      <div v-if="otherPax.length === 0" class="empty-tip">暂无顺路乘客</div>
      <div v-for="p in otherPax" :key="p.inviteeTripId || p.id" class="passenger-card">
        <div class="passenger-top" @click="goChat(p.inviteeTripId)">
          <van-image round width="44" height="44" :src="p.avatar || defaultAvatar" fit="cover" />
          <div class="passenger-info">
            <div class="passenger-name">
              {{ p.useralias || '用户' }}
              <span v-if="p.quickConfirm == 1" class="badge quick-badge">闪电确认</span>
              <span v-if="paxStatusLabel(p)" class="status-tag" :class="paxStatusClass(p)">{{ paxStatusLabel(p) }}</span>
            </div>
            <div class="passenger-route">起: {{ p.startAddr }} → 终: {{ p.endAddr }}</div>
            <div class="passenger-meta">
              <span>顺路度 {{ Number(p.suitability || 0).toFixed(2) }}%</span>
              <span>{{ p._diffDate }}</span>
            </div>
          </div>
        </div>
        <div class="passenger-action">
          <van-button size="small" icon="like-o" type="primary" color="var(--color-primary)" round @click.stop="onInvite(p)">邀请</van-button>
        </div>
      </div>

      <div class="departure-wrapper">
        <van-button round block type="primary" color="#4caf50" size="large" @click="onDeparture">出发！</van-button>
      </div>
    </div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import * as strokeApi from '@/api/stroke'
import { formatDate } from '@/utils'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const defaultAvatar = '/img/default-header.jpg'
const route = useRoute()
const router = useRouter()
const trip = ref(null)
const reqList = ref([])
const passengers = ref([])
const id = route.params.id

const statusMap = { 0: '未确认', 1: '已确认', 2: '已拒绝', 3: '已超时' }
const statusClassMap = { 0: 's-pending', 1: 's-confirmed', 2: 's-rejected', 3: 's-timeout' }

function inviteStatusLabel(s) { return statusMap[s] || '未知' }
function inviteStatusClass(s) { return statusClassMap[s] || '' }

function getInvite(p) { return reqList.value.find(r => r.inviteeTripId === p.inviteeTripId) }

function paxStatusLabel(p) {
  const inv = getInvite(p)
  return inv ? (statusMap[inv.status] || '') : ''
}
function paxStatusClass(p) {
  const inv = getInvite(p)
  return inv ? (statusClassMap[inv.status] || '') : ''
}

const confirmedInvites = computed(() => {
  const list = reqList.value.filter(r => r.status === 1)
  // 按 ID 倒序
  list.sort((a, b) => (b.id || 0) - (a.id || 0))
  // 补时间差
  const tripTime = trip.value ? trip.value.departureTime : null
  list.forEach(r => {
    r._diffDate = tripTime && r.departureTime ? diffDate(tripTime, r.departureTime) : ''
  })
  return list
})

const confirmedPax = computed(() =>
  passengers.value.filter(p => { const inv = getInvite(p); return inv && inv.status === 1 })
)
const otherPax = computed(() =>
  passengers.value.filter(p => { const inv = getInvite(p); return !inv || inv.status !== 1 })
)

function diffDate(d1, d2) { var diff = Math.abs(new Date(d1) - new Date(d2)) / 1000; var hours = Math.floor(diff / 3600); var minutes = Math.floor(diff % 3600 / 60); if (hours > 0) return hours + "小时" + minutes + "分"; return minutes + "分" }

let pollTimer = null

async function loadData() {
  try {
    const [tripRes, reqRes, paxRes] = await Promise.all([
      strokeApi.detail(id),
      strokeApi.inviteList(id),
      strokeApi.itineraryList(id),
    ])
    trip.value = (tripRes && tripRes.data && tripRes.data.length > 0) ? tripRes.data[0] : tripRes
    reqList.value = (reqRes && reqRes.data && Array.isArray(reqRes.data)) ? reqRes.data : (Array.isArray(reqRes) ? reqRes : [])
    const tripTime = trip.value ? trip.value.departureTime : null
    const raw = (paxRes && paxRes.data && Array.isArray(paxRes.data)) ? paxRes.data : (Array.isArray(paxRes) ? paxRes : [])
    passengers.value = raw.map(function(p) {
      p._diffDate = tripTime && p.departureTime ? diffDate(tripTime, p.departureTime) : ''
      return p
    })
  } catch {}
}

onMounted(() => {
  loadData()
  pollTimer = setInterval(loadData, 3000)
})

onBeforeUnmount(() => {
  clearInterval(pollTimer)
})

const onInvite = async (p) => {
  try { await showConfirmDialog({ title: '邀请同行', message: `确定邀请该用户同行吗？` }) }
  catch { return }
  try {
    await strokeApi.invite({ inviterTripId: p.inviterTripId, inviteeTripId: p.inviteeTripId })
    showToast('邀请已发送')
    await loadData()
  } catch {}
}

const onDeparture = async () => {
  try { await strokeApi.departure(id); showToast('已发车'); router.push(`/driver/trip-confirmed/${id}`) }
  catch {}
}

const goChat = (inviteeTripId) => { router.push({ path: '/message', query: { t: inviteeTripId } }) }
</script>

<style scoped>
.page { min-height: 100vh; background: #f7f8fa; padding-bottom: 60px; }
.content { padding: 12px; }
.trip-title { font-size: 18px; font-weight: 600; color: #333; text-align: center; margin: 12px 0; }
.info-card { background: #fff; border-radius: 10px; padding: 14px; margin: 0 12px 14px; }
.info-row { display: flex; align-items: center; gap: 8px; padding: 6px 0; font-size: 14px; color: #333; }
.section-title { font-size: 15px; font-weight: 500; margin: 8px 0; }
.empty-tip { text-align: center; color: #999; font-size: 13px; padding: 20px; }

/* 我邀请的 */
.invite-card { background: #fff; border-radius: 10px; padding: 12px; margin-bottom: 10px; display: flex; align-items: center; gap: 10px; cursor: pointer; }
.invite-info { flex: 1; min-width: 0; }
.invite-name { font-size: 14px; font-weight: 500; margin-bottom: 4px; display: flex; align-items: center; gap: 6px; }
.invite-route { font-size: 12px; color: #666; word-break: break-all; }

/* 同行乘客 + 顺路乘客 */
.passenger-card { background: #fff; border-radius: 10px; padding: 12px; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center; }
.passenger-top { display: flex; gap: 10px; flex: 1; cursor: pointer; min-width: 0; }
.passenger-info { flex: 1; min-width: 0; }
.passenger-name { font-size: 14px; font-weight: 500; margin-bottom: 4px; display: flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.passenger-route { font-size: 12px; color: #666; word-break: break-all; margin-bottom: 4px; }
.passenger-meta { font-size: 11px; color: #999; display: flex; gap: 10px; }
.passenger-action { flex-shrink: 0; margin-left: 10px; }

/* 状态标签 */
.status-tag { font-size: 10px; padding: 1px 5px; border-radius: 3px; white-space: nowrap; }
.s-pending { background: #fff3e0; color: #ff9800; }
.s-confirmed { background: #e8f5e9; color: #4caf50; }
.s-rejected { background: #fce4ec; color: #f44336; }
.s-timeout { background: #f5f5f5; color: #999; }

/* badge */
.badge { font-size: 10px; padding: 1px 5px; border-radius: 3px; white-space: nowrap; }
.quick-badge { background: #e8f5e9; color: #4caf50; }

.departure-wrapper { margin-top: 24px; padding: 0 12px 20px; }
</style>
