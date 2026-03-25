<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" @click="navigateTo('/elderly/list')">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.elderlyCount || 0 }}</div>
              <div class="stat-label">在住住客</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" @click="navigateTo('/room/list')">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="32"><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ roomStats.occupancyRate || 0 }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" @click="navigateTo('/activity/list')">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activityCount || 0 }}</div>
              <div class="stat-label">本月活动</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" @click="navigateTo('/care/record')">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.careCount || 0 }}</div>
              <div class="stat-label">今日护理</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card class="mini-stat-card">
          <div class="mini-stat-content">
            <div class="mini-stat-value text-warning">{{ stats.pendingCareCount || 0 }}</div>
            <div class="mini-stat-label">待执行护理</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="mini-stat-card">
          <div class="mini-stat-content">
            <div class="mini-stat-value text-danger">{{ stats.unpaidBillCount || 0 }}</div>
            <div class="mini-stat-label">待缴账单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="mini-stat-card">
          <div class="mini-stat-content">
            <div class="mini-stat-value text-success">{{ healthStats.healthy || 0 }}</div>
            <div class="mini-stat-label">健康住客</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="mini-stat-card">
          <div class="mini-stat-content">
            <div class="mini-stat-value text-info">{{ healthStats.attention || 0 }}</div>
            <div class="mini-stat-label">需关注</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>今日护理任务</span>
              <el-button type="primary" link @click="navigateTo('/care/record')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="careTasks" stripe style="width: 100%" v-loading="loading.careTasks">
            <el-table-column prop="time" label="时间" width="100">
              <template #default="{ row }">
                {{ formatTime(row.time) }}
              </template>
            </el-table-column>
            <el-table-column prop="elderlyName" label="住客姓名" width="100" />
            <el-table-column prop="task" label="护理任务" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '已完成' ? 'success' : 'warning'" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading.careTasks && careTasks.length === 0" description="暂无护理任务" />
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近期活动</span>
              <el-button type="primary" link @click="navigateTo('/activity/list')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentActivities" stripe style="width: 100%" v-loading="loading.activities">
            <el-table-column prop="name" label="活动名称" show-overflow-tooltip />
            <el-table-column prop="time" label="时间" width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.time) }}
              </template>
            </el-table-column>
            <el-table-column prop="participants" label="参与人数" width="100">
              <template #default="{ row }">
                {{ row.participants || 0 }}人
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading.activities && recentActivities.length === 0" description="暂无近期活动" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>房间状态分布</span>
            </div>
          </template>
          <div class="room-stats">
            <div class="room-stat-item">
              <div class="room-stat-value">{{ roomStats.total || 0 }}</div>
              <div class="room-stat-label">总房间</div>
            </div>
            <div class="room-stat-item">
              <div class="room-stat-value text-success">{{ roomStats.full || 0 }}</div>
              <div class="room-stat-label">已满</div>
            </div>
            <div class="room-stat-item">
              <div class="room-stat-value text-warning">{{ roomStats.partial || 0 }}</div>
              <div class="room-stat-label">部分入住</div>
            </div>
            <div class="room-stat-item">
              <div class="room-stat-value text-info">{{ roomStats.empty || 0 }}</div>
              <div class="room-stat-label">空闲</div>
            </div>
          </div>
          <el-progress 
            :percentage="roomStats.occupancyRate || 0" 
            :stroke-width="20"
            :format="() => '入住率'"
            style="margin-top: 20px"
          />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>健康状态分布</span>
            </div>
          </template>
          <div class="health-stats">
            <div class="health-stat-item">
              <el-progress type="dashboard" :percentage="getHealthPercent('healthy')" :color="'#67c23a'" :width="80">
                <template #default>
                  <span class="health-value">{{ healthStats.healthy || 0 }}</span>
                </template>
              </el-progress>
              <div class="health-label">健康</div>
            </div>
            <div class="health-stat-item">
              <el-progress type="dashboard" :percentage="getHealthPercent('attention')" :color="'#e6a23c'" :width="80">
                <template #default>
                  <span class="health-value">{{ healthStats.attention || 0 }}</span>
                </template>
              </el-progress>
              <div class="health-label">需关注</div>
            </div>
            <div class="health-stat-item">
              <el-progress type="dashboard" :percentage="getHealthPercent('critical')" :color="'#f56c6c'" :width="80">
                <template #default>
                  <span class="health-value">{{ healthStats.critical || 0 }}</span>
                </template>
              </el-progress>
              <div class="health-label">需重点护理</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>本月数据概览</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="summary-item">
                <el-icon :size="24" color="#409eff"><UserFilled /></el-icon>
                <div class="summary-info">
                  <div class="summary-value">{{ monthlySummary.newCheckIns || 0 }}</div>
                  <div class="summary-label">新入住住客</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="summary-item">
                <el-icon :size="24" color="#67c23a"><Trophy /></el-icon>
                <div class="summary-info">
                  <div class="summary-value">{{ monthlySummary.activities || 0 }}</div>
                  <div class="summary-label">举办活动</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="summary-item">
                <el-icon :size="24" color="#e6a23c"><FirstAidKit /></el-icon>
                <div class="summary-info">
                  <div class="summary-value">{{ monthlySummary.healthRecords || 0 }}</div>
                  <div class="summary-label">健康记录</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="summary-item">
                <el-icon :size="24" color="#f56c6c"><Document /></el-icon>
                <div class="summary-info">
                  <div class="summary-value">{{ monthlySummary.careRecords || 0 }}</div>
                  <div class="summary-label">护理记录</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, House, Calendar, Document, UserFilled, Trophy, FirstAidKit } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()

const stats = reactive({
  elderlyCount: 0,
  roomCount: 0,
  activityCount: 0,
  careCount: 0,
  pendingCareCount: 0,
  unpaidBillCount: 0
})

const roomStats = reactive({
  total: 0,
  empty: 0,
  full: 0,
  partial: 0,
  occupancyRate: 0
})

const healthStats = reactive({
  healthy: 0,
  attention: 0,
  critical: 0,
  total: 0
})

const monthlySummary = reactive({
  newCheckIns: 0,
  activities: 0,
  healthRecords: 0,
  careRecords: 0
})

const careTasks = ref([])
const recentActivities = ref([])

const loading = reactive({
  careTasks: false,
  activities: false
})

const getHealthPercent = (type) => {
  const total = healthStats.total || 1
  const value = healthStats[type] || 0
  return Math.round((value / total) * 100)
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const navigateTo = (path) => {
  router.push(path)
}

const fetchStats = async () => {
  try {
    const res = await request.get('/dashboard/stats')
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchRoomStats = async () => {
  try {
    const res = await request.get('/dashboard/room-stats')
    if (res.data) {
      Object.assign(roomStats, res.data)
    }
  } catch (error) {
    console.error('获取房间统计失败:', error)
  }
}

const fetchHealthStats = async () => {
  try {
    const res = await request.get('/dashboard/health-stats')
    if (res.data) {
      Object.assign(healthStats, res.data)
    }
  } catch (error) {
    console.error('获取健康统计失败:', error)
  }
}

const fetchCareTasks = async () => {
  loading.careTasks = true
  try {
    const res = await request.get('/dashboard/today-care-tasks')
    if (res.data) {
      careTasks.value = res.data
    }
  } catch (error) {
    console.error('获取护理任务失败:', error)
  } finally {
    loading.careTasks = false
  }
}

const fetchRecentActivities = async () => {
  loading.activities = true
  try {
    const res = await request.get('/dashboard/recent-activities')
    if (res.data) {
      recentActivities.value = res.data
    }
  } catch (error) {
    console.error('获取近期活动失败:', error)
  } finally {
    loading.activities = false
  }
}

const fetchMonthlySummary = async () => {
  try {
    const res = await request.get('/dashboard/monthly-summary')
    if (res.data) {
      Object.assign(monthlySummary, res.data)
    }
  } catch (error) {
    console.error('获取月度汇总失败:', error)
  }
}

onMounted(() => {
  fetchStats()
  fetchRoomStats()
  fetchHealthStats()
  fetchCareTasks()
  fetchRecentActivities()
  fetchMonthlySummary()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.mini-stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.mini-stat-card:hover {
  transform: translateY(-3px);
}

.mini-stat-content {
  text-align: center;
  padding: 10px 0;
}

.mini-stat-value {
  font-size: 24px;
  font-weight: bold;
}

.mini-stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-stats {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.room-stat-item {
  padding: 10px;
}

.room-stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.room-stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.health-stats {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.health-stat-item {
  padding: 10px;
}

.health-value {
  font-size: 18px;
  font-weight: bold;
}

.health-label {
  font-size: 12px;
  color: #666;
  margin-top: 10px;
}

.summary-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.summary-info {
  margin-left: 15px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.summary-label {
  font-size: 12px;
  color: #999;
  margin-top: 3px;
}

.text-success { color: #67c23a; }
.text-warning { color: #e6a23c; }
.text-danger { color: #f56c6c; }
.text-info { color: #909399; }
</style>
