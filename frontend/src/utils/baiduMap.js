const APP_KEY = 'VITE_BAIDU_MAP_AK'

let loaderPromise = null

export function hasBaiduMapKey() {
  return Boolean(import.meta.env.VITE_BAIDU_MAP_AK)
}

export function loadBaiduMap() {
  if (window.BMapGL) return Promise.resolve(window.BMapGL)
  if (loaderPromise) return loaderPromise

  const ak = import.meta.env.VITE_BAIDU_MAP_AK
  if (!ak) {
    return Promise.reject(new Error('VITE_BAIDU_MAP_AK is not configured'))
  }

  loaderPromise = new Promise((resolve, reject) => {
    const callbackName = `__baiduMapReady_${Date.now()}`
    window[callbackName] = () => {
      delete window[callbackName]
      if (window.BMapGL) resolve(window.BMapGL)
      else reject(new Error('BMapGL is unavailable'))
    }
    const script = document.createElement('script')
    script.async = true
    script.src = `https://api.map.baidu.com/api?v=1.0&type=webgl&ak=${encodeURIComponent(ak)}&callback=${callbackName}`
    script.onerror = () => reject(new Error('Failed to load Baidu Map SDK'))
    document.head.appendChild(script)
  })

  return loaderPromise
}