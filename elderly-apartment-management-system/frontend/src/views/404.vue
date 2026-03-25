<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <el-empty
        description="页面不存在"
        :image-size="200"
      >
        <template #description>
          <div class="description">
            <h1>404</h1>
            <p>抱歉，您访问的页面不存在</p>
          </div>
        </template>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
}

.description h1 {
  font-size: 72px;
  color: #409eff;
  margin: 0 0 20px 0;
  font-weight: bold;
}

.description p {
  font-size: 18px;
  color: #666;
  margin: 0 0 30px 0;
}
</style>
