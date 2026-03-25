<template>
  <div class="payment-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>缴费记录</span>
          <el-button type="success" @click="handleExportExcel">
            <el-icon><Download /></el-icon>导出Excel
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="缴费日期">
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
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="paymentNo" label="缴费单号" width="180" />
        <el-table-column prop="elderlyName" label="住客姓名" width="100" />
        <el-table-column prop="billNo" label="账单编号" width="150" />
        <el-table-column prop="paymentAmount" label="缴费金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.paymentAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="缴费方式" width="100">
          <template #default="{ row }">
            {{ getPaymentMethodText(row.paymentMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="payerName" label="缴费人" width="100" />
        <el-table-column prop="payerPhone" label="联系电话" width="120" />
        <el-table-column prop="receiptNo" label="收据编号" width="120" />
        <el-table-column prop="createTime" label="缴费时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="success" size="small" @click="handlePrintReceipt(row)">下载收据</el-button>
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

    <!-- 缴费详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="缴费详情" width="800px" destroy-on-close>
      <div v-loading="detailLoading" class="detail-content">
        <template v-if="currentPayment">
          <!-- 基本信息卡片 -->
          <el-row :gutter="20" class="info-cards">
            <el-col :span="6">
              <div class="info-card">
                <div class="info-label">缴费金额</div>
                <div class="info-value amount">¥{{ currentPayment.paymentAmount?.toFixed(2) }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-card">
                <div class="info-label">缴费方式</div>
                <div class="info-value">{{ getPaymentMethodText(currentPayment.paymentMethod) }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-card">
                <div class="info-label">缴费时间</div>
                <div class="info-value">{{ currentPayment.createTime }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-card">
                <div class="info-label">收据编号</div>
                <div class="info-value">{{ currentPayment.receiptNo || '-' }}</div>
              </div>
            </el-col>
          </el-row>

          <!-- 缴费信息 -->
          <el-divider content-position="left">缴费信息</el-divider>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="缴费单号">{{ currentPayment.paymentNo }}</el-descriptions-item>
            <el-descriptions-item label="账单编号">{{ currentPayment.billNo || currentPayment.bill_no }}</el-descriptions-item>
            <el-descriptions-item label="住客姓名">{{ currentPayment.elderlyName || currentPayment.elderly_name }}</el-descriptions-item>
            <el-descriptions-item label="住客编号">{{ currentPayment.elderlyId || currentPayment.elderly_id }}</el-descriptions-item>
            <el-descriptions-item label="缴费人">{{ currentPayment.payerName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentPayment.payerPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="缴费方式" :span="2">
              <el-tag :type="getPaymentMethodType(currentPayment.paymentMethod)">
                {{ getPaymentMethodText(currentPayment.paymentMethod) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <!-- 账单信息 -->
          <el-divider content-position="left">关联账单信息</el-divider>
          <el-descriptions :column="2" border v-if="billData">
            <el-descriptions-item label="账单月份">{{ billData.billMonth || billData.bill_month }}</el-descriptions-item>
            <el-descriptions-item label="房间号">{{ billData.roomNumber || billData.room_number || '-' }}</el-descriptions-item>
            <el-descriptions-item label="账单周期">{{ billData.billStartDate || billData.bill_start_date }} 至 {{ billData.billEndDate || billData.bill_end_date }}</el-descriptions-item>
            <el-descriptions-item label="账单状态">
              <el-tag :type="getBillStatusType(billData.status)">
                {{ getBillStatusText(billData.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="应付金额">
              <span class="amount-payable">¥{{ (billData.payableAmount || billData.payable_amount || 0).toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="已付金额">
              <span class="amount-paid">¥{{ (billData.paidAmount || billData.paid_amount || 0).toFixed(2) }}</span>
            </el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="暂无账单信息" />

          <!-- 费用明细 -->
          <template v-if="billData && feeDetails.length > 0">
            <el-divider content-position="left">费用明细</el-divider>
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
          </template>

          <!-- 备注信息 -->
          <el-divider content-position="left">备注信息</el-divider>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="缴费备注">{{ currentPayment.remark || '无' }}</el-descriptions-item>
          </el-descriptions>

          <!-- 操作记录 -->
          <el-divider content-position="left">操作记录</el-divider>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="创建时间">{{ currentPayment.createTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ currentPayment.updateTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handlePrintReceipt(currentPayment)">下载收据</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import request from '@/api/request'
import * as XLSX from 'xlsx'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'

const loading = ref(false)
const detailLoading = ref(false)
const detailDialogVisible = ref(false)
const currentPayment = ref(null)
const billData = ref(null)
const feeDetails = ref([])

const searchForm = reactive({
  elderlyName: '',
  dateRange: []
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
    const params = {
      page: pagination.page,
      size: pagination.size,
      elderlyName: searchForm.elderlyName || undefined,
      startDate: searchForm.dateRange?.[0],
      endDate: searchForm.dateRange?.[1]
    }
    const res = await request.get('/fee/payments', { params })
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

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.dateRange = []
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

// 查看缴费详情
const handleView = async (row) => {
  // 先使用列表页的数据作为初始值
  currentPayment.value = { ...row }
  billData.value = null
  feeDetails.value = []
  detailDialogVisible.value = true

  detailLoading.value = true
  try {
    // 获取缴费记录详情
    const paymentRes = await request.get(`/fee/payments/${row.id}`)
    if (paymentRes.code === 200) {
      // 合并API返回的数据和列表页数据，只合并不为null/undefined的字段
      const apiData = paymentRes.data
      Object.keys(apiData).forEach(key => {
        if (apiData[key] !== null && apiData[key] !== undefined) {
          currentPayment.value[key] = apiData[key]
        }
      })

      // 获取关联账单信息
      if (currentPayment.value.billId) {
        await loadBillDetail(currentPayment.value.billId)
      }
    } else {
      ElMessage.error(paymentRes.message || '获取缴费详情失败')
    }
  } catch (error) {
    console.error('获取缴费详情失败:', error)
    // API失败时保留列表页数据，不显示错误
  } finally {
    detailLoading.value = false
  }
}

// 加载账单详情
const loadBillDetail = async (billId) => {
  try {
    const res = await request.get(`/fee/bills/${billId}`)
    if (res.code === 200) {
      billData.value = res.data
      // 生成费用明细
      feeDetails.value = generateFeeDetailsFromBill(res.data)
    }
  } catch (error) {
    console.error('加载账单详情失败:', error)
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

// 获取缴费方式标签类型
const getPaymentMethodType = (method) => {
  const types = { 1: 'success', 2: 'primary', 3: 'success', 4: 'primary', 5: 'info' }
  return types[method] || 'info'
}

// 获取账单状态文本
const getBillStatusText = (status) => {
  const texts = ['待缴费', '部分缴费', '已缴清', '已逾期']
  return texts[status] || '未知'
}

// 获取账单状态标签类型
const getBillStatusType = (status) => {
  const types = ['warning', 'info', 'success', 'danger']
  return types[status] || 'info'
}

const handlePrintReceipt = async (row) => {
  // 下载收据PDF
  try {
    // 创建临时容器用于生成PDF
    const container = document.createElement('div')
    container.style.cssText = 'position: fixed; left: -9999px; top: 0; width: 600px; padding: 40px; background: white; font-family: "Microsoft YaHei", sans-serif;'
    container.innerHTML = `
      <div style="text-align: center; margin-bottom: 30px; border-bottom: 3px double #333; padding-bottom: 20px;">
        <h2 style="font-size: 28px; margin-bottom: 10px; letter-spacing: 8px;">收 据</h2>
        <p style="color: #666; font-size: 14px;">收据编号：${row.receiptNo || row.paymentNo}</p>
      </div>
      
      <div style="margin-bottom: 30px; font-size: 16px; line-height: 2;">
        <p><strong>今收到</strong> ${row.payerName || row.elderlyName} <strong>交来</strong> 养老公寓费用</p>
        <p><strong>住客姓名：</strong>${row.elderlyName}</p>
        <p><strong>账单编号：</strong>${row.billNo}</p>
        <p><strong>缴费单号：</strong>${row.paymentNo}</p>
      </div>
      
      <div style="margin: 30px 0; padding: 20px; border: 2px solid #333; text-align: center;">
        <span style="font-size: 18px;"><strong>人民币（大写）：</strong></span>
        <span style="font-size: 24px; color: #c00; font-weight: bold;">${numberToChinese(row.paymentAmount)}</span>
      </div>
      
      <div style="margin: 20px 0; font-size: 16px;">
        <p><strong>¥${row.paymentAmount?.toFixed(2)}</strong></p>
      </div>
      
      <div style="margin-top: 30px; font-size: 14px; line-height: 2;">
        <p><strong>缴费方式：</strong>${getPaymentMethodText(row.paymentMethod)}</p>
        <p><strong>联系电话：</strong>${row.payerPhone || '-'}</p>
        <p><strong>缴费时间：</strong>${row.createTime}</p>
        ${row.remark ? `<p><strong>备注：</strong>${row.remark}</p>` : ''}
      </div>
      
      <div style="margin-top: 60px; display: flex; justify-content: space-between; font-size: 14px;">
        <span><strong>收款单位：</strong>养老公寓</span>
        <span><strong>收款人：</strong>___________</span>
      </div>
      
      <div style="text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; font-size: 12px;">
        <p>此收据由养老公寓管理系统自动生成</p>
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
    const ratio = Math.min((pdfWidth - 20) / imgWidth, (pdfHeight - 20) / imgHeight)
    const imgX = (pdfWidth - imgWidth * ratio) / 2
    const imgY = 20
    
    pdf.addImage(imgData, 'PNG', imgX, imgY, imgWidth * ratio, imgHeight * ratio)
    
    // 下载 PDF
    pdf.save(`收据_${row.receiptNo || row.paymentNo}_${row.elderlyName}.pdf`)
    
    // 清理临时元素
    document.body.removeChild(container)
    
    ElMessage.success('收据下载成功')
  } catch (error) {
    console.error('生成PDF失败:', error)
    ElMessage.error('收据下载失败')
  }
}

// 数字转中文大写
const numberToChinese = (num) => {
  const digits = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
  const units = ['', '拾', '佰', '仟']
  const bigUnits = ['', '万', '亿']
  
  if (num === 0) return '零元整'
  
  const parts = num.toFixed(2).split('.')
  const integerPart = parts[0]
  const decimalPart = parts[1]
  
  let result = ''
  
  // 处理整数部分
  if (integerPart !== '0') {
    let zeroFlag = false
    let unitIndex = 0
    
    for (let i = integerPart.length - 1; i >= 0; i--) {
      const digit = parseInt(integerPart[i])
      const unit = units[(integerPart.length - 1 - i) % 4]
      const bigUnit = bigUnits[Math.floor((integerPart.length - 1 - i) / 4)]
      
      if (digit === 0) {
        if (!zeroFlag && result !== '') {
          zeroFlag = true
        }
      } else {
        if (zeroFlag) {
          result = digits[0] + result
          zeroFlag = false
        }
        result = digits[digit] + unit + result
        if ((integerPart.length - 1 - i) % 4 === 0) {
          result = result + bigUnit
        }
      }
    }
    result += '元'
  }
  
  // 处理小数部分
  if (decimalPart === '00') {
    result += '整'
  } else {
    if (integerPart === '0') result = ''
    const jiao = parseInt(decimalPart[0])
    const fen = parseInt(decimalPart[1])
    if (jiao > 0) result += digits[jiao] + '角'
    if (fen > 0) result += digits[fen] + '分'
  }
  
  return result
}

const getPaymentMethodText = (method) => {
  const methods = { 1: '现金', 2: '银行卡', 3: '微信支付', 4: '支付宝', 5: '其他' }
  return methods[method] || '未知'
}

// 导出Excel
const handleExportExcel = () => {
  try {
    if (tableData.value.length === 0) {
      ElMessage.warning('没有数据可导出')
      return
    }
    
    // 准备数据
    const exportData = tableData.value.map(row => ({
      '缴费单号': row.paymentNo,
      '住客姓名': row.elderlyName,
      '账单编号': row.billNo,
      '缴费金额': row.paymentAmount,
      '缴费方式': getPaymentMethodText(row.paymentMethod),
      '缴费人': row.payerName,
      '联系电话': row.payerPhone,
      '收据编号': row.receiptNo,
      '缴费时间': row.createTime,
      '备注': row.remark || ''
    }))
    
    // 创建工作簿
    const wb = XLSX.utils.book_new()
    const ws = XLSX.utils.json_to_sheet(exportData)
    
    // 设置列宽
    const colWidths = [
      { wch: 20 }, // 缴费单号
      { wch: 12 }, // 住客姓名
      { wch: 18 }, // 账单编号
      { wch: 12 }, // 缴费金额
      { wch: 12 }, // 缴费方式
      { wch: 12 }, // 缴费人
      { wch: 15 }, // 联系电话
      { wch: 18 }, // 收据编号
      { wch: 20 }, // 缴费时间
      { wch: 30 }  // 备注
    ]
    ws['!cols'] = colWidths
    
    // 添加工作表到工作簿
    XLSX.utils.book_append_sheet(wb, ws, '缴费记录')
    
    // 生成文件名
    const now = new Date()
    const fileName = `缴费记录_${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}.xlsx`
    
    // 下载文件
    XLSX.writeFile(wb, fileName)
    
    ElMessage.success('Excel导出成功')
  } catch (error) {
    console.error('导出Excel失败:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.payment-container {
  padding: 20px;
}
.amount {
  color: #67c23a;
  font-weight: bold;
}

/* 详情弹窗样式 */
.detail-content {
  max-height: 600px;
  overflow-y: auto;
  padding: 10px 0;
}

.info-cards {
  margin-bottom: 20px;
}

.info-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.info-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.info-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.info-value.amount {
  color: #67c23a;
}

.amount-payable {
  color: #f56c6c;
  font-weight: bold;
}

.amount-paid {
  color: #67c23a;
  font-weight: bold;
}

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}
</style>
