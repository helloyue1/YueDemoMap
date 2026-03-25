<template>
  <div class="safety-index-container">
    <div class="page-header">
      <h2 class="page-title">安全管理</h2>
    </div>
    
    <div class="safety-grid">
      <div class="safety-card" @click="goToAccess">
        <div class="card-icon" style="background: #409eff">
          <el-icon :size="32"><DataAnalysis /></el-icon>
        </div>
        <div class="card-title">访客管理</div>
        <div class="card-desc">来访记录查询与管理</div>
      </div>
      
      <div class="safety-card" @click="goToVisitor">
        <div class="card-icon" style="background: #67c23a">
          <el-icon :size="32"><Bell /></el-icon>
        </div>
        <div class="card-title">访客预约</div>
        <div class="card-desc">访客预约登记管理</div>
      </div>
      
      <div class="safety-card" @click="goToEmergency">
        <div class="card-icon" style="background: #f56c6c">
          <el-icon :size="32"><Warning /></el-icon>
        </div>
        <div class="card-title">紧急呼叫</div>
        <div class="card-desc">紧急呼叫记录与处理</div>
      </div>
      
      <div class="safety-card" @click="goToMonitor">
        <div class="card-icon" style="background: #e6a23c">
          <el-icon :size="32"><VideoCamera /></el-icon>
        </div>
        <div class="card-title">监控中心</div>
        <div class="card-desc">实时监控与录像回放</div>
      </div>
    </div>
    
    <div class="stats-section">
      <h3 class="section-title">今日安全统计</h3>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ stats.visitors }}</div>
          <div class="stat-label">访客人数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.emergency }}</div>
          <div class="stat-label">紧急呼叫</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.alerts }}</div>
          <div class="stat-label">安全预警</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.patrols }}</div>
          <div class="stat-label">巡查次数</div>
        </div>
      </div>
    </div>
    
    <div class="recent-section">
      <h3 class="section-title">最近访客记录</h3>
      <div class="visitor-list">
        <div class="visitor-item" v-for="visitor in recentVisitors" :key="visitor.id">
          <div class="visitor-info">
            <div class="visitor-name">{{ visitor.name }}</div>
            <div class="visitor-target">拜访: {{ visitor.target }}</div>
          </div>
          <div class="visitor-time">{{ visitor.time }}</div>
          <el-tag :type="visitor.status === '已离开' ? 'info' : 'success'" size="small">
            {{ visitor.status }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataAnalysis, Bell, Warning, VideoCamera } from '@element-plus/icons-vue'

const router = useRouter()

const stats = ref({
  visitors: 12,
  emergency: 0,
  alerts: 2,
  patrols: 8
})

const recentVisitors = ref([
  { id: 1, name: '张先生', target: '101房间 王大爷', time: '10:30', status: '已离开' },
  { id: 2, name: '李女士', target: '203房间 张奶奶', time: '09:15', status: '在访中' },
  { id: 3, name: '王先生', target: '305房间 李爷爷', time: '08:45', status: '在访中' },
  { id: 4, name: '赵女士', target: '102房间 刘奶奶', time: '昨天', status: '已离开' }
])

const goToAccess = () => {
  router.push('/access/record')
}

const goToVisitor = () => {
  router.push('/visitor/list')
}

const goToEmergency = () => {
  ElMessage.info('紧急呼叫功能开发中')
}

const goToMonitor = () => {
  ElMessage.info('监控中心功能开发中')
}

onMounted(() => {
  // 这里可以加载实际数据
})
</script>

<style lang="scss" scoped>
.safety-index-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    margin-bottom: 16px;

    .page-title {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }

  .safety-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 16px;

    .safety-card {
      background: #fff;
      border-radius: 12px;
      padding: 16px;
      text-align: center;
      cursor: pointer;
      transition: all 0.2s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }

      &:active {
        transform: scale(0.98);
      }

      .card-icon {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 12px;
        color: #fff;
      }

      .card-title {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }

      .card-desc {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .stats-section {
    background: #fff;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 12px 0;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 8px;

      .stat-item {
        text-align: center;
        padding: 8px 0;

        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #409eff;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  .recent-section {
    background: #fff;
    border-radius: 12px;
    padding: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 12px 0;
    }

    .visitor-list {
      .visitor-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .visitor-info {
          flex: 1;

          .visitor-name {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
            margin-bottom: 2px;
          }

          .visitor-target {
            font-size: 12px;
            color: #909399;
          }
        }

        .visitor-time {
          font-size: 12px;
          color: #909399;
          margin-right: 8px;
        }
      }
    }
  }
}

// 响应式适配
@media screen and (max-width: 375px) {
  .safety-index-container {
    padding: 12px;

    .safety-grid {
      gap: 8px;

      .safety-card {
        padding: 12px;

        .card-icon {
          width: 48px;
          height: 48px;
        }

        .card-title {
          font-size: 14px;
        }

        .card-desc {
          font-size: 11px;
        }
      }
    }

    .stats-section {
      .stats-grid {
        .stat-item {
          .stat-value {
            font-size: 20px;
          }

          .stat-label {
            font-size: 11px;
          }
        }
      }
    }
  }
}
</style>
