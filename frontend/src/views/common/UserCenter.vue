<template>
  <div class="page-container center-page">
    <AppHeader title="个人中心" />

    <section class="profile-card">
      <img :src="user.avatar || '/img/default-header.jpg'" class="avatar" alt="" />
      <div class="profile-main">
        <h2>{{ user.useralias || '未设置昵称' }}</h2>
        <p class="profile-phone">{{ user.phone || '未绑定手机号' }}</p>
        <div class="badges">
          <span class="badge" :class="user.status == 1 ? 'badge--ok' : 'badge--pending'">
            <van-icon :name="user.status == 1 ? 'checked' : 'clock-o'" size="12" />
            实名{{ user.status == 1 ? '已认证' : '未认证' }}
          </span>
          <span class="badge" :class="user.role == 1 ? 'badge--ok' : 'badge--pending'">
            <van-icon :name="user.role == 1 ? 'checked' : 'clock-o'" size="12" />
            车主{{ user.role == 1 ? '已认证' : '未认证' }}
          </span>
        </div>
      </div>
      <button class="edit-btn" aria-label="编辑资料" @click="router.push('/common/edit')">
        <van-icon name="edit" size="16" />
      </button>
    </section>

    <section class="quick-grid">
      <button class="quick-item" @click="router.push('/common/user-verify')">
        <span class="quick-icon quick-icon--orange"><van-icon name="certificate" size="19" /></span>
        <span>实名认证</span>
      </button>
      <button class="quick-item" @click="router.push('/common/car-verify')">
        <span class="quick-icon quick-icon--blue"><van-icon name="passed" size="19" /></span>
        <span>车主认证</span>
      </button>
      <button class="quick-item" @click="showTheme = true">
        <span class="quick-icon quick-icon--green"><van-icon name="brush-o" size="19" /></span>
        <span>更换主题</span>
      </button>
    </section>

    <h3 class="section-label">账号与安全</h3>
    <van-cell-group inset class="menu-group">
      <van-cell v-for="cell in accountCells" :key="cell.title" :title="cell.title" :to="cell.to" is-link>
        <template #icon><span class="cell-icon"><van-icon :name="cell.icon" size="16" /></span></template>
      </van-cell>
    </van-cell-group>

    <h3 class="section-label section-label--gap">我的服务</h3>
    <van-cell-group inset class="menu-group">
      <van-cell v-for="cell in serviceCells" :key="cell.title" :title="cell.title" :to="cell.to" is-link>
        <template #icon><span class="cell-icon"><van-icon :name="cell.icon" size="16" /></span></template>
      </van-cell>
      <van-cell title="我的消息" is-link to="/common/messages">
        <template #icon><span class="cell-icon"><van-icon name="comment-o" size="16" /></span></template>
        <template #value>
          <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
        </template>
      </van-cell>
      <van-cell title="更换主题" is-link :value="theme.currentInfo.label" @click="showTheme = true">
        <template #icon><span class="cell-icon"><van-icon name="brush-o" size="16" /></span></template>
      </van-cell>
    </van-cell-group>

    <van-popup
      v-model:show="showTheme"
      position="bottom"
      round
      teleport="body"
      :style="{ maxHeight: '85vh' }"
    >
      <div class="theme-sheet">
        <div class="sheet-head">
          <div>
            <p class="sheet-title">更换主题</p>
            <p class="sheet-sub">当前主题：{{ theme.currentInfo.label }}</p>
          </div>
          <van-icon name="cross" size="20" class="sheet-close" @click="showTheme = false" />
        </div>

        <div class="theme-list">
          <button
            v-for="item in theme.options"
            :key="item.value"
            class="theme-option"
            :class="{ active: theme.current === item.value }"
            :style="{ '--swatch-main': item.primary, '--swatch-accent': item.secondary }"
            @click="chooseTheme(item.value)"
          >
            <span class="theme-swatch">
              <i class="swatch-main"></i>
              <i class="swatch-accent"></i>
            </span>
            <span class="theme-copy">
              <b>{{ item.label }}</b>
              <small>{{ item.desc }}</small>
            </span>
            <van-icon
              v-if="theme.current === item.value"
              name="success"
              size="18"
              class="theme-check"
            />
          </button>
        </div>
      </div>
    </van-popup>

    <div class="logout-wrap">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>

    <TabBar />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { getUserInfo, getAuthInfo } from '@/api/account'
import { useNoticeMessages } from '@/composables/useNotice'
import AppHeader from '@/components/AppHeader.vue'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const auth = useAuthStore()
const theme = useThemeStore()

const user = ref({})
const authInfo = ref({})
const showTheme = ref(false)
const { unreadCount } = useNoticeMessages()

const accountCells = [
  { title: '修改头像', icon: 'photograph', to: '/common/photo' },
  { title: '完善资料', icon: 'user-o', to: '/common/edit' },
  { title: '修改密码', icon: 'lock', to: '/common/password' },
  { title: '收款码', icon: 'qr', to: '/common/paycode' },
]

const serviceCells = [
  { title: '车辆信息', icon: 'logistics', to: '/common/car-info' },
  { title: '行程统计', icon: 'chart-trending-o', to: '/common/account' },
]

onMounted(async () => {
  try {
    const [userRes, authRes] = await Promise.all([getUserInfo(), getAuthInfo()])
    user.value = userRes.data?.[0] || userRes || {}
    authInfo.value = authRes.data?.[0] || authRes || {}
  } catch {
    /* 拉取失败时展示默认信息 */
  }
})

function handleLogout() {
  showConfirmDialog({
    title: '提示',
    message: '确定要退出登录吗？',
  }).then(() => {
    auth.logout()
    router.push('/login')
  }).catch(() => {})
}

function chooseTheme(value) {
  theme.setTheme(value)
  showTheme.value = false
}
</script>

<style scoped>
.center-page {
  background: var(--bg-page);
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 16px;
  padding: 20px 18px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  box-shadow: var(--shadow-button);
  color: #fff;
}
.avatar {
  width: 62px;
  height: 62px;
  border-radius: 20px;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.7);
  background: #fff;
}
.profile-main {
  flex: 1;
  min-width: 0;
}
.profile-main h2 {
  font-size: 19px;
  font-weight: 700;
}
.profile-phone {
  margin-top: 4px;
  font-size: 12px;
  opacity: 0.85;
}
.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 10px;
}
.badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 3px 9px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 500;
}
.badge--ok {
  background: rgba(255, 255, 255, 0.24);
}
.badge--pending {
  background: rgba(0, 0, 0, 0.14);
}
.edit-btn {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  cursor: pointer;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin: 0 16px 20px;
}
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 14px 6px;
  border: none;
  border-radius: var(--radius-md);
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
  color: var(--text-primary);
  font-size: 12px;
  cursor: pointer;
}
.quick-item:active {
  transform: scale(0.97);
}
.quick-icon {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 13px;
  color: #fff;
}
.quick-icon--orange { background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary)); }
.quick-icon--blue { background: linear-gradient(135deg, #5DA4FA, #2D7DF6); }
.quick-icon--green { background: linear-gradient(135deg, #58B98A, #2D9A6B); }

.section-label--gap {
  margin-top: 22px;
}
.menu-group {
  margin-bottom: 6px;
}
.cell-icon {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  border-radius: 10px;
  background: color-mix(in srgb, var(--color-primary) 10%, transparent);
  color: var(--color-primary);
}
.unread-badge {
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  background: #EE0A24;
  color: #fff;
  font-size: 11px;
}

.logout-wrap {
  padding: 20px 16px 28px;
}
.logout-btn {
  width: 100%;
  height: 46px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-round);
  background: var(--bg-card);
  color: #EF4444;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}
.logout-btn:active {
  background: var(--bg-soft);
}

.theme-sheet {
  padding: 20px 16px calc(20px + env(safe-area-inset-bottom, 0px));
  background: var(--bg-card);
}
.sheet-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 0 4px 16px;
}
.sheet-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}
.sheet-sub {
  margin-top: 4px;
  font-size: 13px;
  color: var(--text-hint);
}
.sheet-close {
  padding: 4px;
  color: var(--text-secondary);
}
.theme-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.theme-option {
  position: relative;
  min-height: 96px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-start;
  padding: 14px;
  border: 1px solid var(--border-color);
  border-radius: 14px;
  background: var(--bg-soft);
  color: var(--text-primary);
  cursor: pointer;
  text-align: left;
}
.theme-option.active {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px color-mix(in srgb, var(--color-primary) 16%, transparent);
}
.theme-swatch {
  position: relative;
  width: 44px;
  height: 26px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--swatch-main), var(--swatch-accent));
  box-shadow: inset 0 0 0 2px rgba(255, 255, 255, 0.38);
  overflow: hidden;
}
.swatch-main,
.swatch-accent {
  position: absolute;
  bottom: 5px;
  height: 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.55);
}
.swatch-main { left: 7px; width: 12px; }
.swatch-accent { right: 7px; width: 12px; }
.theme-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.theme-copy b {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}
.theme-copy small {
  margin-top: 4px;
  font-size: 12px;
  line-height: 1.4;
  color: var(--text-hint);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.theme-check {
  position: absolute;
  right: 12px;
  bottom: 12px;
  color: var(--color-primary);
}
</style>
