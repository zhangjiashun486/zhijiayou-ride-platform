<template>
  <div class="page-container">
    <AppHeader title="收款码" :show-back="true" />

    <div class="photo-section">
      <div class="photo-preview" @click="$refs.fileInput.click()">
        <img v-if="paycodeUrl" :src="paycodeUrl" class="preview-img" />
        <van-icon v-else name="qr" size="48" color="#ccc" />
      </div>
      <input ref="fileInput" type="file" accept="image/*" @change="handleUpload" class="file-input" />
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, modifyUser } from '@/api/account'
import { upload } from '@/api/storage'
import { showToast } from '@/utils/index.js'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const paycodeUrl = ref('')
const fileInput = ref(null)
const saving = ref(false)

onMounted(async () => {
  try {
    const res = await getUserInfo()
    const user = res.data?.[0] || res.data || res || {}
    paycodeUrl.value = user.paycode || ''
  } catch (e) { /* ignore */ }
})

async function handleUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (ev) => { paycodeUrl.value = ev.target.result }
  reader.readAsDataURL(file)

  const fd = new FormData()
  fd.append('file', file)
  try {
    const res = await upload(fd)
    const url = res.data?.[0]?.url || ''
    if (url) paycodeUrl.value = url
    showToast('上传成功')
  } catch (err) {
    showToast('上传失败')
  }
}

async function handleSave() {
  if (!paycodeUrl.value || paycodeUrl.value.startsWith('data:')) {
    showToast('请先上传收款码')
    return
  }
  saving.value = true
  try {
    await modifyUser({ paycode: paycodeUrl.value })
    showToast('收款码保存成功')
    router.push('/common/center')
  } catch (e) {
    showToast('保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.photo-section {
  display: flex; flex-direction: column; align-items: center; padding: 32px 16px;
}
.photo-preview {
  width: 200px; height: 200px; border: 2px dashed #ddd; border-radius: 8px;
  display: flex; align-items: center; justify-content: center; overflow: hidden;
  background: #fafafa; margin-bottom: 16px; cursor: pointer;
}
.preview-img { width: 100%; height: 100%; object-fit: contain; }
.file-input { display: none; }
.btn-wrap { padding: 16px; }
</style>
