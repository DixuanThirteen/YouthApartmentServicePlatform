<template>
  <div class="detail-container" v-loading="loading">

    <div v-if="apartment">
      <div class="breadcrumb-area">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>公寓详情</el-breadcrumb-item>
          <el-breadcrumb-item>{{ apartment.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="overview-card">
        <el-row :gutter="40">
          <el-col :span="14" :xs="24">
            <div class="main-image-box">
              <el-image
                :src="apartment.coverUrl || 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267'"
                fit="cover"
                class="main-image"
                :preview-src-list="[apartment.coverUrl]"
              />
            </div>
          </el-col>
          <el-col :span="10" :xs="24">
            <div class="info-content">
              <h1 class="apt-name">{{ apartment.name }}</h1>
              <div class="tags-row">
                <el-tag v-if="apartment.type === 2" type="primary" effect="dark">企业认证</el-tag>
                <el-tag v-else type="warning" effect="dark">个人房东</el-tag>
                <el-tag type="info" effect="plain">{{ apartment.cityCode }}</el-tag>
              </div>
              <div class="info-list">
                <div class="info-item"><el-icon><Location /></el-icon><span>{{ apartment.address || '暂无详细地址' }}</span></div>
                <div class="info-item"><el-icon><Timer /></el-icon><span>租期要求：{{ apartment.minLeaseMonths }} - {{ apartment.maxLeaseMonths }} 个月</span></div>
                <div class="info-item"><el-icon><Money /></el-icon><span>押金：¥{{ apartment.depositCent ? apartment.depositCent / 100 : '面议' }}</span></div>
              </div>
              <div class="desc-box">
                <h4>公寓简介</h4>
                <p>{{ apartment.description || '暂无介绍' }}</p>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="room-section">
        <div class="section-header">
          <h3>可选房型</h3>
        </div>
        <el-empty v-if="roomTypes.length === 0" description="暂无上架房型" />
        <div v-else class="room-grid">
          <div v-for="rt in roomTypes" :key="rt.id" class="room-card">
            <div class="room-img-box">
              <el-image :src="rt.coverUrl || 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c'" fit="cover" class="room-img" />
            </div>
            <div class="room-content">
              <div class="room-header">
                <h4 class="room-name">{{ rt.name }}</h4>
                <div class="room-price">¥{{ rt.price }}<span class="unit">/月</span></div>
              </div>
              <p class="room-desc">{{ rt.detail || '暂无描述' }}</p>
              <div class="room-footer">
                <el-button class="book-btn" type="primary" @click="openBookDialog(rt)">立即预定</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else-if="!loading" description="未找到公寓信息" />

    <el-dialog
      v-model="dialogVisible"
      title="预定申请"
      width="500px"
      @closed="resetForm"
      destroy-on-close
    >
      <el-form :model="bookForm" label-position="top" size="large">

        <el-alert
          v-if="currentRoomType"
          :title="`当前房型：${currentRoomType.name} (¥${currentRoomType.price}/月)`"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        />

        <el-form-item label="预定方式">
          <el-radio-group v-model="bookForm.mode" @change="handleModeChange">
            <el-radio-button label="auto">系统分配房间</el-radio-button>
            <el-radio-button label="manual">自主选择房间</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="bookForm.mode === 'manual'" label="选择房间">
          <el-select
            v-model="bookForm.roomId"
            placeholder="请选择房间号"
            style="width: 100%"
            :loading="roomsLoading"
            no-data-text="暂无空闲房间"
          >
            <el-option
              v-for="room in availableRooms"
              :key="room.id"
              :label="room.displayName || (room.roomNo + '房')"
              :value="room.id"
            />
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住日期">
              <el-date-picker
                v-model="bookForm.startDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租期 (月)">
              <el-input-number
                v-model="bookForm.leaseMonths"
                :min="apartment?.minLeaseMonths || 1"
                :max="apartment?.maxLeaseMonths || 36"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="tips" v-if="apartment">
          <el-icon><Warning /></el-icon> 该公寓要求最短租期 <b>{{ apartment.minLeaseMonths }}</b> 个月
        </div>

      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBooking" :loading="submitLoading">提交订单</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
// 【修复】删除了未使用的 MagicStick 和 Pointer
import { Location, Timer, Money, Warning } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const apartmentId = route.params.id

const loading = ref(true)
const apartment = ref(null)
const roomTypes = ref([])

// 预定弹窗数据
const dialogVisible = ref(false)
const submitLoading = ref(false)
const roomsLoading = ref(false)
const currentRoomType = ref(null)
const availableRooms = ref([]) // 存储过滤后的空闲房间

const bookForm = reactive({
  mode: 'auto', // 'auto' (系统分配) | 'manual' (自主选房)
  roomId: null,
  startDate: '',
  leaseMonths: 1
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  if (!apartmentId) return
  loading.value = true

  try {
    // 1. 获取公寓详情
    const aptRes = await axios.get(`/api/apartments/${apartmentId}`)
    if (aptRes.data.code === 200) {
      apartment.value = aptRes.data.data
    }

    // 2. 获取房型列表
    const rtRes = await axios.get(`/api/room-types/apartment/${apartmentId}`)

    let list = []
    if (Array.isArray(rtRes.data)) {
      list = rtRes.data
    } else if (rtRes.data && (rtRes.data.code === 200 || rtRes.data.code === 0)) {
      list = rtRes.data.data || []
    } else if (rtRes.data && Array.isArray(rtRes.data.data)) {
      list = rtRes.data.data
    }

    // 处理价格字段：分 -> 元
    roomTypes.value = list.map(item => ({
      ...item,
      price: item.rentCent ? item.rentCent / 100 : '暂无'
    }))

  } catch (error) {
    console.error(error)
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

// === 预定逻辑 ===

// 1. 打开弹窗
const openBookDialog = (rt) => {
  // 检查是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessageBox.confirm('您需要登录后才能预定，是否立即登录？', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '再看看',
      type: 'warning'
    }).then(() => {
      router.push('/login')
    }).catch(() => {})
    return
  }

  currentRoomType.value = rt

  // 重置表单状态
  bookForm.mode = 'auto'
  bookForm.roomId = null
  bookForm.startDate = ''
  // 默认租期：取公寓要求的最低租期，没有则默认为1
  if (apartment.value && apartment.value.minLeaseMonths) {
    bookForm.leaseMonths = apartment.value.minLeaseMonths
  } else {
    bookForm.leaseMonths = 1
  }

  dialogVisible.value = true
}

// 2. 切换模式：如果选了"自主选房"，需要去后端查所有房间并筛选空闲的
const handleModeChange = async (val) => {
  if (val === 'manual' && currentRoomType.value) {
    await fetchAndFilterRooms(currentRoomType.value.id)
  }
}

// 3. 获取并筛选空闲房间
const fetchAndFilterRooms = async (roomTypeId) => {
  roomsLoading.value = true
  availableRooms.value = [] // 清空旧数据
  try {
    // 调用获取所有房间的接口
    const res = await axios.get(`/api/rooms/room-types/${roomTypeId}`)

    let allRooms = []
    if (res.data.code === 200) {
      allRooms = res.data.data || []
    } else if (Array.isArray(res.data)) {
      allRooms = res.data
    }

    // 【关键】前端过滤：rentStatus === 0 (空置) 且 status === 1 (可用)
    availableRooms.value = allRooms.filter(r => r.rentStatus === 0 && r.status === 1)

  } catch (e) {
    console.warn('加载房间列表失败', e)
    // 不弹窗报错，可能只是该房型还没录入房间
  } finally {
    roomsLoading.value = false
  }
}

// 4. 禁止选择过去日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁止选择今天之前的日期
}

// 5. 提交订单
// src/views/ApartmentDetailView.vue

// 提交预定并自动跳转支付
const submitBooking = async () => {
  // 1. 表单校验
  if (!bookForm.startDate) return ElMessage.warning('请选择入住日期')
  if (bookForm.mode === 'manual' && !bookForm.roomId) return ElMessage.warning('请选择具体的房间')

  submitLoading.value = true
  try {
    const token = localStorage.getItem('token')
    const headers = { 'Authorization': `Bearer ${token}` }

    // 2. 准备金额数据 (前端计算继承逻辑)
    let finalRent = 0
    let finalDeposit = 0
    const rtRent = currentRoomType.value.rentCent || 0
    const rtDeposit = currentRoomType.value.depositCent || 0

    if (bookForm.mode === 'manual') {
      const selectedRoom = availableRooms.value.find(r => r.id === bookForm.roomId)
      if (selectedRoom) {
        finalRent = selectedRoom.rentCent ? selectedRoom.rentCent : rtRent
        finalDeposit = selectedRoom.depositCent ? selectedRoom.depositCent : rtDeposit
      } else {
        finalRent = rtRent
        finalDeposit = rtDeposit
      }
    } else {
      finalRent = rtRent
      finalDeposit = rtDeposit
    }

    // 3. 构造预定请求参数
    const payload = {
      startDate: bookForm.startDate,
      leaseMonths: bookForm.leaseMonths,
      rentCent: finalRent,
      depositCent: finalDeposit
    }

    let url = ''
    if (bookForm.mode === 'auto') {
      url = `/api/room-booking/room-types/${currentRoomType.value.id}`
    } else {
      url = `/api/room-booking/rooms/${bookForm.roomId}`
    }

    // === 第一步：发起预定请求 ===
    // console.log('正在提交预定...')
    const bookRes = await axios.post(url, payload, { headers })

    if (bookRes.data.code === 200) {
      // 预定成功！获取 bookingId
      const bookingData = bookRes.data.data
      const newBookingId = bookingData.id

      if (!newBookingId) {
        ElMessage.success('预定成功，请前往“我的预定”查看')
        dialogVisible.value = false
        return
      }

      ElMessage.success('预定提交成功，正在创建支付订单...')

      // === 第二步：自动发起支付准备 (Prepare) ===
      try {
        const payRes = await axios.post('/api/payment/prepare', {
          bookingId: newBookingId,
          paymentMethod: 'alipay'
        })

        if (payRes.data.code === 200 && payRes.data.data) {
          const payData = payRes.data.data
          const targetPaymentId = payData.paymentId // 获取 PAY-xxx

          if (targetPaymentId) {
            // === 第三步：跳转支付收银台 ===
            dialogVisible.value = false // 关闭弹窗
            router.push({
              path: '/payment',
              query: {
                paymentId: targetPaymentId
              }
            })
          } else {
            // 有响应但没ID
            throw new Error('支付单号获取失败')
          }
        } else {
          throw new Error(payRes.data.message || '支付服务异常')
        }
      } catch (payErr) {
        // 如果预定成功但支付创建失败，降级处理：提示用户去列表页支付
        console.error('自动跳转支付失败:', payErr)
        ElMessage.warning('预定已成功，但自动跳转支付失败。请在“我的预定”中手动支付。')
        dialogVisible.value = false
        // 也可以选择跳转到列表页
        // router.push('/my-bookings')
      }

    } else {
      ElMessage.error(bookRes.data.message || '预定失败')
    }
  } catch (error) {
    console.error(error)
    const msg = error.response?.data?.message || error.message || '请求出错'
    ElMessage.error(msg)
  } finally {
    submitLoading.value = false
  }
}

// 抽离出的公共跳转支付逻辑
// const createPaymentAndRedirect = async (bookingId) => {
//   try {
//     // 调用 POST /payment/prepare
//     const payRes = await axios.post('/api/payment/prepare', {
//       bookingId: Number(bookingId),
//       paymentMethod: 'alipay'
//     })

//     if (payRes.data.code === 200) {
//       const payData = payRes.data.data
//       // 假设 payData 包含: { id: 1, orderNo: 'P2023...', amount: 10000 (分) }

//       // 3. 跳转到支付页，通过 URL 传参
//       dialogVisible.value = false
//       router.push({
//         path: '/payment',
//         query: {
//           bookingId: bookingId,
//           paymentId: payData.id,
//           orderNo: payData.orderNo || 'PAY-' + Date.now(), // 防止后端没传
//           amount: payData.amount ? payData.amount / 100 : 0 // 分转元
//         }
//       })
//     } else {
//       ElMessage.error(payRes.data.message || '创建支付订单失败')
//       // 即使支付创建失败，预定其实已经成功了，跳转到列表页
//       router.push('/my-bookings')
//     }
//   } catch (e) {
//     console.error(e)
//     ElMessage.error('支付系统繁忙，请稍后在订单列表中支付')
//     router.push('/my-bookings')
//   }
// }

const resetForm = () => {
  bookForm.roomId = null
  availableRooms.value = []
}
</script>

<style scoped>
/* 样式部分保持一致 */
.detail-container { max-width: 1200px; margin: 0 auto; padding: 20px 24px 60px; min-height: 80vh; background-color: #f5f7fa; }
.breadcrumb-area { margin-bottom: 20px; }
.overview-card { background: #fff; border-radius: 16px; padding: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); margin-bottom: 40px; }
.main-image-box { height: 420px; border-radius: 12px; overflow: hidden; box-shadow: 0 8px 16px rgba(0,0,0,0.1); }
.main-image { width: 100%; height: 100%; }
.info-content { height: 100%; display: flex; flex-direction: column; padding-left: 10px; }
.apt-name { font-size: 30px; font-weight: 800; color: #303133; margin: 0 0 16px; line-height: 1.3; }
.tags-row { margin-bottom: 24px; display: flex; gap: 8px; }
.info-list { display: flex; flex-direction: column; gap: 14px; margin-bottom: 24px; }
.info-item { display: flex; align-items: center; gap: 10px; font-size: 15px; color: #606266; }
.desc-box { background: #f9fafc; padding: 16px; border-radius: 8px; flex: 1; }
.desc-box h4 { margin: 0 0 8px; font-size: 14px; color: #303133; }
.desc-box p { margin: 0; font-size: 13px; color: #666; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 4; -webkit-box-orient: vertical; overflow: hidden; }
.room-section .section-header { margin-bottom: 20px; border-bottom: 1px solid #ebeef5; padding-bottom: 15px; }
.room-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 24px; }
.room-card { background: #fff; border-radius: 12px; overflow: hidden; border: 1px solid #ebeef5; transition: all 0.3s ease; display: flex; flex-direction: column; }
.room-card:hover { transform: translateY(-5px); box-shadow: 0 12px 24px rgba(0,0,0,0.08); border-color: #dcdfe6; }
.room-img-box { height: 200px; overflow: hidden; }
.room-img { width: 100%; height: 100%; transition: transform 0.5s; }
.room-img-box:hover .room-img { transform: scale(1.05); }
.room-content { padding: 16px; flex: 1; display: flex; flex-direction: column; }
.room-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; }
.room-name { margin: 0; font-size: 18px; color: #303133; font-weight: 700; }
.room-price { color: #f56c6c; font-size: 20px; font-weight: 700; }
.room-price .unit { font-size: 12px; color: #999; font-weight: 400; }
.room-desc { font-size: 13px; color: #909399; margin: 0 0 20px; flex: 1; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.book-btn { width: 100%; }

/* 弹窗提示条 */
.tips {
  margin-top: 10px;
  font-size: 12px;
  color: #e6a23c;
  background: #fdf6ec;
  padding: 8px 12px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>
