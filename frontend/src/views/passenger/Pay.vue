<template>
  <div class="page-container">
    <AppHeader title="支付" show-back @click-left="router.back" />

    <!-- 金额展示 -->
    <div class="card" style="text-align: center">
      <div class="text-hint">支付金额</div>
      <div class="amount">¥{{ cost }}</div>
    </div>

    <!-- 二维码（未支付时） -->
    <div class="card" style="text-align: center" v-if="!paid">
      <van-loading v-if="!codeUrl" style="margin: 20px auto" />
      <template v-else>
        <img :src="codeUrl" alt="支付二维码" class="qr-img" />
        <div class="text-sm text-hint mt-md">请使用微信/支付宝扫码支付</div>
        <div class="text-sm text-warn mt-sm">请勿重复支付，支付后及时联系司机确认到款</div>
      </template>
    </div>

    <!-- 已支付 -->
    <div class="card" style="text-align: center" v-if="paid">
      <van-icon name="checked" size="48" color="#10B981" />
      <div class="paid-msg">该订单您已支付，司机已确认到款，感谢您的配合。</div>
    </div>

    <!-- 行程摘要 -->
    <div class="card" v-if="tripSummary">
      <div class="text-sm text-secondary">行程摘要</div>
      <div class="text-bold mt-sm">{{ formatTime(tripSummary.departureTime) }}</div>
      <div class="text-sm text-secondary mt-sm">
        {{ tripSummary.startAddr }} → {{ tripSummary.endAddr }}
      </div>
    </div>

    <!-- 查看订单 -->
    <div style="padding: 20px 16px" v-if="paid">
      <button class="btn-primary" @click="router.push('/passenger/order')">查看订单</button>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import * as paymentApi from '@/api/payment'
import * as strokeApi from '@/api/stroke'

const router = useRouter()
const route = useRoute()

const id = computed(() => route.query.id)
const tripId = computed(() => route.query.tripId)
const cost = computed(() => Number(route.query.c || 0).toFixed(2))

const codeUrl = ref('')
const paid = ref(false)
const tripSummary = ref(null)

let pollTimer = null

function formatTime(dateStr) {
  if (!dateStr) return ''
  return String(dateStr).substring(0, 16)
}

async function pollPayment() {
  try {
    const res = await paymentApi.queryPayment({ orderId: id.value })
    if (res.code === 200 && res.data && res.data.length > 0) {
      if (res.data[0].status === 2) {
        paid.value = true
        stopPolling()
      }
    }
  } catch { /* ignore */ }
}

function startPolling() {
  stopPolling()
  pollTimer = setInterval(pollPayment, 3000)
}

function stopPolling() {
  if (pollTimer) { clearInterval(pollTimer); pollTimer = null }
}

onMounted(async () => {
  try {
    const res = await paymentApi.createPayment({ orderId: id.value })
    if (res.code === -5005) {
      setTimeout(() => router.push('/passenger/order'), 1000)
      return
    }
    if (res.code === 200 && res.data && res.data.length > 0) {
      codeUrl.value = res.data[0].codeUrl || ''
    }
  } catch { /* ignore */ }

  if (tripId.value) {
    try {
      const res = await strokeApi.detail(tripId.value)
      if (res.code === 200 && res.data && res.data.length > 0) {
        tripSummary.value = res.data[0]
      }
    } catch { /* ignore */ }
  }

  startPolling()
})

onUnmounted(() => { stopPolling() })
</script>

<style scoped>
.amount {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-primary);
  margin-top: 8px;
}
.qr-img {
  width: 200px;
  height: 200px;
  object-fit: contain;
  border: 1px solid var(--border-color);
  border-radius: 8px;
}
.paid-msg {
  color: #10B981;
  font-size: 15px;
  font-weight: 600;
  margin-top: 12px;
  line-height: 1.6;
}
.text-warn { color: #E6A23C; }
</style>
