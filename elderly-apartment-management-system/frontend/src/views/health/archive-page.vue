<template>
  <div class="health-archive-page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康档案详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="住客姓名">{{ archive.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ archive.age }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ archive.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ archive.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="血压">{{ archive.bloodPressure }}</el-descriptions-item>
        <el-descriptions-item label="血糖">{{ archive.bloodSugar }}</el-descriptions-item>
        <el-descriptions-item label="心率">{{ archive.heartRate }} 次/分</el-descriptions-item>
        <el-descriptions-item label="体重">{{ archive.weight }} kg</el-descriptions-item>
        <el-descriptions-item label="健康状态">
          <el-tag :type="getHealthStatusType(archive.healthStatus)">
            {{ getHealthStatusText(archive.healthStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最近体检">{{ archive.lastCheckTime }}</el-descriptions-item>
        <el-descriptions-item label="病史" :span="2">{{ archive.medicalHistory }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ archive.notes }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>健康记录</el-divider>

      <el-table :data="healthRecords" stripe style="width: 100%">
        <el-table-column prop="recordDate" label="记录日期" width="160" />
        <el-table-column prop="recordType" label="记录类型" width="120">
          <template #default="{ row }">
            {{ getRecordTypeText(row.recordType) }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="记录内容" />
        <el-table-column prop="recorder" label="记录人" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const archive = ref({
  elderlyName: '张大爷',
  age: 75,
  gender: 1,
  roomNumber: '101',
  bloodPressure: '120/80 mmHg',
  bloodSugar: '5.5 mmol/L',
  heartRate: 75,
  weight: 65.0,
  healthStatus: 1,
  lastCheckTime: '2024-01-10',
  medicalHistory: '高血压、糖尿病',
  notes: '定期复查'
})

const healthRecords = ref([
  { recordDate: '2024-01-15 09:00', recordType: 1, content: '血压测量：120/80 mmHg，正常', recorder: '王护士' },
  { recordDate: '2024-01-14 10:00', recordType: 2, content: '血糖监测：5.5 mmol/L，正常', recorder: '刘护士' }
])

const goBack = () => {
  router.back()
}

const getHealthStatusText = (status) => {
  const map = { 1: '良好', 2: '一般', 3: '较差', 4: '危急' }
  return map[status] || '未知'
}

const getHealthStatusType = (status) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'danger' }
  return map[status] || 'info'
}

const getRecordTypeText = (type) => {
  const map = { 1: '血压测量', 2: '血糖监测', 3: '体重测量', 4: '心率测量' }
  return map[type] || '未知'
}

onMounted(() => {
  ElMessage.info('健康档案详情页面')
})
</script>

<style scoped>
.health-archive-page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-divider {
  margin: 30px 0;
}
</style>
