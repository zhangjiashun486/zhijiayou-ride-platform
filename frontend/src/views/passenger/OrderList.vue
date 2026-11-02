<template>
  <div class="page-container">
    <AppHeader title="我的订单" @click-left="onSwitchRole">
      <template #left><van-icon name="user-o" size="20" color="var(--color-primary)" /></template>
      <template #right><van-icon name="service-o" size="20" color="var(--color-primary)" @click.stop="goAiRoute" /></template>
    </AppHeader>

    <div class="trip-banner passenger-order-banner">
      <div class="banner-text">
        <div class="banner-title">📋 乘客订单</div>
        <div class="banner-sub">管理出行订单与支付</div>
      </div>
    </div>

    <van-loading v-if="loading" style="display:block; margin: 60px auto" />

    <div v-for="order in orderList" :key="order.id" class="card">
      <div class="flex-between">
        <span :style="{ color: orderStatusColor(order.status) }" class="text-bold">{{ orderStatusLabel(order.status) }}</span>
        <span class="text-sm text-hint">{{ formatTime(order.createdTime || order.departureTime) }}</span>
      </div>
      <div class="mt-sm text-secondary">
        <div>🟢 {{ order.passengerStartAddr || order.startAddr || '-' }}</div>
        <div>🔴 {{ order.passengerEndAddr || order.endAddr || '-' }}</div>
      </div>
      <div class="flex-between mt-sm">
        <span class="text-primary-color text-bold">¥{{ (order.cost || 0).toFixed(2) }}</span>
        <button v-if="order.status == 1" class="pay-btn" @click="goPay(order)">点我支付</button>
      </div>
    </div>

    <van-empty v-if="!loading && orderList.length === 0" description="暂无订单" />
    <TabBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import { useAuthStore } from '@/stores/auth'
import * as orderApi from '@/api/order'
import { getUserInfo } from '@/api/account'

const router = useRouter()
const auth = useAuthStore()
const orderList = ref([])
const loading = ref(true)

function onSwitchRole() {
  const newRole = auth.currentRole == 0 ? 1 : 0
  doSwitch(newRole)
}

function goAiRoute() { router.push('/common/ai-route') }

async function doSwitch(newRole) {
  if (newRole === 1) {
    try {
      const res = await getUserInfo()
      const user = res.data?.[0] || res.data || {}
      if (user.role !== 1) return showToast('请先认证为车主')
    } catch { return showToast('网络异常，请重试') }
  }
  auth.setRole(newRole)
  router.push(newRole === 1 ? '/driver/order' : '/passenger/order')
}
function formatTime(s) { return s ? String(s).substring(0, 16) : '' }
function orderStatusLabel(s) { return ({ 1:'未支付',2:'已支付' })[s] || '未知' }
function orderStatusColor(s) { return ({ 1:'#EF4444',2:'#10B981' })[s] || '#9CA3AF' }

function goPay(order) {
  router.push(`/passenger/pay?id=${order.id}&tripId=${order.passengerStrokeId || ''}&c=${order.cost || 0}`)
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await orderApi.list(auth.currentRole)
    orderList.value = (res && res.data && Array.isArray(res.data)) ? res.data : (Array.isArray(res) ? res : [])
  } catch {}
  loading.value = false
})
</script>

<style scoped>
.trip-banner { width: 100%; height: 80px; display: flex; align-items: center; padding: 0 20px; border-radius: 0 0 20px 20px; margin-bottom: 12px; }
.passenger-order-banner { background: linear-gradient(135deg, #f093fb, #f5576c); }
.banner-text { color: #fff; }
.banner-title { font-size: 18px; font-weight: 700; }
.banner-sub { font-size: 12px; opacity: 0.85; margin-top: 4px; }
.pay-btn { padding: 6px 18px; border: none; border-radius: 20px; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light)); color: #fff; font-size: 13px; font-weight: 600; cursor: pointer; }
.pay-btn:active { opacity: 0.85; }
.switch-btn { font-size: 14px; color: var(--color-primary); font-weight: 500; }
</style>
