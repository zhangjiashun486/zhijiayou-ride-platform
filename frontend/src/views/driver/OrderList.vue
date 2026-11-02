<template>
  <div class="page">
    <AppHeader title="我的订单" @click-left="onSwitchRole">
      <template #left><van-icon name="logistics" size="20" color="var(--color-primary)" /></template>
      <template #right><van-icon name="service-o" size="20" color="var(--color-primary)" @click.stop="goAiRoute" /></template>
    </AppHeader>

    <div class="trip-banner driver-order-banner">
      <div class="banner-text">
        <div class="banner-title">💰 司机订单</div>
        <div class="banner-sub">订单管理与收款记录</div>
      </div>
    </div>

    <div class="content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <div v-for="item in list" :key="item.id" class="order-card">
          <div class="card-header">
            <span class="status-tag" :class="statusClass(item.status)">{{ orderStatusLabel(item.status) }}</span>
            <span class="time">{{ formatDate(item.createdTime) }}</span>
          </div>
          <div class="card-body">
            <div class="body-row"><span class="value text-bold">{{ item.passengerUseralias || '—' }}</span></div>
            <div class="body-row">
              <span class="value" style="line-height:1.6; text-align:left; max-width:100%">
                <div>🟢 {{ item.passengerStartAddr }}</div>
                <div>🔴 {{ item.passengerEndAddr }}</div>
              </span>
            </div>
            <div class="body-row"><span class="value price">¥{{ item.cost || 0 }}</span></div>
          </div>
          <div class="card-footer">
            <van-button size="small" round type="primary" color="#667eea" @click="goChat(item.passengerStrokeId)">💬 联系ta</van-button>
            <van-button v-if="item.status == 1" size="small" round type="primary" color="#4caf50" @click="onConfirmPay(item)">到款确认</van-button>
          </div>
        </div>
      </van-pull-refresh>
      <van-empty v-if="!loading && list.length === 0" description="暂无订单" />
    </div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useAuthStore } from '@/stores/auth'
import * as orderApi from '@/api/order'
import * as paymentApi from '@/api/payment'
import { formatDate } from '@/utils'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const auth = useAuthStore()
const list = ref([])
const loading = ref(true)
const refreshing = ref(false)

function onSwitchRole() { auth.setRole(0); router.push('/passenger/order') }

function goAiRoute() { router.push('/common/ai-route') }

const orderStatusMap = { 0: '临时订单', 1: '未支付', 2: '已支付' }
const statusClassMap = { 0: 's-pending', 1: 's-unpaid', 2: 's-paid' }
const orderStatusLabel = (s) => orderStatusMap[s] || '未知'
const statusClass = (s) => statusClassMap[s] || ''

async function fetchList() {
  loading.value = true
  try {
    const res = await orderApi.list(auth.currentRole)
    list.value = (res && res.data && Array.isArray(res.data)) ? res.data : (Array.isArray(res) ? res : [])
  } catch {} finally { loading.value = false }
}

async function onRefresh() { refreshing.value = true; await fetchList(); refreshing.value = false }

const onConfirmPay = async (item) => {
  try { await paymentApi.confirmPay(item.id); showToast('已确认到款'); await fetchList() } catch {}
}

const goChat = (passengerStrokeId) => { router.push({ path: '/message', query: { t: passengerStrokeId } }) }

onMounted(fetchList)
</script>

<style scoped>
.trip-banner { width: 100%; height: 80px; display: flex; align-items: center; padding: 0 20px; border-radius: 0 0 20px 20px; margin-bottom: 12px; }
.driver-order-banner { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }
.banner-text { color: #fff; }
.banner-title { font-size: 18px; font-weight: 700; }
.banner-sub { font-size: 12px; opacity: 0.85; margin-top: 4px; }
.page { min-height: 100vh; background: #f7f8fa; padding-bottom: 60px; }
.content { padding: 12px; }
.order-card { background: #fff; border-radius: 10px; padding: 14px; margin-bottom: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.status-tag { font-size: 12px; padding: 2px 8px; border-radius: 4px; font-weight: 500; }
.s-pending { background: #f5f5f5; color: #999; }
.s-unpaid { background: #fff3e0; color: #ff9800; }
.s-paid { background: #e8f5e9; color: #4caf50; }
.time { font-size: 13px; color: #999; }
.card-body { margin-bottom: 10px; }
.body-row { display: flex; justify-content: space-between; padding: 3px 0; font-size: 13px; }
.body-row .label { color: #999; flex-shrink: 0; }
.body-row .value { color: #333; text-align: right; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 70%; }
.price { color: #f44336 !important; font-weight: 500; }
.card-footer { display: flex; justify-content: flex-end; gap: 8px; }
.switch-btn { font-size: 14px; color: var(--color-primary); font-weight: 500; }
</style>
