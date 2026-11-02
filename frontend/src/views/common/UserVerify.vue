<template>
  <div class="page-container">
    <AppHeader title="实名认证" :show-back="true" />

    <!-- 已认证 -->
    <div class="card verify-card" v-if="isVerified">
      <van-icon name="passed" size="48" color="#07C160" />
      <p class="verify-title">认证通过！</p>
      <p class="verify-subtitle">恭喜您，已经通过实名认证</p>
      <div class="verify-info" v-if="authInfo.realName">
        姓名：{{ authInfo.realName }}
      </div>
      <div class="verify-info" v-if="authInfo.idCard">
        身份证号：{{ authInfo.idCard }}
      </div>
      <div class="verify-info" v-if="authInfo.birthday">
        出生日期：{{ authInfo.birthday }}
      </div>
    </div>

    <!-- 身份证照片（只读） -->
    <div class="card">
      <div class="photo-row">
        <label class="photo-label">个人照片</label>
        <div class="photo-box">
          <img v-if="authInfo.personalPhoto" :src="authInfo.personalPhoto" class="photo-img" />
          <van-icon v-else name="photograph" size="32" color="#ccc" />
        </div>
      </div>
      <div class="photo-row">
        <label class="photo-label">身份证正面</label>
        <div class="photo-box">
          <img v-if="authInfo.cardIdFrontPhoto" :src="authInfo.cardIdFrontPhoto" class="photo-img" />
          <van-icon v-else name="photograph" size="32" color="#ccc" />
        </div>
      </div>
      <div class="photo-row">
        <label class="photo-label">身份证反面</label>
        <div class="photo-box">
          <img v-if="authInfo.cardIdBackPhoto" :src="authInfo.cardIdBackPhoto" class="photo-img" />
          <van-icon v-else name="photograph" size="32" color="#ccc" />
        </div>
      </div>
    </div>

    <!-- 未认证：提交按钮 -->
    <div v-if="!isVerified" class="btn-wrap">
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
import { getAuthInfo, identityAuth } from '@/api/account'
import { showToast } from '@/utils/index.js'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()

const authInfo = ref({})
const submitting = ref(false)

const isVerified = computed(() => !!authInfo.value.useralias)

onMounted(async () => {
  try {
    const res = await getAuthInfo()
    authInfo.value = res.data?.[0] || res.data || res || {}
  } catch (e) {
    // ignore
  }
})

async function handleSubmit() {
  const a = authInfo.value
  if (!a.personalPhoto || !a.cardIdFrontPhoto || !a.cardIdBackPhoto) {
    showToast('请先完善认证资料（个人照片、身份证正反面）')
    return
  }
  submitting.value = true
  try {
    await identityAuth()
    showToast('实名认证提交成功')
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
