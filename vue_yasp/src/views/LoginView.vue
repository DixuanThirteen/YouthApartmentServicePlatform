<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>YASP 公寓服务平台</h2>
        <p>欢迎回来，请登录您的账户</p>
      </div>

      <el-tabs v-model="activeRole" class="role-tabs" stretch>
        <el-tab-pane label="普通用户" name="user"></el-tab-pane>
        <el-tab-pane label="服务商" name="provider"></el-tab-pane>
        <el-tab-pane label="管理员" name="admin"></el-tab-pane>
      </el-tabs>

      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" size="large">

        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <div class="form-options">
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
          <el-link type="primary" :underline="false">忘记密码?</el-link>
        </div>

        <el-button
          type="primary"
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '立即登录' }}
        </el-button>

        <div class="register-link" v-if="activeRole !== 'admin'">
          <span>还没有账号? </span>
          <el-link type="primary" @click="goToRegister">立即注册 {{ roleName }}</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 当前选中的角色，默认为 user
const activeRole = ref('user')

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 计算当前角色的中文名
const roleName = computed(() => {
  if (activeRole.value === 'provider') return '服务商'
  return '用户'
})

// 根据角色获取对应的后端 API URL
const getLoginUrl = () => {
  switch (activeRole.value) {
    case 'provider': return '/api/providers/login'
    case 'admin': return '/api/admins/login'
    case 'user':
    default: return '/api/users/login'
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const url = getLoginUrl()
        const response = await axios.post(url, {
          username: loginForm.username,
          password: loginForm.password,
          rememberMe: loginForm.rememberMe
        })

        if (response.data.code === 200) {

          ElMessage.success('登录成功')

          const resData = response.data

          // 1. 存储 Token
          localStorage.setItem('token', resData.token)

          // 2. 存储后端返回的用户 ID
          localStorage.setItem('userId', resData.id)

          // 3. 存储用户名
          localStorage.setItem('username', resData.username)

          // 4. 【新增】存储用户头像 URL
          // 确保 UserLayout.vue 能读取到这个字段
          localStorage.setItem('userAvatar', resData.avatar)

          const role = resData.role
          localStorage.setItem('role', role)

          if (resData.provider) {
            localStorage.setItem('providerName', resData.provider)
          }

          // 根据角色跳转
          if(resData.role && resData.role.includes('PROVIDER')){
            router.push('/provider/dashboard')
          }else{
            router.push('/home')
          }
        }
      } catch (error) {
        console.error(error)
        if (error.response && error.response.data) {
            const msg = error.response.data.message || '登录失败'
            ElMessage.error(msg)
        } else {
            ElMessage.error('网络连接失败')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px;
  color: #303133;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.role-tabs {
  margin-bottom: 20px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  padding: 20px 0;
}

.register-link {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}
</style>
