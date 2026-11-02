<template>
  <div class="page-container">
    <AppHeader title="行程确认" show-back @click-left="router.push('/passenger/trip-detail/' + route.params.id)" />

    <div class="card" v-if="trip">
      <div class="text-lg text-bold">{{ formatTime(trip.departureTime) }}</div>
      <div class="mt-sm text-secondary">{{ trip.startAddr }} → {{ trip.endAddr }}</div>
    </div>

    <div class="card" v-if="driver">
      <div class="flex" style="align-items: center">
        <van-image round width="56" height="56" :src="driver.avatar || '/img/default-header.jpg'" />
        <div class="flex-1" style="margin-left: 12px">
          <div class="text-bold text-lg">{{ driver.driverUseralias || driver.nickName || '司机' }}</div>
          <div class="text-sm text-secondary mt-sm">车牌: {{ driver.carNumber || driver.plateNo || '-' }}</div>
          <div class="text-sm text-secondary mt-sm">
            费用: ¥{{ driver.cost || ((driver.estimatedTime || 0) / 60).toFixed(2) }}
          </div>
          <div class="text-sm text-secondary mt-sm">
            预计: {{ driver.time || driver.estimatedArrive || '-' }}分钟
          </div>
        </div>
        <van-icon name="chat-o" size="24" color="var(--color-primary)" @click="goChat" />
      </div>
    </div>

    <div style="padding: 20px 16px">
      <button class="btn-primary" :disabled="acting" @click="onAction">
        {{ acting ? '处理中...' : btnText }}
      </button>
    </div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import * as strokeApi from '@/api/stroke'
import * as orderApi from '@/api/order'

const router = useRouter()
const route = useRoute()
const trip = ref(null)
const driver = ref(null)
const acting = ref(false)

const btnText = computed(() => {
  if (!trip.value) return '加载中...'
  return trip.value.status === 1 ? '确认上车' : '确认下车'
})

function formatTime(dateStr) {
  if (!dateStr) return ''
  return String(dateStr).substring(0, 16)
}

function goChat() {
  if (driver.value) {
    router.push(`/message?t=${driver.value.driverStrokeId || route.params.id}`)
  }
}

async function onAction() {
  if (!trip.value) return
  const id = route.params.id
  acting.value = true
  try {
    if (trip.value.status === 1) {
      const res = await strokeApi.hitchhiker(id)
      if (res.code === 200 || res.code === 0) {
        showToast('已确认上车')
        await loadTrip()
      } else {
        showToast(res.message || res.msg || '操作失败')
      }
    } else if (trip.value.status === 2) {
      const res = await strokeApi.freeride(id)
      if (res.code === 200 || res.code === 0) {
        showToast('已确认下车')
        setTimeout(() => router.push('/passenger/order'), 200)
      } else {
        showToast(res.message || res.msg || '操作失败')
      }
    }
  } catch {
    showToast('操作失败，请重试')
  } finally { acting.value = false }
}

async function loadTrip() {
  try {
    const res = await strokeApi.detail(route.params.id)
    if (res.code === 200) trip.value = res.data?.[0] || {}
  } catch { /* ignore */ }
}

onMounted(async () => {
  await loadTrip()
  try {
    const res = await orderApi.viewOrder(route.params.id)
    if (res.code === 200) driver.value = res.data?.[0] || {}
  } catch { /* ignore */ }
})
</script>
