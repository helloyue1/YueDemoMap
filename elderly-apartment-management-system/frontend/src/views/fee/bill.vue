<template>
  <div class="fee-bill-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>费用账单管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加账单
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="账单月份">
          <el-date-picker
            v-model="searchForm.billMonth"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="缴费状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="待缴费" :value="0" />
            <el-option label="已缴费" :value="1" />
            <el-option label="已逾期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="billNo" label="账单编号" width="160" />
        <el-table-column prop="elderlyName" label="住客姓名" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="80" />
        <el-table-column prop="billMonth" label="账单月份" width="100" />
        <el-table-column label="费用明细" min-width="200">
          <template #default="{ row }">
            <div class="fee-detail">
              <span v-if="row.roomFee > 0">住宿:¥{{ row.roomFee }}</span>
              <span v-if="row.careFee > 0">护理:¥{{ row.careFee }}</span>
              <span v-if="row.mealFee > 0">餐饮:¥{{ row.mealFee }}</span>
              <span v-if="row.medicalFee > 0">医疗:¥{{ row.medicalFee }}</span>
              <span v-if="row.otherFee > 0">其他:¥{{ row.otherFee }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="payableAmount" label="应付金额" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.payableAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付金额" width="100">
          <template #default="{ row }">
            <span style="color: #67c23a;">¥{{ row.paidAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="截止日期" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="success" size="small" @click="handlePay(row)" v-if="row.status === 0">缴费</el-button>
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

    <!-- 添加账单弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加账单"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="选择住客" prop="elderlyId">
          <el-select
            v-model="formData.elderlyId"
            placeholder="请选择住客"
            filterable
            style="width: 100%"
            @change="handleElderlyChange"
          >
            <el-option
              v-for="item in elderlyList"
              :key="item.id"
              :label="item.realName + ' - ' + item.roomNumber"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="账单月份" prop="billMonth">
          <el-date-picker
            v-model="formData.billMonth"
            type="month"
            placeholder="选择账单月份"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="账单周期">
          <el-date-picker
            v-model="formData.billStartDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 48%"
          />
          <span style="margin: 0 10px">至</span>
          <el-date-picker
            v-model="formData.billEndDate"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 48%"
          />
        </el-form-item>

        <el-divider content-position="left">费用明细</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住宿费">
              <el-input-number v-model="formData.roomFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理费">
              <el-input-number v-model="formData.careFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="餐饮费">
              <el-input-number v-model="formData.mealFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医疗费">
              <el-input-number v-model="formData.medicalFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="其他费用">
              <el-input-number v-model="formData.otherFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠金额">
              <el-input-number v-model="formData.discountAmount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="应付金额">
          <el-input v-model="calculatedAmount" disabled style="width: 100%">
            <template #append>元</template>
          </el-input>
        </el-form-item>

        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
            v-model="formData.dueDate"
            type="date"
            placeholder="选择截止日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 账单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="账单详情"
      width="500px"
    >
      <el-descriptions :column="1" border v-if="currentBill">
        <el-descriptions-item label="账单编号">{{ currentBill.billNo }}</el-descriptions-item>
        <el-descriptions-item label="住客姓名">{{ currentBill.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentBill.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="账单月份">{{ currentBill.billMonth }}</el-descriptions-item>
        <el-descriptions-item label="费用明细">
          <div v-if="currentBill.roomFee > 0">住宿费: ¥{{ currentBill.roomFee }}</div>
          <div v-if="currentBill.careFee > 0">护理费: ¥{{ currentBill.careFee }}</div>
          <div v-if="currentBill.mealFee > 0">餐饮费: ¥{{ currentBill.mealFee }}</div>
          <div v-if="currentBill.medicalFee > 0">医疗费: ¥{{ currentBill.medicalFee }}</div>
          <div v-if="currentBill.otherFee > 0">其他费: ¥{{ currentBill.otherFee }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="应付金额" label-class-name="amount-label">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">¥{{ currentBill.payableAmount?.toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="已付金额">
          <span style="color: #67c23a;">¥{{ currentBill.paidAmount?.toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentBill.status)">
            {{ getStatusText(currentBill.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="截止日期">{{ currentBill.dueDate }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentBill.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const searchForm = reactive({
  elderlyName: '',
  billMonth: '',
  status: null
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const currentBill = ref(null)
const elderlyList = ref([])

// 表单数据
const formData = reactive({
  elderlyId: null,
  billMonth: '',
  billStartDate: '',
  billEndDate: '',
  roomFee: 0,
  careFee: 0,
  mealFee: 0,
  medicalFee: 0,
  otherFee: 0,
  discountAmount: 0,
  dueDate: '',
  remark: ''
})

// 表单验证规则
const formRules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  billMonth: [{ required: true, message: '请选择账单月份', trigger: 'change' }],
  dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
}

// 计算应付金额
const calculatedAmount = computed(() => {
  const total = formData.roomFee + formData.careFee + formData.mealFee + formData.medicalFee + formData.otherFee
  const payable = total - formData.discountAmount
  return payable > 0 ? payable.toFixed(2) : '0.00'
})

// 加载账单列表
const loadData = async () => {
  try {
    const res = await request.get('/fee/bills', {
      params: {
        page: pagination.page,
        size: pagination.size,
        elderlyName: searchForm.elderlyName || undefined,
        billMonth: searchForm.billMonth || undefined,
        status: searchForm.status
      }
    })
    if (res.success) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

// 加载住客列表
const loadElderlyList = async () => {
  try {
    const res = await request.get('/user/elderly', {
      params: { page: 1, size: 1000, status: 1 }
    })
    if (res.success) {
      elderlyList.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载住客列表失败', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.billMonth = ''
  searchForm.status = null
  handleSearch()
}

// 打开添加弹窗
const handleAdd = () => {
  resetForm()
  loadElderlyList()
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.elderlyId = null
  formData.billMonth = ''
  formData.billStartDate = ''
  formData.billEndDate = ''
  formData.roomFee = 0
  formData.careFee = 0
  formData.mealFee = 0
  formData.medicalFee = 0
  formData.otherFee = 0
  formData.discountAmount = 0
  formData.dueDate = ''
  formData.remark = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 住客选择变化
const handleElderlyChange = (val) => {
  const elderly = elderlyList.value.find(item => item.id === val)
  if (elderly && elderly.roomFee) {
    formData.roomFee = elderly.roomFee
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const payload = {
          ...formData,
          totalAmount: formData.roomFee + formData.careFee + formData.mealFee + formData.medicalFee + formData.otherFee,
          payableAmount: parseFloat(calculatedAmount.value),
          status: 0
        }
        const res = await request.post('/fee/bills', payload)
        if (res.success) {
          ElMessage.success('添加账单成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        ElMessage.error(error.message || '添加账单失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await request.get(`/fee/bills/${row.id}`)
    if (res.success) {
      currentBill.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取账单详情失败')
  }
}

// 缴费
const handlePay = (row) => {
  ElMessage.info('缴费功能开发中')
}

// 删除账单
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该账单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/fee/bills/${row.id}`)
      if (res.success) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getStatusText = (status) => {
  const map = { 0: '待缴费', 1: '已缴费', 2: '已逾期' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.fee-bill-container {
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

.fee-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.fee-detail span {
  background-color: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

:deep(.amount-label) {
  background-color: #fef0f0 !important;
}
</style>
