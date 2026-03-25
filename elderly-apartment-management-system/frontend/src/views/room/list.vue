<template>
  <div class="room-list-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon total">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.total }}</div>
              <div class="stats-label">总房间数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon empty">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.empty }}</div>
              <div class="stats-label">空闲房间</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon partial">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.partial }}</div>
              <div class="stats-label">部分入住</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon full">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.full }}</div>
              <div class="stats-label">已满房间</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon maintenance">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.maintenance }}</div>
              <div class="stats-label">维修中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-item">
            <div class="stats-icon occupancy">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ occupancyRate }}%</div>
              <div class="stats-label">入住率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>房间管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加房间
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="楼层">
          <el-input-number v-model="searchForm.floor" :min="1" :max="20" placeholder="请选择楼层" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="房间类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="单人间" :value="1" />
            <el-option label="双人间" :value="2" />
            <el-option label="三人间" :value="3" />
            <el-option label="四人间" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="空闲" :value="0" />
            <el-option label="已满" :value="1" />
            <el-option label="维修中" :value="2" />
            <el-option label="部分入住" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column label="封面" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.images || row.coverImage"
              :src="getCoverImage(row)"
              :preview-src-list="getImageList(row.images)"
              fit="cover"
              style="width: 80px; height: 60px; border-radius: 4px; cursor: pointer"
              @error="(e) => handleImageLoadError(e, row.images)"
            >
              <template #error>
                <div class="no-image" :title="getCoverImage(row)">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
            <div v-else class="no-image">
              <el-icon><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="type" label="房间类型" width="100">
          <template #default="{ row }">
            {{ getRoomTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column label="床位情况" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="getOccupancyPercentage(row)"
              :color="getProgressColor(row)"
              :format="() => `${row.currentOccupancy || 0}/${row.capacity}`"
            />
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格(元/月)" width="120">
          <template #default="{ row }">
            {{ row.price ? `¥${row.price}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="设施" width="200">
          <template #default="{ row }">
            <div class="facilities-tags">
              <el-tag v-if="row.hasWifi" size="small" effect="plain">WiFi</el-tag>
              <el-tag v-if="row.hasTv" size="small" effect="plain">电视</el-tag>
              <el-tag v-if="row.hasAc" size="small" effect="plain">空调</el-tag>
              <el-tag v-if="row.hasBathroom" size="small" effect="plain">独立卫浴</el-tag>
              <el-tag v-if="row.hasBalcony" size="small" effect="plain">阳台</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" size="small" @click="handleViewElders(row)">入住住客</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="formData.roomNumber" placeholder="请输入房间号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="formData.floor" :min="1" :max="20" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择房间类型" style="width: 100%" @change="handleTypeChange">
                <el-option label="单人间" :value="1" />
                <el-option label="双人间" :value="2" />
                <el-option label="三人间" :value="3" />
                <el-option label="四人间" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位数" prop="capacity">
              <el-input-number v-model="formData.capacity" :min="1" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格(元/月)" prop="price">
              <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="空闲" :value="0" />
                <el-option label="已满" :value="1" />
                <el-option label="维修中" :value="2" />
                <el-option label="部分入住" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="房间设施">
          <el-checkbox-group v-model="facilityList">
            <el-checkbox value="wifi">WiFi</el-checkbox>
            <el-checkbox value="tv">电视</el-checkbox>
            <el-checkbox value="ac">空调</el-checkbox>
            <el-checkbox value="bathroom">独立卫浴</el-checkbox>
            <el-checkbox value="balcony">阳台</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="房间图片">
          <div class="image-upload-container">
            <el-upload
              list-type="picture-card"
              v-model:file-list="imageFileList"
              :http-request="handleImageUpload"
              :on-remove="handleImageRemove"
              :on-exceed="handleImageExceed"
              :before-upload="beforeImageUpload"
              :limit="5"
              accept="image/*"
            >
              <el-icon><Plus /></el-icon>
              <template #file="{ file }">
                <div class="upload-file-item">
                  <img
                    class="el-upload-list__item-thumbnail"
                    :src="file.url"
                    @error="() => handleUploadImageError(file)"
                  />
                  <div v-if="getImagePath(file) === formData.coverImage" class="upload-cover-badge">
                    <el-tag type="success" size="small" effect="dark">封面</el-tag>
                  </div>
                  <span class="el-upload-list__item-actions">
                    <span
                      class="el-upload-list__item-preview"
                      @click="handlePictureCardPreview(file)"
                    >
                      <el-icon><zoom-in /></el-icon>
                    </span>
                    <span
                      class="el-upload-list__item-delete"
                      @click="handleRemove(file)"
                    >
                      <el-icon><Delete /></el-icon>
                    </span>
                  </span>
                </div>
              </template>
            </el-upload>
            <div class="upload-tip">
              <p>最多上传5张图片，支持JPEG、PNG、GIF、WebP格式，单张最大5MB</p>
              <p v-if="formData.coverImage" class="cover-tip">
                <el-tag type="success" size="small">已设置封面</el-tag>
                <el-button link type="primary" size="small" @click="showCoverSelector = true">更换封面</el-button>
              </p>
              <p v-else-if="imageFileList.length > 0" class="cover-tip">
                <el-tag type="warning" size="small">未设置封面</el-tag>
                <el-button link type="primary" size="small" @click="showCoverSelector = true">选择封面</el-button>
              </p>
            </div>
          </div>
          <el-progress
            v-if="uploadProgress.show"
            :percentage="uploadProgress.percentage"
            :status="uploadProgress.status"
            :stroke-width="6"
            style="margin-top: 10px; width: 200px"
          />
        </el-form-item>

        <el-form-item label="设施描述" prop="facilities">
          <el-input v-model="formData.facilities" type="textarea" :rows="2" placeholder="请输入其他设施描述" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 封面选择对话框 -->
    <el-dialog v-model="showCoverSelector" title="选择封面图片" width="600px">
      <div v-if="imageFileList.length > 0" class="cover-selector">
        <div
          v-for="(file, index) in imageFileList"
          :key="index"
          class="cover-item"
          :class="{ active: getImagePath(file) === formData.coverImage }"
          @click="selectCover(file)"
        >
          <el-image
            :src="file.url || getImageUrl(file.response?.relativePath)"
            fit="cover"
            style="width: 120px; height: 90px; border-radius: 4px"
            @error="console.error('封面图片加载失败:', file)"
          >
            <template #error>
              <div class="image-error" style="width: 120px; height: 90px;">
                <el-icon><Picture /></el-icon>
                <span>加载失败</span>
              </div>
            </template>
          </el-image>
          <div class="cover-badge" v-if="getImagePath(file) === formData.coverImage">
            <el-tag type="success" size="small">封面</el-tag>
          </div>
        </div>
      </div>
      <el-empty v-else description="请先上传图片" :image-size="100" />
      <template #footer>
        <el-button @click="showCoverSelector = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="房间详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="房间号">{{ viewData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ viewData.floor }}楼</el-descriptions-item>
        <el-descriptions-item label="房间类型">{{ getRoomTypeText(viewData.type) }}</el-descriptions-item>
        <el-descriptions-item label="床位情况">{{ viewData.currentOccupancy || 0 }}/{{ viewData.capacity }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ viewData.price ? `¥${viewData.price}/月` : '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">{{ getStatusText(viewData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设施" :span="2">
          <el-tag v-if="viewData.hasWifi" size="small">WiFi</el-tag>
          <el-tag v-if="viewData.hasTv" size="small">电视</el-tag>
          <el-tag v-if="viewData.hasAc" size="small">空调</el-tag>
          <el-tag v-if="viewData.hasBathroom" size="small">独立卫浴</el-tag>
          <el-tag v-if="viewData.hasBalcony" size="small">阳台</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设施描述" :span="2">{{ viewData.facilities || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="room-images">
        <h4>房间图片</h4>
        <div v-if="getImageList(viewData.images).length > 0" class="image-list">
          <el-image
            v-for="(url, index) in getImageList(viewData.images)"
            :key="index"
            :src="url"
            :preview-src-list="getImageList(viewData.images)"
            fit="cover"
            style="width: 150px; height: 100px; margin-right: 10px; margin-bottom: 10px; border-radius: 4px; cursor: pointer"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
                <span>加载失败</span>
              </div>
            </template>
          </el-image>
        </div>
        <el-empty v-else description="暂无图片" :image-size="100" />
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="dialogVisible2">
      <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%" />
    </el-dialog>

    <el-dialog v-model="eldersDialogVisible" title="入住住客列表" width="800px">
      <el-table :data="roomElders" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="healthStatus" label="健康状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHealthStatusType(row.healthStatus)">
              {{ getHealthStatusText(row.healthStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="emergencyContact" label="紧急联系人" width="120" />
        <el-table-column prop="emergencyPhone" label="紧急电话" width="130" />
      </el-table>
      <template #footer>
        <el-button @click="eldersDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, OfficeBuilding, CircleCheck, CircleClose, Tools, User, TrendCharts, Picture, ZoomIn, Delete } from '@element-plus/icons-vue'
import { getRoomList, createRoom, updateRoom, deleteRoom, getRoomStats, getRoomElders } from '@/api/room'
import { uploadImage, getImageUrl } from '@/api/file'
import { ElMessage, ElMessageBox } from 'element-plus'
import { validateImageFile, formatFileSize } from '@/utils/imageUpload'

const searchForm = reactive({
  roomNumber: '',
  floor: null,
  type: null,
  status: null
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const stats = reactive({
  total: 0,
  empty: 0,
  partial: 0,
  full: 0,
  maintenance: 0
})

const occupancyRate = computed(() => {
  if (stats.total === 0) return 0
  return Math.round(((stats.total - stats.empty) / stats.total) * 100)
})

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const eldersDialogVisible = ref(false)
const dialogTitle = ref('添加房间')
const submitLoading = ref(false)
const roomElders = ref([])
const showCoverSelector = ref(false)

const uploadProgress = reactive({
  show: false,
  percentage: 0,
  status: ''
})

const formData = reactive({
  id: null,
  roomNumber: '',
  floor: 1,
  type: 2,
  capacity: 2,
  price: null,
  status: 0,
  hasWifi: false,
  hasTv: false,
  hasAc: false,
  hasBathroom: false,
  hasBalcony: false,
  facilities: '',
  coverImage: '',
  images: '',
  remark: ''
})

const facilityList = ref([])
const imageFileList = ref([])

const viewData = reactive({
  roomNumber: '',
  floor: 1,
  type: 2,
  capacity: 2,
  currentOccupancy: 0,
  price: null,
  status: 0,
  hasWifi: false,
  hasTv: false,
  hasAc: false,
  hasBathroom: false,
  hasBalcony: false,
  facilities: '',
  coverImage: '',
  images: '',
  remark: ''
})

const rules = {
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  type: [{ required: true, message: '请选择房间类型', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入床位数', trigger: 'blur' }]
}

const formRef = ref(null)

const loadStats = async () => {
  try {
    const res = await getRoomStats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      roomNumber: searchForm.roomNumber,
      floor: searchForm.floor,
      type: searchForm.type,
      status: searchForm.status
    }
    const res = await getRoomList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.roomNumber = ''
  searchForm.floor = null
  searchForm.type = null
  searchForm.status = null
  handleSearch()
}

const handleTypeChange = (type) => {
  const capacityMap = { 1: 1, 2: 2, 3: 3, 4: 4 }
  formData.capacity = capacityMap[type] || 2
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    roomNumber: '',
    floor: 1,
    type: 2,
    capacity: 2,
    price: null,
    status: 0,
    hasWifi: false,
    hasTv: false,
    hasAc: false,
    hasBathroom: false,
    hasBalcony: false,
    facilities: '',
    coverImage: '',
    images: '',
    remark: ''
  })
  facilityList.value = []
  imageFileList.value = []
  uploadProgress.show = false
  uploadProgress.percentage = 0
}

const beforeImageUpload = (file) => {
  const validation = validateImageFile(file)
  if (!validation.valid) {
    validation.errors.forEach(error => ElMessage.error(error))
    return false
  }
  return true
}

const handleImageUpload = async (options) => {
  const { file, onSuccess, onError } = options
  
  uploadProgress.show = true
  uploadProgress.percentage = 0
  uploadProgress.status = ''
  
  try {
    const res = await uploadImage(file, 'rooms', (progressEvent) => {
      if (progressEvent.total > 0) {
        uploadProgress.percentage = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      }
    })
    
    console.log('上传响应:', res)
    
    if (res && res.code === 200 && res.data) {
      uploadProgress.status = 'success'
      uploadProgress.percentage = 100
      
      // 更新上传的文件信息
      const targetFile = imageFileList.value.find(f => f.uid === file.uid || f.name === file.name)
      if (targetFile) {
        targetFile.response = res.data
        targetFile.status = 'success'
        targetFile.url = res.data.url
      }
      
      // 更新 formData.images
      const currentImages = formData.images ? formData.images.split(',').filter(img => img && img.trim()) : []
      if (res.data.relativePath) {
        // 确保路径不包含 /uploads/ 前缀
        const cleanPath = res.data.relativePath.replace(/^\/uploads\//, '')
        currentImages.push(cleanPath)
        formData.images = currentImages.join(',')
      }
      
      setTimeout(() => {
        uploadProgress.show = false
      }, 1500)
      
      onSuccess(res.data)
      ElMessage.success('图片上传成功')
    } else {
      throw new Error(res?.message || '上传失败')
    }
  } catch (error) {
    console.error('上传错误:', error)
    uploadProgress.status = 'exception'
    uploadProgress.show = false
    
    const targetFile = imageFileList.value.find(f => f.uid === file.uid)
    if (targetFile) {
      targetFile.status = 'fail'
    }
    
    ElMessage.error('图片上传失败: ' + (error.message || '未知错误'))
    onError(error)
  }
}

const handleImageRemove = (file, fileList) => {
  const relativePaths = fileList.map(f => {
    const path = getImagePath(f)
    // 确保路径不包含 /uploads/ 前缀
    return path ? path.replace(/^\/uploads\//, '') : ''
  }).filter(Boolean)
  formData.images = relativePaths.join(',')
}

const handleImageExceed = () => {
  ElMessage.warning('最多只能上传5张图片')
}

const extractPath = (url) => {
  if (!url) return ''
  
  if (url.startsWith('blob:')) {
    return ''
  }
  
  if (!url.startsWith('http') && !url.startsWith('/uploads')) {
    if (url.includes('/uploads/')) {
      const match = url.match(/\/uploads\/(.+)$/)
      return match ? match[1] : ''
    }
    return url
  }
  
  if (url.includes('blob:')) {
    return ''
  }
  
  const match = url.match(/\/uploads\/(.+)$/)
  return match ? match[1] : ''
}

const handleAdd = () => {
  dialogTitle.value = '添加房间'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房间'
  resetForm()
  
  // 先复制基本数据
  Object.assign(formData, {
    id: row.id,
    roomNumber: row.roomNumber,
    floor: row.floor,
    type: row.type,
    capacity: row.capacity,
    price: row.price,
    status: row.status,
    hasWifi: row.hasWifi,
    hasTv: row.hasTv,
    hasAc: row.hasAc,
    hasBathroom: row.hasBathroom,
    hasBalcony: row.hasBalcony,
    facilities: row.facilities,
    coverImage: row.coverImage || '',
    images: row.images || '',
    remark: row.remark
  })

  facilityList.value = []
  if (row.hasWifi) facilityList.value.push('wifi')
  if (row.hasTv) facilityList.value.push('tv')
  if (row.hasAc) facilityList.value.push('ac')
  if (row.hasBathroom) facilityList.value.push('bathroom')
  if (row.hasBalcony) facilityList.value.push('balcony')

  if (row.images) {
    const imagePaths = row.images.split(',').map(url => url.trim()).filter(Boolean)
    imageFileList.value = imagePaths.map((path, index) => {
      // 确保路径不包含 /uploads/ 前缀
      const cleanPath = path.replace(/^\/uploads\//, '')
      const fullUrl = getImageUrl(cleanPath)
      return {
        name: `image-${index}`,
        url: fullUrl,
        response: { relativePath: cleanPath }
      }
    }).filter(f => f.response.relativePath)
  }

  dialogVisible.value = true
}

const handleView = (row) => {
  Object.assign(viewData, row)
  viewDialogVisible.value = true
}

const handleViewElders = async (row) => {
  try {
    const res = await getRoomElders(row.id)
    if (res.code === 200) {
      roomElders.value = res.data || []
      eldersDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载入住住客列表失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该房间吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRoom(row.id)
    ElMessage.success('删除成功')
    loadData()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    formData.hasWifi = facilityList.value.includes('wifi')
    formData.hasTv = facilityList.value.includes('tv')
    formData.hasAc = facilityList.value.includes('ac')
    formData.hasBathroom = facilityList.value.includes('bathroom')
    formData.hasBalcony = facilityList.value.includes('balcony')

    const relativePaths = imageFileList.value.map(f => {
      // 优先使用 getImagePath 获取正确的相对路径
      const path = getImagePath(f)
      // 确保路径不包含 /uploads/ 前缀
      if (path) {
        return path.replace(/^\/uploads\//, '')
      }
      return ''
    }).filter(Boolean)
    formData.images = relativePaths.join(',')

    const submitData = { ...formData }
    
    console.log('提交数据:', submitData)
    console.log('coverImage:', submitData.coverImage)

    if (submitData.id) {
      const res = await updateRoom(submitData.id, submitData)
      if (res.code === 200) {
        ElMessage.success('更新成功')
      }
    } else {
      const res = await createRoom(submitData)
      if (res.code === 200) {
        ElMessage.success('添加成功')
      }
    }
    dialogVisible.value = false
    loadData()
    loadStats()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.message && error.message.includes('validate')) {
      ElMessage.error('请检查表单填写是否正确')
    } else {
      ElMessage.error('操作失败: ' + (error.message || error))
    }
  } finally {
    submitLoading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getRoomTypeText = (type) => {
  const map = { 1: '单人间', 2: '双人间', 3: '三人间', 4: '四人间' }
  return map[type] || '未知'
}

const getStatusText = (status) => {
  const map = { 0: '空闲', 1: '已满', 2: '维修中', 3: '部分入住' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'success', 1: 'danger', 2: 'info', 3: 'warning' }
  return map[status] || 'info'
}

const getHealthStatusText = (status) => {
  const map = { 1: '良好', 2: '一般', 3: '较差', 4: '危急' }
  return map[status] || '未知'
}

const getHealthStatusType = (status) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'danger' }
  return map[status] || 'info'
}

const getOccupancyPercentage = (row) => {
  if (!row.capacity) return 0
  return Math.round(((row.currentOccupancy || 0) / row.capacity) * 100)
}

const getProgressColor = (row) => {
  const percentage = getOccupancyPercentage(row)
  if (percentage === 0) return '#67C23A'
  if (percentage < 100) return '#E6A23C'
  return '#F56C6C'
}

const getFirstImage = (images) => {
  if (!images) {
    console.log('getFirstImage: images is empty')
    return ''
  }
  const firstUrl = images.split(',')[0].trim()
  if (!firstUrl) {
    console.log('getFirstImage: firstUrl is empty')
    return ''
  }
  const fullUrl = getImageUrl(firstUrl)
  console.log('getFirstImage:', { images, firstUrl, fullUrl })
  return fullUrl
}

const getImageList = (images) => {
  if (!images) {
    console.log('getImageList: images is empty')
    return []
  }
  const urls = images.split(',').map(url => getImageUrl(url.trim())).filter(Boolean)
  console.log('getImageList:', { images, urls })
  return urls
}

const handleImageLoadError = (event, images) => {
  console.error('图片加载失败:', {
    images,
    firstImage: getFirstImage(images),
    event
  })
}

const getImagePath = (file) => {
  if (file.response && file.response.relativePath) {
    return file.response.relativePath
  }
  if (file.url) {
    return extractPath(file.url)
  }
  return ''
}

const selectCover = (file) => {
  const path = getImagePath(file)
  if (path) {
    // 确保路径不包含 /uploads/ 前缀
    formData.coverImage = path.replace(/^\/uploads\//, '')
    ElMessage.success('封面设置成功')
    showCoverSelector.value = false
  }
}

const getCoverImage = (row) => {
  if (row.coverImage) {
    return getImageUrl(row.coverImage)
  }
  return getFirstImage(row.images)
}

const handleUploadImageError = (file) => {
  console.error('上传图片加载失败:', file)
  // 尝试重新构建URL
  if (file.response?.relativePath) {
    file.url = getImageUrl(file.response.relativePath)
    console.log('重新构建URL:', file.url)
  }
}

const dialogImageUrl = ref('')
const dialogVisible2 = ref(false)

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url
  dialogVisible2.value = true
}

const handleRemove = (file) => {
  const index = imageFileList.value.indexOf(file)
  if (index > -1) {
    imageFileList.value.splice(index, 1)
    // 更新 formData.images
    const relativePaths = imageFileList.value.map(f => getImagePath(f)).filter(Boolean)
    formData.images = relativePaths.join(',')
    // 如果删除的是封面，清空封面
    if (getImagePath(file) === formData.coverImage) {
      formData.coverImage = ''
    }
  }
}

onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style scoped>
.room-list-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-5px);
}

.stats-item {
  display: flex;
  align-items: center;
}

.stats-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 15px;
}

.stats-icon.total {
  background: #ecf5ff;
  color: #409eff;
}

.stats-icon.empty {
  background: #f0f9eb;
  color: #67c23a;
}

.stats-icon.partial {
  background: #fdf6ec;
  color: #e6a23c;
}

.stats-icon.full {
  background: #fef0f0;
  color: #f56c6c;
}

.stats-icon.maintenance {
  background: #f4f4f5;
  color: #909399;
}

.stats-icon.occupancy {
  background: #e6f7ff;
  color: #1890ff;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.table-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.no-image {
  width: 80px;
  height: 60px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
}

.no-image .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.facilities-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.facilities-tags .el-tag {
  margin-right: 0;
}

.room-images {
  margin-top: 20px;
}

.room-images h4 {
  margin-bottom: 15px;
  color: #606266;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.4;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.image-error .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.image-upload-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cover-tip {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.cover-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  padding: 10px;
}

.cover-item {
  position: relative;
  cursor: pointer;
  border: 3px solid transparent;
  border-radius: 4px;
  transition: all 0.3s;
}

.cover-item:hover {
  border-color: #409eff;
}

.cover-item.active {
  border-color: #67c23a;
}

.cover-badge {
  position: absolute;
  bottom: 5px;
  left: 50%;
  transform: translateX(-50%);
}

.upload-file-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.upload-cover-badge {
  position: absolute;
  top: 5px;
  right: 5px;
}

.upload-image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.upload-image-error .el-icon {
  font-size: 16px;
  margin-bottom: 2px;
}
</style>
