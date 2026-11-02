<template>
  <div class="point-page">
    <div class="back-bar">
      <van-icon name="arrow-left" size="22" @click="router.back" />
      <span class="back-title">实时位置追踪</span>
    </div>
    <div ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import { loadBaiduMap } from '@/utils/baiduMap'
import * as strokeApi from '@/api/stroke'

const router = useRouter()
const route = useRoute()
const tripId = route.query.id || ''
const mapContainer = ref(null)

let map = null, marker = null, timer = null

async function initMap() {
  await nextTick()
  const BMapGL = window.BMapGL
  if (!BMapGL) { showToast('请先配置百度地图 AK'); return }

  map = new BMapGL.Map(mapContainer.value, { enableMapClick: false })
  map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12)
  map.enableScrollWheelZoom(true)
  fetchLocation()
}

async function fetchLocation() {
  if (!tripId) return
  try {
    const d = await strokeApi.currentLocation(tripId)
    // 响应格式: { code:200, data:[{lng,lat}] }
    const loc = d.data?.[0]
    if (loc && loc.lng !== undefined) updateMarker(loc.lng, loc.lat)
  } catch (e) { /* ignore */ }
}

function updateMarker(lng, lat) {
  const BMapGL = window.BMapGL
  const pt = new BMapGL.Point(lng, lat)
  if (marker) map.removeOverlay(marker)
  marker = new BMapGL.Marker(pt, {
    icon: new BMapGL.Icon('/img/car.png', new BMapGL.Size(30, 15), { anchor: new BMapGL.Size(15, 7) })
  })
  map.addOverlay(marker)
  map.centerAndZoom(pt, 18)
}

onMounted(() => { initMap(); timer = setInterval(fetchLocation, 1000) })
onBeforeUnmount(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.point-page { position: relative; width: 100%; height: 100vh; overflow: hidden; }
.back-bar {
  position: absolute; top: 0; left: 0; right: 0; z-index: 100;
  display: flex; align-items: center; gap: 8px; padding: 12px 16px;
  background: rgba(255,255,255,0.95); backdrop-filter: blur(10px);
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.back-title { font-size: 16px; font-weight: 600; color: #1A1A2E; }
.map-container { width: 100%; height: 100%; }
</style>
