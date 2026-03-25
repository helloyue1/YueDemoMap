<template>
  <div class="health-report-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon blue">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalReports || 0 }}</div>
            <div class="stat-label">总报告数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon orange">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.pendingAudit || 0 }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon green">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.audited || 0 }}</div>
            <div class="stat-label">已审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon purple">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.avgScore || 0 }}</div>
            <div class="stat-label">平均健康分</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主内容区 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>体检报告管理</span>
          <el-button type="primary" @click="handleGenerate">
            <el-icon><Plus /></el-icon>
            生成报告
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="报告类型">
          <el-select v-model="searchForm.reportType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="月度报告" :value="1" />
            <el-option label="季度报告" :value="2" />
            <el-option label="年度报告" :value="3" />
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

      <!-- 数据表格 -->
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="elderlyName" label="住客姓名" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="reportType" label="报告类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getReportTypeType(row.reportType)">
              {{ getReportTypeText(row.reportType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportDate" label="报告日期" width="120" />
        <el-table-column prop="startDate" label="统计周期" width="180">
          <template #default="{ row }">
            {{ row.startDate }} 至 {{ row.endDate }}
          </template>
        </el-table-column>
        <el-table-column prop="healthScore" label="健康评分" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.healthScore)" effect="dark">
              {{ row.healthScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已审核' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="审核医生" width="100">
          <template #default="{ row }">
            {{ row.doctorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="success" size="small" @click="handleDownload(row)">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-button 
              link 
              type="warning" 
              size="small" 
              @click="handleAudit(row)" 
              v-if="row.status === 0"
            >
              <el-icon><Check /></el-icon>
              审核
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

    <!-- 生成报告对话框 -->
    <el-dialog v-model="generateDialogVisible" title="生成体检报告" width="500px">
      <el-form :model="generateForm" :rules="generateRules" ref="generateFormRef" label-width="100px">
        <el-form-item label="选择住客" prop="elderlyId">
          <el-select
            v-model="generateForm.elderlyId"
            placeholder="请选择住客"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="elderly in elderlyList"
              :key="elderly.id"
              :label="elderly.realName"
              :value="elderly.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报告类型" prop="reportType">
          <el-select v-model="generateForm.reportType" placeholder="请选择报告类型" style="width: 100%">
            <el-option label="月度报告" :value="1" />
            <el-option label="季度报告" :value="2" />
            <el-option label="年度报告" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告日期" prop="reportDate">
          <el-date-picker
            v-model="generateForm.reportDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate" :loading="generateLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 报告详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="体检报告详情" width="800px">
      <div v-if="currentReport" class="report-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="住客姓名">{{ currentReport.elderlyName }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentReport.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="报告类型">{{ getReportTypeText(currentReport.reportType) }}</el-descriptions-item>
          <el-descriptions-item label="报告日期">{{ currentReport.reportDate }}</el-descriptions-item>
          <el-descriptions-item label="统计周期" :span="2">
            {{ currentReport.startDate }} 至 {{ currentReport.endDate }}
          </el-descriptions-item>
          <el-descriptions-item label="健康评分">
            <el-tag :type="getScoreType(currentReport.healthScore)" effect="dark" size="large">
              {{ currentReport.healthScore }} 分
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="currentReport.status === 1 ? 'success' : 'warning'">
              {{ currentReport.status === 1 ? '已审核' : '待审核' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 检查指标 -->
        <h3 class="section-title">检查指标</h3>
        <el-table :data="currentReport.details" stripe style="width: 100%">
          <el-table-column prop="checkItem" label="检查项目" width="120" />
          <el-table-column prop="checkValue" label="检查值" width="100" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="referenceRange" label="参考范围" width="120" />
          <el-table-column prop="result" label="结果" width="80">
            <template #default="{ row }">
              <el-tag :type="row.result === 1 ? 'success' : 'danger'" size="small">
                {{ row.result === 1 ? '正常' : '异常' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- 总体评估 -->
        <h3 class="section-title">总体评估</h3>
        <el-alert
          :title="currentReport.overallAssessment"
          type="info"
          :closable="false"
          show-icon
        />

        <!-- 健康建议 -->
        <h3 class="section-title">健康建议</h3>
        <el-alert
          :title="currentReport.recommendations"
          type="success"
          :closable="false"
          show-icon
        />

        <!-- 审核信息 -->
        <template v-if="currentReport.status === 1">
          <h3 class="section-title">审核信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="审核医生">{{ currentReport.doctorName }}</el-descriptions-item>
            <el-descriptions-item label="审核时间">{{ currentReport.auditTime }}</el-descriptions-item>
            <el-descriptions-item label="审核意见" :span="2">{{ currentReport.auditOpinion }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" title="审核报告" width="500px">
      <el-form :model="auditForm" :rules="auditRules" ref="auditFormRef" label-width="100px">
        <el-form-item label="审核医生" prop="doctorName">
          <el-input v-model="auditForm.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="审核意见" prop="auditOpinion">
          <el-input
            v-model="auditForm.auditOpinion"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="auditLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Timer,
  CircleCheck,
  TrendCharts,
  Plus,
  Search,
  RefreshRight,
  View,
  Download,
  Check,
  Delete
} from '@element-plus/icons-vue'
import {
  getHealthReportList,
  generateHealthReport,
  getHealthReportById,
  auditHealthReport,
  deleteHealthReport,
  exportHealthReport
} from '@/api/healthReport'
import { getElderlyList } from '@/api/elderly'

// 统计数据
const statistics = reactive({
  totalReports: 0,
  pendingAudit: 0,
  audited: 0,
  avgScore: 0
})

// 搜索表单
const searchForm = reactive({
  elderlyName: '',
  reportType: null,
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

// 生成报告对话框
const generateDialogVisible = ref(false)
const generateLoading = ref(false)
const generateFormRef = ref(null)
const generateForm = reactive({
  elderlyId: null,
  reportType: 1,
  reportDate: new Date().toISOString().split('T')[0]
})

const generateRules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  reportType: [{ required: true, message: '请选择报告类型', trigger: 'change' }],
  reportDate: [{ required: true, message: '请选择报告日期', trigger: 'change' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const currentReport = ref(null)

// 审核对话框
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref(null)
const currentAuditId = ref(null)
const auditForm = reactive({
  doctorName: '',
  auditOpinion: ''
})

const auditRules = {
  doctorName: [{ required: true, message: '请输入医生姓名', trigger: 'blur' }],
  auditOpinion: [{ required: true, message: '请输入审核意见', trigger: 'blur' }]
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
      reportType: searchForm.reportType
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await getHealthReportList(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0

    // 更新统计数据
    updateStatistics()
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStatistics = () => {
  statistics.totalReports = pagination.total
  statistics.pendingAudit = tableData.value.filter(item => item.status === 0).length
  statistics.audited = tableData.value.filter(item => item.status === 1).length
  const scores = tableData.value.filter(item => item.healthScore).map(item => item.healthScore)
  statistics.avgScore = scores.length > 0 
    ? Math.round(scores.reduce((a, b) => a + b, 0) / scores.length) 
    : 0
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadTableData()
}

// 重置
const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.reportType = null
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

// 生成报告
const handleGenerate = () => {
  generateForm.elderlyId = null
  generateForm.reportType = 1
  generateForm.reportDate = new Date().toISOString().split('T')[0]
  generateDialogVisible.value = true
}

const submitGenerate = async () => {
  try {
    await generateFormRef.value.validate()
    generateLoading.value = true
    await generateHealthReport(generateForm)
    ElMessage.success('报告生成成功')
    generateDialogVisible.value = false
    loadTableData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '生成失败')
  } finally {
    generateLoading.value = false
  }
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getHealthReportById(row.id)
    if (res.code === 200) {
      currentReport.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 下载报告
const handleDownload = async (row) => {
  try {
    const fileName = `${row.elderlyName}_${getReportTypeText(row.reportType)}_${row.reportDate}.pdf`
    await exportHealthReport(row.id, fileName)
    ElMessage.success('报告下载成功')
  } catch (error) {
    ElMessage.error('报告下载失败')
    console.error(error)
  }
}

// 审核
const handleAudit = (row) => {
  currentAuditId.value = row.id
  auditForm.doctorName = ''
  auditForm.auditOpinion = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  try {
    await auditFormRef.value.validate()
    auditLoading.value = true
    await auditHealthReport(currentAuditId.value, {
      doctorId: 1, // 当前登录医生ID，实际应从用户信息获取
      doctorName: auditForm.doctorName,
      auditOpinion: auditForm.auditOpinion
    })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadTableData()
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该报告吗？', '提示', { type: 'warning' })
    await deleteHealthReport(row.id)
    ElMessage.success('删除成功')
    loadTableData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 工具函数
const getReportTypeText = (type) => {
  const map = { 1: '月度报告', 2: '季度报告', 3: '年度报告' }
  return map[type] || '未知'
}

const getReportTypeType = (type) => {
  const map = { 1: 'primary', 2: 'success', 3: 'warning' }
  return map[type] || 'info'
}

const getScoreType = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

onMounted(() => {
  loadElderlyList()
  loadTableData()
})
</script>

<style scoped>
.health-report-container {
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
  font-size: 40px;
  margin-right: 15px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.stat-icon.blue {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.stat-icon.orange {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-icon.green {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.stat-icon.purple {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
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
  justify-content: flex-end;
}

.report-detail {
  max-height: 600px;
  overflow-y: auto;
}

.section-title {
  margin: 20px 0 10px 0;
  padding-left: 10px;
  border-left: 4px solid #409eff;
  font-size: 16px;
  font-weight: bold;
}
</style>
