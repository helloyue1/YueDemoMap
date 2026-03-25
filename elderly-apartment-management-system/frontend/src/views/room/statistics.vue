<template>
  <div class="room-statistics-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>房间统计</span>
          <div class="header-actions">
            <el-date-picker
              v-model="selectedMonth"
              type="month"
              placeholder="选择月份"
              format="YYYY年MM月"
              value-format="YYYY-MM"
              :clearable="false"
              style="width: 150px; margin-right: 10px;"
              @change="handleMonthChange"
            />
            <el-button type="primary" @click="loadData" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </div>
      </template>

      <!-- 核心指标卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="4">
          <el-card class="stat-card total" shadow="hover">
            <div class="stat-icon">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.total }}</div>
              <div class="stat-label">总房间数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card empty" shadow="hover">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.empty }}</div>
              <div class="stat-label">空闲房间</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card partial" shadow="hover">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.partial }}</div>
              <div class="stat-label">部分入住</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card full" shadow="hover">
            <div class="stat-icon">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.full }}</div>
              <div class="stat-label">已满房间</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card maintenance" shadow="hover">
            <div class="stat-icon">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.maintenance }}</div>
              <div class="stat-label">维修中</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card occupancy" shadow="hover">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ occupancyRate }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>房间状态分布</span>
              </div>
            </template>
            <div ref="pieChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>房间状态统计</span>
              </div>
            </template>
            <div ref="barChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 楼层分布图 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>楼层房间分布</span>
              </div>
            </template>
            <div ref="floorChartRef" class="chart-container-large"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 入住趋势图 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>月度入住趋势 ({{ selectedMonth || '最近6个月' }})</span>
              </div>
            </template>
            <div ref="trendChartRef" class="chart-container-large"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getRoomStats, getRoomList } from '@/api/room'
import dayjs from 'dayjs'

const loading = ref(false)
const pieChartRef = ref(null)
const barChartRef = ref(null)
const floorChartRef = ref(null)
const trendChartRef = ref(null)

let pieChart = null
let barChart = null
let floorChart = null
let trendChart = null

// 时间筛选
const selectedMonth = ref(dayjs().format('YYYY-MM'))

const statistics = reactive({
  total: 0,
  empty: 0,
  partial: 0,
  full: 0,
  maintenance: 0
})

const occupancyRate = computed(() => {
  if (statistics.total === 0) return 0
  const occupied = statistics.partial + statistics.full
  return ((occupied / statistics.total) * 100).toFixed(1)
})

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return
  
  pieChart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [
      {
        name: '房间状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}间'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 0, name: '空闲', itemStyle: { color: '#67c23a' } },
          { value: 0, name: '部分入住', itemStyle: { color: '#e6a23c' } },
          { value: 0, name: '已满', itemStyle: { color: '#f56c6c' } },
          { value: 0, name: '维修中', itemStyle: { color: '#909399' } }
        ]
      }
    ]
  }
  pieChart.setOption(option)
}

// 初始化柱状图
const initBarChart = () => {
  if (!barChartRef.value) return
  
  barChart = echarts.init(barChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['空闲', '部分入住', '已满', '维修中'],
      axisTick: { alignWithLabel: true }
    },
    yAxis: {
      type: 'value',
      name: '房间数'
    },
    series: [
      {
        name: '房间数',
        type: 'bar',
        barWidth: '50%',
        data: [
          { value: 0, itemStyle: { color: '#67c23a' } },
          { value: 0, itemStyle: { color: '#e6a23c' } },
          { value: 0, itemStyle: { color: '#f56c6c' } },
          { value: 0, itemStyle: { color: '#909399' } }
        ],
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }
  barChart.setOption(option)
}

// 初始化楼层分布图
const initFloorChart = async () => {
  if (!floorChartRef.value) return
  
  floorChart = echarts.init(floorChartRef.value)
  
  // 获取所有房间数据
  try {
    const res = await getRoomList({ page: 1, size: 1000 })
    const rooms = res.data.records || []
    
    // 按楼层统计
    const floorStats = {}
    rooms.forEach(room => {
      const floor = room.floor || 1
      if (!floorStats[floor]) {
        floorStats[floor] = { total: 0, occupied: 0 }
      }
      floorStats[floor].total++
      if (room.currentOccupancy > 0) {
        floorStats[floor].occupied++
      }
    })
    
    const floors = Object.keys(floorStats).sort((a, b) => a - b)
    const totalData = floors.map(f => floorStats[f].total)
    const occupiedData = floors.map(f => floorStats[f].occupied)
    
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      legend: {
        data: ['总房间数', '已入住']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: floors.map(f => `${f}楼`),
        axisTick: { alignWithLabel: true }
      },
      yAxis: {
        type: 'value',
        name: '房间数'
      },
      series: [
        {
          name: '总房间数',
          type: 'bar',
          data: totalData,
          itemStyle: { color: '#409eff' },
          label: {
            show: true,
            position: 'top'
          }
        },
        {
          name: '已入住',
          type: 'bar',
          data: occupiedData,
          itemStyle: { color: '#67c23a' },
          label: {
            show: true,
            position: 'top'
          }
        }
      ]
    }
    floorChart.setOption(option)
  } catch (error) {
    console.error('加载楼层数据失败', error)
  }
}

// 更新图表数据
const updateCharts = () => {
  if (pieChart) {
    pieChart.setOption({
      series: [{
        data: [
          { value: statistics.empty, name: '空闲', itemStyle: { color: '#67c23a' } },
          { value: statistics.partial, name: '部分入住', itemStyle: { color: '#e6a23c' } },
          { value: statistics.full, name: '已满', itemStyle: { color: '#f56c6c' } },
          { value: statistics.maintenance, name: '维修中', itemStyle: { color: '#909399' } }
        ]
      }]
    })
  }
  
  if (barChart) {
    barChart.setOption({
      series: [{
        data: [
          { value: statistics.empty, itemStyle: { color: '#67c23a' } },
          { value: statistics.partial, itemStyle: { color: '#e6a23c' } },
          { value: statistics.full, itemStyle: { color: '#f56c6c' } },
          { value: statistics.maintenance, itemStyle: { color: '#909399' } }
        ]
      }]
    })
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoomStats()
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
    
    nextTick(() => {
      updateCharts()
      initFloorChart()
      initTrendChart()
    })
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  
  // 生成最近6个月的月份数据
  const months = []
  const currentMonth = dayjs(selectedMonth.value)
  for (let i = 5; i >= 0; i--) {
    months.push(currentMonth.subtract(i, 'month').format('YYYY-MM'))
  }
  
  // 模拟趋势数据（实际应该从后端获取）
  const checkInData = months.map(() => Math.floor(Math.random() * 10) + 5)
  const checkOutData = months.map(() => Math.floor(Math.random() * 5) + 1)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['新入住', '退房']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '新入住',
        type: 'line',
        smooth: true,
        data: checkInData,
        itemStyle: { color: '#67c23a' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      },
      {
        name: '退房',
        type: 'line',
        smooth: true,
        data: checkOutData,
        itemStyle: { color: '#f56c6c' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
            { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
          ])
        }
      }
    ]
  }
  trendChart.setOption(option)
}

// 月份切换处理
const handleMonthChange = () => {
  loadData()
}

// 窗口大小改变时重新调整图表大小
const handleResize = () => {
  pieChart?.resize()
  barChart?.resize()
  floorChart?.resize()
  trendChart?.resize()
}

onMounted(() => {
  nextTick(() => {
    initPieChart()
    initBarChart()
    initFloorChart()
    initTrendChart()
    loadData()
  })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
  floorChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.room-statistics-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.stat-content {
  padding: 10px 0;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 不同状态的颜色 */
.stat-card.total .stat-icon { color: #409eff; }
.stat-card.total .stat-value { color: #409eff; }

.stat-card.empty .stat-icon { color: #67c23a; }
.stat-card.empty .stat-value { color: #67c23a; }

.stat-card.partial .stat-icon { color: #e6a23c; }
.stat-card.partial .stat-value { color: #e6a23c; }

.stat-card.full .stat-icon { color: #f56c6c; }
.stat-card.full .stat-value { color: #f56c6c; }

.stat-card.maintenance .stat-icon { color: #909399; }
.stat-card.maintenance .stat-value { color: #909399; }

.stat-card.occupancy .stat-icon { color: #8e44ad; }
.stat-card.occupancy .stat-value { color: #8e44ad; }

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart-header {
  font-weight: bold;
  font-size: 16px;
}

.chart-container {
  height: 320px;
  width: 100%;
}

.chart-container-large {
  height: 350px;
  width: 100%;
}
</style>
