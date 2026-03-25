<template>
  <div class="health-record-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card good" shadow="hover">
          <div class="stat-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.good }}</div>
            <div class="stat-label">健康良好</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card normal" shadow="hover">
          <div class="stat-icon">
            <el-icon><InfoFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.normal }}</div>
            <div class="stat-label">健康一般</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card poor" shadow="hover">
          <div class="stat-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.poor }}</div>
            <div class="stat-label">健康较差</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card critical" shadow="hover">
          <div class="stat-icon">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.critical }}</div>
            <div class="stat-label">健康危急</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主内容区 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康档案管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增档案
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="健康状态">
          <el-select v-model="searchForm.healthStatus" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="良好" :value="1" />
            <el-option label="一般" :value="2" />
            <el-option label="较差" :value="3" />
            <el-option label="危急" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="检查日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="elderlyName" label="住客姓名" width="100">
          <template #default="{ row }">
            <el-link type="primary" @click="handleView(row)">{{ row.elderlyName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="基本信息" width="120">
          <template #default="{ row }">
            <span>{{ row.elderlyGender === 1 ? '男' : '女' }} / {{ row.elderlyAge }}岁</span>
          </template>
        </el-table-column>
        <el-table-column label="血压" width="120">
          <template #default="{ row }">
            <span v-if="row.systolicPressure && row.diastolicPressure">
              {{ row.systolicPressure }}/{{ row.diastolicPressure }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="bloodSugar" label="血糖" width="100">
          <template #default="{ row }">
            <span v-if="row.bloodSugar">{{ row.bloodSugar }} mmol/L</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="heartRate" label="心率" width="100">
          <template #default="{ row }">
            <span v-if="row.heartRate">{{ row.heartRate }} bpm</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="temperature" label="体温" width="100">
          <template #default="{ row }">
            <span v-if="row.temperature">{{ row.temperature }} ℃</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="bloodOxygen" label="血氧" width="100">
          <template #default="{ row }">
            <span v-if="row.bloodOxygen">{{ row.bloodOxygen }}%</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="体重" width="100">
          <template #default="{ row }">
            <span v-if="row.weight">{{ row.weight }} kg</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="height" label="身高" width="100">
          <template #default="{ row }">
            <span v-if="row.height">{{ row.height }} cm</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="healthStatus" label="健康状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHealthStatusType(row.healthStatus)" effect="dark">
              {{ getHealthStatusText(row.healthStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkDate" label="检查日期" width="120" />
        <el-table-column prop="doctor" label="检查医生" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
    >
      <el-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        label-width="120px"
        class="health-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择住客" prop="elderlyId">
              <el-select
                v-model="formData.elderlyId"
                placeholder="请选择住客"
                filterable
                style="width: 100%"
                :disabled="isEdit"
              >
                <el-option
                  v-for="elderly in elderlyList"
                  :key="elderly.id"
                  :label="elderly.realName"
                  :value="elderly.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查日期" prop="checkDate">
              <el-date-picker
                v-model="formData.checkDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>生命体征</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压" prop="systolicPressure">
              <el-input-number
                v-model="formData.systolicPressure"
                :min="50"
                :max="250"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压" prop="diastolicPressure">
              <el-input-number
                v-model="formData.diastolicPressure"
                :min="30"
                :max="150"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血糖" prop="bloodSugar">
              <el-input-number
                v-model="formData.bloodSugar"
                :min="1"
                :max="30"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="心率" prop="heartRate">
              <el-input-number
                v-model="formData.heartRate"
                :min="40"
                :max="200"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体温" prop="temperature">
              <el-input-number
                v-model="formData.temperature"
                :min="35"
                :max="42"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血氧" prop="bloodOxygen">
              <el-input-number
                v-model="formData.bloodOxygen"
                :min="70"
                :max="100"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input-number
                v-model="formData.weight"
                :min="30"
                :max="150"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身高(cm)" prop="height">
              <el-input-number
                v-model="formData.height"
                :min="100"
                :max="200"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="健康状态" prop="healthStatus">
              <el-select v-model="formData.healthStatus" placeholder="请选择" style="width: 100%">
                <el-option label="良好" :value="1" />
                <el-option label="一般" :value="2" />
                <el-option label="较差" :value="3" />
                <el-option label="危急" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查医生" prop="doctor">
              <el-input v-model="formData.doctor" placeholder="请输入医生姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>病史记录</el-divider>

        <el-form-item label="既往病史" prop="medicalHistory">
          <el-input
            v-model="formData.medicalHistory"
            type="textarea"
            :rows="3"
            placeholder="请填写既往病史"
          />
        </el-form-item>

        <el-form-item label="过敏史" prop="allergyHistory">
          <el-input
            v-model="formData.allergyHistory"
            type="textarea"
            :rows="2"
            placeholder="请填写过敏史"
          />
        </el-form-item>

        <el-form-item label="用药记录" prop="medication">
          <el-input
            v-model="formData.medication"
            type="textarea"
            :rows="2"
            placeholder="请填写用药记录"
          />
        </el-form-item>

        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input
            v-model="formData.diagnosis"
            type="textarea"
            :rows="3"
            placeholder="请填写诊断结果"
          />
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="formData.notes"
            type="textarea"
            :rows="2"
            placeholder="其他备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="健康档案详情"
      width="700px"
      destroy-on-close
    >
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="住客姓名" :span="1">{{ detailData.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="性别" :span="1">{{ detailData.elderlyGender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="年龄" :span="1">{{ detailData.elderlyAge }} 岁</el-descriptions-item>
        <el-descriptions-item label="检查日期" :span="1">{{ detailData.checkDate }}</el-descriptions-item>
        <el-descriptions-item label="健康状态" :span="2">
          <el-tag :type="getHealthStatusType(detailData.healthStatus)" effect="dark">
            {{ getHealthStatusText(detailData.healthStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="血压" :span="1">
          <span v-if="detailData.systolicPressure && detailData.diastolicPressure">{{ detailData.systolicPressure }}/{{ detailData.diastolicPressure }} mmHg</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="血糖" :span="1"><span v-if="detailData.bloodSugar">{{ detailData.bloodSugar }} mmol/L</span><span v-else>-</span></el-descriptions-item>
        <el-descriptions-item label="心率" :span="1"><span v-if="detailData.heartRate">{{ detailData.heartRate }} bpm</span><span v-else>-</span></el-descriptions-item>
        <el-descriptions-item label="体温" :span="1"><span v-if="detailData.temperature">{{ detailData.temperature }} ℃</span><span v-else>-</span></el-descriptions-item>
        <el-descriptions-item label="血氧" :span="1"><span v-if="detailData.bloodOxygen">{{ detailData.bloodOxygen }}%</span><span v-else>-</span></el-descriptions-item>
        <el-descriptions-item label="体重" :span="1"><span v-if="detailData.weight">{{ detailData.weight }} kg</span><span v-else>-</span></el-descriptions-item>
        <el-descriptions-item label="身高" :span="1"><span v-if="detailData.height">{{ detailData.height }} cm</span><span v-else>-</span></el-descriptions-item>
        <el-descriptions-item label="检查医生" :span="2">{{ detailData.doctor || '-' }}</el-descriptions-item>
        <el-descriptions-item label="既往病史" :span="2">{{ detailData.medicalHistory || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">{{ detailData.allergyHistory || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用药记录" :span="2">{{ detailData.medication || '-' }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果" :span="2">{{ detailData.diagnosis || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.notes || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getHealthRecordList,
  createHealthRecord,
  updateHealthRecord,
  deleteHealthRecord,
  getHealthStatistics,
  getHealthRecordById
} from '@/api/healthRecord'
import { getElderlyList } from '@/api/elderly'

// 统计数据
const statistics = reactive({
  good: 0,
  normal: 0,
  poor: 0,
  critical: 0
})

// 搜索表单
const searchForm = reactive({
  elderlyName: '',
  healthStatus: null,
  dateRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 住客列表
const elderlyList = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  elderlyId: null,
  systolicPressure: null,
  diastolicPressure: null,
  bloodSugar: null,
  heartRate: null,
  temperature: null,
  bloodOxygen: null,
  weight: null,
  height: null,
  healthStatus: 1,
  medicalHistory: '',
  allergyHistory: '',
  medication: '',
  diagnosis: '',
  doctor: '',
  notes: '',
  checkDate: ''
})

const rules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  checkDate: [{ required: true, message: '请选择检查日期', trigger: 'change' }],
  healthStatus: [{ required: true, message: '请选择健康状态', trigger: 'change' }]
}

// 详情对话框
const detailVisible = ref(false)
const detailData = ref(null)

// 获取统计数据
const loadStatistics = async () => {
  try {
    const res = await getHealthStatistics()
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 获取住客列表
const loadElderlyList = async () => {
  try {
    const res = await getElderlyList({ page: 1, size: 1000 })
    elderlyList.value = res.data.records || []
  } catch (error) {
    console.error('加载住客列表失败', error)
  }
}

// 获取表格数据
const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      elderlyName: searchForm.elderlyName,
      healthStatus: searchForm.healthStatus
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await getHealthRecordList(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadTableData()
}

// 重置
const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.healthStatus = null
  searchForm.dateRange = []
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pagination.size = val
  loadTableData()
}

const handlePageChange = (val) => {
  pagination.page = val
  loadTableData()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增健康档案'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑健康档案'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getHealthRecordById(row.id)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    } else {
      ElMessage.error(res.message || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该健康档案吗？', '提示', {
      type: 'warning'
    })
    await deleteHealthRecord(row.id)
    ElMessage.success('删除成功')
    loadTableData()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateHealthRecord(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createHealthRecord(formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    loadTableData()
    loadStatistics()
  } catch (error) {
    console.error('提交失败', error)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.elderlyId = null
  formData.systolicPressure = null
  formData.diastolicPressure = null
  formData.bloodSugar = null
  formData.heartRate = null
  formData.temperature = null
  formData.bloodOxygen = null
  formData.weight = null
  formData.height = null
  formData.healthStatus = 1
  formData.medicalHistory = ''
  formData.allergyHistory = ''
  formData.medication = ''
  formData.diagnosis = ''
  formData.doctor = ''
  formData.notes = ''
  formData.checkDate = new Date().toISOString().split('T')[0]
}

// 健康状态样式
const getHealthStatusType = (status) => {
  const types = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'info'
  }
  return types[status] || 'info'
}

const getHealthStatusText = (status) => {
  const texts = {
    1: '良好',
    2: '一般',
    3: '较差',
    4: '危急'
  }
  return texts[status] || '未知'
}

onMounted(() => {
  loadStatistics()
  loadElderlyList()
  loadTableData()
})
</script>

<style scoped>
.health-record-container {
  padding: 20px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 15px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-3px);
}

.stat-icon {
  font-size: 40px;
  margin-right: 15px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 不同状态的颜色 */
.stat-card.good .stat-icon {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}
.stat-card.good .stat-value {
  color: #67c23a;
}

.stat-card.normal .stat-icon {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}
.stat-card.normal .stat-value {
  color: #e6a23c;
}

.stat-card.poor .stat-icon {
  background-color: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}
.stat-card.poor .stat-value {
  color: #f56c6c;
}

.stat-card.critical .stat-icon {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}
.stat-card.critical .stat-value {
  color: #909399;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 搜索表单 */
.search-form {
  margin-bottom: 20px;
}

/* 分页 */
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

/* 健康档案表单 */
.health-form {
  max-height: 600px;
  overflow-y: auto;
}

:deep(.el-divider__text) {
  font-size: 14px;
  font-weight: bold;
  color: #409eff;
}
</style>
