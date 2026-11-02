<template>
  <div class="map-page">
    <div class="map-toolbar">
      <input
        ref="searchInput"
        :value="keyword"
        class="search-input"
        placeholder="搜索地点"
        @input="keyword = $event.target.value"
        @keyup.enter="onSearch"
      />
      <button class="btn-cancel" @click="onCancel">取消</button>
      <button class="btn-confirm" :disabled="!selected" @click="onConfirm">确定</button>
    </div>

    <div v-if="results.length > 0 && !selected" class="result-list">
      <div v-for="(item, idx) in results" :key="idx" class="result-item" @click="pickResult(item)">
        <div class="result-title">{{ item && (item.title || item.business || item.name || '') }}</div>
        <div class="result-addr">{{ item && (item.address || (item.city || '') + (item.district || '')) }}</div>
      </div>
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
const mapType = route.query.type || 'f'
const searchInput = ref(null)
const mapContainer = ref(null)
const keyword = ref('')
const selected = ref(null)
const results = ref([])

let map = null, marker = null, ac = null

async function initMap() {
  await nextTick()
  const BMap = window.BMapGL
  if (!BMap) { showToast('请先配置百度地图 AK'); return }

  map = new BMap.Map(mapContainer.value, { enableMapClick: false })
  map.centerAndZoom('北京', 12)
  map.enableScrollWheelZoom(true)

  map.addEventListener('click', (e) => {
    const pt = e.point
    new BMap.Geocoder().getLocation(pt, (rs) => {
      setSelected({ name: rs && rs.address ? rs.address : `${pt.lng.toFixed(6)}, ${pt.lat.toFixed(6)}`, lng: pt.lng, lat: pt.lat })
    })
  })

  ac = new BMap.Autocomplete({ input: searchInput.value, location: map })
  ac.addEventListener('onconfirm', (e) => {
    const v = e.item.value
    const addr = (v.province || '') + (v.city || '') + (v.district || '') + (v.business || '')
    keyword.value = addr
    searchPlace(addr)
  })
}

function searchPlace(query) {
  const BMap = window.BMapGL
  const local = new BMap.LocalSearch(map, {
    onSearchComplete: () => {
      if (local.getStatus() === 0) {
        const n = local.getResults() ? local.getResults().getNumPois() : 0
        const pois = []
        for (let i = 0; i < n; i++) {
          const poi = local.getResults().getPoi(i)
          if (poi) pois.push(poi)
        }
        results.value = pois
      }
    },
  })
  local.search(query)
}

function onSearch() { if (keyword.value.trim()) searchPlace(keyword.value.trim()) }

function pickResult(item) {
  if (!item) return
  results.value = []
  // 优先用完整地址，和图搜框一致
  const name = item.address || item.title || item.business || item.name || ''
  if (item.point) setSelected({ name, lng: item.point.lng, lat: item.point.lat })
}

function setSelected(val) {
  selected.value = val
  results.value = []
  const BMap = window.BMapGL
  const pt = new BMap.Point(val.lng, val.lat)
  if (marker) map.removeOverlay(marker)
  marker = new BMap.Marker(pt)
  map.addOverlay(marker)
  map.centerAndZoom(pt, 16)
}

function onConfirm() {
  if (!selected.value) return
  const name = keyword.value.trim() || selected.value.name
  const val = `${name}#${selected.value.lng},${selected.value.lat}`
  localStorage.setItem(mapType === 'f' ? 'from' : 'to', val)
  router.back()
}

function onCancel() { router.back() }

onMounted(() => { initMap() })
</script>

<style scoped>
.map-page { position: relative; width: 100%; height: 100vh; overflow: hidden; }
.map-toolbar { position: absolute; top: 0; left: 0; right: 0; z-index: 100; display: flex; align-items: center; gap: 8px; padding: 8px 10px; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.search-input { flex: 1; height: 36px; padding: 0 12px; border: 1px solid #E8EAF0; border-radius: 18px; font-size: 14px; outline: none; background: #F2F4F8; }
.search-input:focus { border-color: var(--color-primary); }
.btn-cancel, .btn-confirm { height: 36px; padding: 0 14px; border: none; border-radius: 18px; font-size: 14px; font-weight: 500; cursor: pointer; white-space: nowrap; flex-shrink: 0; }
.btn-cancel { background: #f0f0f0; color: #666; }
.btn-confirm { background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light)); color: #fff; }
.btn-confirm:disabled { opacity: 0.4; }
.result-list { position: absolute; top: 52px; left: 0; right: 0; z-index: 99; max-height: 240px; overflow-y: auto; background: #fff; border-bottom: 1px solid #E8EAF0; }
.result-item { padding: 12px 16px; border-bottom: 1px solid #f5f5f5; cursor: pointer; }
.result-item:active { background: #f8f8f8; }
.result-title { font-size: 15px; font-weight: 500; color: #1A1A2E; }
.result-addr { font-size: 12px; color: #9CA3AF; margin-top: 2px; }
.map-container { width: 100%; height: calc(100vh - 50px); margin-top: 50px; }
</style>
