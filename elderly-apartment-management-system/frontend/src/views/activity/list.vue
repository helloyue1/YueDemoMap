<template>
  <div class="activity-list-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon total">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalActivities || 0 }}</div>
            <div class="stat-label">总活动数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon month">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.monthActivities || 0 }}</div>
            <div class="stat-label">本月活动</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon ongoing">
            <el-icon><VideoPlay /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.ongoingActivities || 0 }}</div>
            <div class="stat-label">进行中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon register">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.registeringActivities || 0 }}</div>
            <div class="stat-label">报名中</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动列表卡片 -->
    <el-card class="activity-card">
      <template #header>
        <div class="card-header">
          <span class="title">活动管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            发布活动
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动名称">
          <el-input 
            v-model="searchForm.title" 
            placeholder="请输入活动名称" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="活动分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择"
            clearable
            style="width: 150px"
            @change="handleCategoryChange"
          >
            <el-option label="全部" :value="null" />
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="草稿" :value="0" />
            <el-option label="报名中" :value="1" />
            <el-option label="即将开始" :value="2" />
            <el-option label="进行中" :value="3" />
            <el-option label="已结束" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 活动表格 -->
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="活动信息" min-width="280">
          <template #default="{ row }">
            <div class="activity-info">
              <el-image 
                :src="row.imageUrl || '/default-activity.png'" 
                class="activity-image"
                fit="cover"
              />
              <div class="activity-detail">
                <div class="activity-title">{{ row.title }}</div>
                <el-tag size="small" :type="getCategoryTag(row.categoryId)">
                  {{ row.activityTypeText || '未分类' }}
                </el-tag>
                <div v-if="row.isRecommended" class="recommended-badge">推荐</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="活动时间" width="180">
          <template #default="{ row }">
            <div class="time-info">
              <div>{{ row.activityDate }}</div>
              <div class="time-range">{{ row.startTime }} - {{ row.endTime }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="活动地点" width="120" />
        <el-table-column label="报名情况" width="150">
          <template #default="{ row }">
            <div class="signup-info">
              <div class="signup-count">
                {{ row.currentParticipants || 0 }} / {{ row.maxParticipants || '不限' }}
              </div>
              <el-progress 
                :percentage="Math.min(row.participationRate || 0, 100)" 
                :status="row.isFull ? 'exception' : ''"
                :stroke-width="8"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="dark">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="浏览/签到" width="120" align="center">
          <template #default="{ row }">
            <div class="view-info">
              <div><el-icon><View /></el-icon> {{ row.viewCount || 0 }}</div>
              <div><el-icon><Check /></el-icon> {{ row.checkedInCount || 0 }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button link type="success" size="small" @click="handleSignup(row)">
              <el-icon><User /></el-icon>报名管理
            </el-button>
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, row)">
              <el-button link type="primary" size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="feedback" v-if="row.status === 4">
                    <el-icon><ChatDotRound /></el-icon>查看反馈
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon>删除活动
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 活动表单对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="800px"
      destroy-on-close
    >
      <el-form 
        :model="formData" 
        :rules="rules" 
        ref="formRef" 
        label-width="100px"
        class="activity-form"
      >
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="活动标题" prop="title">
              <el-input v-model="formData.title" placeholder="请输入活动标题" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="活动分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择" style="width: 100%">
                <el-option 
                  v-for="item in categoryOptions" 
                  :key="item.id" 
                  :label="item.name" 
                  :value="item.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="formData.id">
          <el-col :span="12">
            <el-form-item label="活动状态">
              <el-select v-model="formData.status" placeholder="请选择活动状态" style="width: 100%">
                <el-option label="草稿" :value="0" />
                <el-option label="报名中" :value="1" />
                <el-option label="即将开始" :value="2" />
                <el-option label="进行中" :value="3" />
                <el-option label="已结束" :value="4" />
                <el-option label="已取消" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动日期" prop="activityDate">
              <el-date-picker 
                v-model="formData.activityDate" 
                type="date" 
                placeholder="选择日期" 
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker 
                v-model="formData.startTime" 
                placeholder="开始时间" 
                style="width: 100%"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker 
                v-model="formData.endTime" 
                placeholder="结束时间" 
                style="width: 100%"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动地点" prop="location">
              <el-input v-model="formData.location" placeholder="请输入活动地点">
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最大人数" prop="maxParticipants">
              <el-input-number 
                v-model="formData.maxParticipants" 
                :min="0" 
                :max="999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最小人数" prop="minParticipants">
              <el-input-number 
                v-model="formData.minParticipants" 
                :min="1" 
                :max="999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报名开始">
              <el-date-picker 
                v-model="formData.registrationStartTime" 
                type="datetime" 
                placeholder="报名开始时间" 
                style="width: 100%"
                value-format="YYYY-MM-DDTHH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名截止">
              <el-date-picker 
                v-model="formData.registrationEndTime" 
                type="datetime" 
                placeholder="报名截止时间" 
                style="width: 100%"
                value-format="YYYY-MM-DDTHH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="组织者">
              <el-input v-model="formData.organizer" placeholder="组织者姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="formData.organizerPhone" placeholder="组织者电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="活动封面">
          <el-upload
            class="avatar-uploader"
            :action="uploadAction"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="formData.imageUrl" :src="formData.imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="适合人群">
          <el-input v-model="formData.suitableFor" placeholder="例如：全体住客、能自理住客等" />
        </el-form-item>

        <el-form-item label="所需材料">
          <el-input v-model="formData.materialsNeeded" placeholder="参与活动需要准备的材料" />
        </el-form-item>

        <el-form-item label="参与要求">
          <el-input 
            v-model="formData.requirements" 
            type="textarea" 
            :rows="2"
            placeholder="参与活动的注意事项和要求"
          />
        </el-form-item>

        <el-form-item label="活动描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="4"
            placeholder="详细描述活动内容、流程等"
          />
        </el-form-item>

        <el-form-item label="其他设置">
          <el-checkbox v-model="formData.isRecommended" :true-label="1" :false-label="0">
            推荐活动
          </el-checkbox>
          <el-checkbox v-model="formData.isPublic" :true-label="1" :false-label="0">
            公开活动
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 活动详情对话框 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="700px">
      <div v-if="currentActivity" class="activity-detail-view">
        <div class="detail-header">
          <el-image :src="currentActivity.imageUrl || '/default-activity.png'" class="detail-image" />
          <div class="detail-info">
            <h3>{{ currentActivity.title }}</h3>
            <el-tag :type="getStatusType(currentActivity.status)" effect="dark">
              {{ currentActivity.statusText }}
            </el-tag>
            <el-tag :type="getActivityTypeTag(currentActivity.activityType)" class="ml-2">
              {{ currentActivity.activityTypeText }}
            </el-tag>
          </div>
        </div>
        <el-descriptions :column="2" border class="detail-descriptions">
          <el-descriptions-item label="活动时间">
            {{ currentActivity.activityDate }} {{ currentActivity.startTime }} - {{ currentActivity.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="活动地点">{{ currentActivity.location }}</el-descriptions-item>
          <el-descriptions-item label="组织者">{{ currentActivity.organizer || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentActivity.organizerPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="报名人数">
            {{ currentActivity.currentParticipants }} / {{ currentActivity.maxParticipants || '不限' }}
          </el-descriptions-item>
          <el-descriptions-item label="签到人数">{{ currentActivity.checkedInCount || 0 }}</el-descriptions-item>
        </el-descriptions>
        <div class="detail-section">
          <h4>活动描述</h4>
          <p>{{ currentActivity.description || '暂无描述' }}</p>
        </div>
        <div class="detail-section" v-if="currentActivity.requirements">
          <h4>参与要求</h4>
          <p>{{ currentActivity.requirements }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const IMAGE_SERVER_URL = import.meta.env.VITE_IMAGE_SERVER_URL || 'http://localhost:3001'
const uploadAction = computed(() => `${IMAGE_SERVER_URL}/upload`)
import {
  Plus, Search, Refresh, View, Edit, User, ArrowDown,
  Delete, ChatDotRound, Check,
  Calendar, Clock, VideoPlay, Location
} from '@element-plus/icons-vue'
import {
  getActivityList, createActivity, updateActivity, deleteActivity,
  getActivityStatistics, getActivityCategories
} from '@/api/activity'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('发布活动')
const formRef = ref(null)
const currentActivity = ref(null)

// 统计数据
const statistics = reactive({
  totalActivities: 0,
  monthActivities: 0,
  ongoingActivities: 0,
  registeringActivities: 0
})

// 搜索表单
const searchForm = reactive({
  title: '',
  categoryId: null,
  status: null,
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 分类选项
const categoryOptions = ref([])

// 表单数据
const formData = reactive({
  id: null,
  title: '',
  categoryId: null,
  status: 0,
  activityDate: '',
  startTime: '',
  endTime: '',
  location: '',
  maxParticipants: 50,
  minParticipants: 1,
  organizer: '',
  organizerPhone: '',
  registrationStartTime: '',
  registrationEndTime: '',
  imageUrl: '',
  suitableFor: '',
  materialsNeeded: '',
  requirements: '',
  description: '',
  isRecommended: 0,
  isPublic: 1
})

// 表单规则
const rules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择活动分类', trigger: 'change' }],
  activityDate: [{ required: true, message: '请选择活动日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      title: searchForm.title,
      categoryId: searchForm.categoryId,
      status: searchForm.status
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    console.log('查询参数:', params)
    const res = await getActivityList(params)
    console.log('活动列表数据:', res)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
    console.log('tableData:', tableData.value)
    console.log('pagination.total:', pagination.total)
  } catch (error) {
    console.error('加载数据失败', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载统计
const loadStatistics = async () => {
  try {
    const res = await getActivityStatistics()
    Object.assign(statistics, res.data || {})
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

// 加载分类（只加载启用的分类）
const loadCategories = async () => {
  try {
    const res = await getActivityCategories({ isActive: 1 })
    console.log('分类数据:', res)
    console.log('分类数据.data:', res.data)
    console.log('分类数据.data类型:', typeof res.data)
    console.log('分类数据.data是否为数组:', Array.isArray(res.data))
    categoryOptions.value = res.data || []
    console.log('categoryOptions.value:', categoryOptions.value)
    console.log('categoryOptions.value类型:', typeof categoryOptions.value)
    console.log('categoryOptions.value是否为数组:', Array.isArray(categoryOptions.value))
    console.log('categoryOptions.value长度:', categoryOptions.value.length)
  } catch (error) {
    console.error('加载分类失败', error)
  }
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

// 获取活动类型标签
const getActivityTypeTag = (type) => {
  const types = {
    1: 'success',
    2: 'warning',
    3: 'primary',
    4: 'danger',
    5: 'success',
    6: 'info',
    7: ''
  }
  return types[type] || ''
}

// 获取分类标签样式
const getCategoryTag = (categoryId) => {
  if (!categoryId) return 'info'
  // 根据分类ID返回不同的标签样式
  const tagTypes = ['', 'success', 'warning', 'primary', 'danger', 'info']
  return tagTypes[categoryId % tagTypes.length] || 'info'
}

// 分类选择变化
const handleCategoryChange = (val) => {
  console.log('分类选择变化:', val)
  searchForm.categoryId = val
  handleSearch()
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.title = ''
  searchForm.categoryId = null
  searchForm.status = null
  searchForm.dateRange = []
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '发布活动'
  Object.assign(formData, {
    id: null,
    title: '',
    categoryId: null,
    status: 0,
    activityDate: '',
    startTime: '',
    endTime: '',
    location: '',
    maxParticipants: 50,
    minParticipants: 1,
    organizer: '',
    organizerPhone: '',
    registrationStartTime: '',
    registrationEndTime: '',
    imageUrl: '',
    suitableFor: '',
    materialsNeeded: '',
    requirements: '',
    description: '',
    isRecommended: 0,
    isPublic: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑活动'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 查看
const handleView = (row) => {
  currentActivity.value = row
  detailVisible.value = true
}

// 报名管理
const handleSignup = (row) => {
  router.push(`/activity/signup/${row.id}`)
}

// 更多操作
const handleCommand = async (command, row) => {
  switch (command) {
    case 'feedback':
      router.push(`/activity/feedback/${row.id}`)
      break
    case 'delete':
      try {
        await ElMessageBox.confirm('确定要删除此活动吗？', '提示', { type: 'danger' })
        await deleteActivity(row.id)
        ElMessage.success('删除成功')
        loadData()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
      break
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.id) {
      await updateActivity(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createActivity(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(formData.id ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

// 图片上传成功
const handleImageSuccess = (response) => {
  if (response.code === 200) {
    formData.imageUrl = response.data.url
  }
}

// 图片上传前
const beforeImageUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('只支持 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
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
  loadData()
  loadStatistics()
  loadCategories()
})
</script>

<style scoped lang="scss">
.activity-list-container {
  padding: 20px;

  .statistics-row {
    margin-bottom: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 20px;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        margin-right: 15px;

        &.total {
          background: #e6f7ff;
          color: #1890ff;
        }

        &.month {
          background: #f6ffed;
          color: #52c41a;
        }

        &.ongoing {
          background: #fff7e6;
          color: #fa8c16;
        }

        &.register {
          background: #f9f0ff;
          color: #722ed1;
        }
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #333;
        }

        .stat-label {
          font-size: 14px;
          color: #999;
          margin-top: 5px;
        }
      }
    }
  }

  .activity-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 16px;
        font-weight: bold;
      }
    }

    .search-form {
      margin-bottom: 20px;
    }

    .activity-info {
      display: flex;
      align-items: center;

      .activity-image {
        width: 80px;
        height: 60px;
        border-radius: 4px;
        margin-right: 12px;
        object-fit: cover;
      }

      .activity-detail {
        .activity-title {
          font-weight: bold;
          margin-bottom: 5px;
        }

        .recommended-badge {
          display: inline-block;
          background: #ff4d4f;
          color: white;
          font-size: 12px;
          padding: 2px 6px;
          border-radius: 4px;
          margin-left: 8px;
        }
      }
    }

    .time-info {
      .time-range {
        font-size: 12px;
        color: #999;
        margin-top: 4px;
      }
    }

    .signup-info {
      .signup-count {
        font-weight: bold;
        margin-bottom: 5px;
      }
    }

    .view-info {
      font-size: 13px;
      color: #666;

      div {
        margin: 3px 0;
      }
    }

    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }

  .activity-form {
    :deep(.el-input-number) {
      width: 100%;
      min-width: 200px;
    }

    :deep(.el-input-number .el-input__inner) {
      height: 40px;
      font-size: 14px;
      padding: 0 15px;
    }

    :deep(.el-input-number .el-input-number__decrease),
    :deep(.el-input-number .el-input-number__increase) {
      width: 40px;
      height: 40px;
      line-height: 38px;
    }

    :deep(.el-time-picker) {
      width: 100%;
      min-width: 200px;
    }

    :deep(.el-time-picker .el-input__inner) {
      height: 40px;
      font-size: 14px;
      padding: 0 15px;
    }

    :deep(.el-date-picker) {
      width: 100%;
      min-width: 200px;
    }

    :deep(.el-date-picker .el-input__inner) {
      height: 40px;
      font-size: 14px;
      padding: 0 15px;
    }

    :deep(.el-select) {
      width: 100%;
    }

    :deep(.el-select .el-input__inner) {
      height: 40px;
      font-size: 14px;
    }

    :deep(.el-input) {
      font-size: 14px;
    }

    :deep(.el-input .el-input__inner) {
      height: 40px;
      font-size: 14px;
    }

    :deep(.el-form-item__label) {
      font-size: 14px;
      font-weight: 500;
      padding-bottom: 8px;
    }

    .avatar-uploader {
      :deep(.el-upload) {
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);

        &:hover {
          border-color: var(--el-color-primary);
        }
      }

      .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        text-align: center;
        line-height: 178px;
      }

      .avatar {
        width: 178px;
        height: 178px;
        display: block;
        object-fit: cover;
      }
    }
  }

  .activity-detail-view {
    .detail-header {
      display: flex;
      margin-bottom: 20px;

      .detail-image {
        width: 200px;
        height: 150px;
        border-radius: 8px;
        object-fit: cover;
        margin-right: 20px;
      }

      .detail-info {
        h3 {
          margin: 0 0 10px 0;
        }

        .ml-2 {
          margin-left: 8px;
        }
      }
    }

    .detail-descriptions {
      margin-bottom: 20px;
    }

    .detail-section {
      margin-bottom: 15px;

      h4 {
        margin: 0 0 10px 0;
        color: #333;
        border-left: 4px solid #1890ff;
        padding-left: 10px;
      }

      p {
        margin: 0;
        color: #666;
        line-height: 1.6;
      }
    }
  }

  @media (max-width: 768px) {
    .activity-form {
      :deep(.el-input-number),
      :deep(.el-time-picker),
      :deep(.el-date-picker),
      :deep(.el-select),
      :deep(.el-input) {
        min-width: 100%;
      }

      :deep(.el-input-number .el-input__inner),
      :deep(.el-time-picker .el-input__inner),
      :deep(.el-date-picker .el-input__inner),
      :deep(.el-select .el-input__inner),
      :deep(.el-input .el-input__inner) {
        height: 36px;
        font-size: 13px;
        padding: 0 12px;
      }
    }
  }

  @media (max-width: 480px) {
    .activity-form {
      :deep(.el-input-number .el-input__inner),
      :deep(.el-time-picker .el-input__inner),
      :deep(.el-date-picker .el-input__inner),
      :deep(.el-select .el-input__inner),
      :deep(.el-input .el-input__inner) {
        height: 34px;
        font-size: 13px;
        padding: 0 10px;
      }
    }
  }
}
</style>
