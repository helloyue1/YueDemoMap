<template>
  <div class="banner-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon total">
            <el-icon><Picture /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.total || 0 }}</div>
            <div class="stat-label">总轮播图</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon active">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.active || 0 }}</div>
            <div class="stat-label">启用中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon inactive">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.inactive || 0 }}</div>
            <div class="stat-label">已禁用</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 轮播图列表卡片 -->
    <el-card class="banner-card">
      <template #header>
        <div class="card-header">
          <span class="title">轮播图管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加轮播图
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="轮播图标题">
          <el-input 
            v-model="searchForm.title" 
            placeholder="请输入标题" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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

      <!-- 轮播图列表 -->
      <el-table :data="bannerList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="轮播图" width="200" align="center">
          <template #default="{ row }">
            <el-image
              :src="getImageUrl(row.imageUrl)"
              :preview-src-list="[getImageUrl(row.imageUrl)]"
              fit="cover"
              class="banner-image"
            />
          </template>
        </el-table-column>
        <el-table-column label="标题" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="title-cell">
              <span class="title-text">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="描述" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="跳转链接" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.linkUrl || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              @click="handleToggleStatus(row)"
            >
              <el-icon><Switch /></el-icon>
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="primary" link @click="handleSort(row)">
              <el-icon><Sort /></el-icon>
              排序
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
          v-model:current-page="pageInfo.page"
          v-model:page-size="pageInfo.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pageInfo.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑轮播图弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加轮播图' : '编辑轮播图'"
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="banner-form"
      >
        <el-form-item label="轮播图标题">
          <el-input v-model="formData.title" placeholder="请输入轮播图标题（可选）" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="轮播图片" prop="imageUrl">
          <el-upload
            class="banner-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <img v-if="formData.imageUrl" :src="getImageUrl(formData.imageUrl)" class="uploaded-image" />
            <div v-else class="upload-placeholder">
              <el-icon><Plus /></el-icon>
              <div class="upload-text">点击上传图片</div>
              <div class="upload-tip">建议尺寸 750x280 像素</div>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="formData.linkUrl" placeholder="请输入跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="排序顺序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
          <span class="form-tip">数字越小排序越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 排序弹窗 -->
    <el-dialog
      v-model="sortDialogVisible"
      title="修改排序"
      width="400px"
      destroy-on-close
    >
      <el-form :model="sortForm" label-width="100px">
        <el-form-item label="当前排序">
          <el-input-number v-model="sortForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sortDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSortSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Picture, CircleCheck, CircleClose, Plus, Search, RefreshRight,
  Edit, Delete, Switch, Sort
} from '@element-plus/icons-vue'
import {
  getBannerList,
  createBanner,
  updateBanner,
  deleteBanner,
  updateBannerStatus,
  updateBannerSort,
  getBannerStatistics
} from '@/api/banner'
import { getImageUrl } from '@/api/file'

// 上传配置
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/file/upload'
const uploadHeaders = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

// 统计数据
const statistics = reactive({
  total: 0,
  active: 0,
  inactive: 0
})

// 搜索表单
const searchForm = reactive({
  title: '',
  status: null
})

// 分页信息
const pageInfo = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 轮播图列表
const bannerList = ref([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const formData = reactive({
  id: null,
  title: '',
  imageUrl: '',
  linkUrl: '',
  sortOrder: 0,
  status: 1,
  description: ''
})

const formRules = {
  imageUrl: [{ required: true, message: '请上传轮播图片', trigger: 'change' }]
}

// 排序弹窗
const sortDialogVisible = ref(false)
const sortForm = reactive({
  id: null,
  sortOrder: 0
})

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await getBannerStatistics()
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取轮播图列表
const fetchBannerList = async () => {
  loading.value = true
  try {
    const params = {
      page: pageInfo.page,
      size: pageInfo.size,
      ...searchForm
    }
    const res = await getBannerList(params)
    if (res.code === 200) {
      bannerList.value = res.data.records
      pageInfo.total = res.data.total
    }
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
    ElMessage.error('获取轮播图列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageInfo.page = 1
  fetchBannerList()
}

// 重置
const handleReset = () => {
  searchForm.title = ''
  searchForm.status = null
  pageInfo.page = 1
  fetchBannerList()
}

// 分页
const handleSizeChange = (val) => {
  pageInfo.size = val
  fetchBannerList()
}

const handleCurrentChange = (val) => {
  pageInfo.page = val
  fetchBannerList()
}

// 添加轮播图
const handleAdd = () => {
  dialogType.value = 'add'
  formData.id = null
  formData.title = ''
  formData.imageUrl = ''
  formData.linkUrl = ''
  formData.sortOrder = 0
  formData.status = 1
  formData.description = ''
  dialogVisible.value = true
}

// 编辑轮播图
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.id = row.id
  formData.title = row.title
  formData.imageUrl = row.imageUrl
  formData.linkUrl = row.linkUrl
  formData.sortOrder = row.sortOrder
  formData.status = row.status
  formData.description = row.description
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    const data = { ...formData }
    let res
    if (data.id) {
      res = await updateBanner(data.id, data)
    } else {
      res = await createBanner(data)
    }
    
    if (res.code === 200) {
      ElMessage.success(data.id ? '轮播图更新成功' : '轮播图创建成功')
      dialogVisible.value = false
      fetchBannerList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('保存轮播图失败:', error)
    ElMessage.error('操作失败')
  }
}

// 切换状态
const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const res = await updateBannerStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(newStatus === 1 ? '轮播图已启用' : '轮播图已禁用')
      fetchBannerList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    ElMessage.error('操作失败')
  }
}

// 打开排序弹窗
const handleSort = (row) => {
  sortForm.id = row.id
  sortForm.sortOrder = row.sortOrder
  sortDialogVisible.value = true
}

// 提交排序
const handleSortSubmit = async () => {
  try {
    const res = await updateBannerSort(sortForm.id, sortForm.sortOrder)
    if (res.code === 200) {
      ElMessage.success('排序更新成功')
      sortDialogVisible.value = false
      fetchBannerList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('更新排序失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除轮播图
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该轮播图吗？删除后不可恢复！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteBanner(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchBannerList()
      fetchStatistics()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除轮播图失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 图片上传成功
const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    // 处理响应数据，提取 URL 字符串
    let url = res.data
    if (typeof url === 'object' && url !== null) {
      url = url.url || url.path || url.fileUrl || ''
    }
    formData.imageUrl = url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// 图片上传失败
const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

// 上传前检查
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
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
  fetchBannerList()
})
</script>

<style scoped lang="scss">
.banner-container {
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

    &.active {
      background: #e8f5e9;
      color: #4caf50;
    }

    &.inactive {
      background: #ffebee;
      color: #f44336;
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

.banner-card {
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

.banner-image {
  width: 160px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  .title-text {
    font-weight: 500;
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.banner-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.banner-uploader {
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
}

.uploaded-image {
  width: 300px;
  height: 140px;
  object-fit: cover;
  display: block;
}

.upload-placeholder {
  width: 300px;
  height: 140px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;

  .el-icon {
    font-size: 28px;
    color: #8c939d;
    margin-bottom: 8px;
  }

  .upload-text {
    font-size: 14px;
    color: #606266;
    margin-bottom: 4px;
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
  }
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
