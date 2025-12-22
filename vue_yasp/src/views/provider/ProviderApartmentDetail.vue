<template>
  <div class="detail-container" v-loading="loading">

    <div class="page-header" v-if="apartment">
      <div class="header-left">
        <el-button link @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回列表
        </el-button>
        <el-divider direction="vertical" />
        <span class="page-title">{{ apartment.name }}</span>
        <el-tag v-if="apartment.publishStatus === 1" type="success" effect="dark" size="small" class="ml-2">已上架</el-tag>
        <el-tag v-else type="info" effect="dark" size="small" class="ml-2">未上架</el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" plain @click="handleEditApartment">
          <el-icon style="margin-right: 5px"><Edit /></el-icon> 编辑公寓信息
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="custom-tabs" type="border-card">

      <el-tab-pane label="房型与房间管理" name="rooms">
        <div class="tab-content">
          <div class="action-bar">
            <span class="sub-title">房型列表 ({{ roomTypes.length }})</span>
            <el-button type="success" @click="openRoomTypeDialog('create')">
              <el-icon style="margin-right: 5px"><Plus /></el-icon> 新增房型
            </el-button>
          </div>

          <el-empty v-if="roomTypes.length === 0" description="暂无房型，请先添加" />

          <div v-else class="room-type-list">
            <el-card v-for="rt in roomTypes" :key="rt.id" class="rt-card" shadow="hover">
              <template #header>
                <div class="rt-header">
                  <div class="rt-info">
                    <span class="rt-name">{{ rt.name }}</span>
                    <el-tag size="small" effect="plain" type="warning">
                      ¥{{ rt.rentCent ? rt.rentCent / 100 : 0 }} /月
                    </el-tag>
                    <span class="rt-desc">
                      {{ rt.bedroomCount }}室{{ rt.livingCount }}厅 · {{ rt.areaSqm }}㎡
                    </span>
                  </div>
                  <div class="rt-actions">
                    <el-button type="primary" link @click="openRoomTypeDialog('edit', rt)">
                      <el-icon><Edit /></el-icon> 修改
                    </el-button>
                    <el-divider direction="vertical" />
                    <el-button type="primary" size="small" @click="openAddRoomDialog(rt)">
                      <el-icon style="margin-right: 4px"><Key /></el-icon> 添加房间
                    </el-button>
                  </div>
                </div>
              </template>

              <div class="rt-body">
                <div class="rt-spec">
                  <el-tag size="small" type="info">押金: ¥{{ rt.depositCent ? rt.depositCent / 100 : 0 }}</el-tag>
                  <el-tag size="small" type="info">朝向: {{ getOrientationText(rt.orientation) }}</el-tag>
                  <el-tag size="small" type="info" v-if="rt.hasBalcony === 1">有阳台</el-tag>
                  <el-tag size="small" type="info" v-if="rt.hasWindow === 1">有窗</el-tag>
                </div>
                <div class="rt-desc-text">{{ rt.description || '暂无描述' }}</div>

                <el-divider border-style="dashed" style="margin: 15px 0;" />

                <div class="rooms-container">
                  <div v-if="rt.loadingRooms" class="loading-rooms">
                    <el-icon class="is-loading"><Loading /></el-icon> 加载房间中...
                  </div>

                  <div v-else-if="!rt.rooms || rt.rooms.length === 0" class="no-rooms-tip">
                    暂无具体房间数据，请点击右上角添加
                  </div>

                  <div v-else class="room-list-wrapper">
                    <div class="rooms-header-row">
                      <div class="stat-info">
                        <span class="count-label">共 {{ rt.rooms.length }} 间</span>
                        <span class="tip-text">(点击房间号可修改)</span>
                      </div>
                      <div class="legend-bar">
                        <div class="legend-item"><span class="dot success"></span>空置</div>
                        <div class="legend-item"><span class="dot primary"></span>预订</div>
                        <div class="legend-item"><span class="dot danger"></span>出租</div>
                        <div class="legend-item"><span class="dot info"></span>下架</div>
                      </div>
                    </div>

                    <div class="room-matrix">
                      <el-tooltip
                        v-for="room in rt.rooms"
                        :key="room.id"
                        placement="top"
                        :show-after="500"
                      >
                        <template #content>
                          展示名: {{ room.displayName || room.roomNo }}<br/>
                          状态: {{ getRentStatusText(room.rentStatus) }}<br/>
                          <span v-if="room.rentCent" style="color:#e6a23c">租金: ¥{{room.rentCent/100}} (独立设置)</span>
                          <span v-else>租金: 继承房型</span>
                        </template>
                        <div
                          class="room-box"
                          :class="getRentStatusClass(room.rentStatus)"
                          @click="openEditRoomDialog(room, rt)"
                        >
                          <span class="room-no">{{ room.roomNo }}</span>
                          <span class="room-status-icon"></span>
                        </div>
                      </el-tooltip>
                    </div>
                  </div>
                </div>

              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="公寓基础信息" name="info">
        <el-descriptions border :column="2" class="mt-20">
          <el-descriptions-item label="公寓名称">{{ apartment.name }}</el-descriptions-item>
          <el-descriptions-item label="所在地区">{{ apartment.cityCode }} ({{ apartment.districtCode }})</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ apartment.address }}</el-descriptions-item>
          <el-descriptions-item label="租金范围">
             ¥{{ apartment.rentMinCent ? apartment.rentMinCent/100 : 0 }} -
             {{ apartment.rentMaxCent ? apartment.rentMaxCent/100 : 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="租期要求">{{ apartment.minLeaseMonths }} - {{ apartment.maxLeaseMonths }} 个月</el-descriptions-item>
          <el-descriptions-item label="简介" :span="2">{{ apartment.description }}</el-descriptions-item>
          <el-descriptions-item label="封面图" :span="2">
            <el-image :src="apartment.coverUrl" style="width: 200px; border-radius: 4px;" preview-teleported />
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="editAptDialog.visible" title="编辑公寓信息" width="600px" destroy-on-close>
      <el-form :model="editAptDialog.form" label-width="100px">
        <el-form-item label="公寓名称"><el-input v-model="editAptDialog.form.name" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="editAptDialog.form.address" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="editAptDialog.form.coverUrl" /></el-form-item>
        <el-form-item label="简介描述"><el-input v-model="editAptDialog.form.description" type="textarea" /></el-form-item>
        <el-form-item label="租期(月)">
           <el-col :span="11"><el-input-number v-model="editAptDialog.form.minLeaseMonths" :min="1" style="width:100%" /></el-col>
           <el-col :span="2" align="center">-</el-col>
           <el-col :span="11"><el-input-number v-model="editAptDialog.form.maxLeaseMonths" :min="1" style="width:100%" /></el-col>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editAptDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAptEdit" :loading="loadingSubmit">保存修改</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="rtDialog.visible"
      :title="rtDialog.mode === 'create' ? '新增房型' : '修改房型信息'"
      width="700px"
      destroy-on-close
    >
      <el-form :model="rtDialog.form" label-width="100px" ref="rtFormRef">
        <el-form-item label="房型名称" required><el-input v-model="rtDialog.form.name" /></el-form-item>
        <el-form-item label="户型布局">
          <el-row :gutter="10">
            <el-col :span="6"><el-input-number v-model="rtDialog.form.bedroomCount" :min="1" style="width: 100%" /><div class="sub-label">室</div></el-col>
            <el-col :span="6"><el-input-number v-model="rtDialog.form.livingCount" :min="0" style="width: 100%" /><div class="sub-label">厅</div></el-col>
            <el-col :span="6"><el-input-number v-model="rtDialog.form.bathroomCount" :min="0" style="width: 100%" /><div class="sub-label">卫</div></el-col>
            <el-col :span="6"><el-input-number v-model="rtDialog.form.kitchenCount" :min="0" style="width: 100%" /><div class="sub-label">厨</div></el-col>
          </el-row>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="租金 (元)" required><el-input v-model="rtDialog.form.rentPrice" type="number" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="押金 (元)" required><el-input v-model="rtDialog.form.depositPrice" type="number" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="面积 (㎡)" required><el-input v-model="rtDialog.form.areaSqm" type="number" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="朝向">
              <el-select v-model="rtDialog.form.orientation" placeholder="选择朝向" style="width: 100%">
                <el-option label="东" :value="1" /><el-option label="南" :value="2" />
                <el-option label="西" :value="3" /><el-option label="北" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="房型设施">
          <el-checkbox v-model="rtDialog.form.hasWindow">有窗户</el-checkbox>
          <el-checkbox v-model="rtDialog.form.hasBalcony">有阳台</el-checkbox>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="rtDialog.form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rtDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitRoomType" :loading="loadingSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="roomDialog.visible"
      :title="roomDialog.mode === 'edit' ? '编辑房间' : '添加房间'"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentRoomType" class="mb-20">
        正在管理房型: <el-tag>{{ currentRoomType.name }}</el-tag>
      </div>

      <div v-if="roomDialog.mode === 'edit'">
        <el-form :model="roomDialog.editForm" label-width="120px">
          <el-form-item label="展示名称">
            <el-input v-model="roomDialog.editForm.displayName" placeholder="如: A-201" />
          </el-form-item>
          <el-form-item label="当前状态">
            <el-select v-model="roomDialog.editForm.rentStatus" style="width: 100%">
              <el-option label="空置" :value="0" />
              <el-option label="待确认" :value="1" />
              <el-option label="预订" :value="2" />
              <el-option label="出租" :value="3" />
              <el-option label="下架" :value="4" />
            </el-select>
          </el-form-item>

          <el-divider content-position="left">特殊属性 (留空则自动填充房型数据)</el-divider>

          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="租金 (元)">
                <el-input v-model="roomDialog.editForm.rentPrice" :placeholder="`默认: ${currentRoomType.rentCent/100}`" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="押金 (元)">
                <el-input v-model="roomDialog.editForm.depositPrice" :placeholder="`默认: ${currentRoomType.depositCent/100}`" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="面积 (㎡)">
                <el-input v-model="roomDialog.editForm.areaSqm" :placeholder="`默认: ${currentRoomType.areaSqm}`" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="朝向">
                <el-select v-model="roomDialog.editForm.orientation" :placeholder="`默认: ${getOrientationText(currentRoomType.orientation)}`" style="width: 100%" clearable>
                  <el-option label="东" :value="1" /><el-option label="南" :value="2" />
                  <el-option label="西" :value="3" /><el-option label="北" :value="4" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <el-tabs v-else v-model="roomDialog.mode" type="card">
        <el-tab-pane label="单个添加" name="single">
          <el-form :model="roomDialog.singleForm" label-width="120px" style="margin-top: 15px;">
            <el-row :gutter="10">
              <el-col :span="12"><el-form-item label="楼栋号"><el-input v-model="roomDialog.singleForm.buildingNo" placeholder="如: A" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="单元号"><el-input v-model="roomDialog.singleForm.unitNo" placeholder="如: 1" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12"><el-form-item label="楼层"><el-input-number v-model="roomDialog.singleForm.floorNo" :min="-2" :max="100" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="房间号" required><el-input v-model="roomDialog.singleForm.roomNo" placeholder="如: 201" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="展示名称"><el-input v-model="roomDialog.singleForm.displayName" placeholder="为空则自动生成" /></el-form-item>

            <div style="font-size: 12px; color: #909399; margin-left: 120px;">
              * 租金、押金、面积等属性将自动继承房型数据
            </div>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="批量生成" name="batch">
          <el-alert title="批量生成同楼层连续房间号" type="info" show-icon :closable="false" class="mb-20" />
          <el-form :model="roomDialog.batchForm" label-width="120px" style="margin-top: 15px;">
            <el-row :gutter="10">
              <el-col :span="12"><el-form-item label="楼栋号" required><el-input v-model="roomDialog.batchForm.buildingNo" placeholder="如: A" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="单元号"><el-input v-model="roomDialog.batchForm.unitNo" placeholder="如: 1" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="楼层" required><el-input-number v-model="roomDialog.batchForm.floorNo" :min="-2" :max="100" /></el-form-item>
            <el-row :gutter="10">
              <el-col :span="12"><el-form-item label="起始房号" required><el-input v-model="roomDialog.batchForm.startNo" type="number" placeholder="201" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="生成数量" required><el-input-number v-model="roomDialog.batchForm.count" :min="1" :max="50" /></el-form-item></el-col>
            </el-row>

            <div class="preview-text" v-if="roomDialog.batchForm.startNo">
              预计生成: {{ roomDialog.batchForm.startNo }} ~ {{ Number(roomDialog.batchForm.startNo) + roomDialog.batchForm.count - 1 }}
            </div>
            <div style="font-size: 12px; color: #909399; margin-top: 10px; text-align: right;">
              * 所有生成房间将继承房型属性
            </div>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="roomDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleRoomSubmit" :loading="loadingSubmit">
          {{ roomDialog.mode === 'edit' ? '保存修改' : (roomDialog.mode === 'batch' ? '批量生成' : '确认添加') }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Edit, Plus, Key, Loading } from '@element-plus/icons-vue'

const route = useRoute()
const apartmentId = route.params.id

// Data
const activeTab = ref('rooms')
const loading = ref(false)
const loadingSubmit = ref(false)
const apartment = ref({})
const roomTypes = ref([])

// Dialog States
const editAptDialog = reactive({ visible: false, form: {} })
const rtDialog = reactive({ visible: false, mode: 'create', form: {} })
const currentRoomType = ref(null)

// Room Dialog
const roomDialog = reactive({
  visible: false,
  mode: 'single', // single | batch | edit
  singleForm: { buildingNo: '', unitNo: '', floorNo: 2, roomNo: '', displayName: '', rentPrice: '', depositPrice: '', areaSqm: '', orientation: null },
  batchForm: { buildingNo: '', unitNo: '', floorNo: 2, startNo: '201', count: 5, rentPrice: '', depositPrice: '', areaSqm: '', orientation: null },
  editForm: { id: null, displayName: '', rentStatus: 0, rentPrice: '', depositPrice: '', areaSqm: '', orientation: null }
})

onMounted(() => {
  loadData()
})

const getToken = () => localStorage.getItem('token')
const getHeaders = () => ({ 'Authorization': `Bearer ${getToken()}` })

const loadData = async () => {
  loading.value = true
  try {
    const aptRes = await axios.get(`/api/apartments/${apartmentId}`, { headers: getHeaders() })
    if (aptRes.data && aptRes.data.code === 200) apartment.value = aptRes.data.data

    const rtRes = await axios.get(`/api/room-types/apartment/${apartmentId}`, { headers: getHeaders() })
    let rts = []

    if (Array.isArray(rtRes.data)) {
        rts = rtRes.data
    } else if (rtRes.data && rtRes.data.code === 200) {
        rts = rtRes.data.data || []
    }

    rts.forEach(rt => {
      rt.rooms = []
      rt.loadingRooms = false
    })

    roomTypes.value = rts
    roomTypes.value.forEach(rt => refreshRoomsForType(rt))

  } catch (e) {
    console.error(e)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// Refresh rooms for a single room type
const refreshRoomsForType = async (rt) => {
  rt.loadingRooms = true
  try {
    const res = await axios.get(`/api/rooms/room-types/${rt.id}`, { headers: getHeaders() })
    if (res.data && res.data.code === 200) {
      rt.rooms = res.data.data || []
    } else {
      rt.rooms = []
    }
  } catch (e) {
    if (e.response && e.response.status === 400) {
      rt.rooms = []
    } else {
      console.warn(`加载房型 ${rt.id} 房间失败`, e)
      rt.rooms = []
    }
  } finally {
    rt.loadingRooms = false
  }
}

// === Edit Room ===
const openEditRoomDialog = (room, rt) => {
  currentRoomType.value = rt
  roomDialog.mode = 'edit'
  roomDialog.editForm = {
    id: room.id,
    displayName: room.displayName,
    rentStatus: room.rentStatus,
    // 如果是 null (继承)，表单显示为空；如果是数值，显示数值
    rentPrice: room.rentCent ? room.rentCent / 100 : '',
    depositPrice: room.depositCent ? room.depositCent / 100 : '',
    areaSqm: room.areaSqm || '',
    orientation: room.orientation
  }
  roomDialog.visible = true
}

// === Add Room ===
const openAddRoomDialog = (rt) => {
  currentRoomType.value = rt
  roomDialog.mode = 'single'
  roomDialog.visible = true
  // Reset forms, 默认值为空，代表继承
  roomDialog.singleForm = { buildingNo: '', unitNo: '', floorNo: 2, roomNo: '', displayName: '', rentPrice: '', depositPrice: '', areaSqm: '', orientation: null }
  roomDialog.batchForm = { buildingNo: '', unitNo: '', floorNo: 2, startNo: '201', count: 5, rentPrice: '', depositPrice: '', areaSqm: '', orientation: null }
}

// === Handle Room Submission ===
const handleRoomSubmit = async () => {
  loadingSubmit.value = true

  // 辅助函数：处理金额 (输入值 -> 分 or 房型值)
  // 逻辑：如果用户输入了值，则使用用户输入；如果用户留空，则使用 RoomType 的默认值
  const rt = currentRoomType.value
  const resolveMoney = (inputVal, defaultVal) => {
     if (inputVal !== '' && inputVal !== null && inputVal !== undefined) {
        return Number(inputVal) * 100
     }
     return defaultVal // defaultVal 本身就是分
  }

  // 辅助函数：处理普通数值 (面积、朝向)
  const resolveVal = (inputVal, defaultVal) => {
     if (inputVal !== '' && inputVal !== null && inputVal !== undefined) {
        return inputVal
     }
     return defaultVal
  }

  try {
    const rtId = rt.id

    // 1. 编辑房间
    if (roomDialog.mode === 'edit') {
      const f = roomDialog.editForm
      const payload = {
        id: f.id,
        displayName: f.displayName,
        rentStatus: f.rentStatus,
        // 这里如果是编辑，留空代表重置为继承（即传当前房型的值），或者您希望留空代表 NULL？
        // 根据您的要求“自动把这些数据带到请求体”，我们这里填入房型值
        rentCent: resolveMoney(f.rentPrice, rt.rentCent),
        depositCent: resolveMoney(f.depositPrice, rt.depositCent),
        areaSqm: resolveVal(f.areaSqm, rt.areaSqm),
        orientation: resolveVal(f.orientation, rt.orientation)
      }
      const res = await axios.put(`/api/rooms/${f.id}`, payload, { headers: getHeaders() })
      handleResponse(res)
    }

    // 2. 单个添加
    else if (roomDialog.mode === 'single') {
      const f = roomDialog.singleForm
      if (!f.roomNo) { ElMessage.warning('请输入房号'); loadingSubmit.value=false; return }

      const payload = {
        buildingNo: f.buildingNo, unitNo: f.unitNo, floorNo: f.floorNo, roomNo: f.roomNo,
        displayName: f.displayName || `${f.buildingNo || ''}${f.unitNo ? '-'+f.unitNo : ''} ${f.roomNo}`,
        status: 1, rentStatus: 0,
        // 自动带入房型数据
        rentCent: resolveMoney(f.rentPrice, rt.rentCent),
        depositCent: resolveMoney(f.depositPrice, rt.depositCent),
        areaSqm: resolveVal(f.areaSqm, rt.areaSqm),
        orientation: resolveVal(f.orientation, rt.orientation)
      }
      const res = await axios.post(`/api/rooms/room-types/${rtId}`, payload, { headers: getHeaders() })
      handleResponse(res)
    }

    // 3. 批量添加
    else {
      const f = roomDialog.batchForm
      if (!f.startNo || !f.buildingNo) { ElMessage.warning('请补全楼栋和起始房号'); loadingSubmit.value=false; return }

      const batchList = []
      const start = parseInt(f.startNo)

      // 预先计算好要填入的值
      const finalRentCent = resolveMoney(f.rentPrice, rt.rentCent)
      const finalDepositCent = resolveMoney(f.depositPrice, rt.depositCent)
      const finalAreaSqm = resolveVal(f.areaSqm, rt.areaSqm)
      const finalOrientation = resolveVal(f.orientation, rt.orientation)

      for (let i = 0; i < f.count; i++) {
        const currentRoomNo = String(start + i)
        batchList.push({
          buildingNo: f.buildingNo, unitNo: f.unitNo, floorNo: f.floorNo, roomNo: currentRoomNo,
          displayName: `${f.buildingNo}${f.unitNo ? '-'+f.unitNo : ''} ${currentRoomNo}`,
          status: 1, rentStatus: 0,
          rentCent: finalRentCent,
          depositCent: finalDepositCent,
          areaSqm: finalAreaSqm,
          orientation: finalOrientation
        })
      }
      const res = await axios.post(`/api/rooms/batch-create/room-types/${rtId}`, batchList, { headers: getHeaders() })
      handleResponse(res)
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('操作失败，请检查参数')
  } finally {
    loadingSubmit.value = false
  }
}

const handleResponse = (res) => {
  if (res.data.code === 200) {
    ElMessage.success('操作成功')
    roomDialog.visible = false
    refreshRoomsForType(currentRoomType.value)
  } else {
    ElMessage.error(res.data.message || '操作失败')
  }
}

// === Helpers ===
const getRentStatusClass = (status) => {
  const map = { 0: 'status-empty', 1: 'status-confirm', 2: 'status-book', 3: 'status-rent', 4: 'status-off' }
  return map[status] || 'status-off'
}
const getRentStatusText = (status) => {
  const map = { 0: '空置', 1: '待确认', 2: '预订', 3: '出租', 4: '下架' }
  return map[status] || '未知'
}
const getOrientationText = (val) => {
  const map = { 1:'东', 2:'南', 3:'西', 4:'北' }
  return map[val] || '未知'
}

// === Previous Logic ===
const handleEditApartment = () => { editAptDialog.form = { ...apartment.value }; editAptDialog.visible = true }
const submitAptEdit = async () => { loadingSubmit.value=true; try { const payload = { ...editAptDialog.form }; await axios.put(`/api/apartments/${payload.id}`, payload, { headers: getHeaders() }); ElMessage.success('修改成功'); editAptDialog.visible = false; loadData(); } catch (e) { ElMessage.error('请求出错', e) } finally { loadingSubmit.value = false } }
const openRoomTypeDialog = (mode, data) => { rtDialog.mode = mode; rtDialog.visible = true; if(mode==='create'){ rtDialog.form={name:'',rentPrice:'',depositPrice:'',areaSqm:'',bedroomCount:1,livingCount:0,bathroomCount:1,kitchenCount:0,hasWindow:true,hasBalcony:false,orientation:2,description:''} }else{ rtDialog.form={...data, rentPrice:data.rentCent/100, depositPrice:data.depositCent/100, hasWindow:data.hasWindow===1, hasBalcony:data.hasBalcony===1} } }
const submitRoomType = async () => { loadingSubmit.value=true; try { const form=rtDialog.form; const payload={...form, apartmentId:Number(apartmentId), rentCent:Number(form.rentPrice)*100, depositCent:Number(form.depositPrice)*100, hasWindow:form.hasWindow?1:0, hasBalcony:form.hasBalcony?1:0}; let res; if(rtDialog.mode==='create') res = await axios.post('/api/room-types', payload, {headers:getHeaders()}); else res = await axios.put(`/api/room-types/${form.id}`, payload, {headers:getHeaders()}); if(res.data.code===200){ElMessage.success('操作成功'); rtDialog.visible=false; loadData();}else{ElMessage.error(res.data.message)} } catch(e){ElMessage.error('失败', e)} finally{loadingSubmit.value=false} }
</script>

<style scoped>
.detail-container { padding: 24px; background-color: #f0f2f5; min-height: 100vh; }
.page-header { background: #fff; padding: 16px 24px; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,21,41,0.05); display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-left { display: flex; align-items: center; }
.page-title { font-size: 20px; font-weight: 700; color: #1f2d3d; margin-left: 12px; margin-right: 8px; }
.ml-2 { margin-left: 8px; }
.mt-20 { margin-top: 20px; }
.mb-20 { margin-bottom: 20px; }

/* Tabs */
.custom-tabs { background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,21,41,0.05); border: none; }
:deep(.el-tabs__header) { margin: 0; border-bottom: 1px solid #f0f2f5; padding: 0 20px; }
:deep(.el-tabs__content) { padding: 24px; }

/* Action Bar */
.action-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.sub-title { font-size: 16px; font-weight: 600; color: #303133; border-left: 4px solid #409eff; padding-left: 12px; }

/* Card Style */
.rt-card { margin-bottom: 24px; border: 1px solid #e4e7ed; border-radius: 8px; transition: all 0.3s; overflow: hidden; }
.rt-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); transform: translateY(-2px); }
:deep(.el-card__header) { padding: 15px 24px; background-color: #fafafa; border-bottom: 1px solid #ebeef5; }
.rt-header { display: flex; justify-content: space-between; align-items: center; }
.rt-info { display: flex; align-items: center; gap: 12px; }
.rt-name { font-size: 18px; font-weight: 700; color: #303133; }
.rt-desc { font-size: 13px; color: #909399; background: #fff; padding: 2px 8px; border-radius: 4px; border: 1px solid #ebeef5; }
.rt-actions { display: flex; align-items: center; }
.rt-body { padding: 8px 0; }
.rt-spec { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.rt-desc-text { font-size: 14px; color: #5e6d82; line-height: 1.6; margin-bottom: 20px; padding: 10px; background: #f9fafc; border-radius: 4px; }

/* Room Grid & Legend */
.rooms-container { border-top: 1px dashed #e0e0e0; padding-top: 20px; }
.rooms-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.count-label { font-size: 13px; font-weight: 600; color: #606266; }
.tip-text { font-size: 12px; color: #909399; margin-left: 8px; font-weight: normal; }
.legend-bar { display: flex; gap: 16px; font-size: 12px; color: #606266; background: #f4f4f5; padding: 4px 12px; border-radius: 12px; }
.legend-item { display: flex; align-items: center; gap: 4px; }
.dot { width: 8px; height: 8px; border-radius: 50%; }
.dot.success { background: #67c23a; }
.dot.primary { background: #409eff; }
.dot.danger { background: #f56c6c; }
.dot.info { background: #909399; }

.room-matrix { display: grid; grid-template-columns: repeat(auto-fill, minmax(80px, 1fr)); gap: 12px; }
.room-box { height: 40px; display: flex; align-items: center; justify-content: center; border-radius: 4px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; border: 1px solid transparent; position: relative; background: #fff; }
.room-box:hover { transform: scale(1.05); z-index: 1; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }

/* Status Styles */
.room-box.status-empty { background-color: #f0f9eb; border-color: #e1f3d8; color: #67c23a; }
.room-box.status-empty:hover { border-color: #67c23a; }
.room-box.status-book { background-color: #ecf5ff; border-color: #d9ecff; color: #409eff; }
.room-box.status-book:hover { border-color: #409eff; }
.room-box.status-rent { background-color: #fef0f0; border-color: #fde2e2; color: #f56c6c; }
.room-box.status-confirm { background-color: #fdf6ec; border-color: #faecd8; color: #e6a23c; }
.room-box.status-off { background-color: #f4f4f5; border-color: #e9e9eb; color: #909399; text-decoration: line-through; }

.loading-rooms, .no-rooms-tip { text-align: center; padding: 20px; color: #909399; }
.preview-text { font-size: 12px; color: #909399; margin-top: 5px; text-align: right; }
.sub-label { text-align: center; font-size: 12px; color: #909399; margin-top: 4px; }
</style>
