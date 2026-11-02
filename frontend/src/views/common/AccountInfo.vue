<template>
  <div class="page-container">
    <AppHeader title="行程统计" :show-back="true" />

    <!-- 用户信息头 -->
    <div class="user-header">
      <img :src="auth.currentUser?.avatar || '/img/default-header.jpg'" class="avatar" />
      <div class="user-info">
        <div class="nickname">{{ auth.currentUser?.nickname || '用户' }}</div>
        <div class="stats-row">
          <span>打车 {{ passengerCount }} 次</span>
          <span class="divider">|</span>
          <span>载客 {{ driverCount }} 次</span>
          <span class="divider">|</span>
          <span>收支 {{ balance >= 0 ? '+' : '' }}{{ balance.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div v-if="orders.length === 0" class="empty-wrap">
      <van-empty description="暂无已支付订单" />
    </div>

    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-item">
        <div class="order-role">{{ order.roleLabel }}</div>
        <div class="order-info">
          <div class="order-time">{{ order.timeLabel }}</div>
          <div class="order-cost" :class="order.costNum >= 0 ? 'cost-plus' : 'cost-minus'">
            {{ order.costNum >= 0 ? '+' : '' }}{{ order.costNum.toFixed(2) }}
          </div>
        </div>
        <div class="order-status">{{ order.statusLabel }}</div>
      </div>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { paidList } from '@/api/order'
import { formatDate, orderStatus } from '@/utils/index.js'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const auth = useAuthStore()

const rawOrders = ref([])

const passengerCount = ref(0)
const driverCount = ref(0)
const balance = ref(0)

const orders = computed(() => {
  let a = 0
  let b = 0
  let total = 0
  const currentUserId = auth.currentUser?.id

  const list = rawOrders.value.map(order => {
    const isPassenger = order.passengerId === currentUserId
    const cost = parseFloat(order.cost || order.amount || 0) || 0
    const costNum = isPassenger ? -cost : cost

    if (isPassenger) {
      a++
    } else {
      b++
    }
    total += costNum

    return {
      ...order,
      roleLabel: isPassenger ? '乘客' : '车主',
      timeLabel: formatDate(order.createTime || order.createdAt || order.time),
      costNum,
      statusLabel: orderStatus[order.status] || '未知',
    }
  })

  passengerCount.value = a
  driverCount.value = b
  balance.value = parseFloat(total.toFixed(2))

  return list
})

onMounted(async () => {
  try {
    const res = await paidList()
    rawOrders.value = (res.data || res || [])
    // ensure it's an array
    if (!Array.isArray(rawOrders.value)) {
      rawOrders.value = []
    }
  } catch (e) {
    rawOrders.value = []
  }
})
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
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 2px solid #fff;
  object-fit: cover;
  background: #fff;
}

.user-info {
  flex: 1;
}

.nickname {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.stats-row {
  font-size: 12px;
  opacity: 0.9;
  display: flex;
  gap: 6px;
  align-items: center;
}

.divider {
  opacity: 0.5;
}

.empty-wrap {
  padding-top: 60px;
}

.order-list {
  padding: 8px 16px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 14px 12px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.order-role {
  font-size: 13px;
  color: #666;
  width: 40px;
  flex-shrink: 0;
}

.order-info {
  flex: 1;
  min-width: 0;
}

.order-time {
  font-size: 13px;
  color: #999;
  margin-bottom: 2px;
}

.order-cost {
  font-size: 15px;
  font-weight: 600;
}

.cost-plus {
  color: #07C160;
}

.cost-minus {
  color: var(--color-primary);
}

.order-status {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  margin-left: 8px;
}
</style>
