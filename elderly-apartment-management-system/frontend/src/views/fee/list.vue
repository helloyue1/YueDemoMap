<template>
  <div class="fee-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>费用账单管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="handleAddBill">
              <el-icon><Plus /></el-icon>添加账单
            </el-button>
            <el-button type="primary" @click="handleGenerateBills">
              <el-icon><Plus /></el-icon>生成月度账单
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="statistics-row">
        <el-col :span="6">
          <el-statistic title="本月应收" :value="statistics.totalReceivable || 0" prefix="¥" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="本月实收" :value="statistics.totalReceived || 0" prefix="¥" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="本月未收" :value="statistics.totalUnpaid || 0" prefix="¥" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="收缴率" :value="statistics.collectionRate || 0" suffix="%" :precision="2" />
        </el-col>
      </el-row>

      <!-- 搜索表单 -->
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
          />
        </el-form-item>
        <el-form-item label="缴费状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="待缴费" :value="0" />
            <el-option label="部分缴费" :value="1" />
            <el-option label="已缴清" :value="2" />
            <el-option label="已逾期" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="billNo" label="账单编号" width="150" />
        <el-table-column prop="elderlyName" label="住客姓名" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="billMonth" label="账单月份" width="100" />
        <el-table-column label="费用明细" min-width="200">
          <template #default="{ row }">
            <div class="fee-detail">
              <span v-if="row.roomFee > 0">住宿: ¥{{ row.roomFee?.toFixed(2) }}</span>
              <span v-if="row.careFee > 0">护理: ¥{{ row.careFee?.toFixed(2) }}</span>
              <span v-if="row.mealFee > 0">餐饮: ¥{{ row.mealFee?.toFixed(2) }}</span>
              <span v-if="row.medicalFee > 0">医疗: ¥{{ row.medicalFee?.toFixed(2) }}</span>
              <span v-if="row.otherFee > 0">其他: ¥{{ row.otherFee?.toFixed(2) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="payableAmount" label="应付金额" width="120">
          <template #default="{ row }">
            <span class="amount-payable">¥{{ row.payableAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付金额" width="120">
          <template #default="{ row }">
            <span class="amount-paid">¥{{ row.paidAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="截止日期" width="120" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="success" size="small" @click="handlePay(row)" v-if="row.status !== 2">缴费</el-button>
            <el-button link type="warning" size="small" @click="handlePrint(row)">下载</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加账单对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加账单" width="600px" :close-on-click-modal="false">
      <el-form ref="addFormRef" :model="addForm" :rules="addFormRules" label-width="100px">
        <el-form-item label="选择住客" prop="elderlyId">
          <el-select
            v-model="addForm.elderlyId"
            placeholder="请选择住客"
            filterable
            style="width: 100%"
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
            v-model="addForm.billMonth"
            type="month"
            placeholder="选择账单月份"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="账单周期">
          <el-date-picker
            v-model="addForm.billStartDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 48%"
          />
          <span style="margin: 0 10px">至</span>
          <el-date-picker
            v-model="addForm.billEndDate"
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
              <el-input-number v-model="addForm.roomFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理费">
              <el-input-number v-model="addForm.careFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="餐饮费">
              <el-input-number v-model="addForm.mealFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医疗费">
              <el-input-number v-model="addForm.medicalFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="其他费用">
              <el-input-number v-model="addForm.otherFee" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠金额">
              <el-input-number v-model="addForm.discountAmount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="应付金额">
          <el-input :value="calculatedPayable" disabled style="width: 100%">
            <template #append>元</template>
          </el-input>
        </el-form-item>

        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
            v-model="addForm.dueDate"
            type="date"
            placeholder="选择截止日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="addForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAdd" :loading="adding">确定</el-button>
      </template>
    </el-dialog>

    <!-- 生成账单对话框 -->
    <el-dialog v-model="generateDialogVisible" title="生成月度账单" width="400px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="账单月份">
          <el-date-picker
            v-model="generateForm.billMonth"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmGenerate" :loading="generating">确定生成</el-button>
      </template>
    </el-dialog>

    <!-- 缴费对话框 -->
    <el-dialog v-model="payDialogVisible" title="费用缴纳" width="500px">
      <el-descriptions :column="2" border v-if="currentBill">
        <el-descriptions-item label="账单编号">{{ currentBill.billNo }}</el-descriptions-item>
        <el-descriptions-item label="住客姓名">{{ currentBill.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="应付金额">¥{{ currentBill.payableAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">¥{{ currentBill.paidAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="待付金额" :span="2">
          <span class="amount-due">¥{{ (currentBill.payableAmount - currentBill.paidAmount).toFixed(2) }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-form :model="payForm" label-width="100px" style="margin-top: 20px">
        <el-form-item label="缴费金额">
          <el-input-number v-model="payForm.paymentAmount" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="缴费方式">
          <el-select v-model="payForm.paymentMethod" style="width: 100%">
            <el-option label="现金" :value="1" />
            <el-option label="银行卡" :value="2" />
            <el-option label="微信支付" :value="3" />
            <el-option label="支付宝" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费人">
          <el-input v-model="payForm.payerName" placeholder="请输入缴费人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="payForm.payerPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="payForm.remark" type="textarea" rows="3" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" :loading="paying">确认缴费</el-button>
      </template>
    </el-dialog>

    <!-- 查看账单详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="账单详情" width="700px">
      <div v-if="viewBill" class="bill-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border class="detail-section">
          <el-descriptions-item label="账单编号" :span="2">{{ viewBill.billNo }}</el-descriptions-item>
          <el-descriptions-item label="住客姓名">{{ viewBill.elderlyName }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ viewBill.roomNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="账单月份">{{ viewBill.billMonth }}</el-descriptions-item>
          <el-descriptions-item label="账单状态">
            <el-tag :type="getStatusType(viewBill.status)">{{ getStatusText(viewBill.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="账单周期" :span="2">{{ viewBill.billStartDate }} 至 {{ viewBill.billEndDate }}</el-descriptions-item>
          <el-descriptions-item label="缴费截止" :span="2">{{ viewBill.dueDate }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewBill.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 费用明细 -->
        <div class="detail-section">
          <h4 class="section-title">费用明细</h4>
          <el-table :data="feeDetails" border size="small">
            <el-table-column prop="feeName" label="费用项目" />
            <el-table-column prop="unitPrice" label="单价" width="120">
              <template #default="{ row }">¥{{ row.unitPrice?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">¥{{ row.amount?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="description" label="说明" min-width="150" />
          </el-table>
        </div>

        <!-- 结算信息 -->
        <el-descriptions :column="2" border class="detail-section">
          <el-descriptions-item label="费用总额">¥{{ viewBill.totalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额">¥{{ viewBill.discountAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="应付金额">
            <span class="amount-payable">¥{{ viewBill.payableAmount?.toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="已付金额">
            <span class="amount-paid">¥{{ viewBill.paidAmount?.toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="待付金额" :span="2">
            <span class="amount-due">¥{{ (viewBill.payableAmount - viewBill.paidAmount).toFixed(2) }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 缴费记录 -->
        <div class="detail-section" v-if="paymentRecords.length > 0">
          <h4 class="section-title">缴费记录</h4>
          <el-table :data="paymentRecords" border size="small">
            <el-table-column prop="paymentNo" label="缴费单号" width="150" />
            <el-table-column prop="paymentAmount" label="缴费金额" width="120">
              <template #default="{ row }">¥{{ row.paymentAmount?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="缴费方式" width="100">
              <template #default="{ row }">{{ getPaymentMethodText(row.paymentMethod) }}</template>
            </el-table-column>
            <el-table-column prop="payerName" label="缴费人" width="100" />
            <el-table-column prop="createTime" label="缴费时间" min-width="150" />
            <el-table-column prop="remark" label="备注" min-width="150" />
          </el-table>
        </div>
      </div>

      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handlePrint(viewBill)" v-if="viewBill">下载账单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download } from '@element-plus/icons-vue'
import request from '@/api/request'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'

const loading = ref(false)
const generating = ref(false)
const paying = ref(false)
const adding = ref(false)
const generateDialogVisible = ref(false)
const addDialogVisible = ref(false)
const payDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentBill = ref(null)
const viewBill = ref(null)
const feeDetails = ref([])
const paymentRecords = ref([])
const elderlyList = ref([])
const addFormRef = ref(null)

const searchForm = reactive({
  elderlyName: '',
  billMonth: '',
  status: null
})

const generateForm = reactive({
  billMonth: ''
})

const payForm = reactive({
  paymentAmount: 0,
  paymentMethod: 1,
  payerName: '',
  payerPhone: '',
  remark: ''
})

const addForm = reactive({
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

const addFormRules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  billMonth: [{ required: true, message: '请选择账单月份', trigger: 'change' }],
  dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
}

const calculatedPayable = computed(() => {
  const total = addForm.roomFee + addForm.careFee + addForm.mealFee + addForm.medicalFee + addForm.otherFee
  const payable = total - addForm.discountAmount
  return payable > 0 ? payable.toFixed(2) : '0.00'
})

const statistics = reactive({
  totalReceivable: 0,
  totalReceived: 0,
  totalUnpaid: 0,
  collectionRate: 0
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
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

const loadStatistics = async () => {
  try {
    const res = await request.get('/fee/statistics')
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
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

const handleSizeChange = (val) => {
  pagination.size = val
  loadData()
}

const handlePageChange = (val) => {
  pagination.page = val
  loadData()
}

const handleGenerateBills = () => {
  const now = new Date()
  generateForm.billMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  generateDialogVisible.value = true
}

// 加载住客列表
const loadElderlyList = async () => {
  try {
    const res = await request.get('/user/elderly', {
      params: { page: 1, size: 1000, status: 1 }
    })
    if (res.code === 200) {
      elderlyList.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载住客列表失败', error)
  }
}

// 打开添加账单弹窗
const handleAddBill = () => {
  // 重置表单
  addForm.elderlyId = null
  addForm.billMonth = ''
  addForm.billStartDate = ''
  addForm.billEndDate = ''
  addForm.roomFee = 0
  addForm.careFee = 0
  addForm.mealFee = 0
  addForm.medicalFee = 0
  addForm.otherFee = 0
  addForm.discountAmount = 0
  addForm.dueDate = ''
  addForm.remark = ''

  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }

  loadElderlyList()
  addDialogVisible.value = true
}

// 确认添加账单
const confirmAdd = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      adding.value = true
      try {
        const payload = {
          ...addForm,
          totalAmount: addForm.roomFee + addForm.careFee + addForm.mealFee + addForm.medicalFee + addForm.otherFee,
          payableAmount: parseFloat(calculatedPayable.value),
          status: 0
        }
        const res = await request.post('/fee/bills', payload)
        if (res.code === 200) {
          ElMessage.success('添加账单成功')
          addDialogVisible.value = false
          loadData()
          loadStatistics()
        }
      } catch (error) {
        ElMessage.error(error.message || '添加账单失败')
      } finally {
        adding.value = false
      }
    }
  })
}

const confirmGenerate = async () => {
  if (!generateForm.billMonth) {
    ElMessage.warning('请选择账单月份')
    return
  }
  generating.value = true
  try {
    const res = await request.post('/fee/bills/generate', {
      billMonth: generateForm.billMonth
    })
    if (res.code === 200) {
      ElMessage.success('账单生成成功')
      generateDialogVisible.value = false
      loadData()
      loadStatistics()
    }
  } catch (error) {
    ElMessage.error('账单生成失败')
  } finally {
    generating.value = false
  }
}

const handleView = async (row) => {
  viewBill.value = { ...row }
  feeDetails.value = []
  paymentRecords.value = []
  viewDialogVisible.value = true

  try {
    // 获取账单详情
    const billRes = await request.get(`/fee/bills/${row.id}`)
    if (billRes.code === 200) {
      viewBill.value = billRes.data
    }

    // 获取费用明细
    const detailsRes = await request.get(`/fee/bills/${row.id}/details`)
    if (detailsRes.code === 200 && detailsRes.data && detailsRes.data.length > 0) {
      feeDetails.value = detailsRes.data
    } else {
      // 如果没有费用明细数据，根据账单数据生成
      feeDetails.value = generateFeeDetailsFromBill(viewBill.value)
    }

    // 获取缴费记录
    const paymentsRes = await request.get('/fee/payments', {
      params: {
        page: 1,
        size: 100,
        elderlyName: row.elderlyName
      }
    })
    if (paymentsRes.code === 200) {
      // 过滤出当前账单的缴费记录
      paymentRecords.value = paymentsRes.data.records.filter(
        p => p.billId === row.id
      )
    }
  } catch (error) {
    console.error('加载账单详情失败:', error)
    // 如果API调用失败，使用账单数据生成费用明细
    feeDetails.value = generateFeeDetailsFromBill(viewBill.value)
  }
}

// 根据账单数据生成费用明细
const generateFeeDetailsFromBill = (bill) => {
  const details = []
  if (bill.roomFee > 0) {
    details.push({
      feeName: '住宿费',
      unitPrice: bill.roomFee,
      quantity: 1,
      amount: bill.roomFee,
      description: '月度住宿费用'
    })
  }
  if (bill.careFee > 0) {
    details.push({
      feeName: '护理费',
      unitPrice: bill.careFee,
      quantity: 1,
      amount: bill.careFee,
      description: '月度护理费用'
    })
  }
  if (bill.mealFee > 0) {
    details.push({
      feeName: '餐饮费',
      unitPrice: bill.mealFee,
      quantity: 1,
      amount: bill.mealFee,
      description: '月度餐饮费用'
    })
  }
  if (bill.medicalFee > 0) {
    details.push({
      feeName: '医疗费',
      unitPrice: bill.medicalFee,
      quantity: 1,
      amount: bill.medicalFee,
      description: '医疗费用'
    })
  }
  if (bill.otherFee > 0) {
    details.push({
      feeName: '其他费用',
      unitPrice: bill.otherFee,
      quantity: 1,
      amount: bill.otherFee,
      description: '其他费用'
    })
  }
  return details
}

const getPaymentMethodText = (method) => {
  const map = { 1: '现金', 2: '银行卡', 3: '微信支付', 4: '支付宝' }
  return map[method] || '其他'
}

const handlePay = (row) => {
  currentBill.value = row
  payForm.paymentAmount = Number((row.payableAmount - row.paidAmount).toFixed(2))
  payForm.paymentMethod = 1
  payForm.payerName = ''
  payForm.payerPhone = ''
  payForm.remark = ''
  payDialogVisible.value = true
}

const confirmPay = async () => {
  if (!payForm.paymentAmount || payForm.paymentAmount <= 0) {
    ElMessage.warning('请输入有效的缴费金额')
    return
  }
  paying.value = true
  try {
    const res = await request.post('/fee/payments', {
      billId: currentBill.value.id,
      elderlyId: currentBill.value.elderlyId,
      ...payForm
    })
    if (res.code === 200) {
      ElMessage.success('缴费成功')
      payDialogVisible.value = false
      loadData()
      loadStatistics()
    }
  } catch (error) {
    ElMessage.error('缴费失败')
  } finally {
    paying.value = false
  }
}

const handlePrint = async (row) => {
  // 下载账单PDF
  try {
    ElMessage.info('正在生成PDF...')
    
    // 创建临时容器用于生成PDF
    const container = document.createElement('div')
    container.style.cssText = 'position: fixed; left: -9999px; top: 0; width: 800px; padding: 40px; background: white; font-family: "Microsoft YaHei", sans-serif;'
    container.innerHTML = `
      <div style="text-align: center; margin-bottom: 30px;">
        <h2 style="font-size: 24px; margin-bottom: 10px;">养老公寓费用账单</h2>
        <p style="color: #666; font-size: 14px;">账单编号：${row.billNo}</p>
      </div>
      
      <div style="margin-bottom: 30px; border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
        <h3 style="font-size: 16px; margin-bottom: 15px; border-bottom: 2px solid #409eff; padding-bottom: 10px;">基本信息</h3>
        <table style="width: 100%; font-size: 14px;">
          <tr>
            <td style="padding: 8px 0; width: 50%;"><strong>住客姓名：</strong>${row.elderlyName || '-'}</td>
            <td style="padding: 8px 0; width: 50%;"><strong>房间号：</strong>${row.roomNumber || '-'}</td>
          </tr>
          <tr>
            <td style="padding: 8px 0;"><strong>账单月份：</strong>${row.billMonth}</td>
            <td style="padding: 8px 0;"><strong>账单周期：</strong>${row.billStartDate} 至 ${row.billEndDate}</td>
          </tr>
          <tr>
            <td style="padding: 8px 0;"><strong>缴费截止：</strong>${row.dueDate}</td>
            <td style="padding: 8px 0;"><strong>账单状态：</strong>${getStatusText(row.status)}</td>
          </tr>
        </table>
      </div>
      
      <div style="margin-bottom: 30px; border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
        <h3 style="font-size: 16px; margin-bottom: 15px; border-bottom: 2px solid #409eff; padding-bottom: 10px;">费用明细</h3>
        <table style="width: 100%; font-size: 14px; border-collapse: collapse;">
          <thead>
            <tr style="background: #f5f7fa;">
              <th style="padding: 12px; text-align: left; border: 1px solid #ddd;">费用项目</th>
              <th style="padding: 12px; text-align: right; border: 1px solid #ddd;">金额（元）</th>
            </tr>
          </thead>
          <tbody>
            ${row.roomFee > 0 ? `<tr><td style="padding: 12px; border: 1px solid #ddd;">住宿费</td><td style="padding: 12px; text-align: right; border: 1px solid #ddd;">¥${row.roomFee.toFixed(2)}</td></tr>` : ''}
            ${row.careFee > 0 ? `<tr><td style="padding: 12px; border: 1px solid #ddd;">护理费</td><td style="padding: 12px; text-align: right; border: 1px solid #ddd;">¥${row.careFee.toFixed(2)}</td></tr>` : ''}
            ${row.mealFee > 0 ? `<tr><td style="padding: 12px; border: 1px solid #ddd;">餐饮费</td><td style="padding: 12px; text-align: right; border: 1px solid #ddd;">¥${row.mealFee.toFixed(2)}</td></tr>` : ''}
            ${row.medicalFee > 0 ? `<tr><td style="padding: 12px; border: 1px solid #ddd;">医疗费</td><td style="padding: 12px; text-align: right; border: 1px solid #ddd;">¥${row.medicalFee.toFixed(2)}</td></tr>` : ''}
            ${row.otherFee > 0 ? `<tr><td style="padding: 12px; border: 1px solid #ddd;">其他费用</td><td style="padding: 12px; text-align: right; border: 1px solid #ddd;">¥${row.otherFee.toFixed(2)}</td></tr>` : ''}
          </tbody>
        </table>
      </div>
      
      <div style="margin-bottom: 30px; border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
        <h3 style="font-size: 16px; margin-bottom: 15px; border-bottom: 2px solid #409eff; padding-bottom: 10px;">结算信息</h3>
        <table style="width: 100%; font-size: 14px;">
          <tr>
            <td style="padding: 8px 0;"><strong>费用总额：</strong>¥${row.totalAmount?.toFixed(2)}</td>
            <td style="padding: 8px 0;"><strong>优惠金额：</strong>¥${row.discountAmount?.toFixed(2)}</td>
          </tr>
          <tr>
            <td style="padding: 8px 0;"><strong>应付金额：</strong><span style="color: #f56c6c; font-size: 18px;">¥${row.payableAmount?.toFixed(2)}</span></td>
            <td style="padding: 8px 0;"><strong>已付金额：</strong><span style="color: #67c23a;">¥${row.paidAmount?.toFixed(2)}</span></td>
          </tr>
        </table>
      </div>
      
      ${row.remark ? `<div style="margin-bottom: 30px; padding: 15px; background: #f5f7fa; border-radius: 8px;"><strong>备注：</strong>${row.remark}</div>` : ''}
      
      <div style="text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; font-size: 12px;">
        <p>此账单由养老公寓管理系统自动生成</p>
        <p>生成时间：${new Date().toLocaleString()}</p>
      </div>
    `
    document.body.appendChild(container)
    
    // 使用 html2canvas 将 HTML 转换为图片
    const canvas = await html2canvas(container, {
      scale: 2,
      useCORS: true,
      allowTaint: true,
      backgroundColor: '#ffffff'
    })
    
    // 创建 PDF
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF('p', 'mm', 'a4')
    const pdfWidth = pdf.internal.pageSize.getWidth()
    const pdfHeight = pdf.internal.pageSize.getHeight()
    const imgWidth = canvas.width
    const imgHeight = canvas.height
    const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight)
    const imgX = (pdfWidth - imgWidth * ratio) / 2
    let imgY = 10
    
    pdf.addImage(imgData, 'PNG', imgX, imgY, imgWidth * ratio, imgHeight * ratio)
    
    // 如果内容超过一页，添加新页
    const scaledHeight = imgHeight * ratio
    if (scaledHeight > pdfHeight - 20) {
      let heightLeft = scaledHeight
      let position = 0
      
      pdf.addImage(imgData, 'PNG', imgX, position, imgWidth * ratio, imgHeight * ratio)
      heightLeft -= pdfHeight
      
      while (heightLeft >= 0) {
        position = heightLeft - scaledHeight
        pdf.addPage()
        pdf.addImage(imgData, 'PNG', imgX, position, imgWidth * ratio, imgHeight * ratio)
        heightLeft -= pdfHeight
      }
    }
    
    // 下载 PDF
    pdf.save(`账单_${row.billNo}_${row.elderlyName || '未知'}.pdf`)
    
    // 清理临时元素
    document.body.removeChild(container)
    
    ElMessage.success('账单下载成功')
  } catch (error) {
    console.error('生成PDF失败:', error)
    ElMessage.error('账单下载失败')
  }
}

const getStatusType = (status) => {
  const types = ['warning', 'info', 'success', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待缴费', '部分缴费', '已缴清', '已逾期']
  return texts[status] || '未知'
}

// 删除账单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除账单 ${row.billNo} 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await request.delete(`/fee/bills/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
      loadStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除账单失败:', error)
      ElMessage.error(error.message || '删除账单失败')
    }
  }
}

onMounted(() => {
  loadData()
  loadStatistics()
})
</script>

<style scoped>
.fee-list-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-buttons {
  display: flex;
  gap: 10px;
}
.statistics-row {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}
.search-form {
  margin-bottom: 20px;
}
.fee-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.fee-detail span {
  font-size: 12px;
  color: #666;
}
.amount-payable {
  color: #f56c6c;
  font-weight: bold;
}
.amount-paid {
  color: #67c23a;
  font-weight: bold;
}
.amount-due {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

/* 账单详情样式 */
.bill-detail {
  max-height: 600px;
  overflow-y: auto;
}
.detail-section {
  margin-bottom: 20px;
}
.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
  color: #303133;
}
</style>
