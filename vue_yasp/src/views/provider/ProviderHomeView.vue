<template>
  <div class="dashboard-container">

    <el-row :gutter="24" class="panel-group">
      <el-col :span="8">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-house">
            <el-icon><House /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">房源总数</div>
            <div class="card-panel-num">{{ apartments.length }} <span class="unit">套</span></div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-order">
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">待处理订单</div>
            <div class="card-panel-num">0 <span class="unit">单</span></div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-money">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">本月营收</div>
            <div class="card-panel-num">¥0.00</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="action-bar">
      <div class="bar-title">常用功能</div>
      <div class="bar-btns">
        <el-button type="primary" size="large" @click="handlePublish">
          <el-icon style="margin-right: 6px"><Plus /></el-icon> 发布新房源
        </el-button>
        <el-button size="large" plain @click="fetchMyApartments">刷新列表</el-button>
      </div>
    </div>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-text">我管理的公寓</span>
            <el-tag type="info" effect="plain" round size="small">{{ apartments.length }} 套</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="apartments"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f8f9fa', color: '#606266', fontWeight: '600' }"
        row-key="id"
      >
        <el-table-column prop="id" label="内部ID" width="80" align="center" sortable />

        <el-table-column label="封面图" width="100">
          <template #default="scope">
            <div class="img-wrapper">
              <el-image
                class="table-img"
                :src="scope.row.coverUrl || 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267'"
                fit="cover"
                preview-teleported
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="公寓名称" min-width="150" show-overflow-tooltip />

        <el-table-column label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag
              effect="dark"
              :type="scope.row.publishStatus === 1 ? 'success' : 'info'"
              size="small"
              round
            >
              {{ scope.row.publishStatus === 1 ? '已上架' : '未上架' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="300" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="$router.push(`/provider/apartment/${scope.row.id}`)">
              <el-icon style="margin-right: 2px"><Document /></el-icon> 详情
            </el-button>

            <el-button size="small" type="warning" plain @click="handleEdit(scope.row)">
              <el-icon style="margin-right: 2px"><Edit /></el-icon> 编辑
            </el-button>

            <el-button size="small" type="success" plain @click="handleAddRoomType(scope.row)">
             <el-icon style="margin-right: 2px"><Plus /></el-icon> 房型
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editDialogVisible" title="编辑公寓详情" width="600px" destroy-on-close>
      <el-form :model="editForm" label-width="100px" v-loading="editLoading">
        <el-form-item label="公寓名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="editForm.address" /></el-form-item>
        <el-form-item label="所在城市"><el-input v-model="editForm.cityCode" /></el-form-item>
        <el-form-item label="租金范围 (元)">
          <el-row :gutter="10">
            <el-col :span="11"><el-input v-model="editForm.minPrice" type="number" /></el-col>
            <el-col :span="2" style="text-align: center">-</el-col>
            <el-col :span="11"><el-input v-model="editForm.maxPrice" type="number" /></el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="租期要求 (月)">
          <el-row :gutter="10">
            <el-col :span="11"><el-input-number v-model="editForm.minLeaseMonths" :min="1" style="width: 100%" /></el-col>
            <el-col :span="2" style="text-align: center">-</el-col>
            <el-col :span="11"><el-input-number v-model="editForm.maxLeaseMonths" :min="1" style="width: 100%" /></el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="封面图URL"><el-input v-model="editForm.coverUrl" /></el-form-item>
        <el-form-item label="简介描述"><el-input v-model="editForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="上架状态">
           <el-switch v-model="editForm.publishStatus" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpdate" :loading="submitLoading">保存修改</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="roomTypeDialogVisible"
      title="新增房型"
      width="650px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <div v-if="currentApartment" style="margin-bottom: 20px;">
        <el-alert :title="`正在为【${currentApartment.name}】添加房型`" type="success" :closable="false" show-icon />
      </div>

      <el-form :model="rtForm" label-width="100px" status-icon>
        <el-form-item label="房型名称" required>
          <el-input v-model="rtForm.name" placeholder="例如：豪华单间、两室一厅" />
        </el-form-item>

        <el-form-item label="户型布局" required>
          <el-row :gutter="10">
            <el-col :span="6">
               <el-input-number v-model="rtForm.bedroomCount" :min="0" size="small" controls-position="right" style="width: 100%" />
               <div class="sub-label">室</div>
            </el-col>
            <el-col :span="6">
               <el-input-number v-model="rtForm.livingCount" :min="0" size="small" controls-position="right" style="width: 100%" />
               <div class="sub-label">厅</div>
            </el-col>
            <el-col :span="6">
               <el-input-number v-model="rtForm.bathroomCount" :min="0" size="small" controls-position="right" style="width: 100%" />
               <div class="sub-label">卫</div>
            </el-col>
            <el-col :span="6">
               <el-input-number v-model="rtForm.kitchenCount" :min="0" size="small" controls-position="right" style="width: 100%" />
               <div class="sub-label">厨</div>
            </el-col>
          </el-row>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面积 (㎡)" required>
              <el-input v-model="rtForm.areaSqm" type="number" placeholder="如: 35.5" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="朝向">
              <el-select v-model="rtForm.orientation" placeholder="选择朝向" style="width: 100%">
                <el-option label="东" :value="1" />
                <el-option label="南" :value="2" />
                <el-option label="西" :value="3" />
                <el-option label="北" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="租金 (元)" required>
              <el-input v-model="rtForm.rentPrice" type="number" placeholder="每月租金" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="押金 (元)" required>
              <el-input v-model="rtForm.depositPrice" type="number" placeholder="押金金额" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="床铺数">
               <el-input-number v-model="rtForm.bedCount" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宜居人数">
               <el-input-number v-model="rtForm.capacity" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="房型设施">
          <el-checkbox v-model="rtForm.hasWindow">有窗户</el-checkbox>
          <el-checkbox v-model="rtForm.hasBalcony">有阳台</el-checkbox>
        </el-form-item>

        <el-form-item label="房型描述">
          <el-input v-model="rtForm.description" type="textarea" :rows="3" placeholder="介绍一下这个房型的亮点..." />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roomTypeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRoomType" :loading="rtSubmitLoading">确认创建</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { House, Tickets, Wallet, Plus, Edit, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const apartments = ref([])

// 编辑公寓相关状态
const editDialogVisible = ref(false)
const editLoading = ref(false)
const submitLoading = ref(false)
const editForm = reactive({
  id: null, name: '', address: '', cityCode: '',
  minPrice: '', maxPrice: '', minLeaseMonths: 1, maxLeaseMonths: 12,
  coverUrl: '', description: '', publishStatus: 1
})

// 【新增】房型相关状态
const roomTypeDialogVisible = ref(false)
const rtSubmitLoading = ref(false)
const currentApartment = ref(null) // 当前选中的公寓对象
const rtForm = reactive({
  name: '',
  bedroomCount: 1,
  livingCount: 0,
  bathroomCount: 1,
  kitchenCount: 0,
  areaSqm: '',
  bedCount: 1,
  capacity: 1,
  rentPrice: '',    // 显示用（元）
  depositPrice: '', // 显示用（元）
  hasWindow: true,  // boolean, 提交时转 1/0
  hasBalcony: false,// boolean, 提交时转 1/0
  orientation: 2,   // 默认南
  description: ''
})

onMounted(() => {
  fetchMyApartments()
})

const fetchMyApartments = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const providerName = localStorage.getItem('providerName')
    if (!providerName) {
      loading.value = false
      return
    }
    const res = await axios.get(`/api/apartments/provider/${providerName}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.data.code === 200) {
      let list = res.data.data || []
      list.sort((a, b) => a.id - b.id)
      apartments.value = list
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleEdit = async (row) => {
  editDialogVisible.value = true
  editLoading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get(`/api/apartments/${row.id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.data.code === 200) {
      const data = res.data.data
      editForm.id = data.id
      editForm.name = data.name
      editForm.address = data.address
      editForm.cityCode = data.cityCode
      editForm.minPrice = data.rentMinCent ? data.rentMinCent / 100 : ''
      editForm.maxPrice = data.rentMaxCent ? data.rentMaxCent / 100 : ''
      editForm.minLeaseMonths = data.minLeaseMonths
      editForm.maxLeaseMonths = data.maxLeaseMonths
      editForm.coverUrl = data.coverUrl
      editForm.description = data.description
      editForm.publishStatus = data.publishStatus
    }
  } catch (e) {
    ElMessage.error('获取详情失败', e)
  } finally {
    editLoading.value = false
  }
}

const submitUpdate = async () => {
  submitLoading.value = true
  try {
    const token = localStorage.getItem('token')
    const payload = {
      ...editForm,
      rentMinCent: editForm.minPrice ? Number(editForm.minPrice) * 100 : 0,
      rentMaxCent: editForm.maxPrice ? Number(editForm.maxPrice) * 100 : 0
    }
    const res = await axios.put(`/api/apartments/${editForm.id}`, payload, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.data.code === 200) {
      ElMessage.success('修改成功')
      editDialogVisible.value = false
      fetchMyApartments()
    } else {
      ElMessage.error(res.data.message || '修改失败')
    }
  } catch (e) {
    ElMessage.error('请求出错', e)
  } finally {
    submitLoading.value = false
  }
}

// === 【新增】房型相关逻辑 ===

// 1. 点击“加房型”按钮
const handleAddRoomType = (row) => {
  currentApartment.value = row
  // 重置表单
  rtForm.name = ''
  rtForm.bedroomCount = 1
  rtForm.livingCount = 0
  rtForm.bathroomCount = 1
  rtForm.kitchenCount = 0
  rtForm.areaSqm = ''
  rtForm.bedCount = 1
  rtForm.capacity = 1
  rtForm.rentPrice = ''
  rtForm.depositPrice = ''
  rtForm.hasWindow = true
  rtForm.hasBalcony = false
  rtForm.orientation = 2
  rtForm.description = ''

  roomTypeDialogVisible.value = true
}

// 2. 提交房型
const submitRoomType = async () => {
  // 简单校验
  if (!rtForm.name || !rtForm.areaSqm || !rtForm.rentPrice) {
    ElMessage.warning('请补全房型名称、面积和租金信息')
    return
  }

  rtSubmitLoading.value = true
  try {
    const token = localStorage.getItem('token')

    // 构造请求体，匹配后端 DTO 格式
    const payload = {
      apartmentId: currentApartment.value.id, // 关联当前公寓
      name: rtForm.name,
      bedroomCount: rtForm.bedroomCount,
      livingCount: rtForm.livingCount,
      bathroomCount: rtForm.bathroomCount,
      kitchenCount: rtForm.kitchenCount,
      areaSqm: rtForm.areaSqm.toString(), // 转字符串
      bedCount: rtForm.bedCount,
      capacity: rtForm.capacity,
      // 金额：元 -> 分
      rentCent: Number(rtForm.rentPrice) * 100,
      depositCent: Number(rtForm.depositPrice) * 100,
      // Boolean -> 1/0
      hasWindow: rtForm.hasWindow ? 1 : 0,
      hasBalcony: rtForm.hasBalcony ? 1 : 0,
      orientation: rtForm.orientation,
      description: rtForm.description
    }

    // 发送 POST 请求
    const res = await axios.post('/api/room-types', payload, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (res.data.code === 200) {
      ElMessage.success('创建房型成功！')
      roomTypeDialogVisible.value = false
    } else {
      ElMessage.error(res.data.message || '创建失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求错误')
  } finally {
    rtSubmitLoading.value = false
  }
}

const handlePublish = () => {
  router.push('/provider/apartments/publish')
}
</script>

<style scoped>
/* 原有样式保持不变 */
.dashboard-container { padding: 0; }
.panel-group { margin-bottom: 24px; }
.card-panel { height: 108px; background: #fff; border-radius: 8px; display: flex; align-items: center; padding: 0 24px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); transition: all 0.3s ease; cursor: pointer; border: 1px solid #ebeef5; }
.card-panel:hover { transform: translateY(-4px); box-shadow: 0 8px 20px 0 rgba(0, 0, 0, 0.1); }
.card-panel-icon-wrapper { width: 64px; height: 64px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 32px; }
.icon-house { color: #fff; background: linear-gradient(135deg, #69c0ff 0%, #1890ff 100%); }
.icon-order { color: #fff; background: linear-gradient(135deg, #ffd666 0%, #faad14 100%); }
.icon-money { color: #fff; background: linear-gradient(135deg, #ff85c0 0%, #eb2f96 100%); }
.card-panel-description { flex: 1; text-align: right; margin-left: 20px; }
.card-panel-text { color: #909399; font-size: 14px; margin-bottom: 8px; }
.card-panel-num { font-size: 28px; font-weight: 700; color: #303133; }
.unit { font-size: 14px; font-weight: 400; color: #909399; }
.action-bar { background: #fff; padding: 20px 24px; border-radius: 8px; margin-bottom: 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); }
.bar-title { font-size: 16px; font-weight: 600; color: #303133; border-left: 4px solid #409eff; padding-left: 12px; }
.bar-btns { display: flex; gap: 12px; }
.table-card { border: none; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.title-text { font-size: 16px; font-weight: 600; color: #303133; margin-right: 12px; }
.img-wrapper { width: 80px; height: 50px; border-radius: 4px; overflow: hidden; border: 1px solid #f0f2f5; background: #f5f7fa; }
.table-img { width: 100%; height: 100%; }

/* 新增的微调样式 */
.sub-label {
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
