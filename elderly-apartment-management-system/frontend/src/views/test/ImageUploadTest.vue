<template>
  <div class="upload-test-container">
    <ImagePathDebugger style="margin-bottom: 20px" />
    <ImageDebug style="margin-bottom: 20px" />
    
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <span>图片上传功能测试</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="单文件上传" name="single">
          <div class="upload-section">
            <h3>单文件上传测试</h3>
            <el-upload
              class="upload-demo"
              drag
              action="#"
              :http-request="handleSingleUpload"
              :before-upload="beforeUpload"
              :on-success="handleSuccess"
              :on-error="handleError"
              accept="image/*"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽文件到此处或 <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 JPEG、PNG、GIF、WebP 格式，单张最大 5MB
                </div>
              </template>
            </el-upload>

            <div v-if="singleUploadResult" class="result-section">
              <h4>上传结果：</h4>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="相对路径">{{ singleUploadResult.relativePath }}</el-descriptions-item>
                <el-descriptions-item label="完整URL">
                  <el-link :href="singleUploadResult.url" target="_blank" type="primary">
                    {{ singleUploadResult.url }}
                  </el-link>
                </el-descriptions-item>
                <el-descriptions-item label="文件名">{{ singleUploadResult.filename }}</el-descriptions-item>
                <el-descriptions-item label="文件大小">{{ formatFileSize(singleUploadResult.size) }}</el-descriptions-item>
              </el-descriptions>
              <div class="preview-section">
                <h4>图片预览：</h4>
                <el-image
                  :src="singleUploadResult.url"
                  fit="cover"
                  style="width: 200px; height: 150px; border-radius: 4px"
                >
                  <template #error>
                    <div class="image-error">加载失败</div>
                  </template>
                </el-image>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="批量上传" name="batch">
          <div class="upload-section">
            <h3>批量上传测试</h3>
            <el-upload
              class="upload-demo"
              drag
              action="#"
              :http-request="handleBatchUpload"
              :before-upload="beforeUpload"
              multiple
              accept="image/*"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽多个文件到此处或 <em>点击上传</em>
              </div>
            </el-upload>

            <div v-if="batchUploadResults.length > 0" class="result-section">
              <h4>上传结果 ({{ batchUploadResults.length }} 个文件)：</h4>
              <el-table :data="batchUploadResults" stripe>
                <el-table-column prop="filename" label="文件名" />
                <el-table-column prop="relativePath" label="相对路径" />
                <el-table-column label="预览" width="120">
                  <template #default="{ row }">
                    <el-image
                      :src="row.url"
                      fit="cover"
                      style="width: 80px; height: 60px; border-radius: 4px"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="上传进度" name="progress">
          <div class="upload-section">
            <h3>上传进度测试</h3>
            <el-upload
              class="upload-demo"
              drag
              action="#"
              :http-request="handleProgressUpload"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽文件到此处或 <em>点击上传</em>
              </div>
            </el-upload>

            <div v-if="uploadProgress.show" class="progress-section">
              <h4>上传进度：</h4>
              <el-progress
                :percentage="uploadProgress.percentage"
                :status="uploadProgress.status"
                :stroke-width="20"
                striped
                striped-flow
              />
              <p>已上传: {{ formatFileSize(uploadProgress.loaded) }} / {{ formatFileSize(uploadProgress.total) }}</p>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="文件验证" name="validation">
          <div class="upload-section">
            <h3>文件验证测试</h3>
            <el-alert
              title="测试各种文件类型的验证"
              type="info"
              description="尝试上传不同类型的文件来测试验证功能"
              show-icon
              :closable="false"
              style="margin-bottom: 20px"
            />
            <el-upload
              class="upload-demo"
              drag
              action="#"
              :http-request="handleValidationUpload"
              :before-upload="beforeUploadWithValidation"
              accept="*/*"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽任意文件到此处或 <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  尝试上传非图片文件或大文件来测试验证
                </div>
              </template>
            </el-upload>

            <div v-if="validationResults.length > 0" class="result-section">
              <h4>验证结果：</h4>
              <el-timeline>
                <el-timeline-item
                  v-for="(result, index) in validationResults"
                  :key="index"
                  :type="result.valid ? 'success' : 'danger'"
                  :icon="result.valid ? 'Check' : 'Close'"
                >
                  <p><strong>{{ result.filename }}</strong></p>
                  <p>类型: {{ result.type }}</p>
                  <p>大小: {{ formatFileSize(result.size) }}</p>
                  <p :class="result.valid ? 'text-success' : 'text-danger'">
                    {{ result.valid ? '验证通过' : '验证失败: ' + result.errors.join(', ') }}
                  </p>
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="图片工具函数" name="utils">
          <div class="upload-section">
            <h3>图片工具函数测试</h3>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-card>
                  <template #header>extractRelativePath</template>
                  <el-input v-model="testUrl" placeholder="输入URL测试" />
                  <p class="result">结果: {{ extractRelativePath(testUrl) }}</p>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <template #header>buildImageUrl</template>
                  <el-input v-model="testPath" placeholder="输入相对路径测试" />
                  <p class="result">结果: {{ buildImageUrl(testPath) }}</p>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top: 20px">
              <el-col :span="12">
                <el-card>
                  <template #header>parseImageList</template>
                  <el-input
                    v-model="testImageList"
                    type="textarea"
                    :rows="3"
                    placeholder="输入逗号分隔的图片路径"
                  />
                  <pre class="result">{{ JSON.stringify(parseImageList(testImageList), null, 2) }}</pre>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <template #header>formatFileSize</template>
                  <el-input-number v-model="testFileSize" :min="0" style="width: 100%" />
                  <p class="result">结果: {{ formatFileSize(testFileSize) }}</p>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { UploadFilled, Check, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { uploadImage, uploadImages } from '@/api/file'
import {
  validateImageFile,
  formatFileSize,
  extractRelativePath,
  buildImageUrl,
  parseImageList
} from '@/utils/imageUpload'
import ImageDebug from '@/components/ImageDebug.vue'
import ImagePathDebugger from '@/components/ImagePathDebugger.vue'

const activeTab = ref('single')
const singleUploadResult = ref(null)
const batchUploadResults = ref([])
const validationResults = ref([])

const uploadProgress = reactive({
  show: false,
  percentage: 0,
  status: '',
  loaded: 0,
  total: 0
})

const testUrl = ref('http://localhost:3001/uploads/rooms/20260208/test.jpg')
const testPath = ref('rooms/20260208/test.jpg')
const testImageList = ref('rooms/20260208/test1.jpg, rooms/20260208/test2.jpg')
const testFileSize = ref(1024 * 1024 * 2.5)

const beforeUpload = (file) => {
  const validation = validateImageFile(file)
  if (!validation.valid) {
    validation.errors.forEach(error => ElMessage.error(error))
    return false
  }
  return true
}

const beforeUploadWithValidation = (file) => {
  const validation = validateImageFile(file)
  validationResults.value.push({
    filename: file.name,
    type: file.type,
    size: file.size,
    valid: validation.valid,
    errors: validation.errors
  })
  return validation.valid
}

const handleSingleUpload = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    const res = await uploadImage(file, 'test')
    if (res && res.code === 200 && res.data) {
      singleUploadResult.value = res.data
      onSuccess(res.data)
      ElMessage.success('上传成功')
    } else {
      throw new Error(res?.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败: ' + error.message)
    onError(error)
  }
}

const handleBatchUpload = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    const res = await uploadImage(file, 'test')
    if (res && res.code === 200 && res.data) {
      batchUploadResults.value.push(res.data)
      onSuccess(res.data)
    } else {
      throw new Error(res?.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败: ' + error.message)
    onError(error)
  }
}

const handleProgressUpload = async (options) => {
  const { file, onSuccess, onError } = options
  
  uploadProgress.show = true
  uploadProgress.percentage = 0
  uploadProgress.status = ''
  uploadProgress.loaded = 0
  uploadProgress.total = file.size
  
  try {
    const res = await uploadImage(file, 'test', (progress) => {
      uploadProgress.percentage = progress.percentage
      uploadProgress.loaded = progress.loaded
      uploadProgress.total = progress.total
    })
    
    if (res && res.code === 200 && res.data) {
      uploadProgress.status = 'success'
      uploadProgress.percentage = 100
      onSuccess(res.data)
      ElMessage.success('上传成功')
      setTimeout(() => {
        uploadProgress.show = false
      }, 2000)
    } else {
      throw new Error(res?.message || '上传失败')
    }
  } catch (error) {
    uploadProgress.status = 'exception'
    ElMessage.error('上传失败: ' + error.message)
    onError(error)
  }
}

const handleValidationUpload = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    const res = await uploadImage(file, 'test')
    if (res && res.code === 200 && res.data) {
      onSuccess(res.data)
      ElMessage.success('验证通过并上传成功')
    } else {
      throw new Error(res?.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败: ' + error.message)
    onError(error)
  }
}

const handleSuccess = () => {
  console.log('上传成功')
}

const handleError = (error) => {
  console.error('上传错误:', error)
}
</script>

<style scoped>
.upload-test-container {
  padding: 20px;
}

.test-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.upload-section {
  padding: 20px;
}

.upload-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.upload-demo {
  width: 100%;
}

.result-section {
  margin-top: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.result-section h4 {
  margin-bottom: 15px;
  color: #606266;
}

.preview-section {
  margin-top: 20px;
}

.progress-section {
  margin-top: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.progress-section h4 {
  margin-bottom: 15px;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
}

.text-success {
  color: #67c23a;
}

.text-danger {
  color: #f56c6c;
}

.result {
  margin-top: 10px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  font-family: monospace;
  word-break: break-all;
}

pre.result {
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}
</style>
