<template>
  <div class="page-container">
    <AppHeader title="车主认证" :show-back="true" />

    <!-- 已认证 -->
    <div class="card verify-card" v-if="isAuthed">
      <van-icon name="passed" size="48" color="#07C160" />
      <p class="verify-title">认证通过！</p>
      <p class="verify-subtitle">恭喜您，已经认证为车主</p>
      <div class="verify-info" v-if="vehicleInfo.carNumber">
        车牌号：{{ vehicleInfo.carNumber }}
      </div>
    </div>

    <!-- 车辆照片（只读） -->
    <div class="card">
      <div class="photo-row" v-for="item in photoList" :key="item.label">
        <label class="photo-label">{{ item.label }}</label>
        <div class="photo-box">
          <img v-if="item.url" :src="item.url" class="photo-img" />
          <van-icon v-else name="photograph" size="32" color="#ccc" />
        </div>
      </div>
    </div>

    <!-- 未认证：提交按钮 -->
    <div v-if="!isAuthed" class="btn-wrap">
      <button class="btn-primary" @click="handleSubmit" :disabled="submitting">
        {{ submitting ? '提交中...' : '提交认证' }}
      </button>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getVehicleInfo, vehicleAuth } from '@/api/account'
import { showToast } from '@/utils/index.js'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()

const vehicleInfo = ref({})
const submitting = ref(false)

const isAuthed = computed(() => !!vehicleInfo.value.carNumber)

const photoList = computed(() => [
  { label: '车辆正面', url: vehicleInfo.value.carFrontPhoto || '' },
  { label: '人车同框', url: vehicleInfo.value.carSidePhoto || '' },
  { label: '行驶证', url: vehicleInfo.value.carBackPhoto || '' },
])

onMounted(async () => {
  try {
    const res = await getVehicleInfo()
    vehicleInfo.value = (res.data && res.data[0]) || res || {}
  } catch (e) {
    // ignore
  }
})

async function handleSubmit() {
  const v = vehicleInfo.value
  if (!v.carFrontPhoto || !v.carSidePhoto || !v.carBackPhoto) {
    showToast('请先完善车辆信息照片')
    return
  }
  submitting.value = true
  try {
    await vehicleAuth()
    showToast('认证提交成功')
    router.push('/common/center')
  } catch (e) {
    showToast('认证失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.photo-row {
  margin-bottom: 16px;
}

.photo-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.photo-box {
  width: 100%;
  height: 160px;
  border: 1px solid #eee;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #fafafa;
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.verify-card {
  text-align: center;
  padding: 32px 20px;
}

.verify-title {
  font-size: 22px;
  font-weight: 700;
  color: #07C160;
  margin: 12px 0 4px;
}

.verify-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.verify-info {
  font-size: 15px;
  color: #333;
  margin-top: 4px;
}

.btn-wrap {
  padding: 24px 16px;
}
</style>
