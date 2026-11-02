import axios from 'axios'
import { showToast } from 'vant'

const SESSION_TOKEN_KEY = 'SESSION_TOKEN_KEY'

const api = axios.create({
  baseURL: '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(SESSION_TOKEN_KEY)
    if (token) {
      config.headers[SESSION_TOKEN_KEY] = token
    }
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        localStorage.removeItem(SESSION_TOKEN_KEY)
        localStorage.removeItem('currentUser')
        localStorage.removeItem('currentRole')
        window.location.hash = '#/login'
        showToast('登录已过期，请重新登录')
      } else if (status >= 500) {
        showToast('服务器错误，请稍后重试')
      }
    } else {
      showToast('网络异常，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default api

export { SESSION_TOKEN_KEY }
