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

            <div class="booking-footer">

              <template v-if="item.status === 0">
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
              </template>

              <template v-if="item.status === 1">
                <el-button
                  type="success"
                  size="small"
                  @click="handleCheckIn(item)"
                >
                  办理入住
                </el-button>
              </template>

              <template v-if="item.status === 3">
                 <span style="color: #409eff; font-size: 12px;">当前正在履约中</span>
              </template>

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
import { ElMessage, ElMessageBox } from 'element-plus'
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

    const res = await axios.get(`/api/room-booking/user/${userId}`)
    if (res.data.code === 200) {
      bookings.value = (res.data.data || []).reverse()
    }
  } catch (e) {
    console.error('加载列表失败', e)
    ElMessage.error('加载订单列表失败')
  }
}

// --- 办理入住逻辑 ---
const handleCheckIn = (item) => {
  ElMessageBox.confirm(
    '确认现在办理入住并生成租房合同吗？',
    '提示',
    {
      confirmButtonText: '确定办理',
      cancelButtonText: '取消',
      type: 'success',
    }
  ).then(async () => {
    try {
      const res = await axios.post(`/api/lease-contract/${item.id}`)

      if (res.data.code === 200) {
        ElMessage.success('入住办理成功，合同已生成！')
        fetchBookings() // 刷新后，状态变为3，按钮会自动消失
      }
    } catch (e) {
      if (e.response && e.response.data) {
        const errorData = e.response.data
        if (errorData.message) {
          ElMessage.warning(errorData.message)
        } else {
          ElMessage.error('办理失败，未知原因')
        }
      } else {
        console.error(e)
        ElMessage.error('网络请求失败')
      }
    }
  }).catch(() => {})
}

// --- 取消订单逻辑 ---
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
        fetchBookings()
      } else {
        ElMessage.error(res.data.message || '取消失败')
      }
    } catch (e) {
      console.error(e)
      ElMessage.error('网络请求失败')
    }
  }).catch(() => {})
}

// --- 支付逻辑 ---
const handlePay = async (item) => {
  if (loading.value) return
  loading.value = true
  try {
    const payRes = await axios.post('/api/payment/prepare', {
      bookingId: item.id,
      paymentMethod: 'alipay'
    })
    const resBody = payRes.data

    if (resBody && resBody.code === 200 && resBody.data) {
      const { paymentId, totalAmount } = resBody.data
      if (!paymentId) {
        ElMessage.error('支付单号缺失')
        return
      }
      router.push({
        path: '/payment',
        query: {
          paymentId,
          amount: totalAmount ? totalAmount / 100 : 0
        }
      })
    } else {
      ElMessage.error(resBody?.message || '创建支付订单失败')
    }
  } catch (e) {
    console.error(e)
    if (e.response && e.response.status === 401) return
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
  }
}

// --- 状态映射修改 ---
const getStatusText = (status) => {
  const map = {
    0: '待确认',
    1: '已生效', // (已支付，待入住)
    2: '已取消',
    3: '已入住'  // 【修改点】 status 3 = 已入住
  }
  return map[status] || '未知状态'
}

const getStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'primary' // 【修改点】 status 3 用深蓝色或主色调
  }
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
  min-height: 32px;
}
</style>
