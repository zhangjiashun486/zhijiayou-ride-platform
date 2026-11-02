<template>
  <div class="login-page">
    <!-- 顶部渐变背景 -->
    <div class="login-header">
      <div class="header-logo">
        <img class="brand-logo" src="/brand/zhijiaoyou-logo-light.svg" alt="智驾游 AI 出行助手" />
        <p class="header-subtitle">安全 · 便捷 · 值得信赖</p>
      </div>
    </div>

    <!-- 表单卡片 -->
    <div class="login-card">
      <van-tabs v-model:active="activeTab" animated swipeable>
        <van-tab title="登录" name="login" />
        <van-tab title="注册" name="register" />
      </van-tabs>

      <!-- 登录表单 -->
      <van-form
        v-if="activeTab === 'login'"
        ref="loginFormRef"
        @submit="handleLogin"
        class="login-form"
      >
        <van-field
          v-model="loginForm.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
          left-icon="user-o"
          clearable
        />
        <van-field
          v-model="loginForm.password"
          name="password"
          label="密码"
          type="password"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
          left-icon="lock"
          clearable
        />
        <div class="form-submit">
          <van-button
            block
            round
            type="primary"
            native-type="submit"
            :loading="loginLoading"
            class="submit-btn"
          >
            登 录
          </van-button>
        </div>
      </van-form>

      <!-- 注册表单 -->
      <van-form
        v-else
        ref="registerFormRef"
        @submit="handleRegister"
        class="login-form"
      >
        <van-field
          v-model="registerForm.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
          left-icon="user-o"
          clearable
        />
        <van-field
          v-model="registerForm.phone"
          name="phone"
          label="手机号"
          type="tel"
          placeholder="请输入手机号"
          :rules="[
            { required: true, message: '请输入手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
          ]"
          left-icon="phone-o"
          clearable
        />
        <van-field
          v-model="registerForm.password"
          name="password"
          label="密码"
          type="password"
          placeholder="请输入密码"
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 6, message: '密码至少6位' }
          ]"
          left-icon="lock"
          clearable
        />
        <van-field
          v-model="registerForm.confirmPassword"
          name="confirmPassword"
          label="确认密码"
          type="password"
          placeholder="请再次输入密码"
          :rules="[{ required: true, message: '请确认密码' }]"
          left-icon="lock"
          clearable
        />
        <div class="form-submit">
          <van-button
            block
            round
            type="primary"
            native-type="submit"
            :loading="registerLoading"
            class="submit-btn"
          >
            注 册
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 底部版权 -->
    <div class="login-footer">
      <p>© 2026 智驾游 All Rights Reserved</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login as loginApi, register as registerApi, getUserInfo } from '@/api/account'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const activeTab = ref('login')
const loginLoading = ref(false)
const registerLoading = ref(false)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: '',
})

const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
})

// 登录
async function handleLogin() {
  loginLoading.value = true
  try {
    const d = await loginApi({
      username: loginForm.username,
      password: loginForm.password,
    })
    // 响应结构：d.data[0].token
    if (d && d.data && d.data.length > 0 && d.data[0].token) {
      auth.setToken(d.data[0].token)
      auth.setRole(0)
      // 获取用户信息写入 store
      try {
        const u = await getUserInfo()
        if (u && u.data && u.data.length > 0) auth.setUser(u.data[0])
      } catch { /* 非关键路径 */ }
      router.push('/passenger/home')
    } else {
      showToast('登录失败，请检查用户名和密码')
    }
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loginLoading.value = false
  }
}

// 注册
async function handleRegister() {
  // 验证两次密码一致
  if (registerForm.password !== registerForm.confirmPassword) {
    showToast('两次输入的密码不一致')
    return
  }

  registerLoading.value = true
  try {
    await registerApi({
      username: registerForm.username,
      phone: registerForm.phone,
      password: registerForm.password,
    })
    showToast('注册成功，请登录')
    // 切回登录表单
    activeTab.value = 'login'
    // 清空注册表单
    registerForm.username = ''
    registerForm.phone = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    // 预填登录用户名
    loginForm.username = registerForm.username || loginForm.username
  } catch {
    // 错误已在拦截器中处理
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--bg-page);
  display: flex;
  flex-direction: column;
}

/* 顶部渐变区域 */
.login-header {
  height: 200px;
  background: linear-gradient(160deg, var(--color-primary) 0%, var(--color-primary-light) 65%, #FFB347 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0 0 50% 50% / 0 0 40px 40px;
  position: relative;
  overflow: hidden;
}

.login-header::before {
  content: '';
  position: absolute;
  top: -60px;
  right: -40px;
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.login-header::after {
  content: '';
  position: absolute;
  bottom: -30px;
  left: -30px;
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.header-logo {
  text-align: center;
  z-index: 1;
}

.brand-logo { width: 220px; height: auto; display: block; margin: 0 auto 8px; }
.header-title {
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  margin-top: 8px;
  letter-spacing: 2px;
}

.header-subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-size: var(--font-size-sm);
  margin-top: 4px;
  letter-spacing: 4px;
}

/* 卡片 */
.login-card {
  margin: -30px 16px 0;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
  z-index: 2;
}

.login-card :deep(.van-tabs__nav) {
  padding-top: 8px;
}

.login-card :deep(.van-tab--active) {
  color: var(--color-primary);
  font-weight: 600;
}

.login-card :deep(.van-tabs__line) {
  background: var(--color-primary);
}

/* 表单 */
.login-form {
  padding: 12px 16px 20px;
}

.login-form :deep(.van-field__left-icon) {
  color: var(--text-hint);
}

.form-submit {
  margin-top: 24px;
  padding: 0 4px;
}

.submit-btn {
  height: 44px;
  font-size: var(--font-size-lg);
  font-weight: 600;
  letter-spacing: 4px;
}

.submit-btn :deep(.van-button) {
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  border: none;
}

/* 底部 */
.login-footer {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 24px;
}

.login-footer p {
  color: var(--text-hint);
  font-size: var(--font-size-xs);
}
</style>
