<template>
  <div class="profile-container">
    <el-card class="profile-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>个人资料设置</span>
        </div>
      </template>

      <el-row :gutter="40">
        <el-col :span="8" class="left-col">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              action="/api/upload"
              :show-file-list="false"
              :headers="uploadHeaders"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="form.avatar" :src="form.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>

              <div class="upload-mask">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </el-upload>

            <div class="upload-tip-text">点击图片上传，支持 JPG/PNG</div>
          </div>

          <div class="account-status">
            <h3>{{ form.nickName || form.username || '未设置昵称' }}</h3>
            <p>
              <el-tag size="small" :type="form.role === 'PROVIDER' ? 'warning' : 'info'">
                {{ form.role === 'PROVIDER' ? '服务商账户' : '普通用户' }}
              </el-tag>
            </p>
          </div>
        </el-col>

        <el-col :span="16">
          <el-form
            :model="form"
            label-width="100px"
            size="large"
            ref="formRef"
          >
            <el-divider content-position="left">基本信息 (可修改)</el-divider>

            <el-form-item label="昵称">
              <el-input v-model="form.nickName" placeholder="请输入您的昵称" />
            </el-form-item>

            <el-form-item label="所在地区">
              <el-input v-model="form.region" placeholder="例如：上海市浦东新区" />
            </el-form-item>

            <el-form-item label="个人简介">
              <el-input
                v-model="form.introduction"
                type="textarea"
                :rows="4"
                placeholder="简单介绍一下自己..."
                maxlength="200"
                show-word-limit
              />
            </el-form-item>

            <el-divider content-position="left">账户安全 (隐私保护)</el-divider>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="真实姓名">
                  <el-input :model-value="maskName(form.realName)" disabled placeholder="未实名">
                    <template #suffix><el-icon><Lock /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号码">
                  <el-input :model-value="maskPhone(form.phone)" disabled placeholder="未绑定">
                     <template #suffix><el-icon><Iphone /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="身份证号">
               <el-input :model-value="maskIdCard(form.idNumber)" disabled placeholder="未认证">
                  <template #suffix><el-icon><Postcard /></el-icon></template>
               </el-input>
            </el-form-item>

            <el-form-item label="注册邮箱">
              <el-input v-model="form.email" disabled placeholder="未设置" />
            </el-form-item>

            <div class="form-actions">
              <el-button type="primary" @click="handleUpdate" :loading="submitLoading">
                保存修改
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </div>
          </el-form>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue' // 引入 computed
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, Iphone, Postcard, Plus, Camera } from '@element-plus/icons-vue' // 引入 Plus 和 Camera 图标

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
let originalData = {}

const form = reactive({
  id: '',
  username: '',
  role: '',
  nickName: '',
  avatar: '',
  region: '',
  introduction: '',
  realName: '',
  phone: '',
  email: '',
  idNumber: ''
})

// === 1. 上传请求头 (关键！) ===
// 这里的 action="/api/upload" 不会经过 axios 拦截器，所以必须手动加 Authorization
const uploadHeaders = computed(() => {
  return {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
})

// === 2. 上传成功回调 ===
// src/views/UserProfileView.vue

const handleAvatarSuccess = async (response) => {
  // 1. 解析后端返回的路径
  const responseData = response.data || ''
  let finalUrl = ''

  if (response.code === 200 || (typeof responseData === 'string' && responseData.includes('上传成功'))) {
    // 兼容处理：提取纯路径
    finalUrl = responseData
    if (typeof responseData === 'string' && responseData.startsWith('上传成功:')) {
      finalUrl = responseData.replace('上传成功:', '')
    }

    // 2. 更新前端显示的图片
    form.avatar = finalUrl

    // 3. 【关键一步】立即调用保存接口，写入数据库！
    // 这样您就不用再手动点“保存修改”了
    await saveAvatarToDb(finalUrl)

  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 【新增】专门用于单独保存头像的函数
const saveAvatarToDb = async (avatarUrl) => {
  try {
    const userId = form.id || localStorage.getItem('userId')
    const token = localStorage.getItem('token')

    // 只构建包含头像的 payload，或者复用 handleUpdate 的逻辑
    // 这里我们为了稳妥，复用 handleUpdate 的逻辑，把当前页面所有信息都提交一遍
    // 也可以只发 avatar 字段，取决于后端 UserUpdate 是否支持部分更新（MyBatis 的 <if> 标签支持）
    const updatePayload = {
      id: userId,
      nickName: form.nickName,
      avatar: avatarUrl, // 确保用最新的 URL
      region: form.region,
      introduction: form.introduction
    }

    const res = await axios.put(`/api/users/${userId}`, updatePayload, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (res.status === 200) {
      ElMessage.success('头像修改成功并已保存')
      // 更新本地缓存（如果需要）
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('头像上传成功，但保存到数据库失败')
  }
}

// === 3. 上传前校验 ===
const beforeAvatarUpload = (rawFile) => {
  const isImage = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png';
  const isLt2M = rawFile.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// ... (以下是之前的 fetchUserInfo, handleUpdate 等代码，保持不变) ...
// === 脱敏函数 ===
const maskPhone = (val) => val ? val.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2') : ''
const maskIdCard = (val) => (val && val.length > 10) ? val.replace(/^(\d{6})\d+(\d{4})$/, '$1********$2') : val
const maskName = (val) => (val && val.length > 0) ? val.substring(0, 1) + new Array(val.length).join('*') : ''

onMounted(() => {
  fetchUserInfo()
})

const fetchUserInfo = async () => {
    // ... (保持原样)
    loading.value = true
    try {
        const token = localStorage.getItem('token')
        const userId = localStorage.getItem('userId')
        if (!token || !userId) {
            ElMessage.warning('登录已过期')
            router.push('/login')
            return
        }
        const res = await axios.get(`/api/users/${userId}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        })
        if (res.status === 200 && res.data) {
            Object.assign(form, res.data)
            originalData = JSON.parse(JSON.stringify(res.data))
        }
    } catch {
        ElMessage.error('获取信息失败')
    } finally {
        loading.value = false
    }
}

const handleUpdate = async () => {
    // ... (保持原样)
    submitLoading.value = true
    try {
        const userId = form.id || localStorage.getItem('userId')
        const token = localStorage.getItem('token')
        const updatePayload = {
            id: userId,
            nickName: form.nickName,
            avatar: form.avatar, // 这里现在已经是上传后的路径了
            region: form.region,
            introduction: form.introduction
        }
        const res = await axios.put(`/api/users/${userId}`, updatePayload, {
            headers: { 'Authorization': `Bearer ${token}` }
        })
        if (res.status === 200) {
            ElMessage.success('保存成功')
            if (form.nickName) localStorage.setItem('username', form.nickName)
            originalData = JSON.parse(JSON.stringify(form))
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('保存失败')
    } finally {
        submitLoading.value = false
    }
}

const resetForm = () => {
  Object.assign(form, originalData)
  ElMessage.info('已重置')
}
</script>

<style scoped>
.profile-container { max-width: 1000px; margin: 20px auto; padding: 0 20px; }
.profile-card { border-radius: 12px; }
.card-header { font-weight: bold; font-size: 18px; }
.left-col { text-align: center; border-right: 1px solid #f0f2f5; padding-right: 30px; }
.avatar-section { margin-top: 20px; display: flex; flex-direction: column; align-items: center; gap: 15px; }
.upload-tip { width: 100%; }
.account-status { margin-top: 30px; }
.account-status h3 { margin: 0 0 10px 0; font-size: 24px; color: #303133; }
.form-actions { margin-top: 30px; display: flex; gap: 15px; }
:deep(.el-input.is-disabled .el-input__inner) { color: #606266; -webkit-text-fill-color: #606266; }
:deep(.el-divider__text) { color: #909399; }
/* 头像上传组件样式 */
.avatar-uploader {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px dashed #d9d9d9;
  cursor: pointer;
  transition: .3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  display: flex; /* 修正居中 */
  justify-content: center;
  align-items: center;
}

/* 悬停时的遮罩层效果 */
.upload-mask {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-uploader:hover .upload-mask {
  opacity: 1;
}

.upload-tip-text {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
