import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// --- Element Plus 相关 ---
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// --- 新增：引入 Axios 和 ElMessage ---
import axios from 'axios'
import { ElMessage } from 'element-plus'

const app = createApp(App)

app.use(router)
app.use(ElementPlus)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// ================== 【新增】Axios 全局配置 ==================

// 1. 请求拦截器：每次发请求自动带上 Token
// 这样您就不需要在每个页面的 headers 里手动写 'Authorization': ... 了
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      // 注意：这里默认添加 Bearer 前缀，符合您的后端 JWT 规范
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 2. 响应拦截器：全局处理 401 错误 (Token 失效)
axios.interceptors.response.use(
  (response) => {
    // 如果响应正常 (2xx)，直接返回数据
    return response
  },
  (error) => {
    // 如果有响应结果（即服务器回话了，但状态码不是 2xx）
    if (error.response) {
      const status = error.response.status
      // 获取后端返回的错误信息，如果后端直接返回字符串则用 data，如果是 JSON 对象则取 data.message
      const errorData = error.response.data
      const msg = (typeof errorData === 'string' ? errorData : errorData?.message) || '请求出错'

      // --- 核心逻辑：拦截 401 ---
      if (status === 401) {
        // 只有当不是在登录页时才弹窗提示，避免登录失败时也弹这个
        if (router.currentRoute.value.path !== '/login') {
             ElMessage.error('登录已过期，请重新登录')

             // 1. 清除本地存储的所有用户信息
             localStorage.clear()

             // 2. 强制跳转回登录页
             router.push('/login')
        } else {
             // 如果是在登录页报 401 (账号密码错误)，通常由页面自己处理报错，这里只透传错误
        }
      } else {
        // 其他错误 (如 403, 404, 500) 可选处理
        // ElMessage.error(msg)
      }
    } else {
      // 网络断了，或者服务器没启动
      ElMessage.error('网络连接异常，请检查网络')
    }

    return Promise.reject(error)
  }
)

// ==========================================================

app.mount('#app')
