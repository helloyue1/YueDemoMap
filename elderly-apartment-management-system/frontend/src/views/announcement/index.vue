<template>
  <div class="announcement-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon total">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.total || 0 }}</div>
            <div class="stat-label">总公告数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon published">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.published || 0 }}</div>
            <div class="stat-label">已发布</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon draft">
            <el-icon><EditPen /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.draft || 0 }}</div>
            <div class="stat-label">草稿</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon top">
            <el-icon><Top /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.top || 0 }}</div>
            <div class="stat-label">置顶中</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告列表卡片 -->
    <el-card class="announcement-card">
      <template #header>
        <div class="card-header">
          <span class="title">公告管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            发布公告
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="公告标题">
          <el-input 
            v-model="searchForm.title" 
            placeholder="请输入公告标题" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="普通公告" :value="1" />
            <el-option label="重要公告" :value="2" />
            <el-option label="紧急公告" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 公告列表 -->
      <el-table :data="announcementList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="公告标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="title-cell">
              <el-tag v-if="row.top === 1" type="danger" size="small" effect="dark" class="top-tag">置顶</el-tag>
              <el-tag v-if="row.type === 3" type="danger" size="small">紧急</el-tag>
              <el-tag v-else-if="row.type === 2" type="warning" size="small">重要</el-tag>
              <span class="title-text">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.type === 1" type="info">普通</el-tag>
            <el-tag v-else-if="row.type === 2" type="warning">重要</el-tag>
            <el-tag v-else-if="row.type === 3" type="danger">紧急</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已发布</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" align="center" />
        <el-table-column label="发布时间" width="160" align="center">
          <template #default="{ row }">
            {{ row.publishTime ? formatDateTime(row.publishTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center" />
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              link 
              @click="handlePublish(row)"
            >
              <el-icon><Promotion /></el-icon>
              发布
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              :type="row.top === 1 ? 'warning' : 'success'" 
              link 
              @click="handleToggleTop(row)"
            >
              <el-icon><Top /></el-icon>
              {{ row.top === 1 ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageInfo.pageNum"
          v-model:page-size="pageInfo.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pageInfo.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑公告弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布公告' : '编辑公告'"
      width="800px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="announcement-form"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择公告类型" style="width: 100%">
                <el-option label="普通公告" :value="1" />
                <el-option label="重要公告" :value="2" />
                <el-option label="紧急公告" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否置顶">
              <el-switch v-model="formData.top" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="formData.top === 1" label="置顶截止时间">
          <el-date-picker
            v-model="formData.topEndTime"
            type="datetime"
            placeholder="选择置顶截止时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="info" @click="handleSaveDraft">保存草稿</el-button>
        <el-button type="primary" @click="handleSubmit">立即发布</el-button>
      </template>
    </el-dialog>

    <!-- 查看公告弹窗 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="700px"
      destroy-on-close
    >
      <div class="announcement-detail" v-if="currentAnnouncement">
        <h2 class="detail-title">
          <el-tag v-if="currentAnnouncement.top === 1" type="danger" size="small" effect="dark">置顶</el-tag>
          {{ currentAnnouncement.title }}
        </h2>
        <div class="detail-meta">
          <span><el-icon><User /></el-icon> {{ currentAnnouncement.publisherName || '管理员' }}</span>
          <span><el-icon><Clock /></el-icon> {{ formatDateTime(currentAnnouncement.publishTime) }}</span>
          <span><el-icon><View /></el-icon> 浏览 {{ currentAnnouncement.viewCount || 0 }} 次</span>
          <el-tag v-if="currentAnnouncement.type === 1" type="info">普通公告</el-tag>
          <el-tag v-else-if="currentAnnouncement.type === 2" type="warning">重要公告</el-tag>
          <el-tag v-else-if="currentAnnouncement.type === 3" type="danger">紧急公告</el-tag>
        </div>
        <div class="detail-content">
          <pre>{{ currentAnnouncement.content }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document, CircleCheck, EditPen, Top, Plus, Search, RefreshRight,
  View, Edit, Delete, Promotion, User, Clock
} from '@element-plus/icons-vue'
import request from '@/api/request'

// 统计数据
const statistics = reactive({
  total: 0,
  published: 0,
  draft: 0,
  top: 0
})

// 搜索表单
const searchForm = reactive({
  title: '',
  type: null,
  status: null
})

// 分页信息
const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 公告列表
const announcementList = ref([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const formData = reactive({
  id: null,
  title: '',
  content: '',
  type: 1,
  top: 0,
  topEndTime: null,
  status: 1
})

const formRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }]
}

// 查看详情相关
const viewDialogVisible = ref(false)
const currentAnnouncement = ref(null)

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await request.get('/announcement/statistics')
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取公告列表
const fetchAnnouncementList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      ...searchForm
    }
    const res = await request.get('/announcement/list', { params })
    if (res.code === 200) {
      announcementList.value = res.data.list
      pageInfo.total = res.data.total
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageInfo.pageNum = 1
  fetchAnnouncementList()
}

// 重置
const handleReset = () => {
  searchForm.title = ''
  searchForm.type = null
  searchForm.status = null
  pageInfo.pageNum = 1
  fetchAnnouncementList()
}

// 分页
const handleSizeChange = (val) => {
  pageInfo.pageSize = val
  fetchAnnouncementList()
}

const handleCurrentChange = (val) => {
  pageInfo.pageNum = val
  fetchAnnouncementList()
}

// 添加公告
const handleAdd = () => {
  dialogType.value = 'add'
  formData.id = null
  formData.title = ''
  formData.content = ''
  formData.type = 1
  formData.top = 0
  formData.topEndTime = null
  formData.status = 1
  dialogVisible.value = true
}

// 编辑公告
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.id = row.id
  formData.title = row.title
  formData.content = row.content
  formData.type = row.type
  formData.top = row.top
  formData.topEndTime = row.topEndTime
  formData.status = row.status
  dialogVisible.value = true
}

// 查看公告
const handleView = (row) => {
  currentAnnouncement.value = row
  viewDialogVisible.value = true
  // 增加浏览量
  request.post(`/announcement/view/${row.id}`).catch(() => {})
}

// 保存草稿
const handleSaveDraft = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    const data = { ...formData, status: 0 }
    const url = data.id ? `/announcement/update/${data.id}` : '/announcement/create'
    const method = data.id ? 'put' : 'post'
    const res = await request[method](url, data)
    
    if (res.code === 200) {
      ElMessage.success('草稿保存成功')
      dialogVisible.value = false
      fetchAnnouncementList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存草稿失败')
  }
}

// 发布/提交
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    const data = { ...formData, status: 1 }
    const url = data.id ? `/announcement/update/${data.id}` : '/announcement/create'
    const method = data.id ? 'put' : 'post'
    const res = await request[method](url, data)
    
    if (res.code === 200) {
      ElMessage.success(data.id ? '公告更新成功' : '公告发布成功')
      dialogVisible.value = false
      fetchAnnouncementList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('发布公告失败:', error)
    ElMessage.error('操作失败')
  }
}

// 发布公告（从草稿状态）
const handlePublish = async (row) => {
  try {
    const res = await request.put(`/announcement/publish/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('公告发布成功')
      fetchAnnouncementList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (error) {
    console.error('发布公告失败:', error)
    ElMessage.error('发布失败')
  }
}

// 置顶/取消置顶
const handleToggleTop = async (row) => {
  try {
    const res = await request.put(`/announcement/toggle-top/${row.id}`)
    if (res.code === 200) {
      ElMessage.success(row.top === 1 ? '已取消置顶' : '置顶成功')
      fetchAnnouncementList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('置顶操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除公告
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？删除后不可恢复！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete(`/announcement/delete/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchAnnouncementList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchStatistics()
  fetchAnnouncementList()
})
</script>

<style scoped lang="scss">
.announcement-container {
  padding: 20px;
}

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 15px;

    &.total {
      background: #e3f2fd;
      color: #2196f3;
    }

    &.published {
      background: #e8f5e9;
      color: #4caf50;
    }

    &.draft {
      background: #fff3e0;
      color: #ff9800;
    }

    &.top {
      background: #fce4ec;
      color: #e91e63;
    }

    .el-icon {
      font-size: 28px;
    }
  }

  .stat-info {
    flex: 1;

    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 5px;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }
}

.announcement-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: bold;
    }
  }
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  .top-tag {
    margin-right: 4px;
  }

  .title-text {
    font-weight: 500;
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.announcement-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.announcement-detail {
  .detail-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .detail-meta {
    display: flex;
    align-items: center;
    gap: 20px;
    color: #909399;
    font-size: 14px;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ebeef5;

    span {
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }

  .detail-content {
    line-height: 1.8;
    color: #303133;
    font-size: 14px;

    pre {
      white-space: pre-wrap;
      word-wrap: break-word;
      margin: 0;
      font-family: inherit;
    }
  }
}
</style>
