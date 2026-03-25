<template>
  <div class="health-record-page-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon blue">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.todayCount }}</div>
            <div class="stat-label">今日记录</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon green">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.weekCount }}</div>
            <div class="stat-label">本周记录</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon orange">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalCount }}</div>
            <div class="stat-label">总记录数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon purple">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ tableData.length > 0 ? '正常' : '暂无' }}</div>
            <div class="stat-label">监测状态</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康记录</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增记录
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="记录类型">
          <el-select v-model="searchForm.recordType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="血压测量" :value="1" />
            <el-option label="血糖监测" :value="2" />
            <el-option label="体重测量" :value="3" />
            <el-option label="心率测量" :value="4" />
            <el-option label="体温测量" :value="5" />
            <el-option label="血氧测量" :value="6" />
            <el-option label="综合记录" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
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

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="elderlyName" label="住客姓名" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="recordType" label="记录类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ getRecordTypeText(row.recordType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="检测数据" min-width="200">
          <template #default="{ row }">
            <!-- 血压测量 -->
            <template v-if="row.recordType === 1">
              <span v-if="row.systolicPressure && row.diastolicPressure" class="data-item">
                <el-tag size="small" type="danger">血压</el-tag> {{ row.systolicPressure }}/{{ row.diastolicPressure }} mmHg
              </span>
              <span v-else>-</span>
            </template>
            <!-- 血糖监测 -->
            <template v-else-if="row.recordType === 2">
              <span v-if="row.bloodSugar" class="data-item">
                <el-tag size="small" type="warning">血糖</el-tag> {{ row.bloodSugar }} mmol/L
              </span>
              <span v-else>-</span>
            </template>
            <!-- 体重测量 -->
            <template v-else-if="row.recordType === 3">
              <span v-if="row.weight" class="data-item">
                <el-tag size="small" type="success">体重</el-tag> {{ row.weight }} kg
              </span>
              <span v-else>-</span>
            </template>
            <!-- 心率测量 -->
            <template v-else-if="row.recordType === 4">
              <span v-if="row.heartRate" class="data-item">
                <el-tag size="small" type="primary">心率</el-tag> {{ row.heartRate }} bpm
              </span>
              <span v-else>-</span>
            </template>
            <!-- 体温测量 -->
            <template v-else-if="row.recordType === 5">
              <span v-if="row.bodyTemperature" class="data-item">
                <el-tag size="small" type="info">体温</el-tag> {{ row.bodyTemperature }} ℃
              </span>
              <span v-else>-</span>
            </template>
            <!-- 血氧测量 -->
            <template v-else-if="row.recordType === 6">
              <span v-if="row.bloodOxygen" class="data-item">
                <el-tag size="small" type="success">血氧</el-tag> {{ row.bloodOxygen }}%
              </span>
              <span v-else>-</span>
            </template>
            <!-- 综合记录 -->
            <template v-else-if="row.recordType === 7">
              <div class="multi-data">
                <span v-if="row.systolicPressure && row.diastolicPressure" class="data-item">
                  <el-tag size="small" type="danger">血压</el-tag> {{ row.systolicPressure }}/{{ row.diastolicPressure }}
                </span>
                <span v-if="row.bloodSugar" class="data-item">
                  <el-tag size="small" type="warning">血糖</el-tag> {{ row.bloodSugar }}
                </span>
                <span v-if="row.weight" class="data-item">
                  <el-tag size="small" type="success">体重</el-tag> {{ row.weight }}
                </span>
                <span v-if="row.heartRate" class="data-item">
                  <el-tag size="small" type="primary">心率</el-tag> {{ row.heartRate }}
                </span>
                <span v-if="row.bodyTemperature" class="data-item">
                  <el-tag size="small" type="info">体温</el-tag> {{ row.bodyTemperature }}
                </span>
                <span v-if="row.bloodOxygen" class="data-item">
                  <el-tag size="small" type="success">血氧</el-tag> {{ row.bloodOxygen }}%
                </span>
                <span v-if="!row.systolicPressure && !row.bloodSugar && !row.weight && !row.heartRate && !row.bodyTemperature && !row.bloodOxygen">-</span>
              </div>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="healthNote" label="健康备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="recorderName" label="记录人" width="120" />
        <el-table-column prop="recordTime" label="记录时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
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
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        label-width="100px"
        class="health-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住客" prop="elderlyId">
              <el-select
                v-model="formData.elderlyId"
                placeholder="请选择住客"
                style="width: 100%"
                filterable
              >
                <el-option
                  v-for="item in elderlyList"
                  :key="item.id"
                  :label="item.realName + ' (' + item.roomNumber + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录类型" prop="recordType">
              <el-select v-model="formData.recordType" placeholder="请选择记录类型" style="width: 100%">
                <el-option label="血压测量" :value="1" />
                <el-option label="血糖监测" :value="2" />
                <el-option label="体重测量" :value="3" />
                <el-option label="心率测量" :value="4" />
                <el-option label="体温测量" :value="5" />
                <el-option label="血氧测量" :value="6" />
                <el-option label="综合记录" :value="7" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="记录时间" prop="recordTime">
              <el-date-picker
                v-model="formData.recordTime"
                type="datetime"
                placeholder="选择记录时间"
                style="width: 100%"
                value-format="YYYY-MM-DDTHH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录人">
              <el-input v-model="formData.recorderName" placeholder="请输入记录人姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 血压测量 -->
        <template v-if="formData.recordType === 1 || formData.recordType === 7">
          <el-divider>血压数据</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="收缩压">
                <el-input-number v-model="formData.systolicPressure" :min="50" :max="250" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="舒张压">
                <el-input-number v-model="formData.diastolicPressure" :min="30" :max="150" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 血糖监测 -->
        <template v-if="formData.recordType === 2 || formData.recordType === 7">
          <el-divider>血糖数据</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="血糖">
                <el-input-number v-model="formData.bloodSugar" :min="1" :max="30" :precision="1" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单位">
                <span style="line-height: 32px;">mmol/L</span>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 体重测量 -->
        <template v-if="formData.recordType === 3 || formData.recordType === 7">
          <el-divider>体重数据</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="体重">
                <el-input-number v-model="formData.weight" :min="20" :max="200" :precision="1" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单位">
                <span style="line-height: 32px;">kg</span>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 心率测量 -->
        <template v-if="formData.recordType === 4 || formData.recordType === 7">
          <el-divider>心率数据</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="心率">
                <el-input-number v-model="formData.heartRate" :min="30" :max="200" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单位">
                <span style="line-height: 32px;">次/分钟</span>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 体温测量 -->
        <template v-if="formData.recordType === 5 || formData.recordType === 7">
          <el-divider>体温数据</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="体温">
                <el-input-number v-model="formData.bodyTemperature" :min="35" :max="42" :precision="1" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单位">
                <span style="line-height: 32px;">℃</span>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 血氧测量 -->
        <template v-if="formData.recordType === 6 || formData.recordType === 7">
          <el-divider>血氧数据</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="血氧">
                <el-input-number v-model="formData.bloodOxygen" :min="50" :max="100" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单位">
                <span style="line-height: 32px;">%</span>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <el-form-item label="健康备注">
          <el-input
            v-model="formData.healthNote"
            type="textarea"
            :rows="3"
            placeholder="请输入健康备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="健康记录详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="住客姓名">{{ currentRow.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentRow.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="记录类型">
          <el-tag :type="getRecordTypeTag(currentRow.recordType)">
            {{ getRecordTypeText(currentRow.recordType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="记录时间">{{ currentRow.recordTime }}</el-descriptions-item>
        <!-- 血压测量 -->
        <template v-if="currentRow.recordType === 1">
          <el-descriptions-item label="收缩压">{{ currentRow.systolicPressure || '-' }} mmHg</el-descriptions-item>
          <el-descriptions-item label="舒张压">{{ currentRow.diastolicPressure || '-' }} mmHg</el-descriptions-item>
        </template>
        <!-- 血糖监测 -->
        <template v-if="currentRow.recordType === 2">
          <el-descriptions-item label="血糖" :span="2">{{ currentRow.bloodSugar || '-' }} mmol/L</el-descriptions-item>
        </template>
        <!-- 体重测量 -->
        <template v-if="currentRow.recordType === 3">
          <el-descriptions-item label="体重" :span="2">{{ currentRow.weight || '-' }} kg</el-descriptions-item>
        </template>
        <!-- 心率测量 -->
        <template v-if="currentRow.recordType === 4">
          <el-descriptions-item label="心率" :span="2">{{ currentRow.heartRate || '-' }} bpm</el-descriptions-item>
        </template>
        <!-- 体温测量 -->
        <template v-if="currentRow.recordType === 5">
          <el-descriptions-item label="体温" :span="2">{{ currentRow.bodyTemperature || '-' }} ℃</el-descriptions-item>
        </template>
        <!-- 血氧测量 -->
        <template v-if="currentRow.recordType === 6">
          <el-descriptions-item label="血氧" :span="2">{{ currentRow.bloodOxygen || '-' }}%</el-descriptions-item>
        </template>
        <!-- 综合记录 -->
        <template v-if="currentRow.recordType === 7">
          <el-descriptions-item label="收缩压">{{ currentRow.systolicPressure || '-' }} mmHg</el-descriptions-item>
          <el-descriptions-item label="舒张压">{{ currentRow.diastolicPressure || '-' }} mmHg</el-descriptions-item>
          <el-descriptions-item label="血糖">{{ currentRow.bloodSugar || '-' }} mmol/L</el-descriptions-item>
          <el-descriptions-item label="体重">{{ currentRow.weight || '-' }} kg</el-descriptions-item>
          <el-descriptions-item label="心率">{{ currentRow.heartRate || '-' }} bpm</el-descriptions-item>
          <el-descriptions-item label="体温">{{ currentRow.bodyTemperature || '-' }} ℃</el-descriptions-item>
          <el-descriptions-item label="血氧" :span="2">{{ currentRow.bloodOxygen || '-' }}%</el-descriptions-item>
        </template>
        <el-descriptions-item label="记录人">{{ currentRow.recorderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="健康备注">{{ currentRow.healthNote || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search, RefreshRight, View, Edit, Delete, Document, TrendCharts, DataLine, User } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getHealthDataList,
  createHealthData,
  updateHealthData,
  deleteHealthData,
  getHealthDataStatistics
} from '@/api/healthData'
import { getElderlyList } from '@/api/elderly'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增健康记录')
const formRef = ref(null)
const currentRow = ref(null)
const isEdit = ref(false)

const searchForm = reactive({
  elderlyName: '',
  recordType: null,
  dateRange: []
})

const formData = reactive({
  id: null,
  elderlyId: null,
  recordType: 1,
  recordTime: null,
  recorderName: '',
  systolicPressure: null,
  diastolicPressure: null,
  bloodPressure: '',
  heartRate: null,
  bodyTemperature: null,
  bloodSugar: null,
  bloodOxygen: null,
  weight: null,
  healthNote: ''
})

const formRules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  recordTime: [{ required: true, message: '请选择记录时间', trigger: 'change' }]
}

const tableData = ref([])
const elderlyList = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const statistics = reactive({
  todayCount: 0,
  weekCount: 0,
  totalCount: 0
})

// 加载住客列表
const loadElderlyList = async () => {
  try {
    const res = await getElderlyList({ page: 1, size: 1000 })
    if (res.code === 200) {
      elderlyList.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载住客列表失败', error)
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getHealthDataStatistics()
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 加载表格数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      elderlyName: searchForm.elderlyName || undefined,
      recordType: searchForm.recordType || undefined,
      startDate: searchForm.dateRange && searchForm.dateRange[0] ? searchForm.dateRange[0] : undefined,
      endDate: searchForm.dateRange && searchForm.dateRange[1] ? searchForm.dateRange[1] : undefined
    }
    const res = await getHealthDataList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.recordType = null
  searchForm.dateRange = []
  handleSearch()
}

const resetForm = () => {
  formData.id = null
  formData.elderlyId = null
  formData.recordType = 1
  const now = new Date()
  formData.recordTime = now.toISOString().slice(0, 19)
  formData.recorderName = ''
  formData.systolicPressure = null
  formData.diastolicPressure = null
  formData.bloodPressure = ''
  formData.heartRate = null
  formData.bodyTemperature = null
  formData.bloodSugar = null
  formData.bloodOxygen = null
  formData.weight = null
  formData.healthNote = ''
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增健康记录'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑健康记录'
  Object.assign(formData, row)
  // 解析血压
  if (row.bloodPressure) {
    const parts = row.bloodPressure.split('/')
    if (parts.length === 2) {
      formData.systolicPressure = parseInt(parts[0])
      formData.diastolicPressure = parseInt(parts[1])
    }
  }
  dialogVisible.value = true
}

const handleView = (row) => {
  currentRow.value = row
  viewDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条健康记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteHealthData(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
        loadStatistics()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    // 构建血压字符串
    if (formData.systolicPressure && formData.diastolicPressure) {
      formData.bloodPressure = `${formData.systolicPressure}/${formData.diastolicPressure}`
    }

    let res
    if (isEdit.value) {
      res = await updateHealthData(formData.id, formData)
    } else {
      res = await createHealthData(formData)
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
      loadStatistics()
    }
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getRecordTypeText = (type) => {
  const map = {
    1: '血压测量',
    2: '血糖监测',
    3: '体重测量',
    4: '心率测量',
    5: '体温测量',
    6: '血氧测量',
    7: '综合记录'
  }
  return map[type] || '未知'
}

const getRecordTypeTag = (type) => {
  const map = {
    1: 'danger',
    2: 'warning',
    3: 'success',
    4: 'primary',
    5: 'info',
    6: 'success',
    7: ''
  }
  return map[type] || ''
}

onMounted(() => {
  loadData()
  loadStatistics()
  loadElderlyList()
})
</script>

<style scoped>
.health-record-page-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 15px;
}

.stat-icon.blue {
  background-color: #ecf5ff;
  color: #409eff;
}

.stat-icon.green {
  background-color: #f0f9eb;
  color: #67c23a;
}

.stat-icon.orange {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.stat-icon.purple {
  background-color: #f5f0ff;
  color: #9254de;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.health-form {
  max-height: 500px;
  overflow-y: auto;
}

.el-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.data-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-right: 8px;
}

.multi-data {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.multi-data .data-item {
  margin-right: 0;
}
</style>
