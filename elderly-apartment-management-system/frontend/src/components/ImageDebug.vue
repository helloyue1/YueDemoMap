<template>
  <div class="image-debug">
    <el-card>
      <template #header>
        <span>图片URL调试工具</span>
      </template>
      
      <el-form label-width="120px">
        <el-form-item label="原始路径">
          <el-input v-model="originalPath" placeholder="输入数据库中的图片路径" />
        </el-form-item>
        
        <el-form-item label="构建的URL">
          <el-input v-model="builtUrl" readonly />
        </el-form-item>
        
        <el-form-item label="提取的相对路径">
          <el-input v-model="extractedPath" readonly />
        </el-form-item>
        
        <el-form-item label="图片预览">
          <div v-if="builtUrl" class="preview-container">
            <el-image
              :src="builtUrl"
              fit="cover"
              style="width: 200px; height: 150px; border-radius: 4px"
              @load="handleLoad"
              @error="handleError"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
            <p :class="loadStatus">{{ loadStatusText }}</p>
          </div>
        </el-form-item>
        
        <el-form-item label="环境变量">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="IMAGE_SERVER_URL">
              {{ IMAGE_SERVER_URL }}
            </el-descriptions-item>
            <el-descriptions-item label="API_BASE_URL">
              {{ API_BASE_URL }}
            </el-descriptions-item>
          </el-descriptions>
        </el-form-item>
      </el-form>
      
      <el-divider />
      
      <h4>常见路径格式测试</h4>
      <el-table :data="testCases" stripe style="width: 100%">
        <el-table-column prop="name" label="格式类型" width="150" />
        <el-table-column prop="input" label="输入路径" />
        <el-table-column prop="output" label="构建的URL" />
        <el-table-column label="预览" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.output"
              fit="cover"
              style="width: 60px; height: 45px; border-radius: 4px"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { getImageUrl, extractPath, IMAGE_SERVER_URL } from '@/api/file'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://8.152.163.213:8080'

const originalPath = ref('temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg')
const loadStatus = ref('')
const loadStatusText = ref('')

const builtUrl = computed(() => {
  return getImageUrl(originalPath.value)
})

const extractedPath = computed(() => {
  return extractPath(originalPath.value)
})

const handleLoad = () => {
  loadStatus.value = 'success'
  loadStatusText.value = '图片加载成功'
}

const handleError = () => {
  loadStatus.value = 'error'
  loadStatusText.value = '图片加载失败，请检查路径'
}

const testCases = computed(() => [
  {
    name: '相对路径',
    input: 'temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg',
    output: getImageUrl('temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg')
  },
  {
    name: '带/uploads/前缀',
    input: '/uploads/temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg',
    output: getImageUrl('/uploads/temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg')
  },
  {
    name: '完整URL(3001端口)',
    input: 'http://localhost:3001/uploads/temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg',
    output: getImageUrl('http://localhost:3001/uploads/temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg')
  },
  {
    name: '完整URL(8080端口)',
    input: 'http://localhost:8080/uploads/temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg',
    output: getImageUrl('http://localhost:8080/uploads/temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg')
  }
])

watch(originalPath, (newVal) => {
  console.log('路径调试:', {
    original: newVal,
    built: getImageUrl(newVal),
    extracted: extractPath(newVal)
  })
})
</script>

<style scoped>
.image-debug {
  padding: 20px;
}

.preview-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.image-error {
  width: 200px;
  height: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  border-radius: 4px;
}

.success {
  color: #67c23a;
}

.error {
  color: #f56c6c;
}
</style>
