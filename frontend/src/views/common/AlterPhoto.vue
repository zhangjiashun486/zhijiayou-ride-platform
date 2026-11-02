<template>
  <div class="page-container">
    <AppHeader title="修改头像" :show-back="true" />

    <div class="photo-section">
      <div class="photo-preview" @click="$refs.fileInput.click()">
        <img v-if="avatarUrl" :src="avatarUrl" class="preview-img" />
        <van-icon v-else name="user-o" size="48" color="#ccc" />
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
const avatarUrl = ref('')
const fileInput = ref(null)
const saving = ref(false)

onMounted(async () => {
  try {
    const res = await getUserInfo()
    const user = res.data?.[0] || res.data || res || {}
    avatarUrl.value = user.avatar || ''
  } catch (e) { /* ignore */ }
})

async function handleUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return

  // 本地即时预览
  const reader = new FileReader()
  reader.onload = (ev) => { avatarUrl.value = ev.target.result }
  reader.readAsDataURL(file)

  const fd = new FormData()
  fd.append('file', file)
  try {
    const res = await upload(fd)
    // 响应格式: { code:200, data:[{url:"..."}] }
    const url = res.data?.[0]?.url || ''
    if (url) avatarUrl.value = url
    showToast('上传成功')
  } catch (err) {
    showToast('上传失败')
  }
}

async function handleSave() {
  if (!avatarUrl.value || avatarUrl.value.startsWith('data:')) {
    showToast('请先上传头像')
    return
  }
  saving.value = true
  try {
    await modifyUser({ avatar: avatarUrl.value })
    showToast('头像修改成功')
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
  width: 120px; height: 120px; border-radius: 50%;
  border: 2px dashed #ddd; display: flex; align-items: center; justify-content: center;
  overflow: hidden; background: #fafafa; margin-bottom: 16px; cursor: pointer;
}
.preview-img { width: 100%; height: 100%; object-fit: cover; }
.file-input { display: none; }
.btn-wrap { padding: 16px; }
</style>
