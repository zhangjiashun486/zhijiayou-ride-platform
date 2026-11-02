<template>
  <div class="page-container">
    <AppHeader title="智能问路" :show-back="true" />

    <div class="placeholder">
      <van-empty image="network" description="智能问路功能开发中，敬请期待" />
      <p class="hint">此页面将与消息聊天功能合并实现</p>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
</script>

<style scoped>
.placeholder {
  padding-top: 60px;
  text-align: center;
}

.hint {
  font-size: 13px;
  color: #999;
  margin-top: 8px;
  padding: 0 16px;
}
</style>
