<template>
  <div class="activity-index-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>活动统计</span>
            </div>
          </template>
          <div class="stat-item">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总活动数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.ongoing }}</div>
            <div class="stat-label">进行中</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.upcoming }}</div>
            <div class="stat-label">即将开始</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>即将开始的活动</span>
              <el-button type="primary" size="small" @click="goToList">查看全部</el-button>
            </div>
          </template>
          <el-table :data="upcomingActivities" stripe>
            <el-table-column prop="name" label="活动名称" width="200" />
            <el-table-column prop="startTime" label="开始时间" width="160" />
            <el-table-column prop="location" label="地点" width="120" />
            <el-table-column prop="participantCount" label="报名人数" width="100" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const stats = ref({
  total: 0,
  ongoing: 0,
  upcoming: 0
})

const upcomingActivities = ref([])

const loadData = async () => {
  try {
    stats.value = {
      total: 45,
      ongoing: 3,
      upcoming: 5
    }
    upcomingActivities.value = [
      { id: 1, name: '重阳节文艺汇演', startTime: '2024-10-16 14:00', location: '多功能厅', participantCount: 45 },
      { id: 2, name: '健康讲座', startTime: '2024-10-17 09:00', location: '会议室', participantCount: 30 },
      { id: 3, name: '书法兴趣班', startTime: '2024-10-18 14:30', location: '活动室', participantCount: 20 }
    ]
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const goToList = () => {
  router.push('/activity/list')
}

const handleDetail = (row) => {
  ElMessage.info('查看详情功能开发中')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.activity-index-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
