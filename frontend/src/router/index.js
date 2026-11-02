import { createRouter, createWebHashHistory } from 'vue-router'

const SESSION_TOKEN_KEY = 'SESSION_TOKEN_KEY'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/map',
    name: 'Map',
    component: () => import('@/views/Map.vue'),
    meta: { title: '选择地点' },
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('@/views/Message.vue'),
    meta: { title: '即时消息' },
  },
  {
    path: '/point',
    name: 'Point',
    component: () => import('@/views/Point.vue'),
    meta: { title: '实时位置' },
  },
  {
    path: '/way',
    name: 'Way',
    component: () => import('@/views/Way.vue'),
    meta: { title: '路径规划' },
  },

  // 乘客端
  {
    path: '/passenger/home',
    name: 'PassengerHome',
    component: () => import('@/views/passenger/Home.vue'),
    meta: { title: '乘客首页', role: 'passenger' },
  },
  {
    path: '/passenger/trip',
    name: 'PassengerTripList',
    component: () => import('@/views/passenger/TripList.vue'),
    meta: { title: '我的行程', role: 'passenger' },
  },
  {
    path: '/passenger/trip-detail/:id',
    name: 'PassengerTripDetail',
    component: () => import('@/views/passenger/TripDetail.vue'),
    meta: { title: '行程详情', role: 'passenger' },
  },
  {
    path: '/passenger/trip-confirmed/:id',
    name: 'PassengerTripConfirmed',
    component: () => import('@/views/passenger/TripConfirmed.vue'),
    meta: { title: '行程确认', role: 'passenger' },
  },
  {
    path: '/passenger/trip-info/:id',
    name: 'PassengerTripInfo',
    component: () => import('@/views/passenger/TripInfo.vue'),
    meta: { title: '行程信息', role: 'passenger' },
  },
  {
    path: '/passenger/trip-req/:id',
    name: 'PassengerTripReq',
    component: () => import('@/views/passenger/TripReq.vue'),
    meta: { title: '行程需求', role: 'passenger' },
  },
  {
    path: '/passenger/order',
    name: 'PassengerOrderList',
    component: () => import('@/views/passenger/OrderList.vue'),
    meta: { title: '我的订单', role: 'passenger' },
  },
  {
    path: '/passenger/pay',
    name: 'PassengerPay',
    component: () => import('@/views/passenger/Pay.vue'),
    meta: { title: '支付', role: 'passenger' },
  },

  // 司机端
  {
    path: '/driver/home',
    name: 'DriverHome',
    component: () => import('@/views/driver/Home.vue'),
    meta: { title: '司机首页', role: 'driver' },
  },
  {
    path: '/driver/trip',
    name: 'DriverTripList',
    component: () => import('@/views/driver/TripList.vue'),
    meta: { title: '我的行程', role: 'driver' },
  },
  {
    path: '/driver/trip-detail/:id',
    name: 'DriverTripDetail',
    component: () => import('@/views/driver/TripDetail.vue'),
    meta: { title: '行程详情', role: 'driver' },
  },
  {
    path: '/driver/trip-confirmed/:id',
    name: 'DriverTripConfirmed',
    component: () => import('@/views/driver/TripConfirmed.vue'),
    meta: { title: '行程确认', role: 'driver' },
  },
  {
    path: '/driver/trip-info/:id',
    name: 'DriverTripInfo',
    component: () => import('@/views/driver/TripInfo.vue'),
    meta: { title: '行程信息', role: 'driver' },
  },
  {
    path: '/driver/trip-req/:id',
    name: 'DriverTripReq',
    component: () => import('@/views/driver/TripReq.vue'),
    meta: { title: '行程需求', role: 'driver' },
  },
  {
    path: '/driver/order',
    name: 'DriverOrderList',
    component: () => import('@/views/driver/OrderList.vue'),
    meta: { title: '我的订单', role: 'driver' },
  },
  {
    path: '/driver/verify',
    name: 'DriverVerify',
    component: () => import('@/views/driver/Verify.vue'),
    meta: { title: '司机认证', role: 'driver' },
  },

  // 共用页面
  {
    path: '/common/center',
    name: 'UserCenter',
    component: () => import('@/views/common/UserCenter.vue'),
    meta: { title: '个人中心' },
  },
  {
    path: '/common/edit',
    name: 'UserEdit',
    component: () => import('@/views/common/UserEdit.vue'),
    meta: { title: '编辑资料' },
  },
  {
    path: '/common/password',
    name: 'AlterPwd',
    component: () => import('@/views/common/AlterPwd.vue'),
    meta: { title: '修改密码' },
  },
  {
    path: '/common/photo',
    name: 'AlterPhoto',
    component: () => import('@/views/common/AlterPhoto.vue'),
    meta: { title: '修改头像' },
  },
  {
    path: '/common/paycode',
    name: 'AlterPaycode',
    component: () => import('@/views/common/AlterPaycode.vue'),
    meta: { title: '支付密码' },
  },
  {
    path: '/common/car-info',
    name: 'CarInfo',
    component: () => import('@/views/common/CarInfo.vue'),
    meta: { title: '车辆信息' },
  },
  {
    path: '/common/car-verify',
    name: 'CarVerify',
    component: () => import('@/views/common/CarVerify.vue'),
    meta: { title: '车辆认证' },
  },
  {
    path: '/common/user-verify',
    name: 'UserVerify',
    component: () => import('@/views/common/UserVerify.vue'),
    meta: { title: '实名认证' },
  },
  {
    path: '/common/messages',
    name: 'UserMsg',
    component: () => import('@/views/common/UserMsg.vue'),
    meta: { title: '消息中心' },
  },
  {
    path: '/common/account',
    name: 'AccountInfo',
    component: () => import('@/views/common/AccountInfo.vue'),
    meta: { title: '账户信息' },
  },
  {
    path: '/common/ai',
    name: 'Ai',
    component: () => import('@/views/common/Ai.vue'),
    meta: { title: 'AI助手' },
  },
  {
    path: '/common/ai-route',
    name: 'AiRoute',
    component: () => import('@/views/common/AiRoute.vue'),
    meta: { title: '智能问路' },
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 智驾游` : '智驾游'

  if (to.path === '/login') {
    return next()
  }

  const token = localStorage.getItem(SESSION_TOKEN_KEY)
  if (!token) {
    return next('/login')
  }

  next()
})

export default router
