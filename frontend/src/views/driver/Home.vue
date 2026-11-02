<template>
  <div class="page-container">
    <AppHeader title="司机端 - 智驾游" @click-left="onSwitchRole">
      <template #left><van-icon name="logistics" size="20" color="var(--color-primary)" /></template>
      <template #right><van-icon name="service-o" size="20" color="var(--color-primary)" @click.stop="goAiRoute" /></template>
    </AppHeader>

    <HomeFeed />

    <TabBar />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppHeader from '@/components/AppHeader.vue'
import HomeFeed from '@/components/HomeFeed.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const auth = useAuthStore()

function onSwitchRole() {
  auth.setRole(0)
  router.push('/passenger/home')
}

function goAiRoute() { router.push('/common/ai-route') }
</script>
