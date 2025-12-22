import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login' // 暂时默认跳转到登录，后续改为 Dashboard
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },

    // --- 用户首页 ---
    {
      path: '/home',
      //用 Layout 组件作为父级
      component: () => import('../layout/UserLayout.vue'),
      // 子路由
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue')
        },
        {
          // 公寓详情页 (动态路由)
          // 访问 /apartment/123 时渲染
          path: '/apartment/:id',
          name: 'apartment-detail',
          component: () => import('../views/ApartmentDetailView.vue')
        },
        // --- 个人中心 ---
        {
          path: '/profile',// 访问路径 /home/profile 或者直接 /profile 取决于你是不是嵌套
          // 如果想让它也在 Layout 里面（有头部导航），就放在这里
          name: 'profile',
          component: () => import('../views/UserProfileView.vue')
        },
        {
          path: '/my-bookings',
          name: 'my-bookings',
          component: () => import('../views/UserBookingsView.vue')
        }
      ]
    },
    // --- 新增：服务商路由 ---
    {
      path: '/provider',
      component: () => import('@/layout/ProviderLayout.vue'),
      redirect: '/provider/dashboard', // 默认跳到控制台
      children: [
        {
          path: 'dashboard',
          name: 'provider-dashboard',
          component: () => import('../views/provider/ProviderHomeView.vue')
        },
        // --- 创建公寓 ---
        {
          path: 'apartments/publish',
          name: 'provider-publish',
          // 确保文件路径正确
          component: () => import('../views/provider/ApartmentPublishView.vue')
        },
        // 之前的占位路由，如果不需要可以删掉或保留作为列表页
        {
          path: 'apartments',
          name: 'provider-apartments',
          component: () => import('../views/provider/ProviderHomeView.vue')
        },
        {
          path: 'apartment/:id',
          name: 'provider-apartment-detail',
          component: () => import('../views/provider/ProviderApartmentDetail.vue')
        }
      ]
    },
    //支付页
    {
      path: '/payment',
      name: 'payment',
      component: () => import('../views/PaymentView.vue'),
      meta: { title: '收银台' }
    }

  ]
})

export default router
