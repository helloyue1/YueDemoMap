<template>
  <div class="statistics-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月应收</div>
              <div class="stat-value">¥{{ overview.totalReceivable?.toFixed(2) || '0.00' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月实收</div>
              <div class="stat-value">¥{{ overview.totalReceived?.toFixed(2) || '0.00' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月欠费</div>
              <div class="stat-value">¥{{ overview.totalUnpaid?.toFixed(2) || '0.00' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon" style="background: #909399;">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">收缴率</div>
              <div class="stat-value">{{ overview.collectionRate?.toFixed(2) || '0.00' }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>月度收费趋势</span>
          </template>
          <div ref="trendChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>费用类型占比</span>
          </template>
          <div ref="pieChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 欠费住客列表 -->
    <el-card class="debtor-card">
      <template #header>
        <span>欠费住客列表</span>
      </template>
      <el-table :data="debtorList" stripe style="width: 100%">
        <el-table-column prop="elderlyName" label="住客姓名" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="totalUnpaid" label="欠费金额" width="120">
          <template #default="{ row }">
            <span class="debt-amount">¥{{ Number(row.totalUnpaid || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unpaidMonths" label="欠费月数" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleNotify(row)">发送催缴通知</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Money, Check, Warning, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/api/request'

const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart = null
let pieChart = null

const overview = reactive({
  totalReceivable: 0,
  totalReceived: 0,
  totalUnpaid: 0,
  collectionRate: 0
})

const debtorList = ref([])

const loadStatistics = async () => {
  try {
    const res = await request.get('/fee/statistics')
    if (res.code === 200) {
      Object.assign(overview, res.data)
      initCharts(res.data.monthlyStats || [])
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

const loadDebtors = async () => {
  try {
    const res = await request.get('/fee/debtors')
    if (res.code === 200) {
      debtorList.value = res.data || []
    }
  } catch (error) {
    console.error('加载欠费列表失败', error)
  }
}

const initCharts = (monthlyStats) => {
  // 趋势图
  trendChart = echarts.init(trendChartRef.value)
  const months = monthlyStats.map(item => item.month) || []
  const receivables = monthlyStats.map(item => item.receivable) || []
  const receiveds = monthlyStats.map(item => item.received) || []
  
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['应收金额', '实收金额'] },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value' },
    series: [
      { name: '应收金额', type: 'line', data: receivables, smooth: true },
      { name: '实收金额', type: 'line', data: receiveds, smooth: true }
    ]
  })

  // 饼图
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { value: 10480, name: '住宿费' },
        { value: 7350, name: '护理费' },
        { value: 5800, name: '餐饮费' },
        { value: 4840, name: '医疗费' },
        { value: 3000, name: '其他' }
      ]
    }]
  })
}

const handleNotify = (row) => {
  ElMessage.success(`已向 ${row.elderlyName} 发送催缴通知`)
}

onMounted(() => {
  loadStatistics()
  loadDebtors()
  window.addEventListener('resize', () => {
    trendChart?.resize()
    pieChart?.resize()
  })
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}
.overview-row {
  margin-bottom: 20px;
}
.stat-item {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 15px;
}
.stat-info {
  flex: 1;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.chart-row {
  margin-bottom: 20px;
}
.debtor-card {
  margin-top: 20px;
}
.debt-amount {
  color: #f56c6c;
  font-weight: bold;
}
</style>
