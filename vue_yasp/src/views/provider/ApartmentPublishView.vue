<template>
  <div class="publish-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">发布新房源</span>
          <el-button @click="$router.back()">返回列表</el-button>
        </div>
      </template>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="120px"
        v-loading="loading"
        status-icon
        size="large"
      >
        <div class="section-title">基本信息</div>

        <el-form-item label="公寓名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入公寓名称，如：蓝色港湾人才公寓" />
        </el-form-item>

        <el-form-item label="公寓类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">集中式公寓 (独栋)</el-radio>
            <el-radio :label="2">分散式公寓 (合租/整租)</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="封面图片" prop="coverUrl">
          <div class="cover-upload-wrapper">
            <el-upload
              class="cover-uploader"
              action="/api/upload"
              :show-file-list="false"
              :headers="uploadHeaders"
              :on-success="handleCoverSuccess"
              :before-upload="beforeCoverUpload"
            >
              <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-img" />
              <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>

              <div class="upload-mask" v-if="form.coverUrl">
                <span>点击更换图片</span>
              </div>
            </el-upload>

            <div class="url-input-box">
              <el-input
                v-model="form.coverUrl"
                placeholder="可以直接粘贴图片链接，或点击左侧上传"
                clearable
              >
                <template #prepend>图片URL</template>
              </el-input>
              <div class="tip-text">支持 JPG/PNG 格式，建议尺寸 16:9 (如 800x450)，大小不超过 2MB</div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="公寓简介" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="4"
            placeholder="介绍一下公寓的特色、周边环境、交通情况等..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <div class="section-title">位置信息</div>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份代码" prop="provinceCode">
              <el-input v-model="form.provinceCode" placeholder="如: 310000" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市代码" prop="cityCode">
              <el-input v-model="form.cityCode" placeholder="如: 310100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县代码" prop="districtCode">
              <el-input v-model="form.districtCode" placeholder="如: 310115" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址（不需要包含省市区）" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度 (Lng)" prop="longitude">
              <el-input-number v-model="form.longitude" :precision="6" :step="0.0001" style="width: 100%" placeholder="121.xxxxx" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度 (Lat)" prop="latitude">
              <el-input-number v-model="form.latitude" :precision="6" :step="0.0001" style="width: 100%" placeholder="31.xxxxx" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="section-title">租务规则</div>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低租金" prop="rentMinPrice">
              <el-input v-model="form.rentMinPrice" type="number" placeholder="0">
                 <template #append>元/月</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高租金" prop="rentMaxPrice">
              <el-input v-model="form.rentMaxPrice" type="number" placeholder="0">
                 <template #append>元/月</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="押金标准" prop="depositPrice">
              <el-input v-model="form.depositPrice" type="number" placeholder="0">
                <template #append>元</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付周期" prop="payCycle">
               <el-select v-model="form.payCycle" placeholder="请选择" style="width: 100%">
                 <el-option label="月付 (1)" :value="1" />
                 <el-option label="季付 (3)" :value="3" />
                 <el-option label="半年付 (6)" :value="6" />
                 <el-option label="年付 (12)" :value="12" />
               </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
             <el-form-item label="最短租期" prop="minLeaseMonths">
                <el-input-number v-model="form.minLeaseMonths" :min="1" style="width: 100%" />
                <span class="suffix-text">个月</span>
             </el-form-item>
          </el-col>
          <el-col :span="12">
             <el-form-item label="最长租期" prop="maxLeaseMonths">
                <el-input-number v-model="form.maxLeaseMonths" :min="1" style="width: 100%" />
                <span class="suffix-text">个月</span>
             </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="上架设置">
          <div class="publish-switch">
             <el-switch
              v-model="form.publishStatus"
              :active-value="1"
              :inactive-value="0"
              active-text="立即上架"
              inactive-text="存为草稿"
            />
            <span class="switch-tip" v-if="form.publishStatus === 1">发布后用户可在首页搜索到</span>
            <span class="switch-tip" v-else>仅保存在后台，用户不可见</span>
          </div>
        </el-form-item>

        <div class="form-footer">
          <el-button @click="resetForm" size="large">重置</el-button>
          <el-button type="primary" @click="submitForm" :loading="loading" size="large" style="width: 150px">确认发布</el-button>
        </div>

      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

// 表单数据
const form = reactive({
  name: '',
  type: 1,
  coverUrl: '',
  description: '',

  // 默认定位上海陆家嘴附近，方便调试
  provinceCode: '310000',
  cityCode: '310100',
  districtCode: '310115',
  address: '',
  longitude: 121.50,
  latitude: 31.23,

  rentMinPrice: '', // 字符型方便清空
  rentMaxPrice: '',
  depositPrice: '',
  payCycle: 1,

  minLeaseMonths: 1,
  maxLeaseMonths: 12,

  status: 1,
  publishStatus: 1
})

// 校验规则
const rules = {
  name: [{ required: true, message: '请输入公寓名称', trigger: 'blur' }],
  coverUrl: [{ required: true, message: '请上传封面图或输入URL', trigger: 'change' }], // 这里监听 change
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  rentMinPrice: [{ required: true, message: '请输入最低租金', trigger: 'blur' }],
  rentMaxPrice: [{ required: true, message: '请输入最高租金', trigger: 'blur' }],
  payCycle: [{ required: true, message: '请选择支付周期', trigger: 'change' }]
}

// === 上传相关逻辑 ===
const uploadHeaders = computed(() => {
  return { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
})

const handleCoverSuccess = (response) => {
  // 处理后端可能返回的两种格式
  let url = ''
  if (response.code === 200) {
     // 1. 标准格式 { code: 200, data: "/images/xxx.png" }
     url = response.data
  } else if (response.data && typeof response.data === 'string' && response.data.includes('上传成功')) {
     // 2. 兼容格式 { data: "上传成功:/images/xxx.png" }
     url = response.data.replace('上传成功:', '')
  }

  if (url) {
    form.coverUrl = url
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeCoverUpload = (rawFile) => {
  const isImg = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png';
  const isLt2M = rawFile.size / 1024 / 1024 < 2;

  if (!isImg) {
    ElMessage.error('只能上传 JPG/PNG 文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// === 提交表单 ===
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const token = localStorage.getItem('token')

        // DTO 转换
        const payload = {
          ...form,
          // 金额：元 -> 分
          rentMinCent: Number(form.rentMinPrice) * 100,
          rentMaxCent: Number(form.rentMaxPrice) * 100,
          depositCent: Number(form.depositPrice) * 100,
        }

        const res = await axios.post('/api/apartments', payload, {
          headers: { 'Authorization': `Bearer ${token}` }
        })

        if (res.data.code === 200) {
          ElMessage.success('发布成功！')
          router.push('/provider/dashboard') // 成功后返回
        } else {
          ElMessage.error(res.data.message || '发布失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('网络请求错误')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请检查必填项是否完整')
      return false
    }
  })
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
}
</script>

<style scoped>
.publish-container {
  max-width: 960px;
  margin: 20px auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 20px 0 15px;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}

/* 封面上传区样式 */
.cover-upload-wrapper {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.cover-uploader {
  width: 240px;  /* 16:9 比例宽度 */
  height: 135px; /* 16:9 比例高度 */
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;
  transition: all .3s;
  flex-shrink: 0;
}
.cover-uploader:hover {
  border-color: #409eff;
}
.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 悬停时的遮罩 */
.upload-mask {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 14px;
}
.cover-uploader:hover .upload-mask {
  opacity: 1;
}

.url-input-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-top: 10px;
}
.tip-text {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.5;
}

.suffix-text {
  margin-left: 10px;
  color: #606266;
}

.publish-switch {
  display: flex;
  align-items: center;
  gap: 15px;
}
.switch-tip {
  font-size: 12px;
  color: #909399;
}

.form-footer {
  margin-top: 40px;
  text-align: center;
}
</style>
