<template>
  <div class="page-container trip-page">
    <AppHeader title="我的行程" @click-left="onSwitchRole">
      <template #left><van-icon name="user-o" size="20" /></template>
      <template #right><van-icon name="service-o" size="20" @click.stop="goAiRoute" /></template>
    </AppHeader>

    <section class="trip-summary">
      <div>
        <p class="summary-label">司机行程</p>
        <h1 class="summary-title">共 {{ list.length }} 个行程</h1>
        <p class="summary-sub">发布路线，搭载同行乘客</p>
      </div>
      <button class="switch-btn" @click="onSwitchRole">
        <van-icon name="swap" size="14" /> 切到乘客
      </button>
    </section>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" class="trip-list">
      <div v-if="loading" class="skeleton-list">
        <div v-for="n in 3" :key="n" class="skeleton-card"></div>
      </div>

      <template v-else>
        <article v-for="item in list" :key="item.id" class="trip-card" @click="onItemClick(item)">
          <div class="trip-top">
            <span class="trip-time">{{ formatTime(item.departureTime) }}</span>
            <span class="status-pill" :class="statusClass(item.status)">{{ statusLabel(item.status) }}</span>
          </div>
          <div class="route">
            <div class="route-dots">
              <span class="dot dot--start"></span>
              <span class="dash"></span>
              <span class="dot dot--end"></span>
            </div>
            <div class="route-text">
              <p>{{ item.startAddr || '未填写出发地' }}</p>
              <p>{{ item.endAddr || '未填写目的地' }}</p>
            </div>
          </div>
          <div class="trip-foot">
            <span v-if="item.quickConfirm == 1" class="quick-tag"><van-icon name="flash" size="12" /> 闪电确认</span>
            <span v-else class="quick-tag quick-tag--muted">普通确认</span>
            <span class="detail-link">查看详情 <van-icon name="arrow" size="12" /></span>
          </div>
        </article>

        <van-empty v-if="list.length === 0" image="search" description="还没有行程，去首页发布一个吧">
          <button class="btn-primary empty-btn" @click="router.push('/driver/home')">去发布行程</button>
        </van-empty>
      </template>
    </van-pull-refresh>

    <TabBar />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import { useAuthStore } from '@/stores/auth'
import * as strokeApi from '@/api/stroke'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const list = ref([])
const loading = ref(true)
const refreshing = ref(false)

function onSwitchRole() {
  auth.setRole(0)
  router.push('/passenger/trip')
}

function goAiRoute() {
  router.push('/common/ai-route')
}

function formatTime(s) {
  return s ? String(s).substring(0, 16) : ''
}

function statusLabel(s) {
  return ({ 0: '邀请中', 1: '已发车', 3: '已送达' })[s] || '未知'
}

function statusClass(s) {
  return ({
    0: 'status-pill--waiting',
    1: 'status-pill--active',
    3: 'status-pill--done',
  })[s] || 'status-pill--cancel'
}

function onItemClick(item) {
  const s = Number(item.status)
  if (s === 0) router.push(`/driver/trip-detail/${item.id}`)
  else if (s === 1 || s === 2) router.push(`/driver/trip-confirmed/${item.id}`)
  else router.push(`/driver/trip-info/${item.id}`)
}

async function fetchList() {
  loading.value = true
  try {
    const res = await strokeApi.list(auth.currentRole)
    list.value = (res && res.data && Array.isArray(res.data)) ? res.data : (Array.isArray(res) ? res : [])
  } catch {
    /* 下拉刷新时静默失败 */
  } finally {
    loading.value = false
  }
}

async function onRefresh() {
  refreshing.value = true
  await fetchList()
  refreshing.value = false
}

watch(() => route.fullPath, () => fetchList())
onMounted(fetchList)
</script>

<style scoped>
.trip-page {
  background: var(--bg-page);
}

.trip-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 16px;
  padding: 20px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  color: #fff;
  box-shadow: var(--shadow-button);
}
.summary-label {
  font-size: 12px;
  opacity: 0.85;
}
.summary-title {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
}
.summary-sub {
  margin-top: 6px;
  font-size: 12px;
  opacity: 0.85;
}
.switch-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  border: none;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  backdrop-filter: blur(6px);
}

.trip-list {
  padding: 0 16px 24px;
}
.trip-card {
  margin-bottom: 12px;
  padding: 16px;
  border-radius: var(--radius-md);
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
  cursor: pointer;
}
.trip-card:active {
  transform: scale(0.99);
}
.trip-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.trip-time {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}
.status-pill {
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}
.status-pill--waiting { background: #FFF7E6; color: #B45309; }
.status-pill--active { background: #EFF6FF; color: #1D4ED8; }
.status-pill--done { background: #ECFDF5; color: #047857; }
.status-pill--cancel { background: var(--bg-soft); color: var(--text-hint); }

.route {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}
.route-dots {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 5px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.dot--start { background: var(--color-secondary); }
.dot--end { background: var(--color-primary); }
.dash {
  width: 2px;
  flex: 1;
  min-height: 18px;
  margin: 3px 0;
  background: var(--border-color);
}
.route-text {
  flex: 1;
  min-width: 0;
}
.route-text p {
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.route-text p + p {
  margin-top: 10px;
}

.trip-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--divider-color);
}
.quick-tag {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 3px 9px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--color-primary) 12%, transparent);
  color: var(--color-primary);
  font-size: 11px;
  font-weight: 600;
}
.quick-tag--muted {
  background: var(--bg-soft);
  color: var(--text-hint);
}
.detail-link {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  color: var(--text-hint);
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.skeleton-card {
  height: 140px;
  border-radius: var(--radius-md);
  background: linear-gradient(90deg, var(--bg-card) 25%, var(--bg-soft) 37%, var(--bg-card) 63%);
  background-size: 400% 100%;
  animation: shimmer 1.3s ease infinite;
}
@keyframes shimmer {
  0% { background-position: 100% 0; }
  100% { background-position: 0 0; }
}
.empty-btn {
  width: 160px;
  margin: 16px auto 0;
}
</style>
