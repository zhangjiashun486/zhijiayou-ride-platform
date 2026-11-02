<template>
  <div class="home-feed">
    <van-pull-refresh v-model="refreshing" pull-distance="72" @refresh="loadData(true)">
      <header class="feed-header">
        <div class="feed-header__text">
          <p class="feed-date">{{ dateText }}</p>
          <h1 class="feed-greeting">{{ greeting }}</h1>
        </div>
        <button class="ai-fab" aria-label="AI 助手" @click="goAi">
          <van-icon name="chat-o" size="20" />
        </button>
      </header>

      <div class="search-band">
        <van-search
          v-model="searchText"
          placeholder="搜索地点、美食或攻略"
          shape="round"
          clearable
        />
      </div>

      <div class="category-band">
        <button
          v-for="item in categories"
          :key="item.value"
          class="chip"
          :class="{ active: category === item.value }"
          @click="category = item.value"
        >
          {{ item.label }}
        </button>
      </div>

      <div v-if="loading" class="feed-content">
        <div class="skeleton skeleton--hero"></div>
        <div class="skeleton-grid">
          <div v-for="n in 4" :key="n" class="skeleton skeleton--card"></div>
        </div>
      </div>

      <div v-else class="feed-content">
        <section v-if="featured" class="feature-card" @click="openDetail(featured)">
          <img
            v-if="featured.pic"
            :src="normalizeImage(featured.pic)"
            alt=""
            class="feature-img"
            @error="handleImageError"
          />
          <div v-else class="feature-img feature-img--fallback"></div>
          <div class="feature-mask"></div>
          <div class="feature-body">
            <span class="feature-badge">{{ categoryLabel(featured) }}</span>
            <h2>{{ featured.title }}</h2>
            <p>{{ excerpt(featured.content, 60) }}</p>
          </div>
        </section>

        <section class="feed-section">
          <div class="section-head">
            <h2>{{ activeCategoryLabel }}</h2>
            <span>{{ listItems.length }} 条内容</span>
          </div>

          <div v-if="listItems.length" class="news-grid">
            <article
              v-for="(item, index) in listItems"
              :key="item.id || index"
              class="news-card"
              @click="openDetail(item)"
            >
              <div class="news-cover">
                <img
                  v-if="item.pic"
                  :src="normalizeImage(item.pic)"
                  alt=""
                  loading="lazy"
                  @error="handleImageError"
                />
                <span v-else class="news-cover__fallback">
                  <van-icon name="photo-o" size="22" />
                </span>
              </div>
              <div class="news-body">
                <span class="news-tag">{{ categoryLabel(item) }}</span>
                <h3>{{ item.title }}</h3>
                <time>{{ formatDate(item.createTime) }}</time>
              </div>
            </article>
          </div>

          <van-empty v-else image="search" description="没有找到相关资讯" />
        </section>
      </div>
    </van-pull-refresh>

    <van-popup
      v-model:show="detailVisible"
      position="bottom"
      round
      teleport="body"
      class="detail-popup"
      :style="{ maxHeight: '92vh' }"
    >
      <article v-if="activeDetail" class="detail-sheet">
        <van-nav-bar title="资讯详情" left-arrow @click-left="detailVisible = false" />
        <div class="detail-scroll">
          <div v-if="activeDetail.pic" class="detail-cover">
            <img :src="normalizeImage(activeDetail.pic)" alt="" @error="handleImageError" />
          </div>
          <div class="detail-body">
            <span class="detail-category">{{ categoryLabel(activeDetail) }}</span>
            <h2>{{ activeDetail.title }}</h2>
            <time class="detail-time">{{ formatDate(activeDetail.createTime) }}</time>
            <div class="detail-html" v-html="detailHtml"></div>
          </div>
        </div>
        <div class="detail-footer">
          <button class="ask-ai-btn" @click="askAi(activeDetail)">
            <van-icon name="chat-o" size="16" /> 问问 AI 助手
          </button>
        </div>
      </article>
    </van-popup>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getAiMsgList } from '@/api/ai'

const router = useRouter()

const categories = [
  { value: 'all', label: '全部' },
  { value: '1', label: '地标名片' },
  { value: '2', label: '旅游资讯' },
  { value: '3', label: '特色美食' },
  { value: '4', label: '旅行社' },
]

const allItems = ref([])
const loading = ref(true)
const refreshing = ref(false)
const searchText = ref('')
const category = ref('all')
const detailVisible = ref(false)
const activeDetail = ref(null)

const weekText = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
const now = new Date()
const dateText = `${now.getMonth() + 1}月${now.getDate()}日 · ${weekText[now.getDay()]}`

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，明早去看风景'
  if (hour < 12) return '早上好，今天想去哪'
  if (hour < 18) return '下午好，路上也有好风景'
  return '晚上好，收藏一条下个去处'
})

const filteredItems = computed(() => {
  const keyword = searchText.value.trim().toLowerCase()
  return allItems.value.filter((item) => {
    const categoryMatched = category.value === 'all' || String(item.category) === String(category.value)
    const text = `${item.title || ''} ${item.content || ''} ${item.dictLabel || ''}`.toLowerCase()
    return categoryMatched && (!keyword || text.includes(keyword))
  })
})

const featured = computed(() => filteredItems.value[0] || null)
const listItems = computed(() => filteredItems.value.slice(1))

const activeCategoryLabel = computed(() => {
  return categories.find((item) => item.value === category.value)?.label || '全部'
})

const detailHtml = computed(() => {
  const raw = String(activeDetail.value?.content || '').trim()
  if (!raw) return '<p>这里还没有更多文字介绍。</p>'
  if (/<\/?[a-z][\s\S]*>/i.test(raw)) return raw
  return raw.replace(/\n/g, '<br />')
})

function normalizeImage(pic) {
  if (!pic) return ''
  if (/^(https?:|data:|blob:|\/\/)/i.test(pic)) return pic
  const path = pic.startsWith('/') ? pic : `/${pic}`
  return `http://localhost:9999${path}`
}

function plainText(value) {
  return String(value || '')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/gi, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

function excerpt(value, length = 42) {
  const text = plainText(value)
  return text.length > length ? `${text.slice(0, length)}…` : text
}

function categoryLabel(item) {
  if (!item) return '旅游资讯'
  if (item.dictLabel) return item.dictLabel
  return categories.find((entry) => String(entry.value) === String(item.category))?.label || '旅游资讯'
}

function formatDate(value) {
  if (!value) return ''
  const match = /^(\d{4})-(\d{2})-(\d{2})/.exec(String(value))
  if (match) return `${Number(match[1])}年${Number(match[2])}月${Number(match[3])}日`
  return String(value).slice(0, 10)
}

function handleImageError(event) {
  event.currentTarget.style.display = 'none'
}

function openDetail(item) {
  activeDetail.value = item
  detailVisible.value = true
}

function goAi() {
  router.push('/common/ai-route')
}

function askAi(item) {
  detailVisible.value = false
  router.push({ path: '/common/ai-route', query: { q: `介绍一下${item.title}` } })
}

async function loadData(fromRefresh = false) {
  if (fromRefresh) {
    refreshing.value = true
  } else {
    loading.value = true
  }

  try {
    const res = await getAiMsgList({})
    const list = Array.isArray(res)
      ? res
      : res?.value || res?.rows || res?.data || res?.list || []
    allItems.value = Array.isArray(list) ? list.filter(Boolean) : []
  } catch {
    if (!fromRefresh) showToast('资讯加载失败')
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

onMounted(() => loadData(false))
</script>

<style scoped>
.home-feed {
  min-height: calc(100vh - 60px);
  background: var(--bg-page);
}

.feed-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 16px 4px;
}
.feed-date {
  font-size: 12px;
  color: var(--text-hint);
}
.feed-greeting {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.3;
  color: var(--text-primary);
}
.ai-fab {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 14px;
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-button);
  cursor: pointer;
}
.ai-fab:active {
  transform: scale(0.94);
}

.search-band {
  padding: 12px 12px 4px;
}
.search-band :deep(.van-search) {
  padding: 0;
}
.search-band :deep(.van-search__content) {
  border-radius: 14px;
}

.category-band {
  display: flex;
  gap: 8px;
  padding: 10px 16px 14px;
  overflow-x: auto;
  scrollbar-width: none;
}
.category-band::-webkit-scrollbar {
  display: none;
}

.feed-content {
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
  padding: 0 16px 28px;
}

.feature-card {
  position: relative;
  height: 196px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  cursor: pointer;
}
.feature-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.feature-img--fallback {
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
}
.feature-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(10, 15, 25, 0.05) 20%, rgba(10, 15, 25, 0.78) 100%);
}
.feature-body {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 16px;
  color: #fff;
}
.feature-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
  backdrop-filter: blur(6px);
  font-size: 11px;
  font-weight: 600;
}
.feature-body h2 {
  margin-top: 10px;
  font-size: 19px;
  line-height: 1.35;
  font-weight: 700;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.feature-body p {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.5;
  opacity: 0.88;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.feed-section {
  margin-top: 20px;
}
.section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
}
.section-head h2 {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}
.section-head span {
  font-size: 12px;
  color: var(--text-hint);
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.news-card {
  border-radius: var(--radius-md);
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.15s ease;
}
.news-card:active {
  transform: scale(0.98);
}
.news-cover {
  height: 104px;
  background: var(--bg-soft);
}
.news-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.news-cover__fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: var(--text-hint);
  background: linear-gradient(135deg, color-mix(in srgb, var(--color-primary) 14%, transparent), color-mix(in srgb, var(--color-secondary) 14%, transparent));
}
.news-body {
  padding: 10px 12px 12px;
}
.news-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--color-primary) 12%, transparent);
  color: var(--color-primary);
  font-size: 10px;
  font-weight: 600;
}
.news-body h3 {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.42;
  font-weight: 600;
  color: var(--text-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
}
.news-body time {
  display: block;
  margin-top: 8px;
  font-size: 11px;
  color: var(--text-hint);
}

.skeleton {
  border-radius: var(--radius-md);
  background: linear-gradient(90deg, var(--bg-card) 25%, var(--bg-soft) 37%, var(--bg-card) 63%);
  background-size: 400% 100%;
  animation: shimmer 1.3s ease infinite;
}
.skeleton--hero {
  height: 196px;
  border-radius: var(--radius-lg);
}
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 20px;
}
.skeleton--card {
  height: 176px;
}
@keyframes shimmer {
  0% { background-position: 100% 0; }
  100% { background-position: 0 0; }
}

.detail-popup {
  background: var(--bg-card);
}
.detail-sheet {
  display: flex;
  flex-direction: column;
  max-height: 92vh;
  background: var(--bg-card);
}
.detail-scroll {
  flex: 1;
  overflow-y: auto;
}
.detail-cover {
  height: 200px;
  background: var(--bg-soft);
}
.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.detail-body {
  padding: 20px 20px 8px;
}
.detail-category {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--color-primary) 12%, transparent);
  color: var(--color-primary);
  font-size: 11px;
  font-weight: 600;
}
.detail-body h2 {
  margin-top: 12px;
  font-size: 21px;
  line-height: 1.4;
  font-weight: 700;
  color: var(--text-primary);
}
.detail-time {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-hint);
}
.detail-html {
  margin-top: 16px;
  font-size: 15px;
  line-height: 1.85;
  color: var(--text-secondary);
  word-break: break-word;
}
.detail-html :deep(p) { margin-bottom: 12px; }
.detail-html :deep(img) { max-width: 100%; height: auto; border-radius: 10px; margin: 8px 0; }
.detail-html :deep(h1),
.detail-html :deep(h2),
.detail-html :deep(h3) { margin: 18px 0 10px; color: var(--text-primary); }
.detail-html :deep(a) { color: var(--color-primary); }

.detail-footer {
  padding: 12px 20px calc(16px + env(safe-area-inset-bottom, 0px));
  border-top: 1px solid var(--divider-color);
  background: var(--bg-card);
}
.ask-ai-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  height: 46px;
  border: none;
  border-radius: var(--radius-round);
  background: var(--gradient-primary);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  box-shadow: var(--shadow-button);
  cursor: pointer;
}
.ask-ai-btn:active { opacity: 0.88; }
</style>
