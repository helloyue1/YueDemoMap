<template>
  <div class="check-out-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>住客退住</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="realName" label="住客姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="healthStatus" label="健康状况" width="100">
          <template #default="{ row }">
            <el-tag :type="getHealthStatusType(row.healthStatus)">
              {{ getHealthStatusText(row.healthStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="emergencyContact" label="紧急联系人" width="120" />
        <el-table-column prop="emergencyPhone" label="紧急电话" width="130" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleCheckOut(row)">办理退住</el-button>
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

    <el-dialog v-model="dialogVisible" title="退住确认" width="600px">
      <el-form :model="checkOutForm" :rules="checkOutRules" ref="checkOutFormRef" label-width="120px">
        <el-form-item label="住客姓名">
          <el-input v-model="checkOutForm.realName" disabled />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="checkOutForm.roomNumber" disabled />
        </el-form-item>
        <el-form-item label="入住日期">
          <el-input v-model="checkOutForm.checkInDate" disabled />
        </el-form-item>
        <el-form-item label="退住日期" prop="checkOutDate">
          <el-date-picker 
            v-model="checkOutForm.checkOutDate" 
            type="date" 
            placeholder="选择退住日期" 
            style="width: 100%" 
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="退住原因" prop="reason">
          <el-select v-model="checkOutForm.reason" placeholder="请选择退住原因" style="width: 100%">
            <el-option label="健康原因" :value="1" />
            <el-option label="家庭原因" :value="2" />
            <el-option label="经济原因" :value="3" />
            <el-option label="转院" :value="4" />
            <el-option label="其他原因" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="checkOutForm.notes" type="textarea" :rows="3" placeholder="请输入备注信息" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="submitLoading">确认退住</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getElderlyList, checkOutElderly } from '@/api/elderly'

const searchForm = reactive({
  name: '',
  roomNumber: ''
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const submitLoading = ref(false)
const checkOutForm = reactive({
  id: null,
  name: '',
  roomNumber: '',
  checkInDate: '',
  checkOutDate: '',
  reason: null,
  notes: ''
})

const checkOutRules = {
  checkOutDate: [{ required: true, message: '请选择退住日期', trigger: 'change' }],
  reason: [{ required: true, message: '请选择退住原因', trigger: 'change' }]
}

const checkOutFormRef = ref(null)

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      realName: searchForm.realName,
      roomNumber: searchForm.roomNumber,
      status: 1  // 只查询在住的住客
    }
    const res = await getElderlyList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
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
  searchForm.realName = ''
  searchForm.roomNumber = ''
  handleSearch()
}

const handleCheckOut = (row) => {
  Object.assign(checkOutForm, {
    id: row.id,
    realName: row.realName,
    roomNumber: row.roomNumber,
    checkInDate: row.checkInDate,
    checkOutDate: new Date().toISOString().split('T')[0],
    reason: null,
    notes: ''
  })
  dialogVisible.value = true
}

const handleConfirm = async () => {
  try {
    await checkOutFormRef.value.validate()
    await ElMessageBox.confirm('确定要为该住客办理退住吗？退住后该房间将释放一个床位。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    submitLoading.value = true
    
    // 调用退住接口
    await checkOutElderly(checkOutForm.id)
    
    ElMessage.success('退住办理成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退住办理失败')
    }
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
.check-out-container {
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
