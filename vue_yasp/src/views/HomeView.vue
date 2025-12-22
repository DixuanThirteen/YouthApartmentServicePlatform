<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-overlay"></div>

      <div class="hero-content">
        <h1 class="hero-title">寻找您在城市的理想港湾</h1>
        <p class="hero-subtitle">YASP 公寓平台 · 严选好房 · 拎包入住 · 品质生活</p>

        <div class="search-bar-wrapper">
          <div class="search-bar">
            <el-row :gutter="0" align="middle" class="search-row">
              <el-col :span="9" class="search-col">
                <el-input
                  v-model="searchQuery.keyword"
                  placeholder="请输入城市、小区或公寓名"
                  class="custom-input no-border"
                  :prefix-icon="Location"
                  @keyup.enter="handleSearch"
                />
              </el-col>

              <el-col :span="1" class="divider-col">
                <div class="vertical-divider"></div>
              </el-col>

              <el-col :span="9" class="search-col price-inputs">
                <div class="price-wrapper">
                  <el-input
                    v-model="searchQuery.minPrice"
                    placeholder="最低预算"
                    class="custom-input price-input"
                    type="number"
                    @keyup.enter="handleSearch"
                  />
                  <span class="price-separator">-</span>
                  <el-input
                    v-model="searchQuery.maxPrice"
                    placeholder="最高预算"
                    class="custom-input price-input"
                    type="number"
                    @keyup.enter="handleSearch"
                  />
                </div>
              </el-col>

              <el-col :span="5" class="search-btn-col">
                <el-button type="primary" class="search-btn" @click="handleSearch" :loading="loading">
                  <el-icon class="el-icon--left"><Search /></el-icon>
                  搜好房
                </el-button>
                <el-tooltip content="重置条件" placement="top">
                  <el-button class="reset-btn" :icon="Refresh" circle @click="handleReset" />
                </el-tooltip>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="list-header">
        <div class="left-title">
          <h2 class="section-title">
            {{ isSearching ? '搜索结果' : '热门推荐' }}
          </h2>
          <span v-if="isSearching" class="result-badge">
            共找到 {{ apartments.length }} 套房源
          </span>
        </div>
      </div>

      <el-skeleton v-if="loading" :rows="3" animated count="4" class="custom-skeleton" />

      <div v-else>
        <el-row :gutter="24">
          <el-col
            v-for="apt in apartments"
            :key="apt.id"
            :xs="24" :sm="12" :md="8" :lg="6"
          >
            <el-card
              class="apt-card clickable-card"
              :body-style="{ padding: '0px' }"
              shadow="never"
              @click="goToDetail(apt.id)"
            >
              <div class="image-box">
                <el-image
                  :src="apt.coverUrl || 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'"
                  fit="cover"
                  class="apt-image"
                  loading="lazy"
                >
                  <template #placeholder>
                    <div class="image-placeholder">Loading...</div>
                  </template>
                </el-image>

                <div class="card-tags">
                  <el-tag v-if="apt.type === 2" type="primary" effect="dark" size="small" class="custom-tag">企业公寓</el-tag>
                  <el-tag v-else type="warning" effect="dark" size="small" class="custom-tag">个人房东</el-tag>
                </div>
              </div>

              <div class="card-info">
                <h3 class="apt-title" :title="apt.name">
                  {{ apt.name }}
                </h3>

                <div class="apt-meta">
                  <div class="location">
                    <el-icon><LocationInformation /></el-icon>
                    <span>{{ apt.address || apt.cityCode }}</span>
                  </div>
                </div>

                <div class="apt-tags-row">
                  <span class="feature-tag">近地铁</span>
                  <span class="feature-tag">拎包入住</span>
                </div>

                <el-divider class="card-divider" />

                <div class="card-footer">
                  <div class="price-wrapper">
                    <span class="currency">¥</span>
                    <span class="amount">{{ apt.rentMinCent ? apt.rentMinCent / 100 : '暂无' }}</span>
                    <span class="unit">/月起</span>
                  </div>
                  </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty
          v-if="!loading && apartments.length === 0"
          :image-size="200"
          description="暂无符合条件的房源，换个条件试试？"
        >
          <el-button type="primary" @click="handleReset" v-if="isSearching">查看所有房源</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Location, LocationInformation } from '@element-plus/icons-vue'

const router = useRouter()
const apartments = ref([])
const loading = ref(false)
const isSearching = ref(false)

const searchQuery = reactive({
  keyword: '',
  minPrice: '',
  maxPrice: ''
})

onMounted(() => {
  fetchApartments()
})

const fetchApartments = async (isSearch = false) => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const headers = { 'Authorization': `Bearer ${token}` }
    let res

    if (isSearch) {
      // 搜索模式
      const searchPayload = {
        keyword: searchQuery.keyword || null,
        rentMinCentGte: searchQuery.minPrice ? Number(searchQuery.minPrice) * 100 : null,
        rentMaxCentLte: searchQuery.maxPrice ? Number(searchQuery.maxPrice) * 100 : null
      }
      res = await axios.post('/api/apartments/search', searchPayload, { headers })
      isSearching.value = true
    } else {
      // 默认模式
      res = await axios.get('/api/apartments', { headers })
      isSearching.value = false
    }

    if (res.data.code === 200) {
      apartments.value = res.data.data || []
    }
  } catch (error) {
    console.error('API Error:', error)
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '获取数据失败')
    } else {
      ElMessage.error('网络连接异常')
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (!searchQuery.keyword && !searchQuery.minPrice && !searchQuery.maxPrice) {
    handleReset()
    return
  }
  fetchApartments(true)
}

const handleReset = () => {
  searchQuery.keyword = ''
  searchQuery.minPrice = ''
  searchQuery.maxPrice = ''
  fetchApartments(false)
}

const goToDetail = (id) => {
  router.push(`/apartment/${id}`)
}
</script>

<style scoped>
.home-container {
  min-height: 100%;
  background-color: #f5f7fa;
}

/* --- Hero 区域设计 --- */
.hero-section {
  position: relative;
  height: 400px;
  background-image: url('https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=1600&q=80');
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.hero-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, rgba(0, 50, 100, 0.7) 0%, rgba(0, 0, 0, 0.4) 100%);
}

.hero-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 900px;
  padding: 0 20px;
}

.hero-title {
  color: #fff;
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 12px;
  text-shadow: 0 4px 10px rgba(0,0,0,0.3);
  letter-spacing: 1px;
}

.hero-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  margin-bottom: 40px;
  font-weight: 300;
}

/* --- 悬浮搜索栏设计 --- */
.search-bar-wrapper {
  margin-top: 20px;
}

.search-bar {
  background: #ffffff;
  border-radius: 12px;
  padding: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s;
}

.search-bar:hover {
  transform: translateY(-2px);
}

.search-row {
  height: 50px;
}

.search-col {
  display: flex;
  align-items: center;
}

:deep(.custom-input .el-input__wrapper) {
  box-shadow: none !important;
  background-color: transparent;
}

:deep(.custom-input .el-input__inner) {
  font-size: 16px;
  color: #333;
}

.divider-col {
  display: flex;
  justify-content: center;
  align-items: center;
}
.vertical-divider {
  width: 1px;
  height: 24px;
  background-color: #e0e0e0;
}

.price-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
}
.price-input {
  text-align: center;
}
:deep(.price-input .el-input__inner) {
  text-align: center;
}
.price-separator {
  color: #999;
  margin: 0 10px;
}

.search-btn-col {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  padding-right: 5px;
}

.search-btn {
  height: 42px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  padding: 0 25px;
  background: linear-gradient(90deg, #409eff, #337ecc);
  border: none;
}
.search-btn:hover {
  background: linear-gradient(90deg, #66b1ff, #409eff);
  transform: scale(1.02);
}

.reset-btn {
  height: 42px;
  width: 42px;
  border: 1px solid #dcdfe6;
  color: #909399;
}
.reset-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

/* --- 主内容区域 --- */
.main-content {
  max-width: 1280px;
  margin: -40px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 3;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  padding: 0 8px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
  display: inline-block;
}

.section-title::after {
  content: '';
  display: block;
  width: 40px;
  height: 4px;
  background: #409eff;
  margin-top: 6px;
  border-radius: 2px;
}

.result-badge {
  margin-left: 12px;
  font-size: 14px;
  color: #606266;
  background: #fff;
  padding: 2px 10px;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

/* --- 卡片样式优化 --- */
.clickable-card {
  cursor: pointer;
  border: none;
  border-radius: 12px;
  margin-bottom: 24px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: #fff;
  overflow: hidden;
}

.clickable-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.image-box {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.apt-image {
  width: 100%;
  height: 100%;
  transition: transform 0.6s ease;
}

.image-box:hover .apt-image {
  transform: scale(1.08);
}

.card-tags {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 6px;
}

.custom-tag {
  border: none;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.card-info {
  padding: 16px;
}

.apt-title {
  font-size: 17px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.2s;
}

.apt-title:hover {
  color: #409eff;
}

.apt-meta {
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 13px;
  margin-bottom: 10px;
}

.location {
  display: flex;
  align-items: center;
  gap: 4px;
}

.apt-tags-row {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}

.feature-tag {
  font-size: 12px;
  color: #606266;
  background: #f4f4f5;
  padding: 2px 6px;
  border-radius: 4px;
}

.card-divider {
  margin: 12px 0;
  border-color: #f0f2f5;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-wrapper {
  color: #f56c6c;
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 14px;
  font-weight: 600;
}

.amount {
  font-size: 22px;
  font-weight: 800;
  margin: 0 2px;
  letter-spacing: -0.5px;
}

.unit {
  font-size: 12px;
  color: #999;
}
</style>
