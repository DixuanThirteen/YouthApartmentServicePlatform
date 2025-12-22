<template>
  <div class="bookings-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的预定</span>
          <el-button style="float: right; padding: 3px 0" text @click="fetchBookings">刷新</el-button>
        </div>
      </template>

      <div>
        <el-empty v-if="bookings.length === 0" description="暂无预定记录" />

        <div v-else class="booking-list">
          <el-card
            v-for="item in bookings"
            :key="item.id"
            class="booking-item"
            shadow="hover"
          >
            <div class="booking-header">
              <span class="booking-no">订单号: {{ item.bookingNo }}</span>
              <el-tag :type="getStatusType(item.status)" effect="dark" size="small">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>

            <el-descriptions :column="3" border size="small" class="booking-desc">
              <el-descriptions-item label="房间">#{{ item.roomId }}</el-descriptions-item>
              <el-descriptions-item label="入住日期">{{ formatDate(item.startDate) }}</el-descriptions-item>
              <el-descriptions-item label="租期">{{ item.leaseMonths }} 个月</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDateTime(item.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="截止时间" v-if="item.status === 0">
                <span style="color: #f56c6c">{{ formatDateTime(item.holdUntil) }}</span>
              </el-descriptions-item>
            </el-descriptions>

            <div class="booking-footer" v-if="item.status === 0">
              <el-button
                type="primary"
                size="small"
                plain
                @click="handlePay(item)"
              >
                去支付
              </el-button>

              <el-button
                type="danger"
                size="small"
                text
                @click="handleCancel(item)"
              >
                取消订单
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus' // 引入 ElMessageBox
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const bookings = ref([])

onMounted(() => {
  fetchBookings()
})

const fetchBookings = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) return

    // 假设你的后端前缀是 /api，根据你下面的 handlePay 逻辑推断
    const res = await axios.get(`/api/room-booking/user/${userId}`)
    if (res.data.code === 200) {
      bookings.value = res.data.data || []
    }
  } catch (e) {
    console.error('加载列表失败', e)
    ElMessage.error('加载订单列表失败')
  }
}

// --- 新增：取消订单逻辑 ---
// --- 最终版：取消后自动刷新 ---
const handleCancel = (item) => {
  ElMessageBox.confirm(
    '确定要取消该预定订单吗？取消后无法恢复。',
    '提示',
    {
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await axios.put(`/api/room-booking/${item.id}`)

      if (res.data.code === 200) {
        ElMessage.success('订单取消成功')

        // 【关键修改】直接重新加载列表，确保数据绝对同步
        fetchBookings()

      } else {
        ElMessage.error(res.data.message || '取消失败')
      }
    } catch (e) {
      console.error(e)
      ElMessage.error('网络请求失败')
    }
  }).catch(() => {
    // 用户点击取消，不做操作
  })
}

// 核心支付逻辑
const handlePay = async (item) => {
  console.log('1. 进入 handlePay 函数', item)

  if (loading.value) {
    console.warn('2. 请求正在进行中，被防抖拦截')
    return
  }

  loading.value = true
  try {
    console.log('3. 发起 API 请求...')
    // 1. 调用准备支付接口
    const payRes = await axios.post('/api/payment/prepare', {
      bookingId: item.id,
      paymentMethod: 'alipay'
    })

    console.log('4. 收到后端响应:', payRes)

    // 2. 检查响应结构
    const resBody = payRes.data

    if (resBody && resBody.code === 200 && resBody.data) {
      const payData = resBody.data
      console.log('5. Data 数据:', payData)

      const targetId = payData.paymentId
      const amount = payData.totalAmount

      if (!targetId) {
        console.error('6. 错误: paymentId 不存在!', payData)
        ElMessage.error('支付单号缺失')
        return
      }

      console.log('7. 准备跳转, paymentId:', targetId)

      router.push({
        path: '/payment',
        query: {
          paymentId: targetId,
          amount: amount ? amount / 100 : 0
        }
      })
    } else {
      console.error('后端返回错误:', resBody)
      ElMessage.error(resBody?.message || '创建支付订单失败')
    }
  } catch (e) {
    console.error('8. 捕获到异常:', e)
    if (e.response && e.response.status === 401) return
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
  }
}

// 辅助函数
const getStatusText = (status) => {
  const map = { 0: '待确认', 1: '已生效', 2: '已取消', 3: '已过期' }
  return map[status] || '未知'
}
const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return map[status] || 'info'
}
const formatDate = (str) => str ? str.split('T')[0] : ''
const formatDateTime = (str) => str ? str.replace('T', ' ').substring(0, 16) : ''
</script>

<style scoped>
.bookings-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}
.booking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #eee;
}
.booking-no {
  font-size: 13px;
  color: #909399;
  font-family: monospace;
}
.booking-footer {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
