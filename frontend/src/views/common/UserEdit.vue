<template>
  <div class="page-container">
    <AppHeader title="完善资料" :show-back="true" />

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
import { getAuthInfo, modifyAuth } from '@/api/account'
import { upload } from '@/api/storage'
import { showToast } from '@/utils/index.js'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const saving = ref(false)
const inputRefs = reactive({})

const uploadFields = [
  { key: 'personalPhoto', label: '个人照片' },
  { key: 'cardIdFrontPhoto', label: '身份证正面' },
  { key: 'cardIdBackPhoto', label: '身份证反面' },
]

const form = reactive({
  personalPhoto: '',
  cardIdFrontPhoto: '',
  cardIdBackPhoto: '',
})

onMounted(async () => {
  try {
    const res = await getAuthInfo()
    const data = res.data?.[0] || res.data || res || {}
    if (data.personalPhoto) form.personalPhoto = data.personalPhoto
    if (data.cardIdFrontPhoto) form.cardIdFrontPhoto = data.cardIdFrontPhoto
    if (data.cardIdBackPhoto) form.cardIdBackPhoto = data.cardIdBackPhoto
  } catch (e) { /* ignore */ }
})

function triggerUpload(key) {
  inputRefs[key]?.click()
}

async function handleUpload(e, field) {
  const file = e.target.files?.[0]
  if (!file) return

  // 本地即时预览
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
  // 检查是否有未完成的上传（URL 以 data: 开头说明是本地预览未替换）
  if (!form.personalPhoto || form.personalPhoto.startsWith('data:')) {
    showToast('请上传个人照片')
    return
  }
  saving.value = true
  try {
    await modifyAuth({
      personalPhoto: form.personalPhoto,
      cardIdFrontPhoto: form.cardIdFrontPhoto,
      cardIdBackPhoto: form.cardIdBackPhoto,
    })
    showToast('保存成功')
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
