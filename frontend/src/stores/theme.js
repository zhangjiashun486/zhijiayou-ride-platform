import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

export const THEME_KEY = 'app_theme'

export const themeOptions = [
  {
    value: 'default',
    label: '暖阳橙',
    desc: '鲜活热力，适合一路出发',
    primary: '#FF6B35',
    secondary: '#4ECDC4',
    background: '#F2F4F8',
  },
  {
    value: 'ocean',
    label: '海盐蓝',
    desc: '清爽通透，像海风一样松弛',
    primary: '#2D7DF6',
    secondary: '#54C6EB',
    background: '#EEF6FC',
  },
  {
    value: 'forest',
    label: '森野绿',
    desc: '自然沉稳，适合到山林里去',
    primary: '#2D9A6B',
    secondary: '#E4B363',
    background: '#F1F7F2',
  },
  {
    value: 'dark',
    label: '深空夜',
    desc: '夜间模式，低亮度高辨识度',
    primary: '#8EA4FF',
    secondary: '#2ED3B7',
    background: '#101418',
  },
]

function normalizeTheme(value) {
  return themeOptions.some((item) => item.value === value) ? value : 'default'
}

export const useThemeStore = defineStore('theme', () => {
  const current = ref(normalizeTheme(localStorage.getItem(THEME_KEY)))

  const currentInfo = computed(
    () => themeOptions.find((item) => item.value === current.value) || themeOptions[0],
  )
  const mainColor = computed(() => currentInfo.value.primary)
  const accentColor = computed(() => currentInfo.value.secondary)
  const isDark = computed(() => current.value === 'dark')

  function applyTheme(value) {
    const next = normalizeTheme(value)
    current.value = next
    document.documentElement.dataset.theme = next
    localStorage.setItem(THEME_KEY, next)
  }

  function setTheme(value) {
    applyTheme(value)
  }

  function init() {
    applyTheme(current.value)
  }

  return {
    current,
    currentInfo,
    mainColor,
    accentColor,
    isDark,
    options: themeOptions,
    setTheme,
    init,
  }
})
