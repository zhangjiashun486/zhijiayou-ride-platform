<template>
  <div class="page-container">
    <AppHeader title="修改密码" :show-back="true" />

    <!-- 用户信息头部 -->
    <div class="user-header">
      <img :src="auth.currentUser?.avatar || '/img/default-header.jpg'" class="avatar" />
      <span class="nickname">{{ auth.currentUser?.nickname || '用户' }}</span>
    </div>

    <!-- 表单 -->
    <div class="card">
      <van-field
        v-model="form.password"
        type="password"
        label="旧密码"
        placeholder="请输入旧密码"
      />
      <van-field
        v-model="form.newPassword"
        type="password"
        label="新密码"
        placeholder="请输入新密码"
      />
      <van-field
        v-model="form.confirmPassword"
        type="password"
        label="确认密码"
        placeholder="请再次输入新密码"
      />
    </div>

    <div class="btn-wrap">
      <button class="btn-primary" @click="handleSubmit" :disabled="submitting">
        {{ submitting ? '提交中...' : '确认修改' }}
      </button>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { modifyPassword } from '@/api/account'
import { showToast } from '@/utils/index.js'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const auth = useAuthStore()

const submitting = ref(false)

const form = reactive({
  password: '',
  newPassword: '',
  confirmPassword: '',
})

async function handleSubmit() {
  if (!form.password) {
    showToast('请输入旧密码')
    return
  }
  if (!form.newPassword) {
    showToast('请输入新密码')
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    showToast('两次密码输入不一致')
    return
  }
  submitting.value = true
  try {
    await modifyPassword({
      password: form.password,
      newPassword: form.newPassword,
    })
    showToast('密码修改成功，请重新登录')
    auth.logout()
    router.push('/login')
  } catch (e) {
    showToast('修改失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  color: #fff;
  margin-bottom: 8px;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 2px solid #fff;
  object-fit: cover;
  background: #fff;
}

.nickname {
  font-size: 18px;
  font-weight: 600;
}

.btn-wrap {
  padding: 24px 16px 0;
}
</style>
