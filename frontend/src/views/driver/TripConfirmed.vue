<template>
  <div class="page">
    <AppHeader title="行程详情" show-back @click-left="$router.back()" />
    <div class="content" v-if="trip">
      <div class="trip-title arrived">已发车</div>
      <div class="info-card">
        <div class="info-row"><van-icon name="clock-o" /><span>{{ formatDate(trip.departureTime) }}</span></div>
        <div class="info-row"><van-icon name="location-o" color="#4caf50" /><span>{{ trip.startAddr }}</span></div>
        <div class="info-row"><van-icon name="flag-o" color="#f44336" /><span>{{ trip.endAddr }}</span></div>
      </div>
      <div class="section-title">同行乘客</div>
      <div v-if="fellows.length === 0" class="empty-tip">暂无同行乘客</div>
      <div v-for="f in fellows" :key="f.passengerStrokeId || f.id" class="fellow-card" @click="goChat(f.passengerStrokeId)">
        <van-image round width="44" height="44" :src="f.passengerAvatar || f.avatar || defaultAvatar" fit="cover" />
        <div class="fellow-info">
          <div class="fellow-name">{{ f.passengerUseralias || f.useralias || '乘客' }}</div>
          <div class="fellow-route">{{ f.passengerStartAddr || f.startAddr }} → {{ f.passengerEndAddr || f.endAddr }}</div>
          <div class="fellow-status">{{ fellowStatusLabel(f.passengerStrokeStatus || f.status) }}</div>
        </div>
        <van-icon name="arrow" color="#ccc" />
      </div>
      <div class="action-wrapper">
        <van-button round block type="primary" color="#4caf50" size="large" @click="onDelivery">确认送达</van-button>
      </div>
    </div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import * as strokeApi from '@/api/stroke'
import * as orderApi from '@/api/order'
import { formatDate } from '@/utils'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const defaultAvatar = '/img/default-header.jpg'
const route = useRoute()
const router = useRouter()
const trip = ref(null)
const fellows = ref([])
const id = route.params.id
const fellowStatusMap = { 0: '邀请中', 1: '已确认同行', 2: '已上车', 3: '已下车', 4: '已取消' }
const fellowStatusLabel = (s) => fellowStatusMap[s] || '未知'

onMounted(async () => {
  try {
    const [tripRes, fellowRes] = await Promise.all([
      strokeApi.detail(id),
      orderApi.fellows({ driverStrokeId: id }),
    ])
    trip.value = (tripRes && tripRes.data && tripRes.data.length > 0) ? tripRes.data[0] : tripRes
    fellows.value = (fellowRes && fellowRes.data && Array.isArray(fellowRes.data)) ? fellowRes.data : (Array.isArray(fellowRes) ? fellowRes : [])
  } catch {}
})

const onDelivery = async () => {
  try { await strokeApi.delivery(id); showToast('已确认送达'); setTimeout(() => router.push(`/driver/trip-info/${id}`), 200) }
  catch { showToast('操作失败，请重试') }
}

const goChat = (passengerStrokeId) => { router.push({ path: '/message', query: { t: passengerStrokeId } }) }
</script>

<style scoped>
.page { min-height: 100vh; background: #f7f8fa; padding-bottom: 60px; }
.content { padding: 12px; }
.trip-title { font-size: 18px; font-weight: 600; text-align: center; margin: 12px 0; }
.trip-title.arrived { color: #4caf50; }
.info-card { background: #fff; border-radius: 10px; padding: 14px; margin-bottom: 14px; }
.info-row { display: flex; align-items: center; gap: 8px; padding: 6px 0; font-size: 14px; color: #333; }
.section-title { font-size: 15px; font-weight: 500; margin: 8px 0; }
.empty-tip { text-align: center; color: #999; font-size: 13px; padding: 20px; }
.fellow-card { background: #fff; border-radius: 10px; padding: 12px; margin-bottom: 10px; display: flex; align-items: center; gap: 10px; cursor: pointer; }
.fellow-info { flex: 1; min-width: 0; }
.fellow-name { font-size: 14px; font-weight: 500; margin-bottom: 2px; }
.fellow-route { font-size: 12px; color: #666; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.fellow-status { font-size: 11px; color: #999; }
.action-wrapper { margin-top: 24px; padding: 0 12px 20px; }
</style>
