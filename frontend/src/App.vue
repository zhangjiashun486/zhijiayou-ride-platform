<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="route.meta.transition || 'fade-slide'" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
import { onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { initNoticeSocket, closeNoticeSocket, clearNoticeMsg } from '@/composables/useNotice'

const authStore = useAuthStore()

onMounted(() => {
  authStore.init()
  // 已登录则启动全局 WebSocket 监听
  if (authStore.token) {
    initNoticeSocket()
  }
})

// 监听 token 变化：登录后启动 WS，登出后断开
watch(() => authStore.token, (val) => {
  if (val) {
    initNoticeSocket()
  } else {
    closeNoticeSocket()
    clearNoticeMsg()
  }
})
</script>

<style>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
