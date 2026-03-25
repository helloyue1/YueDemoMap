<template>
  <div class="care-record-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>护理记录管理</span>
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
        <el-form-item label="护理员">
          <el-input v-model="searchForm.caregiverName" placeholder="请输入护理员姓名" clearable />
        </el-form-item>
        <el-form-item label="护理类型">
          <el-select v-model="searchForm.careType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="晨间护理" :value="1" />
            <el-option label="晚间护理" :value="2" />
            <el-option label="饮食护理" :value="3" />
            <el-option label="康复训练" :value="4" />
            <el-option label="用药协助" :value="5" />
            <el-option label="其他" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="待执行" :value="1" />
            <el-option label="执行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="elderlyName" label="住客姓名" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="careType" label="护理类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCareTypeType(row.careType)">
              {{ getCareTypeText(row.careType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="careContent" label="护理内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="caregiverName" label="护理员" width="120" />
        <el-table-column prop="careDuration" label="时长(分)" width="90" />
        <el-table-column prop="careEffect" label="效果" width="80">
          <template #default="{ row }">
            <el-tag :type="getEffectType(row.careEffect)">
              {{ getEffectText(row.careEffect) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="careTime" label="护理时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住客" prop="elderlyId">
              <el-select v-model="formData.elderlyId" placeholder="请选择住客" style="width: 100%" filterable @change="handleElderlyChange">
                <el-option
                  v-for="item in elderlyList"
                  :key="item.id"
                  :label="item.realName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理类型" prop="careType">
              <el-select v-model="formData.careType" placeholder="请选择护理类型" style="width: 100%">
                <el-option label="晨间护理" :value="1" />
                <el-option label="晚间护理" :value="2" />
                <el-option label="饮食护理" :value="3" />
                <el-option label="康复训练" :value="4" />
                <el-option label="用药协助" :value="5" />
                <el-option label="其他" :value="6" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="护理内容" prop="careContent">
          <el-input v-model="formData.careContent" type="textarea" :rows="3" placeholder="请输入护理内容" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理员" prop="caregiverId">
              <el-select v-model="formData.caregiverId" placeholder="请选择护理员" style="width: 100%" filterable>
                <el-option
                  v-for="item in caregiverList"
                  :key="item.id"
                  :label="item.realName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理时长" prop="careDuration">
              <el-input-number v-model="formData.careDuration" :min="1" :max="480" style="width: 100%" placeholder="分钟" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理效果" prop="careEffect">
              <el-select v-model="formData.careEffect" placeholder="请选择护理效果" style="width: 100%">
                <el-option label="良好" :value="1" />
                <el-option label="一般" :value="2" />
                <el-option label="较差" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理时间" prop="careTime">
              <el-date-picker v-model="formData.careTime" type="datetime" placeholder="选择护理时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="住客状况" prop="elderlyCondition">
          <el-input v-model="formData.elderlyCondition" placeholder="请输入住客护理前后的状况" />
        </el-form-item>
        <el-form-item label="异常情况" prop="abnormalSituation">
          <el-input v-model="formData.abnormalSituation" type="textarea" :rows="2" placeholder="如有异常情况请描述" />
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="formData.notes" type="textarea" :rows="2" placeholder="其他备注信息" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待执行" :value="1" />
            <el-option label="执行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="护理记录详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="住客姓名">{{ viewData.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ viewData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="护理类型">
          <el-tag :type="getCareTypeType(viewData.careType)">{{ getCareTypeText(viewData.careType) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="护理效果">
          <el-tag :type="getEffectType(viewData.careEffect)">{{ getEffectText(viewData.careEffect) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="护理内容" :span="2">{{ viewData.careContent }}</el-descriptions-item>
        <el-descriptions-item label="护理员">{{ viewData.caregiverName }}</el-descriptions-item>
        <el-descriptions-item label="护理时长">{{ viewData.careDuration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="护理时间">{{ viewData.careTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">{{ getStatusText(viewData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="住客状况" :span="2">{{ viewData.elderlyCondition || '无' }}</el-descriptions-item>
        <el-descriptions-item label="异常情况" :span="2">{{ viewData.abnormalSituation || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.notes || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getCareRecordList, createCareRecord, updateCareRecord, deleteCareRecord, getCareRecordById } from '@/api/care'
import { getElderlyList } from '@/api/elderly'
import { getUserList } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const loading = ref(false)

const searchForm = reactive({
  elderlyName: '',
  caregiverName: '',
  careType: null,
  status: null
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增护理记录')
const formData = reactive({
  id: null,
  elderlyId: null,
  elderlyName: '',
  roomNumber: '',
  carePlanId: null,
  careType: 1,
  careContent: '',
  caregiverId: null,
  caregiverName: '',
  careDuration: 30,
  careEffect: 1,
  elderlyCondition: '',
  abnormalSituation: '',
  notes: '',
  careTime: '',
  status: 1
})

const viewData = reactive({})

const elderlyList = ref([])
const caregiverList = ref([])

const rules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  careType: [{ required: true, message: '请选择护理类型', trigger: 'change' }],
  careContent: [{ required: true, message: '请输入护理内容', trigger: 'blur' }],
  caregiverId: [{ required: true, message: '请选择护理员', trigger: 'change' }],
  careTime: [{ required: true, message: '请选择护理时间', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const formRef = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      elderlyName: searchForm.elderlyName || undefined,
      caregiverName: searchForm.caregiverName || undefined,
      careType: searchForm.careType || undefined,
      status: searchForm.status || undefined
    }
    const res = await getCareRecordList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadElderlyList = async () => {
  try {
    const res = await getElderlyList({ page: 1, size: 1000 })
    if (res.code === 200) {
      elderlyList.value = res.data.records
    }
  } catch (error) {
    console.error('加载住客列表失败', error)
  }
}

const loadCaregiverList = async () => {
  try {
    const res = await getUserList({ page: 1, size: 1000 })
    if (res.code === 200) {
      caregiverList.value = res.data.records.filter(user => user.status === 1)
    }
  } catch (error) {
    console.error('加载护理员列表失败', error)
  }
}

const handleElderlyChange = (elderlyId) => {
  const selectedElderly = elderlyList.value.find(e => e.id === elderlyId)
  if (selectedElderly) {
    formData.elderlyName = selectedElderly.realName
    formData.roomNumber = selectedElderly.roomNumber
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.caregiverName = ''
  searchForm.careType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增护理记录'
  Object.assign(formData, {
    id: null,
    elderlyId: route.query.elderlyId ? parseInt(route.query.elderlyId) : null,
    elderlyName: '',
    roomNumber: '',
    carePlanId: route.query.planId ? parseInt(route.query.planId) : null,
    careType: 1,
    careContent: '',
    caregiverId: null,
    caregiverName: '',
    careDuration: 30,
    careEffect: 1,
    elderlyCondition: '',
    abnormalSituation: '',
    notes: '',
    careTime: new Date().toISOString().slice(0, 19),
    status: 1
  })
  
  // 如果有传入住客ID，自动设置住客信息
  if (formData.elderlyId) {
    handleElderlyChange(formData.elderlyId)
  }
  
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑护理记录'
  try {
    const res = await getCareRecordById(row.id)
    if (res.code === 200) {
      Object.assign(formData, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取护理记录详情失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getCareRecordById(row.id)
    if (res.code === 200) {
      Object.assign(viewData, res.data)
      viewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取护理记录详情失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该护理记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteCareRecord(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 设置护理员姓名
    const selectedCaregiver = caregiverList.value.find(c => c.id === formData.caregiverId)
    
    // 转换日期格式为ISO格式（带T）
    let careTime = formData.careTime
    if (careTime && typeof careTime === 'string' && careTime.includes(' ')) {
      careTime = careTime.replace(' ', 'T')
    }
    
    const submitData = {
      ...formData,
      caregiverName: selectedCaregiver?.realName || '',
      careTime: careTime
    }
    
    const res = formData.id 
      ? await updateCareRecord(formData.id, submitData) 
      : await createCareRecord(submitData)
    
    if (res.code === 200) {
      ElMessage.success(formData.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) {
    console.error('提交失败', error)
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

const getCareTypeText = (type) => {
  const map = { 1: '晨间护理', 2: '晚间护理', 3: '饮食护理', 4: '康复训练', 5: '用药协助', 6: '其他' }
  return map[type] || '未知'
}

const getCareTypeType = (type) => {
  const map = { 1: 'success', 2: 'primary', 3: 'warning', 4: 'danger', 5: 'info', 6: '' }
  return map[type] || ''
}

const getEffectText = (effect) => {
  const map = { 1: '良好', 2: '一般', 3: '较差' }
  return map[effect] || '未知'
}

const getEffectType = (effect) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[effect] || ''
}

const getStatusText = (status) => {
  const map = { 1: '待执行', 2: '执行中', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }
  return map[status] || ''
}

onMounted(() => {
  loadData()
  loadElderlyList()
  loadCaregiverList()
})
</script>

<style scoped>
.care-record-container {
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
