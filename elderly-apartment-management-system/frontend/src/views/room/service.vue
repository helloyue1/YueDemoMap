<template>
  <div class="room-service-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.pendingCount || 0 }}</div>
              <div class="stats-label">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon cleaning">
              <el-icon><Brush /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ getTypeCount('CLEANING') }}</div>
              <div class="stats-label">保洁服务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon repair">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ getTypeCount('REPAIR') }}</div>
              <div class="stats-label">维修服务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon maintenance">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ getTypeCount('MAINTENANCE') }}</div>
              <div class="stats-label">设备维护</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon completed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ getStatusCount('COMPLETED') }}</div>
              <div class="stats-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon rating">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ avgRating }}</div>
              <div class="stats-label">平均评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 服务列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>房间服务管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增服务申请
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="房间">
          <el-select v-model="searchForm.roomId" placeholder="选择房间" clearable style="width: 150px">
            <el-option
              v-for="room in roomList"
              :key="room.id"
              :label="room.roomNumber"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="searchForm.serviceType" placeholder="选择类型" clearable style="width: 150px">
            <el-option
              v-for="type in serviceTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 150px">
            <el-option
              v-for="status in serviceStatuses"
              :key="status.value"
              :label="status.label"
              :value="status.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-select v-model="searchForm.urgency" placeholder="选择紧急程度" clearable style="width: 150px">
            <el-option
              v-for="level in urgencyLevels"
              :key="level.value"
              :label="level.label"
              :value="level.value"
            />
          </el-select>
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

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="requestNo" label="申请编号" width="140" />
        <el-table-column prop="roomNumber" label="房间号" width="100" align="center" />
        <el-table-column prop="serviceType" label="服务类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :color="getServiceTypeColor(row.serviceType)" effect="dark">
              {{ getServiceTypeLabel(row.serviceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="serviceSubtype" label="子类型" width="120" />
        <el-table-column prop="description" label="服务描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="urgency" label="紧急程度" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getUrgencyType(row.urgency)" effect="light">
              {{ getUrgencyLabel(row.urgency) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="100" />
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="primary"
              link
              size="small"
              @click="handleAssign(row)"
            >
              分配
            </el-button>
            <el-button
              v-if="row.status === 'ASSIGNED'"
              type="primary"
              link
              size="small"
              @click="handleStart(row)"
            >
              开始
            </el-button>
            <el-button
              v-if="row.status === 'IN_PROGRESS'"
              type="success"
              link
              size="small"
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button
              v-if="row.status === 'PENDING' || row.status === 'ASSIGNED'"
              type="danger"
              link
              size="small"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button
              v-if="row.status === 'COMPLETED'"
              type="warning"
              link
              size="small"
              @click="handleViewEvaluation(row)"
            >
              评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="房间" prop="roomId">
          <el-select v-model="form.roomId" placeholder="选择房间" style="width: 100%">
            <el-option
              v-for="room in roomList"
              :key="room.id"
              :label="room.roomNumber"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
          <el-select v-model="form.serviceType" placeholder="选择服务类型" style="width: 100%">
            <el-option
              v-for="type in serviceTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="子类型" prop="serviceSubtype">
          <el-select v-model="form.serviceSubtype" placeholder="选择子类型" style="width: 100%">
            <el-option
              v-for="subtype in currentSubtypes"
              :key="subtype.value"
              :label="subtype.label"
              :value="subtype.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度" prop="urgency">
          <el-radio-group v-model="form.urgency">
            <el-radio-button
              v-for="level in urgencyLevels"
              :key="level.value"
              :label="level.value"
            >
              {{ level.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="期望时间" prop="preferredTime">
          <el-select v-model="form.preferredTime" placeholder="选择期望服务时间" style="width: 100%">
            <el-option label="08:00-10:00" value="08:00-10:00" />
            <el-option label="10:00-12:00" value="10:00-12:00" />
            <el-option label="14:00-16:00" value="14:00-16:00" />
            <el-option label="16:00-18:00" value="16:00-18:00" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述服务需求"
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="服务详情"
      width="700px"
      destroy-on-close
    >
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="申请编号">{{ currentRow.requestNo }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentRow.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">
          <el-tag :color="getServiceTypeColor(currentRow.serviceType)" effect="dark">
            {{ getServiceTypeLabel(currentRow.serviceType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="子类型">{{ currentRow.serviceSubtype }}</el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyType(currentRow.urgency)" effect="light">
            {{ getUrgencyLabel(currentRow.urgency) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)" effect="light">
            {{ getStatusLabel(currentRow.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="期望时间" :span="2">{{ currentRow.preferredTime }}</el-descriptions-item>
        <el-descriptions-item label="服务描述" :span="2">{{ currentRow.description }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentRow.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentRow.handleTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2">{{ currentRow.handleNotes || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentRow.completeTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 分配处理人对话框 -->
    <el-dialog
      v-model="assignVisible"
      title="分配处理人"
      width="400px"
    >
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="处理人">
          <el-input v-model="assignForm.handlerName" placeholder="请输入处理人姓名" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="assignForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完成服务对话框 -->
    <el-dialog
      v-model="completeVisible"
      title="完成服务"
      width="400px"
    >
      <el-form :model="completeForm" label-width="80px">
        <el-form-item label="处理备注">
          <el-input
            v-model="completeForm.notes"
            type="textarea"
            :rows="4"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="success" @click="submitComplete">完成</el-button>
      </template>
    </el-dialog>

    <!-- 查看评价对话框 -->
    <el-dialog
      v-model="evaluationVisible"
      title="服务评价"
      width="500px"
      destroy-on-close
    >
      <div v-if="currentEvaluation" class="evaluation-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="服务编号">{{ currentRow?.requestNo }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentRow?.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="服务类型">
            <el-tag :color="getServiceTypeColor(currentRow?.serviceType)" effect="dark">
              {{ getServiceTypeLabel(currentRow?.serviceType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate v-model="currentEvaluation.rating" disabled show-score text-color="#ff9900" />
          </el-descriptions-item>
          <el-descriptions-item label="评价人">{{ currentEvaluation.elderlyName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="评价时间">{{ currentEvaluation.createTime }}</el-descriptions-item>
          <el-descriptions-item label="标签">
            <el-tag v-for="tag in parseTags(currentEvaluation.tags)" :key="tag" size="small" class="tag-item">
              {{ tag }}
            </el-tag>
            <span v-if="!currentEvaluation.tags">-</span>
          </el-descriptions-item>
          <el-descriptions-item label="评价内容">
            <div class="evaluation-text">{{ currentEvaluation.content || '暂无评价内容' }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else description="暂无评价" />
      <template #footer>
        <el-button @click="evaluationVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRoomServiceRequests,
  createRoomServiceRequest,
  assignHandler,
  startHandle,
  completeRequest,
  cancelRequest,
  getRoomServiceStats,
  getRoomServiceEvaluations,
  SERVICE_TYPES,
  SERVICE_SUBTYPES,
  URGENCY_LEVELS,
  SERVICE_STATUSES,
  getServiceTypeLabel,
  getServiceTypeColor,
  getUrgencyLabel,
  getUrgencyType,
  getStatusLabel,
  getStatusType
} from '@/api/roomService'
import { getRoomList } from '@/api/room'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const roomList = ref([])
const stats = ref({})

const serviceTypes = SERVICE_TYPES
const urgencyLevels = URGENCY_LEVELS
const serviceStatuses = SERVICE_STATUSES

const searchForm = reactive({
  roomId: null,
  serviceType: '',
  status: '',
  urgency: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增服务申请')
const formRef = ref(null)
const form = reactive({
  roomId: null,
  serviceType: '',
  serviceSubtype: '',
  urgency: 'NORMAL',
  preferredTime: '',
  description: '',
  contactPhone: ''
})

const rules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  serviceSubtype: [{ required: true, message: '请选择子类型', trigger: 'change' }],
  urgency: [{ required: true, message: '请选择紧急程度', trigger: 'change' }],
  description: [{ required: true, message: '请输入服务描述', trigger: 'blur' }]
}

const currentSubtypes = computed(() => {
  return form.serviceType ? SERVICE_SUBTYPES[form.serviceType] || [] : []
})

watch(() => form.serviceType, () => {
  form.serviceSubtype = ''
})

const detailVisible = ref(false)
const currentRow = ref(null)

const assignVisible = ref(false)
const assignForm = reactive({
  handlerName: '',
  notes: ''
})

const completeVisible = ref(false)
const completeForm = reactive({
  notes: ''
})

const evaluationVisible = ref(false)
const currentEvaluation = ref(null)

const avgRating = computed(() => {
  if (!stats.value.avgRating) return '0.0'
  return Number(stats.value.avgRating).toFixed(1)
})

const parseTags = (tags) => {
  if (!tags) return []
  return tags.split(',').filter(tag => tag.trim())
}

const handleViewEvaluation = async (row) => {
  currentRow.value = row
  currentEvaluation.value = null
  evaluationVisible.value = true

  try {
    const res = await getRoomServiceEvaluations({
      page: 1,
      size: 1,
      requestId: row.id
    })
    if (res.code === 200 && res.data.records && res.data.records.length > 0) {
      currentEvaluation.value = res.data.records[0]
    }
  } catch (error) {
    console.error('获取评价失败:', error)
  }
}

const getTypeCount = (type) => {
  if (!stats.value.typeStats) return 0
  const item = stats.value.typeStats.find(s => s.type === type)
  return item ? item.count : 0
}

const getStatusCount = (status) => {
  if (!stats.value.statusStats) return 0
  const item = stats.value.statusStats.find(s => s.status === status)
  return item ? item.count : 0
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoomServiceRequests({
      page: page.value,
      size: size.value,
      ...searchForm
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取服务列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  try {
    const res = await getRoomServiceStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchRooms = async () => {
  try {
    const res = await getRoomList({ page: 1, size: 1000 })
    if (res.code === 200) {
      roomList.value = res.data.records
    }
  } catch (error) {
    console.error('获取房间列表失败:', error)
  }
}

const handleSearch = () => {
  page.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.roomId = null
  searchForm.serviceType = ''
  searchForm.status = ''
  searchForm.urgency = ''
  page.value = 1
  fetchData()
}

const handleSizeChange = (val) => {
  size.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  page.value = val
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增服务申请'
  form.roomId = null
  form.serviceType = ''
  form.serviceSubtype = ''
  form.urgency = 'NORMAL'
  form.preferredTime = ''
  form.description = ''
  form.contactPhone = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const res = await createRoomServiceRequest(form)
    if (res.code === 200) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchData()
      fetchStats()
    }
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleView = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

const handleAssign = (row) => {
  currentRow.value = row
  assignForm.handlerName = ''
  assignForm.notes = ''
  assignVisible.value = true
}

const submitAssign = async () => {
  if (!assignForm.handlerName) {
    ElMessage.warning('请输入处理人姓名')
    return
  }
  try {
    const res = await assignHandler(currentRow.value.id, 0, assignForm.handlerName)
    if (res.code === 200) {
      ElMessage.success('分配成功')
      assignVisible.value = false
      fetchData()
    }
  } catch (error) {
    ElMessage.error('分配失败')
  }
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm('确认开始处理该服务申请？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await startHandle(row.id)
    if (res.code === 200) {
      ElMessage.success('已开始处理')
      fetchData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleComplete = (row) => {
  currentRow.value = row
  completeForm.notes = ''
  completeVisible.value = true
}

const submitComplete = async () => {
  try {
    const res = await completeRequest(currentRow.value.id, completeForm.notes)
    if (res.code === 200) {
      ElMessage.success('服务已完成')
      completeVisible.value = false
      fetchData()
      fetchStats()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCancel = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '取消服务', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入取消原因'
    })
    const res = await cancelRequest(row.id, value)
    if (res.code === 200) {
      ElMessage.success('已取消')
      fetchData()
      fetchStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchData()
  fetchStats()
  fetchRooms()
})
</script>

<style scoped lang="scss">
.room-service-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  .stats-item {
    display: flex;
    align-items: center;
    padding: 10px;
  }

  .stats-icon {
    width: 50px;
    height: 50px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    margin-right: 15px;

    &.pending {
      background: #fdf6ec;
      color: #e6a23c;
    }

    &.cleaning {
      background: #f0f9eb;
      color: #67c23a;
    }

    &.repair {
      background: #fef0f0;
      color: #f56c6c;
    }

    &.maintenance {
      background: #ecf5ff;
      color: #409eff;
    }

    &.completed {
      background: #f0f9eb;
      color: #67c23a;
    }

    &.rating {
      background: #fdf6ec;
      color: #e6a23c;
    }
  }

  .stats-info {
    flex: 1;
  }

  .stats-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    line-height: 1.2;
  }

  .stats-label {
    font-size: 14px;
    color: #909399;
    margin-top: 5px;
  }
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.evaluation-content {
  .tag-item {
    margin-right: 5px;
    margin-bottom: 5px;
  }

  .evaluation-text {
    white-space: pre-wrap;
    line-height: 1.6;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;
    min-height: 60px;
  }
}
</style>
