<template>
  <div class="page">
    <AppHeader title="司机认证" />

    <div class="content" v-if="!isCertified">
      <!-- 驾驶证（cardIdFrontPhoto） -->
      <div class="upload-section">
        <div class="section-label">驾驶证</div>
        <div class="upload-box" @click="triggerUpload('cardIdFront')">
          <van-image
            v-if="preview.cardIdFront"
            :src="preview.cardIdFront"
            fit="cover"
            class="preview-img"
          />
          <div v-else class="upload-placeholder">
            <van-icon name="photograph" size="28" color="#bbb" />
            <span>点击上传</span>
          </div>
        </div>
        <input
          ref="cardIdFrontInput"
          type="file"
          accept="image/*"
          hidden
          @change="(e) => onFileChange(e, 'cardIdFront')"
        />
      </div>

      <!-- 行驶证（cardIdBackPhoto） -->
      <div class="upload-section">
        <div class="section-label">行驶证</div>
        <div class="upload-box" @click="triggerUpload('cardIdBack')">
          <van-image
            v-if="preview.cardIdBack"
            :src="preview.cardIdBack"
            fit="cover"
            class="preview-img"
          />
          <div v-else class="upload-placeholder">
            <van-icon name="photograph" size="28" color="#bbb" />
            <span>点击上传</span>
          </div>
        </div>
        <input
          ref="cardIdBackInput"
          type="file"
          accept="image/*"
          hidden
          @change="(e) => onFileChange(e, 'cardIdBack')"
        />
      </div>

      <!-- 车辆照片（carFrontPhoto） -->
      <div class="upload-section">
        <div class="section-label">车辆照片</div>
        <div class="upload-box" @click="triggerUpload('carFront')">
          <van-image
            v-if="preview.carFront"
            :src="preview.carFront"
            fit="cover"
            class="preview-img"
          />
          <div v-else class="upload-placeholder">
            <van-icon name="photograph" size="28" color="#bbb" />
            <span>点击上传</span>
          </div>
        </div>
        <input
          ref="carFrontInput"
          type="file"
          accept="image/*"
          hidden
          @change="(e) => onFileChange(e, 'carFront')"
        />
      </div>

      <!-- 提交 -->
      <div class="submit-wrapper">
        <van-button
          round
          block
          type="primary"
          color="var(--color-primary)"
          size="large"
          :loading="submitting"
          @click="onSubmit"
        >
          提交认证
        </van-button>
      </div>
    </div>

    <!-- 已认证 -->
    <div class="content" v-else>
      <div class="cert-success">
        <van-icon name="checked" size="56" color="#2ED573" />
        <h3>认证已通过</h3>
        <p class="cert-desc">{{ certDescription }}</p>
      </div>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import { useAuthStore } from '@/stores/auth'
import * as storageApi from '@/api/storage'
import * as accountApi from '@/api/account'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const auth = useAuthStore()

const isCertified = computed(() => auth.currentUser?.role === 1)

const certDescription = computed(() => {
  const u = auth.currentUser
  if (!u) return ''
  return `姓名: ${u.realName || '—'} | 车牌: ${u.carPlate || '—'}`
})

const submitting = ref(false)

const cardIdFrontInput = ref(null)
const cardIdBackInput = ref(null)
const carFrontInput = ref(null)

const uploadRefs = {
  cardIdFront: cardIdFrontInput,
  cardIdBack: cardIdBackInput,
  carFront: carFrontInput,
}

const preview = reactive({
  cardIdFront: '',
  cardIdBack: '',
  carFront: '',
})

const uploaded = reactive({
  cardIdFront: '',
  cardIdBack: '',
  carFront: '',
})

const triggerUpload = (key) => {
  uploadRefs[key].value?.click()
}

const onFileChange = async (e, key) => {
  const file = e.target.files?.[0]
  if (!file) return

  // local preview
  const reader = new FileReader()
  reader.onload = (ev) => {
    preview[key] = ev.target.result
  }
  reader.readAsDataURL(file)

  // upload
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await storageApi.upload(formData)
    // 响应格式: { code:200, data:[{url:"..."}] }
    const url = res.data?.[0]?.url || ''
    if (url) { preview[key] = url; uploaded[key] = url }
    showToast('上传成功')
  } catch {
    // handled
  }

  // reset input so same file can be re-selected
  e.target.value = ''
}

const onSubmit = async () => {
  if (!uploaded.cardIdFront || !uploaded.cardIdBack || !uploaded.carFront) {
    showToast('请上传所有证件照片')
    return
  }

  try {
    await showConfirmDialog({
      title: '提交认证',
      message: '确认提交认证信息？提交后将无法修改。',
    })
  } catch {
    return
  }

  submitting.value = true
  try {
    await accountApi.vehicleAuth({
      cardIdFrontPhoto: uploaded.cardIdFront,
      cardIdBackPhoto: uploaded.cardIdBack,
      carFrontPhoto: uploaded.carFront,
    })
    showToast('提交成功，请等待审核')
    // refresh user info
    const user = await accountApi.getUserInfo()
    if (user) {
      auth.setUser(user)
    }
  } catch {
    // handled
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 60px;
}

.content {
  padding: 12px;
}

.upload-section {
  margin-bottom: 16px;
}

.section-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.upload-box {
  width: 100%;
  height: 180px;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
  border: 1px dashed #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #bbb;
  font-size: 13px;
}

.preview-img {
  width: 100%;
  height: 100%;
}

.submit-wrapper {
  margin-top: 24px;
  padding: 0 12px 20px;
}
</style>
