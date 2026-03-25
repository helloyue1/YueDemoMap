<template>
  <div class="health-archive-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康档案</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增档案
          </el-button>
        </div>
      </template>

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
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="elderlyName" label="住客姓名" width="120" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="bloodPressure" label="血压" width="120" />
        <el-table-column prop="bloodSugar" label="血糖" width="100" />
        <el-table-column prop="heartRate" label="心率" width="100" />
        <el-table-column prop="healthStatus" label="健康状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHealthStatusType(row.healthStatus)">
              {{ getHealthStatusText(row.healthStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastCheckTime" label="最近体检" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看详情</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="handleRecord(row)">记录</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="住客姓名" prop="elderlyName">
          <el-input v-model="formData.elderlyName" placeholder="请输入住客姓名" />
        </el-form-item>
        <el-form-item label="血压" prop="bloodPressure">
          <el-input v-model="formData.bloodPressure" placeholder="例如：120/80 mmHg" />
        </el-form-item>
        <el-form-item label="血糖" prop="bloodSugar">
          <el-input v-model="formData.bloodSugar" placeholder="例如：5.5 mmol/L" />
        </el-form-item>
        <el-form-item label="心率" prop="heartRate">
          <el-input-number v-model="formData.heartRate" :min="40" :max="200" />
        </el-form-item>
        <el-form-item label="体重" prop="weight">
          <el-input-number v-model="formData.weight" :min="30" :max="200" :step="0.1" />
        </el-form-item>
        <el-form-item label="健康状态" prop="healthStatus">
          <el-select v-model="formData.healthStatus" placeholder="请选择健康状态" style="width: 100%">
            <el-option label="良好" :value="1" />
            <el-option label="一般" :value="2" />
            <el-option label="较差" :value="3" />
            <el-option label="危急" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="病史" prop="medicalHistory">
          <el-input v-model="formData.medicalHistory" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="formData.notes" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getHealthRecordList, createHealthRecord, updateHealthRecord } from '@/api/healthRecord'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  elderlyName: '',
  healthStatus: null
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增健康档案')
const formData = reactive({
  id: null,
  elderlyId: null,
  elderlyName: '',
  bloodPressure: '',
  bloodSugar: '',
  heartRate: 75,
  weight: 65.0,
  healthStatus: 1,
  medicalHistory: '',
  notes: ''
})

const rules = {
  elderlyName: [{ required: true, message: '请输入住客姓名', trigger: 'blur' }],
  bloodPressure: [{ required: true, message: '请输入血压', trigger: 'blur' }],
  bloodSugar: [{ required: true, message: '请输入血糖', trigger: 'blur' }],
  heartRate: [{ required: true, message: '请输入心率', trigger: 'blur' }],
  healthStatus: [{ required: true, message: '请选择健康状态', trigger: 'change' }]
}

const formRef = ref(null)

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      elderlyName: searchForm.elderlyName,
      healthStatus: searchForm.healthStatus
    }
    const res = await getHealthRecordList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.healthStatus = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增健康档案'
  Object.assign(formData, {
    id: null,
    elderlyId: null,
    elderlyName: '',
    bloodPressure: '',
    bloodSugar: '',
    heartRate: 75,
    weight: 65.0,
    healthStatus: 1,
    medicalHistory: '',
    notes: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑健康档案'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleView = (row) => {
  ElMessage.info('查看详情功能开发中')
}

const handleRecord = (row) => {
  ElMessage.info('记录功能开发中')
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    const res = formData.id ? await updateHealthRecord(formData.id, formData) : await createHealthRecord(formData)
    if (res.code === 200) {
      ElMessage.success(formData.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('操作失败')
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

const getHealthStatusText = (status) => {
  const map = { 1: '良好', 2: '一般', 3: '较差', 4: '危急' }
  return map[status] || '未知'
}

const getHealthStatusType = (status) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.health-archive-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
