<template>
  <div class="user-layout">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <div class="logo" @click="$router.push('/home')">
            <el-icon class="logo-icon"><House /></el-icon>
            <span>YASP 公寓平台</span>
          </div>

          <div class="nav-menu">
            <router-link to="/home" class="nav-item" exact-active-class="active">首页</router-link>

            <router-link to="/my-bookings" class="nav-item" active-class="active">我的预定</router-link>
          </div>

          <div class="user-area">
            <el-dropdown @command="handleCommand" v-if="username">
              <span class="el-dropdown-link">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                <span class="username">{{ username }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <div v-else>
              <el-button type="primary" link @click="$router.push('/login')">登录</el-button>
            </div>
          </div>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>

      <el-footer class="footer">
        © 2025 YASP Apartment Platform. All rights reserved.
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { House, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const username = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username')
})

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    localStorage.clear()
    router.push('/login')
    ElMessage.success('已退出登录')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
}
.logo-icon { font-size: 24px; }

/* 导航菜单样式 */
.nav-menu {
  display: flex;
  gap: 30px;
  flex: 1;
  margin-left: 60px;
}

.nav-item {
  text-decoration: none;
  color: #606266;
  font-size: 16px;
  font-weight: 500;
  padding: 0 5px;
  height: 60px;
  line-height: 60px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.nav-item:hover {
  color: #409eff;
}

/* 激活状态 */
.nav-item.active {
  color: #409eff;
  border-bottom-color: #409eff;
}

.user-area {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}
.username { margin: 0 8px; font-size: 14px; }

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 20px;
  min-height: calc(100vh - 120px);
}

.footer {
  text-align: center;
  color: #909399;
  padding: 20px;
  font-size: 12px;
}
</style>
