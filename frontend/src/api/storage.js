import api from './index'

/**
 * 文件上传 API
 */

// 上传文件
export function upload(formData) {
  return api.post('/storage/api/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
