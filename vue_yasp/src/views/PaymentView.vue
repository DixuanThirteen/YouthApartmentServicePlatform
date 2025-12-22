<template>
  <div class="payment-container">
    <el-card class="payment-card" v-loading="loading">
      <template #header>
        <div class="header">
          <span><el-icon><Money /></el-icon> 收银台</span>
        </div>
      </template>

      <div class="payment-content" v-if="orderInfo">

        <el-alert
          v-if="orderInfo.status === 'PENDING'"
          title="订单已创建，请确认支付"
          type="warning"
          show-icon
          :closable="false"
          class="mb-20"
        />
        <el-alert
          v-else-if="orderInfo.status === 'SUCCESS'"
          title="订单已支付成功"
          type="success"
          show-icon
          :closable="false"
          class="mb-20"
        />
        <el-alert
          v-else-if="orderInfo.status === 'CANCELLED'"
          title="订单已取消"
          type="info"
          show-icon
          :closable="false"
          class="mb-20"
        />

        <div class="amount-section">
          <p class="label">支付金额</p>
          <div class="amount">
            <span class="symbol">¥</span>
            <span class="num">{{ (orderInfo.amountCent / 100).toFixed(2) }}</span>
          </div>
        </div>

        <el-descriptions :column="1" border class="order-desc">
          <el-descriptions-item label="支付单号">{{ orderInfo.paymentId }}</el-descriptions-item>
          <el-descriptions-item label="关联预定ID">#{{ orderInfo.bookingId }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(orderInfo.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(orderInfo.status)">{{ orderInfo.status }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="payment-methods" v-if="orderInfo.status === 'PENDING'">
          <p class="method-title">选择支付方式</p>
          <div class="method-item active">
            <img src="https://upload.wikimedia.org/wikipedia/commons/4/4e/Alipay_logo.png" alt="Alipay" class="alipay-logo" />
            <span class="check-icon"><el-icon><Select /></el-icon></span>
          </div>
        </div>

        <div class="actions">
          <template v-if="orderInfo.status === 'PENDING'">
            <el-button type="primary" size="large" class="pay-btn" :loading="paying" @click="handleCallback('SUCCESS')">
              确认支付 ¥{{ (orderInfo.amountCent / 100).toFixed(2) }}
            </el-button>
            <el-button size="large" class="cancel-btn" :loading="paying" @click="handleCallback('CANCELLED')">
              取消支付
            </el-button>
          </template>

          <template v-else>
            <el-button type="default" size="large" class="pay-btn" @click="$router.push('/my-bookings')">
              返回我的预定
            </el-button>
          </template>
        </div>
      </div>

      <div v-else-if="!loading" class="error-state">
        <el-empty description="未找到支付订单信息" />
        <el-button @click="$router.push('/my-bookings')">返回列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Money, Select } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const paying = ref(false)
const orderInfo = ref(null)

onMounted(() => {
  const paymentId = route.query.paymentId
  // 如果 URL 里没有 paymentId，就无法查询
  if (!paymentId) {
    console.log(paymentId)
    ElMessage.error('参数缺失')
    router.replace('/my-bookings')
    return
  }
  fetchPaymentDetails(paymentId)
})

const fetchPaymentDetails = async (paymentId) => {
  loading.value = true
  try {
    const res = await axios.get(`/api/payment/${paymentId}`)

    if (res.data.code === 200) {
      orderInfo.value = res.data.data
    } else {
      ElMessage.error(res.data.message || '获取订单详情失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
  }
}

const handleCallback = async (targetStatus) => {
  if (!orderInfo.value) return

  if (targetStatus === 'CANCELLED') {
    try {
      await ElMessageBox.confirm('确定要放弃本次支付吗？', '提示', {
        confirmButtonText: '放弃支付',
        cancelButtonText: '继续支付',
        type: 'warning'
      })
    } catch {
      return
    }
  }

  paying.value = true
  try {
    const payload = {
      paymentId: orderInfo.value.paymentId,
      bookingId: orderInfo.value.bookingId,
      status: targetStatus
    }

    const res = await axios.post('/api/payment/callback', payload)

    if (res.data.code === 200) {
      if (targetStatus === 'SUCCESS') {
        ElMessage.success('支付成功！')
        orderInfo.value.status = 'SUCCESS'
        setTimeout(() => {
           ElMessageBox.alert('支付成功，房间已为您保留。', '提示', {
             confirmButtonText: '查看订单',
             callback: () => router.replace('/my-bookings')
           })
        }, 500)
      } else {
        // 如果后端对于 SUCCESS 返回 200，但对于 CANCELLED 可能也返回 200（这取决于你后端怎么写的）
        // 这里是处理 200 的情况
        ElMessage.info('支付已取消')
        orderInfo.value.status = 'CANCELLED'
        router.replace('/my-bookings')
      }
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    // 【核心修复区域】
    // 专门捕获后端的 400 错误 (支付失败/取消)
    if (e.response && e.response.status === 400) {
       const resData = e.response.data
       // 如果错误信息是 "支付失败"，我们认为这是取消操作的正常反馈
       if (resData && (resData.message === '支付失败' || targetStatus === 'CANCELLED')) {
          ElMessage.info('支付已取消')
          orderInfo.value.status = 'CANCELLED'
          // 延迟一点跳转，让用户看清提示
          setTimeout(() => {
             router.replace('/my-bookings')
          }, 1000)
          return
       }
    }

    console.error(e)
    ElMessage.error('请求出错，请检查网络')
  } finally {
    paying.value = false
  }
}

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ') : '-'

const getStatusType = (status) => {
  if (status === 'SUCCESS') return 'success'
  if (status === 'PENDING') return 'warning'
  if (status === 'CANCELLED') return 'info'
  return 'primary'
}
</script>

<style scoped>
.payment-container { padding: 40px 20px; background-color: #f5f7fa; min-height: calc(100vh - 60px); display: flex; justify-content: center; align-items: flex-start; }
.payment-card { width: 100%; max-width: 500px; }
.header { font-size: 18px; font-weight: bold; display: flex; align-items: center; gap: 8px; }
.mb-20 { margin-bottom: 20px; }
.amount-section { text-align: center; margin: 30px 0; }
.amount-section .label { font-size: 14px; color: #909399; margin-bottom: 5px; }
.amount-section .amount { color: #f56c6c; font-weight: bold; }
.amount .symbol { font-size: 20px; }
.amount .num { font-size: 40px; }
.order-desc { margin-bottom: 30px; }
.method-title { margin-bottom: 10px; font-size: 14px; font-weight: 500; }
.method-item { border: 1px solid #dcdfe6; border-radius: 4px; padding: 10px; display: flex; align-items: center; justify-content: space-between; cursor: pointer; transition: all 0.3s; }
.method-item.active { border-color: #409eff; background-color: #ecf5ff; }
.alipay-logo { height: 30px; }
.check-icon { color: #409eff; font-weight: bold; }
.actions { display: flex; flex-direction: column; gap: 15px; margin-top: 40px; }
.pay-btn { width: 100%; font-size: 16px; font-weight: bold; }
.cancel-btn { width: 100%; }
.error-state { text-align: center; padding: 20px; }
</style>
