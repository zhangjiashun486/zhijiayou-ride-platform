<template>
  <van-nav-bar
    class="app-header"
    :title="title"
    :left-text="leftText"
    :left-arrow="showBack"
    :fixed="fixed"
    :placeholder="fixed"
    @click-left="onClickLeft"
    @click-right="onClickRight"
  >
    <template v-if="$slots.left" #left>
      <slot name="left" />
    </template>
    <template #right>
      <slot name="right" />
      <van-icon v-if="rightIcon" :name="rightIcon" size="20" @click="onClickRight" />
    </template>
  </van-nav-bar>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  title: { type: String, default: '智驾游' },
  leftText: { type: String, default: '' },
  showBack: { type: Boolean, default: false },
  fixed: { type: Boolean, default: true },
  rightIcon: { type: String, default: '' },
})

const emit = defineEmits(['click-left', 'click-right'])
const router = useRouter()

const onClickLeft = () => {
  // 有监听者交给父组件处理，否则默认返回上一页
  emit('click-left')
  if (props.showBack) router.back()
}
const onClickRight = () => emit('click-right')
</script>

<style scoped>
.app-header {
  background: var(--bg-card);
}
.app-header :deep(.van-nav-bar__content) {
  height: 52px;
}
.app-header :deep(.van-nav-bar__title) {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}
.app-header :deep(.van-nav-bar__left),
.app-header :deep(.van-nav-bar__right) {
  padding: 0 12px;
}
.app-header :deep(.van-icon) {
  color: var(--color-primary);
}
.app-header::after {
  border-bottom-color: var(--border-color);
}
</style>
