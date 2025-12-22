<template>
  <div class="provider-layout">
    <el-aside width="240px" class="aside">
      <div class="logo-area">
        <div class="logo-wrapper">
          <el-icon class="logo-icon"><OfficeBuilding /></el-icon>
          <span class="logo-text">YASP 商户通</span>
        </div>
      </div>

      <el-menu
        active-text-color="#fff"
        background-color="#2b3648"
        text-color="#a0aec0"
        :default-active="$route.path"
        router
        class="el-menu-vertical"
      >
        <el-menu-item index="/provider/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>工作台</span>
        </el-menu-item>

        <div class="menu-group-title">房源管理</div>

        <el-menu-item index="/provider/apartments">
           <el-icon><House /></el-icon>
           <span>我的公寓</span>
        </el-menu-item>
        <el-menu-item index="/provider/apartments/publish">
           <el-icon><Plus /></el-icon>
           <span>发布新房</span>
        </el-menu-item>

        <div class="menu-group-title">交易中心</div>

        <el-menu-item index="/provider/orders">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>

        <el-menu-item index="/provider/settings">
          <el-icon><Setting /></el-icon>
          <span>商户设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">工作台</span>
        </div>
        <div class="header-right">
          <div class="action-item">
            <el-icon><Bell /></el-icon>
          </div>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="avatar-wrapper">
              <el-avatar :size="36" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <div class="user-info">
                <span class="username">{{ username }}</span>
                <span class="role-badge">服务商</span>
              </div>
              <el-icon class="el-icon--right"><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout" style="color: #f56c6c;">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  OfficeBuilding, Odometer, House, Tickets, Setting, CaretBottom, Plus, Bell
} from '@element-plus/icons-vue'

const router = useRouter()
const username = ref('服务商')

onMounted(() => {
  username.value = localStorage.getItem('username') || '商户'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.clear()
      router.push('/login')
      ElMessage.success('已退出')
    })
  }
}
</script>

<style scoped>
.provider-layout {
  display: flex;
  height: 100vh;
  width: 100%;
}

.aside {
  background-color: #2b3648; /* 更深邃的高级蓝灰 */
  color: #fff;
  transition: width 0.3s;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
}

.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #222b3c;
}
.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  background: -webkit-linear-gradient(45deg, #409eff, #36d1dc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.logo-icon { font-size: 24px; color: #409eff; }

.el-menu-vertical {
  border-right: none;
}
/* 选中菜单的高亮样式 */
.el-menu-item.is-active {
  background-color: #409eff !important;
  color: #fff !important;
}
.el-menu-item:hover {
  background-color: #344052 !important;
}

.menu-group-title {
  padding: 10px 20px;
  font-size: 12px;
  color: #6a7a92;
  margin-top: 10px;
  font-weight: 600;
}

.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.header {
  background: #fff;
  height: 64px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  z-index: 9;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-item {
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  color: #606266;
  transition: background 0.3s;
}
.action-item:hover { background: #f6f6f6; }

.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.3s;
}
.avatar-wrapper:hover { background: #f9fafc; }

.user-info {
  margin: 0 8px;
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}
.username { font-size: 14px; font-weight: 500; color: #333; }
.role-badge { font-size: 10px; color: #909399; }

.app-main {
  background-color: #f0f2f5; /* 经典的后台灰底 */
  padding: 24px;
  overflow-y: auto;
}

/* 页面切换动画 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>
