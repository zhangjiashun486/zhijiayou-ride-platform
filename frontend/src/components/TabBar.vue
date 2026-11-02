<template>
  <div class="tabbar-wrapper">
    <van-tabbar
      :model-value="active"
      :fixed="true"
      :safe-area-inset-bottom="true"
      :active-color="theme.mainColor"
      inactive-color="var(--text-hint)"
      class="app-tabbar"
    >
      <van-tabbar-item
        v-for="item in tabs"
        :key="item.name"
        :icon="item.icon"
        @click="router.push(item.to)"
      >
        {{ item.label }}
      </van-tabbar-item>
    </van-tabbar>

    <!-- 居中圆形发布按钮 -->
    <div class="center-btn" @click="openPublish">
      <van-icon name="plus" size="26" color="#fff" />
    </div>

    <!-- 发布行程浮窗 -->
    <van-popup
      v-model:show="showPopup"
      position="bottom"
      round
      :style="{ height: 'auto', maxHeight: '85vh' }"
      teleport="body"
    >
      <div class="popup-content">
        <div class="popup-handle"></div>
        <div class="popup-header">
          <div>
            <span class="popup-title">{{ isDriver ? '发布行程' : '发布出行需求' }}</span>
            <p class="popup-sub">{{ isDriver ? '填写路线，搭载顺路乘客' : '说说你想去哪，等待车主邀请' }}</p>
          </div>
          <van-icon name="cross" size="20" color="var(--text-hint)" @click="closePublish" />
        </div>

        <div class="popup-body">
          <div class="form-item">
            <label class="form-label"><van-icon name="clock-o" /> 出发时间</label>
            <input v-model="form.departureTime" type="datetime-local" class="input-field" />
          </div>
          <div class="form-item">
            <label class="form-label"><van-icon name="location-o" /> 出发地</label>
            <input
              :value="form.startAddr"
              readonly
              class="input-field"
              placeholder="点击选择出发地"
              @click="goMap('f')"
            />
          </div>
          <div class="form-item">
            <label class="form-label"><van-icon name="location-o" /> 目的地</label>
            <input
              :value="form.endAddr"
              readonly
              class="input-field"
              placeholder="点击选择目的地"
              @click="goMap('t')"
            />
          </div>

          <!-- 司机端：座位数 -->
          <div v-if="isDriver" class="form-item">
            <label class="form-label"><van-icon name="friends-o" /> 可载座位：{{ form.quantity }} 座</label>
            <input
              v-model.number="form.quantity"
              type="range"
              min="1"
              max="5"
              step="1"
              class="range-input"
            />
          </div>

          <!-- 乘客端：闪电确认 -->
          <div v-if="!isDriver" class="form-item switch-row">
            <div>
              <label class="form-label"><van-icon name="flash" /> 闪电确认</label>
              <p class="form-tip">开启后车主可直接确认同行，无需等待</p>
            </div>
            <van-switch v-model="form.quickConfirm" size="24" :active-color="theme.mainColor" />
          </div>

          <button class="btn-primary" :disabled="publishing" @click="onPublish">
            {{ publishing ? '发布中...' : '立即发布' }}
          </button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { reactive, ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import * as strokeApi from '@/api/stroke'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const theme = useThemeStore()

const role = computed(() => (auth.currentRole == 0 ? 'passenger' : 'driver'))
const isDriver = computed(() => auth.currentRole === 1)

const tabs = computed(() => [
  { name: 'home',  label: '首页', icon: 'home-o',   to: `/${role.value}/home` },
  { name: 'trip',  label: '行程', icon: 'orders-o', to: `/${role.value}/trip` },
  { name: 'order', label: '订单', icon: 'bill-o',   to: `/${role.value}/order` },
  { name: 'center',label: '我的', icon: 'user-o',   to: '/common/center' },
])

const active = computed(() => {
  const path = route.path
  if (path.startsWith('/common/')) return 3
  const m = tabs.value.findIndex(t => path.startsWith(t.to))
  return m >= 0 ? m : 0
})

// ---- 发布浮窗 ----
const PUBLISHING_FLAG = 'publishing_trip'
const showPopup = ref(false)

const form = reactive({
  departureTime: '',
  startAddr: '',
  startGeoLng: '',
  startGeoLat: '',
  endAddr: '',
  endGeoLng: '',
  endGeoLat: '',
  quantity: 3,
  quickConfirm: false,
})

const publishing = ref(false)

function parseLocation(raw, addrField, lngField, latField) {
  if (!raw) return
  const parts = raw.split('#')
  form[addrField] = parts[0] || ''
  if (parts[1]) {
    const coords = parts[1].split(',')
    form[lngField] = coords[0] || ''
    form[latField] = coords[1] || ''
  }
}

function loadFromStorage() {
  const f = localStorage.getItem('from')
  const t = localStorage.getItem('to')
  const time = localStorage.getItem('time')
  if (f) parseLocation(f, 'startAddr', 'startGeoLng', 'startGeoLat')
  if (t) parseLocation(t, 'endAddr', 'endGeoLng', 'endGeoLat')
  if (time) form.departureTime = time
}

function openPublish() {
  loadFromStorage()
  showPopup.value = true
}

function closePublish() {
  showPopup.value = false
}

function goMap(type) {
  localStorage.setItem(PUBLISHING_FLAG, '1')
  if (type === 'f') localStorage.removeItem('from')
  else localStorage.removeItem('to')
  router.push('/map')
}

watch(() => route.path, (path) => {
  const isHome = path.startsWith(`/${role.value}/home`)
  if (isHome && localStorage.getItem(PUBLISHING_FLAG) === '1') {
    loadFromStorage()
    showPopup.value = true
  } else if (!isHome && showPopup.value) {
    showPopup.value = false
  }
})

async function onPublish() {
  if (!form.departureTime) return showToast('请选择出发时间')
  if (!form.startAddr) return showToast('请选择出发地')
  if (!form.endAddr) return showToast('请选择目的地')

  publishing.value = true
  try {
    const res = await strokeApi.publish({
      departureTime: form.departureTime + ':00.000+0000',
      startAddr: form.startAddr,
      startGeoLng: form.startGeoLng,
      startGeoLat: form.startGeoLat,
      endAddr: form.endAddr,
      endGeoLng: form.endGeoLng,
      endGeoLat: form.endGeoLat,
      quantity: form.quantity,
      quickConfirm: form.quickConfirm ? 1 : 0,
      role: isDriver.value ? 1 : 0,
      status: 0,
    })
    if (res.code === 200 || res.code === 0) {
      showToast('行程发布成功')
      localStorage.removeItem('from')
      localStorage.removeItem('to')
      localStorage.removeItem('time')
      localStorage.removeItem(PUBLISHING_FLAG)
      showPopup.value = false
      // 用 query 参数强制触发路由变化，确保行程列表重新加载
      router.replace({ path: `/${role.value}/trip`, query: { _t: Date.now() } })
    } else {
      showToast(res.message || res.msg || '发布失败')
    }
  } catch {
    showToast('发布失败，请重试')
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped>
.tabbar-wrapper {
  position: relative;
}

.app-tabbar {
  height: 58px;
  border-radius: 20px 20px 0 0;
  box-shadow: 0 -6px 24px rgba(17, 24, 39, 0.08);
  overflow: hidden;
}
.app-tabbar :deep(.van-tabbar-item) {
  gap: 2px;
}
.app-tabbar :deep(.van-tabbar-item__icon) {
  font-size: 21px;
}

/* 居中圆形按钮 */
.center-btn {
  position: fixed;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: var(--gradient-primary);
  box-shadow: var(--shadow-button), 0 0 0 5px var(--bg-page);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}
.center-btn:active {
  transform: translateX(-50%) scale(0.92);
}

/* 浮窗 */
.popup-content {
  display: flex;
  flex-direction: column;
  max-height: 85vh;
}
.popup-handle {
  width: 36px;
  height: 4px;
  margin: 10px auto 0;
  border-radius: 2px;
  background: var(--border-color);
}
.popup-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 14px 20px 12px;
  border-bottom: 1px solid var(--divider-color);
}
.popup-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}
.popup-sub {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-hint);
}
.popup-body {
  padding: 16px 20px 24px;
  overflow-y: auto;
}

/* 表单 */
.form-item {
  margin-bottom: 16px;
}
.form-label {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}
.form-label .van-icon {
  color: var(--color-primary);
}
.form-tip {
  font-size: 12px;
  color: var(--text-hint);
}
.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.switch-row .form-label {
  margin-bottom: 2px;
}
.range-input {
  width: 100%;
  accent-color: var(--color-primary);
}
</style>
