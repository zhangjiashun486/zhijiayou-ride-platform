<template>
  <div class="page-container">
    <AppHeader title="乘客端 - 智驾游" :show-back="false" @click-left="onSwitchRole">
      <template #left><van-icon name="user-o" size="20" color="var(--color-primary)" /></template>
      <template #right><van-icon name="service-o" size="20" color="var(--color-primary)" @click.stop="goAiRoute" /></template>
    </AppHeader>

    <HomeFeed />

    <TabBar />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import AppHeader from '@/components/AppHeader.vue'
import HomeFeed from '@/components/HomeFeed.vue'
import TabBar from '@/components/TabBar.vue'
import { useAuthStore } from '@/stores/auth'
import { getUserInfo } from '@/api/account'

const router = useRouter()
const auth = useAuthStore()

function onSwitchRole() {
  const newRole = auth.currentRole === 0 ? 1 : 0
  doSwitch(newRole)
}

async function doSwitch(newRole) {
  if (newRole === 1) {
    try {
      const res = await getUserInfo()
      const user = res.data?.[0] || res.data || {}
      if (user.role !== 1) return showToast('请先认证为车主')
    } catch { return showToast('网络异常，请重试') }
  }
  auth.setRole(newRole)
  router.push(newRole === 1 ? '/driver/home' : '/passenger/home')
}

function goAiRoute() { router.push('/common/ai-route') }
</script>
