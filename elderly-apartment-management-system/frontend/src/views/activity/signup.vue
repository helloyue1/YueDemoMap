<template>
  <div class="activity-signup-container">
    <!-- 活动信息卡片 -->
    <el-card v-if="activityInfo" class="activity-info-card">
      <div class="activity-header">
        <el-image :src="activityInfo.imageUrl || '/default-activity.png'" class="activity-image" />
        <div class="activity-detail">
          <h3>{{ activityInfo.title }}</h3>
          <p><el-icon><Calendar /></el-icon> {{ activityInfo.activityDate }} {{ activityInfo.startTime }}</p>
          <p><el-icon><Location /></el-icon> {{ activityInfo.location }}</p>
          <div class="activity-tags">
            <el-tag :type="getStatusType(activityInfo.status)" effect="dark">{{ activityInfo.statusText }}</el-tag>
            <el-button 
              type="warning" 
              size="small" 
              @click="handleGenerateActivityQrCode"
              class="activity-qr-btn"
            >
              <el-icon><FullScreen /></el-icon>活动签到二维码
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value">{{ signupStats.totalSignups || 0 }}</div>
          <div class="stat-label">总报名</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value text-success">{{ signupStats.confirmedSignups || 0 }}</div>
          <div class="stat-label">已报名</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value text-primary">{{ signupStats.checkedInCount || 0 }}</div>
          <div class="stat-label">已签到</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value text-danger">{{ signupStats.cancelledCount || 0 }}</div>
          <div class="stat-label">已取消</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value text-warning">{{ signupStats.waitlistCount || 0 }}</div>
          <div class="stat-label">候补中</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value">{{ signupStats.checkinRate || 0 }}%</div>
          <div class="stat-label">签到率</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 报名列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报名管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>添加报名
            </el-button>
            <el-button type="success" @click="handleBatchCheckIn" :disabled="selectedRows.length === 0">
              <el-icon><Check /></el-icon>批量签到
            </el-button>
            <el-button @click="handleExport">
              <el-icon><Download /></el-icon>导出
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="报名状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="已报名" :value="1" />
            <el-option label="已签到" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="已拒绝" :value="4" />
            <el-option label="候补中" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 报名表格 -->
      <el-table 
        :data="tableData" 
        stripe 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column label="住客信息" min-width="200">
          <template #default="{ row }">
            <div class="elderly-info">
              <div class="elderly-name">{{ row.elderlyName }}</div>
              <div class="elderly-room" v-if="row.roomNumber">房间: {{ row.roomNumber }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="elderlyPhone" label="联系电话" width="120" />
        <el-table-column prop="emergencyContactName" label="紧急联系人" width="120" />
        <el-table-column prop="emergencyContactPhone" label="紧急电话" width="120" />
        <el-table-column label="报名来源" width="100">
          <template #default="{ row }">
            {{ row.signupSourceText }}
          </template>
        </el-table-column>
        <el-table-column prop="signupTime" label="报名时间" width="160" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="dark">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到信息" width="160">
          <template #default="{ row }">
            <div v-if="row.checkinTime">
              <div>{{ row.checkinTime }}</div>
              <div class="checkin-method">{{ row.checkinMethodText }}</div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              link type="success" 
              size="small" 
              @click="handleApprove(row)"
            >
              审核
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 5"
              link type="primary"
              size="small"
              @click="handleCheckIn(row)"
            >
              签到
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 5"
              link type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
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
        class="pagination"
      />
    </el-card>

    <!-- 添加报名对话框 -->
    <el-dialog v-model="dialogVisible" title="添加报名" width="600px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="选择住客" prop="elderlyId">
          <el-select 
            v-model="formData.elderlyId" 
            placeholder="请选择住客" 
            filterable
            remote
            :remote-method="searchElderly"
            :loading="elderlyLoading"
            style="width: 100%"
            @change="handleElderlyChange"
          >
            <el-option 
              v-for="item in elderlyOptions" 
              :key="item.id" 
              :label="`${item.realName} (${item.roomNumber || '未分配房间'})`" 
              :value="item.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.elderlyPhone" placeholder="住客联系电话" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="formData.emergencyContactName" placeholder="紧急联系人姓名" />
        </el-form-item>
        <el-form-item label="紧急电话">
          <el-input v-model="formData.emergencyContactPhone" placeholder="紧急联系人电话" />
        </el-form-item>
        <el-form-item label="健康状况">
          <el-input 
            v-model="formData.healthStatus" 
            type="textarea" 
            :rows="2"
            placeholder="住客健康状况说明"
          />
        </el-form-item>
        <el-form-item label="特殊需求">
          <el-input 
            v-model="formData.specialRequirements" 
            type="textarea" 
            :rows="2"
            placeholder="特殊需求或注意事项"
          />
        </el-form-item>
        <el-form-item label="报名来源" prop="signupSource">
          <el-radio-group v-model="formData.signupSource">
            <el-radio :label="1">本人报名</el-radio>
            <el-radio :label="2">家属代报</el-radio>
            <el-radio :label="3">工作人员代报</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.notes" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 报名详情对话框 -->
    <el-dialog v-model="detailVisible" title="报名详情" width="600px">
      <el-descriptions :column="2" border v-if="currentSignup">
        <el-descriptions-item label="住客姓名">{{ currentSignup.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentSignup.roomNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentSignup.elderlyPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ currentSignup.emergencyContactName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急电话">{{ currentSignup.emergencyContactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报名来源">{{ currentSignup.signupSourceText }}</el-descriptions-item>
        <el-descriptions-item label="报名时间">{{ currentSignup.signupTime }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getStatusTag(currentSignup.status)" effect="dark">
            {{ currentSignup.statusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签到时间" v-if="currentSignup.checkinTime">
          {{ currentSignup.checkinTime }}
        </el-descriptions-item>
        <el-descriptions-item label="签到方式" v-if="currentSignup.checkinMethodText">
          {{ currentSignup.checkinMethodText }}
        </el-descriptions-item>
        <el-descriptions-item label="取消原因" v-if="currentSignup.cancelReason" :span="2">
          {{ currentSignup.cancelReason }}
        </el-descriptions-item>
        <el-descriptions-item label="拒绝原因" v-if="currentSignup.rejectReason" :span="2">
          {{ currentSignup.rejectReason }}
        </el-descriptions-item>
        <el-descriptions-item label="健康状况" :span="2">
          {{ currentSignup.healthStatus || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="特殊需求" :span="2">
          {{ currentSignup.specialRequirements || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentSignup.notes || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 活动签到二维码对话框 -->
    <el-dialog v-model="qrCodeVisible" title="活动签到二维码" width="400px" center>
      <div class="qr-code-container" v-if="qrCodeData">
        <div class="qr-info">
          <p><strong>活动：</strong>{{ activityInfo?.title }}</p>
          <p><strong>有效期：</strong>{{ qrCodeData.expireMinutes }}分钟</p>
        </div>
        <div class="qr-image-wrapper">
          <img :src="qrCodeData.qrCode" alt="签到二维码" class="qr-image" />
        </div>
        <div class="qr-tips">
          <p>请使用APP扫码，选择住客完成签到</p>
          <p class="qr-warning">二维码{{ qrCodeData.expireMinutes }}分钟内有效，请尽快使用</p>
        </div>
      </div>
      <div v-else class="qr-loading">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>正在生成二维码...</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Check, Download, Search, Refresh, Calendar, Location, Loading, FullScreen
} from '@element-plus/icons-vue'
import {
  getActivitySignupList, createSignup, getSignupStatistics,
  checkInSignup, cancelSignup, approveSignup, batchCheckIn,
  getActivityById, generateActivityQrCode
} from '@/api/activity'
import { getElderlyList } from '@/api/elderly'

const route = useRoute()
const activityId = computed(() => {
  const id = route.params.id
  if (!id || id === ':id' || id === '') {
    return null
  }
  return parseInt(id)
})

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const qrCodeVisible = ref(false)
const formRef = ref(null)
const currentSignup = ref(null)
const activityInfo = ref(null)
const selectedRows = ref([])
const qrCodeData = ref(null)

// 统计数据
const signupStats = reactive({
  totalSignups: 0,
  confirmedSignups: 0,
  checkedInCount: 0,
  cancelledCount: 0,
  waitlistCount: 0,
  checkinRate: 0
})

// 搜索表单
const searchForm = reactive({
  elderlyName: '',
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 住客选项
const elderlyOptions = ref([])
const elderlyLoading = ref(false)

// 表单数据
const formData = reactive({
  activityId: activityId.value,
  elderlyId: null,
  elderlyPhone: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  healthStatus: '',
  specialRequirements: '',
  signupSource: 3,
  notes: ''
})

// 表单规则
const rules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  signupSource: [{ required: true, message: '请选择报名来源', trigger: 'change' }]
}

// 加载活动信息
const loadActivityInfo = async () => {
  if (!activityId.value) return
  try {
    const res = await getActivityById(activityId.value)
    if (res.code === 200) {
      activityInfo.value = res.data
    }
  } catch (error) {
    console.error('加载活动信息失败', error)
  }
}

// 加载统计数据
const loadStatistics = async () => {
  if (!activityId.value) return
  try {
    const res = await getSignupStatistics(activityId.value)
    if (res.code === 200) {
      Object.assign(signupStats, res.data)
    }
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

// 加载报名列表
const loadData = async () => {
  if (!activityId.value) return
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      activityId: activityId.value,
      elderlyName: searchForm.elderlyName,
      status: searchForm.status
    }
    const res = await getActivitySignupList(params)
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

// 搜索住客
const searchElderly = async (query) => {
  if (query.length < 1) return
  elderlyLoading.value = true
  try {
    const res = await getElderlyList({ name: query, page: 1, size: 20 })
    if (res.code === 200) {
      elderlyOptions.value = res.data.records
    }
  } catch (error) {
    console.error('搜索住客失败', error)
  } finally {
    elderlyLoading.value = false
  }
}

// 住客选择变化
const handleElderlyChange = (val) => {
  const elderly = elderlyOptions.value.find(e => e.id === val)
  if (elderly) {
    formData.elderlyPhone = elderly.phone || ''
  }
}

// 获取状态标签类型
const getStatusTag = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'primary',
    3: 'info',
    4: 'danger',
    5: 'warning'
  }
  return types[status] || 'info'
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'info',
    5: 'danger'
  }
  return types[status] || 'info'
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.status = null
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 添加报名
const handleAdd = () => {
  Object.assign(formData, {
    activityId: activityId.value,
    elderlyId: null,
    elderlyPhone: '',
    emergencyContactName: '',
    emergencyContactPhone: '',
    healthStatus: '',
    specialRequirements: '',
    signupSource: 3,
    notes: ''
  })
  elderlyOptions.value = []
  dialogVisible.value = true
}

// 提交报名
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await createSignup(formData)
    ElMessage.success('报名成功')
    dialogVisible.value = false
    loadData()
    loadStatistics()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '报名失败')
  } finally {
    submitLoading.value = false
  }
}

// 查看详情
const handleView = (row) => {
  currentSignup.value = row
  detailVisible.value = true
}

// 审核通过
const handleApprove = async (row) => {
  try {
    await approveSignup(row.id)
    ElMessage.success('审核通过')
    loadData()
    loadStatistics()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 签到
const handleCheckIn = async (row) => {
  try {
    await ElMessageBox.confirm('确认签到该住客？', '提示', { type: 'warning' })
    await checkInSignup(row.id, { checkinMethod: 2 })
    ElMessage.success('签到成功')
    loadData()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('签到失败')
    }
  }
}

// 批量签到
const handleBatchCheckIn = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要签到的记录')
    return
  }

  try {
    await ElMessageBox.confirm(`确认批量签到 ${selectedRows.value.length} 人？`, '提示', { type: 'warning' })
    const ids = selectedRows.value.map(row => row.id)
    await batchCheckIn({ ids, checkinMethod: 2 })
    ElMessage.success('批量签到成功')
    loadData()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量签到失败')
    }
  }
}

// 取消报名
const handleCancel = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '取消报名', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入取消原因'
    })
    await cancelSignup(row.id, { cancelReason: value })
    ElMessage.success('取消成功')
    loadData()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

// 生成活动签到二维码
const handleGenerateActivityQrCode = async () => {
  if (!activityId.value) {
    ElMessage.error('活动ID不存在')
    return
  }
  qrCodeData.value = null
  qrCodeVisible.value = true
  try {
    const res = await generateActivityQrCode(activityId.value)
    if (res.code === 200) {
      qrCodeData.value = res.data
    } else {
      ElMessage.error(res.message || '二维码生成失败')
      qrCodeVisible.value = false
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '二维码生成失败')
    qrCodeVisible.value = false
  }
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

onMounted(() => {
  loadActivityInfo()
  loadData()
  loadStatistics()
})

watch(() => route.params.id, (newId) => {
  if (newId && newId !== ':id') {
    formData.activityId = parseInt(newId)
    pagination.page = 1
    loadActivityInfo()
    loadData()
    loadStatistics()
  }
})
</script>

<style scoped lang="scss">
.activity-signup-container {
  padding: 20px;

  .activity-info-card {
    margin-bottom: 20px;

    .activity-header {
      display: flex;
      align-items: center;

      .activity-image {
        width: 120px;
        height: 90px;
        border-radius: 8px;
        object-fit: cover;
        margin-right: 20px;
      }

      .activity-detail {
        h3 {
          margin: 0 0 10px 0;
        }

        p {
          margin: 5px 0;
          color: #666;

          .el-icon {
            margin-right: 5px;
          }
        }

        .activity-tags {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-top: 10px;

          .activity-qr-btn {
            margin-left: 10px;
          }
        }
      }
    }
  }

  .statistics-row {
    margin-bottom: 20px;

    .stat-card {
      text-align: center;
      padding: 15px;

      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;

        &.text-success {
          color: #67c23a;
        }

        &.text-primary {
          color: #409eff;
        }

        &.text-danger {
          color: #f56c6c;
        }

        &.text-warning {
          color: #e6a23c;
        }
      }

      .stat-label {
        font-size: 13px;
        color: #999;
        margin-top: 5px;
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      .el-button {
        margin-left: 10px;
      }
    }
  }

  .search-form {
    margin-bottom: 20px;
  }

  .elderly-info {
    .elderly-name {
      font-weight: bold;
    }

    .elderly-room {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }
  }

  .checkin-method {
    font-size: 12px;
    color: #999;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }

  .qr-code-container {
    text-align: center;
    padding: 20px;

    .qr-info {
      margin-bottom: 20px;

      p {
        margin: 8px 0;
        font-size: 14px;
        color: #333;
      }
    }

    .qr-image-wrapper {
      display: flex;
      justify-content: center;
      margin: 20px 0;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;

      .qr-image {
        width: 250px;
        height: 250px;
        border-radius: 8px;
      }
    }

    .qr-tips {
      margin-top: 20px;

      p {
        margin: 5px 0;
        font-size: 14px;
        color: #666;
      }

      .qr-warning {
        color: #e6a23c;
        font-size: 12px;
      }
    }
  }

  .qr-loading {
    text-align: center;
    padding: 40px;

    .loading-icon {
      font-size: 32px;
      color: #409eff;
      animation: rotating 2s linear infinite;
    }

    p {
      margin-top: 10px;
      color: #666;
    }
  }

  @keyframes rotating {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
}
</style>
