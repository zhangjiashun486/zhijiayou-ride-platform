<template>
  <div class="page-container">
    <AppHeader title="收到的邀请" show-back @click-left="router.push('/passenger/trip-detail/' + route.params.id)" />

    <van-loading v-if="loading" style="display:block; margin: 60px auto" />

    <div
      v-for="inv in inviteList"
      :key="inv.id"
      class="card invite-card"
    >
      <div class="flex" style="align-items: center">
        <van-image
          round
          width="48"
          height="48"
          :src="inv.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'"
        />
        <div class="flex-1" style="margin-left: 12px">
          <div class="flex-between">
            <span class="text-bold">{{ inv.nickName || '车主' }}</span>
            <span :style="{ color: invStatusColor(inv.status) }" class="text-sm text-bold">
              {{ invStatusLabel(inv.status) }}
            </span>
          </div>
          <div class="text-sm text-secondary mt-sm">
            {{ inv.startAddr || '' }} → {{ inv.endAddr || '' }}
          </div>
        </div>
      </div>

      <!-- 操作按钮（仅未确认状态） -->
      <div v-if="inv.status === 0" class="invite-actions mt-md">
        <!-- 已满员 -->
        <span v-if="inv.quantity === 0" class="full-tag">已满员</span>
        <template v-else>
          <button class="btn-accept" :disabled="acting[inv.id]" @click="onAccept(inv)">
            {{ acting[inv.id] ? '处理中...' : '确认同行' }}
          </button>
          <button class="btn-reject" :disabled="acting[inv.id]" @click="onReject(inv)">
            不合适
          </button>
        </template>
      </div>
    </div>

    <van-empty v-if="!loading && inviteList.length === 0" description="暂无邀请" />

    <TabBar />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'
import * as strokeApi from '@/api/stroke'

const router = useRouter()
const route = useRoute()

const inviteList = ref([])
const loading = ref(false)
const acting = reactive({})

function invStatusLabel(status) {
  const map = { 0: '未确认', 1: '已确认', 2: '已拒绝', 3: '已超时' }
  return map[status] || '未知'
}

function invStatusColor(status) {
  const map = { 0: '#F59E0B', 1: '#3B82F6', 2: '#EF4444', 3: '#9CA3AF' }
  return map[status] || '#9CA3AF'
}

async function loadInvites() {
  loading.value = true
  try {
    const res = await strokeApi.inviteList(route.params.id)
    if (res.code === 200) {
      inviteList.value = res.data || []
    }
  } catch { /* ignore */ }
  loading.value = false
}

async function onAccept(inv) {
  try {
    await showConfirmDialog({
      title: '确认同行',
      message: `确定要接受 ${inv.nickName || '该车主'} 的邀请吗？`,
    })
  } catch {
    return // 取消
  }

  acting[inv.id] = true
  try {
    const res = await strokeApi.acceptInvite({
      inviterTripId: inv.inviteeTripId,
      inviteeTripId: inv.inviterTripId,
      status: 1,
    })
    if (res.code === 200) {
      showToast('已确认同行')
      router.push(`/passenger/trip-confirmed/${route.params.id}`)
    } else if (res.code === -5002) {
      showToast('行程已发车')
      await loadInvites()
    } else {
      showToast(res.msg || '操作失败')
    }
  } catch {
    showToast('操作失败')
  } finally {
    acting[inv.id] = false
  }
}

async function onReject(inv) {
  try {
    await showConfirmDialog({
      title: '不合适',
      message: `确定要拒绝 ${inv.nickName || '该车主'} 的邀请吗？`,
    })
  } catch {
    return // 取消
  }

  acting[inv.id] = true
  try {
    const res = await strokeApi.acceptInvite({
      inviterTripId: inv.inviteeTripId,
      inviteeTripId: inv.inviterTripId,
      status: 2,
    })
    if (res.code === 200) {
      showToast('已拒绝')
      router.push(`/passenger/trip-detail/${route.params.id}`)
    } else if (res.code === -5002) {
      showToast('行程已发车')
      await loadInvites()
    } else {
      showToast(res.msg || '操作失败')
    }
  } catch {
    showToast('操作失败')
  } finally {
    acting[inv.id] = false
  }
}

onMounted(() => {
  loadInvites()
})
</script>

<style scoped>
.invite-card {
  /* base */
}
.invite-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
.btn-accept {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}
.btn-accept:active {
  opacity: 0.85;
}
.btn-accept:disabled {
  opacity: 0.5;
}
.btn-reject {
  padding: 8px 20px;
  border: 1.5px solid #ccc;
  border-radius: 20px;
  background: #fff;
  color: #999;
  font-size: 14px;
  cursor: pointer;
}
.btn-reject:active {
  background: #f5f5f5;
}
.full-tag {
  font-size: 13px;
  color: #EF4444;
  font-weight: 600;
}
</style>
