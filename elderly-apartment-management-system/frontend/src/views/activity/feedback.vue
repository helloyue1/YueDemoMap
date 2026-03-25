<template>
  <div class="activity-feedback-container">
    <!-- 活动信息卡片 -->
    <el-card v-if="activityInfo" class="activity-info-card">
      <div class="activity-header">
        <el-image :src="activityInfo.imageUrl || '/default-activity.png'" class="activity-image" fit="cover" />
        <div class="activity-detail">
          <h3>{{ activityInfo.title }}</h3>
          <p><el-icon><Calendar /></el-icon> {{ activityInfo.activityDate }} {{ activityInfo.startTime }}</p>
          <p><el-icon><Location /></el-icon> {{ activityInfo.location }}</p>
          <el-tag :type="getStatusType(activityInfo.status)" effect="dark">{{ activityInfo.statusText }}</el-tag>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon total">
            <el-icon><ChatLineRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ feedbackStats.totalFeedbacks || 0 }}</div>
            <div class="stat-label">总反馈数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon good">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ feedbackStats.averageRating || 0 }}</div>
            <div class="stat-label">平均评分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon excellent">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ feedbackStats.satisfactionRate || 0 }}%</div>
            <div class="stat-label">满意度</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon pending">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ feedbackStats.pendingReply || 0 }}</div>
            <div class="stat-label">待回复</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 评分分布 -->
    <el-card class="rating-distribution-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>评分分布</span>
        </div>
      </template>
      <div class="rating-bars">
        <div v-for="(count, rating) in feedbackStats.ratingDistribution || {}" :key="rating" class="rating-bar-item">
          <span class="rating-label">{{ rating }}星</span>
          <el-progress 
            :percentage="calculatePercentage(count, feedbackStats.totalFeedbacks)" 
            :color="getRatingColor(rating)"
            :stroke-width="16"
            :show-text="true"
          />
          <span class="rating-count">{{ count }}人</span>
        </div>
      </div>
    </el-card>

    <!-- 反馈列表 -->
    <el-card class="feedback-list-card">
      <template #header>
        <div class="card-header">
          <span>活动反馈</span>
          <div class="header-actions">
            <el-radio-group v-model="searchForm.rating" size="small" @change="handleRatingChange">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button :label="5">5星</el-radio-button>
              <el-radio-button :label="4">4星</el-radio-button>
              <el-radio-button :label="3">3星</el-radio-button>
              <el-radio-button :label="2">2星</el-radio-button>
              <el-radio-button :label="1">1星</el-radio-button>
            </el-radio-group>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索住客姓名或反馈内容"
              style="width: 250px; margin-left: 10px;"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch" style="margin-left: 10px;">
              <el-icon><Search /></el-icon> 搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon> 重置
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="feedbackList"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="住客信息" min-width="150">
          <template #default="{ row }">
            <div class="elderly-info">
              <span class="elderly-name">{{ row.elderlyName }}</span>
              <span class="elderly-room">{{ row.roomNumber }}室</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="150" align="center">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="tags" label="标签" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="tag in parseTags(row.tags)" :key="tag" size="small" style="margin: 2px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="feedbackTime" label="反馈时间" width="160" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.replyContent ? 'success' : 'warning'" size="small">
              {{ row.replyContent ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)">
              查看
            </el-button>
            <el-button type="primary" link size="small" @click="handleReply(row)">
              {{ row.replyContent ? '修改回复' : '回复' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="反馈详情"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentFeedback" class="feedback-detail">
        <div class="detail-section">
          <h4>住客信息</h4>
          <p><strong>姓名：</strong>{{ currentFeedback.elderlyName }}</p>
          <p><strong>房间号：</strong>{{ currentFeedback.roomNumber }}室</p>
          <p><strong>联系方式：</strong>{{ currentFeedback.contactPhone || '未填写' }}</p>
        </div>
        <div class="detail-section">
          <h4>评分</h4>
          <el-rate v-model="currentFeedback.rating" disabled show-score text-color="#ff9900" />
        </div>
        <div class="detail-section">
          <h4>反馈内容</h4>
          <p class="feedback-content">{{ currentFeedback.content }}</p>
        </div>
        <div v-if="currentFeedback.tags" class="detail-section">
          <h4>标签</h4>
          <el-tag v-for="tag in parseTags(currentFeedback.tags)" :key="tag" style="margin: 2px;">
            {{ tag }}
          </el-tag>
        </div>
        <div v-if="currentFeedback.images" class="detail-section">
          <h4>图片</h4>
          <el-image
            v-for="(img, index) in parseImages(currentFeedback.images)"
            :key="index"
            :src="img"
            :preview-src-list="parseImages(currentFeedback.images)"
            style="width: 100px; height: 100px; margin: 5px;"
            fit="cover"
          />
        </div>
        <div class="detail-section">
          <h4>反馈时间</h4>
          <p>{{ currentFeedback.feedbackTime }}</p>
        </div>
        <div v-if="currentFeedback.replyContent" class="detail-section">
          <h4>管理员回复</h4>
          <p class="reply-content">{{ currentFeedback.replyContent }}</p>
          <p class="reply-time">回复时间：{{ currentFeedback.replyTime }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      :title="currentFeedback?.replyContent ? '修改回复' : '回复反馈'"
      width="500px"
      destroy-on-close
    >
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="住客姓名">
          <span>{{ currentFeedback?.elderlyName }}</span>
        </el-form-item>
        <el-form-item label="反馈内容">
          <div class="feedback-preview">{{ currentFeedback?.content }}</div>
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent" required>
          <el-input
            v-model="replyForm.replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="submitReply">
          提交回复
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Calendar, Location, ChatLineRound,
  Star, CircleCheck, Warning
} from '@element-plus/icons-vue'
import {
  getFeedbackList, getFeedbackStatistics, replyFeedback, getActivityById
} from '@/api/activity'

const route = useRoute()
const activityId = computed(() => {
  const id = route.params.id
  if (!id || id === ':id' || id === '') {
    return null
  }
  return parseInt(id)
})

// 活动信息
const activityInfo = ref(null)

// 统计数据
const feedbackStats = ref({
  totalFeedbacks: 0,
  averageRating: 0,
  satisfactionRate: 0,
  pendingReply: 0,
  ratingDistribution: {}
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  rating: ''
})

// 反馈列表
const feedbackList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentFeedback = ref(null)

// 回复对话框
const replyDialogVisible = ref(false)
const replyLoading = ref(false)
const replyForm = reactive({
  replyContent: ''
})

// 获取活动信息
const fetchActivityInfo = async () => {
  if (!activityId.value) return
  try {
    const res = await getActivityById(activityId.value)
    if (res.code === 200) {
      activityInfo.value = res.data
    }
  } catch (error) {
    console.error('获取活动信息失败:', error)
  }
}

// 获取反馈统计
const fetchFeedbackStats = async () => {
  if (!activityId.value) return
  try {
    const res = await getFeedbackStatistics(activityId.value)
    if (res.code === 200) {
      feedbackStats.value = res.data
    }
  } catch (error) {
    console.error('获取反馈统计失败:', error)
  }
}

// 获取反馈列表
const fetchFeedbackList = async () => {
  if (!activityId.value) return
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      activityId: activityId.value,
      keyword: searchForm.keyword,
      rating: searchForm.rating
    }
    const res = await getFeedbackList(params)
    if (res.code === 200) {
      feedbackList.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取反馈列表失败:', error)
    ElMessage.error('获取反馈列表失败')
  } finally {
    loading.value = false
  }
}

// 计算百分比
const calculatePercentage = (count, total) => {
  if (!total || total === 0) return 0
  return Math.round((count / total) * 100)
}

// 获取评分颜色
const getRatingColor = (rating) => {
  const colors = {
    5: '#67C23A',
    4: '#95D475',
    3: '#E6A23C',
    2: '#F89898',
    1: '#F56C6C'
  }
  return colors[rating] || '#909399'
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'info'
  }
  return types[status] || 'info'
}

// 解析标签
const parseTags = (tags) => {
  if (!tags) return []
  try {
    return JSON.parse(tags)
  } catch {
    return tags.split(',').map(t => t.trim())
  }
}

// 解析图片
const parseImages = (images) => {
  if (!images) return []
  try {
    return JSON.parse(images)
  } catch {
    return images.split(',').map(i => i.trim())
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchFeedbackList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.rating = ''
  pagination.page = 1
  fetchFeedbackList()
}

// 评分筛选变化
const handleRatingChange = () => {
  pagination.page = 1
  fetchFeedbackList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchFeedbackList()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  fetchFeedbackList()
}

// 查看详情
const handleViewDetail = (row) => {
  currentFeedback.value = row
  detailDialogVisible.value = true
}

// 回复
const handleReply = (row) => {
  currentFeedback.value = row
  replyForm.replyContent = row.replyContent || ''
  replyDialogVisible.value = true
}

// 提交回复
const submitReply = async () => {
  if (!replyForm.replyContent.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  replyLoading.value = true
  try {
    const res = await replyFeedback({
      id: currentFeedback.value.id,
      replyContent: replyForm.replyContent
    })
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      fetchFeedbackList()
      fetchFeedbackStats()
    } else {
      ElMessage.error(res.message || '回复失败')
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  } finally {
    replyLoading.value = false
  }
}

onMounted(() => {
  fetchActivityInfo()
  fetchFeedbackStats()
  fetchFeedbackList()
})

watch(() => route.params.id, (newId) => {
  if (newId && newId !== ':id') {
    fetchActivityInfo()
    fetchFeedbackStats()
    fetchFeedbackList()
  }
})
</script>

<style scoped lang="scss">
.activity-feedback-container {
  padding: 20px;

  .activity-info-card {
    margin-bottom: 20px;

    .activity-header {
      display: flex;
      align-items: center;
      gap: 20px;

      .activity-image {
        width: 120px;
        height: 80px;
        border-radius: 8px;
      }

      .activity-detail {
        flex: 1;

        h3 {
          margin: 0 0 10px 0;
          font-size: 18px;
          color: #303133;
        }

        p {
          margin: 5px 0;
          color: #606266;
          font-size: 14px;
          display: flex;
          align-items: center;
          gap: 5px;
        }
      }
    }
  }

  .statistics-row {
    margin-bottom: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 15px;

      .stat-icon {
        width: 50px;
        height: 50px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        margin-right: 15px;

        &.total {
          background: #ecf5ff;
          color: #409eff;
        }

        &.good {
          background: #f0f9eb;
          color: #67c23a;
        }

        &.excellent {
          background: #fdf6ec;
          color: #e6a23c;
        }

        &.pending {
          background: #fef0f0;
          color: #f56c6c;
        }
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }

  .rating-distribution-card {
    margin-bottom: 20px;

    .rating-bars {
      .rating-bar-item {
        display: flex;
        align-items: center;
        margin-bottom: 15px;

        .rating-label {
          width: 50px;
          font-size: 14px;
          color: #606266;
        }

        .el-progress {
          flex: 1;
          margin: 0 15px;
        }

        .rating-count {
          width: 60px;
          font-size: 14px;
          color: #909399;
          text-align: right;
        }
      }
    }
  }

  .feedback-list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      span {
        font-size: 16px;
        font-weight: bold;
      }

      .header-actions {
        display: flex;
        align-items: center;
      }
    }

    .elderly-info {
      display: flex;
      flex-direction: column;

      .elderly-name {
        font-weight: bold;
        color: #303133;
      }

      .elderly-room {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
      }
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .feedback-detail {
    .detail-section {
      margin-bottom: 20px;

      h4 {
        margin: 0 0 10px 0;
        font-size: 14px;
        color: #303133;
        border-left: 4px solid #409eff;
        padding-left: 10px;
      }

      p {
        margin: 5px 0;
        color: #606266;
        line-height: 1.6;
      }

      .feedback-content {
        background: #f5f7fa;
        padding: 15px;
        border-radius: 8px;
        color: #303133;
      }

      .reply-content {
        background: #f0f9eb;
        padding: 15px;
        border-radius: 8px;
        color: #67c23a;
      }

      .reply-time {
        font-size: 12px;
        color: #909399;
        margin-top: 5px;
      }
    }
  }

  .feedback-preview {
    background: #f5f7fa;
    padding: 10px;
    border-radius: 4px;
    color: #606266;
    max-height: 100px;
    overflow-y: auto;
  }
}
</style>
