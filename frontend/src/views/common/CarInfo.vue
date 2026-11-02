<template>
  <div class="page-container">
    <AppHeader title="车辆信息" :show-back="true" />

    <div class="card">
      <div v-for="item in uploadFields" :key="item.key" class="upload-section">
        <label class="upload-label">{{ item.label }}</label>
        <div class="upload-preview" @click="triggerUpload(item.key)">
          <img v-if="form[item.key]" :src="form[item.key]" class="preview-img" />
          <van-icon v-else name="photograph" size="32" color="#ccc" />
        </div>
        <input
          :ref="(el) => inputRefs[item.key] = el"
          type="file" accept="image/*"
          @change="(e) => handleUpload(e, item.key)"
          class="file-input"
        />
      </div>
    </div>

    <div class="btn-wrap">
      <button class="btn-primary" @click="handleSave" :disabled="saving">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getVehicleInfo, modifyVehicle } from '@/api/account'
import { upload } from '@/api/storage'
import { showToast } from '@/utils/index.js'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const saving = ref(false)
const inputRefs = reactive({})

const uploadFields = [
  { key: 'carFrontPhoto', label: '车辆正面' },
  { key: 'carSidePhoto', label: '人车同框' },
  { key: 'carBackPhoto', label: '行驶证' },
]

const form = reactive({
  carFrontPhoto: '',
  carSidePhoto: '',
  carBackPhoto: '',
})

onMounted(async () => {
  try {
    const res = await getVehicleInfo()
    const data = res.data?.[0] || res.data || res || {}
    if (data.carFrontPhoto) form.carFrontPhoto = data.carFrontPhoto
    if (data.carSidePhoto) form.carSidePhoto = data.carSidePhoto
    if (data.carBackPhoto) form.carBackPhoto = data.carBackPhoto
  } catch (e) { /* ignore */ }
})

function triggerUpload(key) {
  inputRefs[key]?.click()
}

async function handleUpload(e, field) {
  const file = e.target.files?.[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (ev) => { form[field] = ev.target.result }
  reader.readAsDataURL(file)

  const fd = new FormData()
  fd.append('file', file)
  try {
    const res = await upload(fd)
    const url = res.data?.[0]?.url || ''
    if (url) form[field] = url
    showToast('上传成功')
  } catch (err) {
    showToast('上传失败')
  }
}

async function handleSave() {
  saving.value = true
  try {
    await modifyVehicle({
      carFrontPhoto: form.carFrontPhoto,
      carSidePhoto: form.carSidePhoto,
      carBackPhoto: form.carBackPhoto,
    })
    showToast('车辆信息保存成功')
    router.push('/common/center')
  } catch (e) {
    showToast('保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.upload-section { margin-bottom: 20px; }
.upload-label { display: block; font-size: 14px; font-weight: 500; color: #333; margin-bottom: 8px; }
.upload-preview {
  width: 100%; height: 160px; border: 1px dashed #ddd; border-radius: 8px;
  display: flex; align-items: center; justify-content: center; overflow: hidden;
  background: #fafafa; margin-bottom: 8px; cursor: pointer;
}
.preview-img { width: 100%; height: 100%; object-fit: contain; }
.file-input { display: none; }
.btn-wrap { padding: 16px; }
</style>
