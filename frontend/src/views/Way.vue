<template>
  <div class="way-page">
    <div class="back-bar">
      <van-icon name="arrow-left" size="22" @click="router.back" />
      <span class="back-title">路径规划</span>
    </div>
    <div ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import { loadBaiduMap } from '@/utils/baiduMap'

const router = useRouter()
const route = useRoute()
const a1 = parseFloat(route.query.a1) || 0
const a2 = parseFloat(route.query.a2) || 0
const b1 = parseFloat(route.query.b1) || 0
const b2 = parseFloat(route.query.b2) || 0
const mapContainer = ref(null)

async function initMap() {
  await nextTick()
  const BMapGL = window.BMapGL
  if (!BMapGL) { showToast('请先配置百度地图 AK'); return }

  const startPt = new BMapGL.Point(a1, a2)
  const endPt = new BMapGL.Point(b1, b2)
  const center = new BMapGL.Point((a1 + b1) / 2, (a2 + b2) / 2)

  const map = new BMapGL.Map(mapContainer.value, { enableMapClick: false })
  map.centerAndZoom(center, 12)
  map.enableScrollWheelZoom(true)

  const driving = new BMapGL.DrivingRoute(map, {
    renderOptions: { map, autoViewport: true, enableDragging: false },
  })
  driving.search(startPt, endPt)
}

onMounted(() => { initMap() })
</script>

<style scoped>
.way-page { position: relative; width: 100%; height: 100vh; overflow: hidden; }
.back-bar {
  position: absolute; top: 0; left: 0; right: 0; z-index: 100;
  display: flex; align-items: center; gap: 8px; padding: 12px 16px;
  background: rgba(255,255,255,0.95); backdrop-filter: blur(10px);
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.back-title { font-size: 16px; font-weight: 600; color: #1A1A2E; }
.map-container { width: 100%; height: 100%; }
</style>
