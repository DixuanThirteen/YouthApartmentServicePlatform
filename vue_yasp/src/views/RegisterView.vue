<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>创建账户</h2>
        <p>加入 YASP，开启您的公寓服务之旅</p>
      </div>

      <el-tabs v-model="activeRole" class="role-tabs" stretch @tab-change="handleTabChange">
        <el-tab-pane label="注册为普通用户" name="user"></el-tab-pane>
        <el-tab-pane label="注册为服务商" name="provider"></el-tab-pane>
      </el-tabs>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        size="default"
        label-position="top"
        class="register-form"
      >
        <template v-if="activeRole === 'user'">
          <el-divider content-position="left">账号信息</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" placeholder="登录账号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="昵称 (选填)" prop="nickName">
                <el-input v-model="form.nickName" placeholder="显示昵称" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" type="password" show-password placeholder="设置密码" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="form.confirmPassword" type="password" show-password placeholder="确认密码" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider content-position="left">个人详细资料</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idNumber">
                <el-input v-model="form.idNumber" placeholder="输入18位身份证号" maxlength="18"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别 (自动生成)" prop="gender">
                <el-select v-model="form.gender" placeholder="自动生成" disabled>
                  <el-option label="男" value="male" />
                  <el-option label="女" value="female" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出生日期 (自动生成)" prop="bornDate">
                <el-date-picker
                  v-model="form.bornDate"
                  type="date"
                  placeholder="自动生成"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>

           <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="所在地区 (选填)" prop="region">
                <el-input v-model="form.region" placeholder="如: 广州" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学历" prop="degree">
                <el-select v-model="form.degree" placeholder="请选择">
                  <el-option label="高中及以下" value="below" />
                  <el-option label="大专" value="college" />
                  <el-option label="本科" value="bachelor" />
                  <el-option label="硕士" value="master" />
                  <el-option label="博士" value="doctorate" />
                  <el-option label="其他/博士后" value="above" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="个人简介 (选填)" prop="introduction">
            <el-input v-model="form.introduction" type="textarea" :rows="2" placeholder="请填写个人简介" />
          </el-form-item>
        </template>

        <template v-if="activeRole === 'provider'">
          <el-divider content-position="left">账户与机构信息</el-divider>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="机构/公司名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入公司全称" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
             <el-col :span="12">
              <el-form-item label="管理员账号" prop="defaultAccount">
                <el-input v-model="form.defaultAccount" placeholder="用于登录的用户名" />
              </el-form-item>
            </el-col>
             <el-col :span="12">
              <el-form-item label="机构类型" prop="type">
                 <el-select v-model="form.type" placeholder="请选择类型">
                  <el-option label="企业" value="2" />
                  <el-option label="个人商户" value="1" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" type="password" show-password />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="form.confirmPassword" type="password" show-password />
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider content-position="left">联系方式与认证</el-divider>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="联系人" prop="contactPerson">
                <el-input v-model="form.contactPerson" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="form.contactPhone" />
              </el-form-item>
            </el-col>
             <el-col :span="8">
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input v-model="form.contactEmail" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="营业执照号" prop="licenseNumber">
            <el-input v-model="form.licenseNumber" />
          </el-form-item>

          <el-form-item label="详细地址" prop="address">
            <el-input v-model="form.address" />
          </el-form-item>

          <el-form-item label="机构描述 (选填)" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="2" />
          </el-form-item>
        </template>

        <el-button
          type="primary"
          class="register-btn"
          :loading="loading"
          @click="handleRegister"
          size="large"
        >
          立即注册
        </el-button>

        <div class="login-link">
          <span>已有账号? </span>
          <el-link type="primary" @click="router.push('/login')">去登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const activeRole = ref('user')

// 表单数据
const form = reactive({
  // User 字段
  username: '',
  nickName: '', // 可为空
  realName: '',
  idNumber: '',
  bornDate: '',
  gender: '',
  introduction: '', // 可为空
  region: '',       // 可为空
  degree: '',

  // Provider 字段
  name: '',
  type: '2',
  defaultAccount: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  licenseNumber: '',
  description: '', // 可为空

  // 公共字段
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
})

// 监听角色切换，清除校验状态
const handleTabChange = () => {
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 身份证自动解析出生日期和性别
watch(() => form.idNumber, (newVal) => {
  if (activeRole.value === 'user' && newVal && newVal.length === 18) {
    const year = newVal.substring(6, 10)
    const month = newVal.substring(10, 12)
    const day = newVal.substring(12, 14)
    if (!isNaN(year) && !isNaN(month) && !isNaN(day)) {
      form.bornDate = `${year}-${month}-${day}`
    }
    const genderChar = newVal.charAt(16)
    const genderNum = parseInt(genderChar)
    if (!isNaN(genderNum)) {
      form.gender = genderNum % 2 === 1 ? 'male' : 'female'
    }
  }
})

// 校验规则辅助函数
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

// === 校验规则更新 ===
const rules = {
  // --- 公共部分 ---
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],

  // --- User 部分 ---
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  // nickName 选填
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { min: 18, max: 18, message: '请输入18位身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '需通过身份证自动生成', trigger: 'change' }],
  bornDate: [{ required: true, message: '需通过身份证自动生成', trigger: 'change' }],
  // region 选填
  degree: [{ required: true, message: '请选择学历', trigger: 'change' }],
  // introduction 选填

  // --- Provider 部分 ---
  name: [{ required: true, message: '请输入机构/公司名称', trigger: 'blur' }],
  defaultAccount: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  type: [{ required: true, message: '请选择机构类型', trigger: 'change' }],
  contactPerson: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  licenseNumber: [{ required: true, message: '请输入营业执照号', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],

  // 【修改点】description 选填，已移除校验
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        let url = ''
        let requestBody = {}

        if (activeRole.value === 'user') {
          url = '/api/users'
          requestBody = {
            username: form.username,
            nickName: form.nickName,
            realName: form.realName,
            password: form.password,
            idNumber: form.idNumber,
            phone: form.phone,
            email: form.email,
            bornDate: form.bornDate,
            gender: form.gender,
            avatar: '/images/DefaultAvatar.png',
            introduction: form.introduction,
            region: form.region,
            degree: form.degree
          }
        } else {
          url = '/api/providers'
          requestBody = {
            name: form.name,
            type: form.type,
            defaultAccount: form.defaultAccount,
            password: form.password,
            contactPerson: form.contactPerson,
            contactPhone: form.contactPhone,
            contactEmail: form.contactEmail,
            address: form.address,
            licenseNumber: form.licenseNumber,
            description: form.description // 选填
          }
        }

        const response = await axios.post(url, requestBody)

        if (response.status === 200 || response.data.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
           ElMessage.error(response.data.message || '注册失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error(error.response?.data?.message || '注册请求失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px 0;
}

.register-box {
  width: 700px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 25px;
}

.register-header h2 {
  margin: 0 0 10px;
  color: #303133;
}

.register-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.role-tabs {
  margin-bottom: 20px;
}

.register-btn {
  width: 100%;
  margin-top: 10px;
  font-size: 16px;
}

.login-link {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

:deep(.el-divider__text) {
  font-size: 13px;
  color: #909399;
}
</style>
