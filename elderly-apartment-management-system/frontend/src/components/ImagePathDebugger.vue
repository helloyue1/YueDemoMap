<template>
  <div class="image-path-debugger">
    <el-card>
      <template #header>
        <span>图片路径调试器</span>
      </template>
      
      <el-alert
        title="此工具用于调试数据库中的图片路径"
        type="info"
        description="输入数据库中存储的图片路径，查看构建的URL是否正确"
        show-icon
        :closable="false"
        style="margin-bottom: 20px"
      />
      
      <el-form label-width="120px">
        <el-form-item label="数据库路径">
          <el-input 
            v-model="dbPath" 
            placeholder="输入数据库中的图片路径，如: rooms/20260208/test.jpg"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="构建的URL">
          <el-input v-model="builtUrl" readonly />
        </el-form-item>
        
        <el-form-item label="直接访问测试">
          <el-link :href="builtUrl" target="_blank" type="primary">
            {{ builtUrl }}
          </el-link>
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
            <p v-if="loadStatus" :class="loadStatus.type">{{ loadStatus.message }}</p>
          </div>
        </el-form-item>
      </el-form>
      
      <el-divider />
      
      <h4>常见问题</h4>
      <el-collapse>
        <el-collapse-item title="路径格式说明" name="1">
          <p><strong>支持的格式：</strong></p>
          <ul>
            <li>相对路径: <code>rooms/20260208/test.jpg</code></li>
            <li>带/uploads/前缀: <code>/uploads/rooms/20260208/test.jpg</code></li>
            <li>完整URL: <code>http://localhost:3001/uploads/rooms/20260208/test.jpg</code></li>
          </ul>
          <p><strong>构建后的URL：</strong></p>
          <p>所有格式最终都会构建为: <code>http://localhost:3001/uploads/rooms/20260208/test.jpg</code></p>
        </el-collapse-item>
        <el-collapse-item title="图片加载失败排查" name="2">
          <ol>
            <li>检查图片服务器是否运行: <code>http://localhost:3001/health</code></li>
            <li>检查图片文件是否存在: 查看 <code>image-server/uploads</code> 目录</li>
            <li>检查CORS配置: 确保前端端口在允许列表中</li>
            <li>检查浏览器控制台是否有错误信息</li>
          </ol>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { getImageUrl } from '@/api/file'

const dbPath = ref('temp/20260208/417abf2f43eb44eea4c8e71616bc8515.jpg')
const loadStatus = ref(null)

const builtUrl = computed(() => {
  return getImageUrl(dbPath.value)
})

const handleLoad = () => {
  loadStatus.value = { type: 'success', message: '图片加载成功' }
}

const handleError = () => {
  loadStatus.value = { type: 'error', message: '图片加载失败，请检查路径是否正确' }
}

watch(dbPath, (newVal) => {
  loadStatus.value = null
  console.log('图片路径调试:', {
    dbPath: newVal,
    builtUrl: getImageUrl(newVal)
  })
})
</script>

<style scoped>
.image-path-debugger {
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

code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: monospace;
}
</style>
