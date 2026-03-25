<template>
  <div class="meal-order-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订餐管理</span>
          <div class="header-actions">
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出订单
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计面板 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :xs="12" :sm="6">
          <el-statistic title="今日订单" :value="stats.todayTotal">
            <template #suffix>单</template>
          </el-statistic>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-statistic title="已接单" :value="stats.todayPending" value-style="color: #409eff">
            <template #suffix>单</template>
          </el-statistic>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-statistic title="配送中" :value="stats.todayDelivered" value-style="color: #e6a23c">
            <template #suffix>单</template>
          </el-statistic>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-statistic title="今日金额" :value="stats.todayAmount" value-style="color: #409eff">
            <template #prefix>¥</template>
          </el-statistic>
        </el-col>
      </el-row>

      <el-divider />

      <!-- 餐别统计卡片 -->
      <el-row :gutter="20" class="meal-type-stats">
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="meal-card breakfast">
            <div class="meal-info">
              <div class="meal-title">早餐</div>
              <div class="meal-count">{{ stats.breakfastCount }} 单</div>
              <div class="meal-amount">¥{{ stats.breakfastAmount.toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="meal-card lunch">
            <div class="meal-info">
              <div class="meal-title">午餐</div>
              <div class="meal-count">{{ stats.lunchCount }} 单</div>
              <div class="meal-amount">¥{{ stats.lunchAmount.toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="meal-card dinner">
            <div class="meal-info">
              <div class="meal-title">晚餐</div>
              <div class="meal-count">{{ stats.dinnerCount }} 单</div>
              <div class="meal-amount">¥{{ stats.dinnerAmount.toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-divider />

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="订餐日期">
          <el-date-picker
            v-model="searchForm.orderDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="餐别">
          <el-select v-model="searchForm.mealType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="早餐" :value="1" />
            <el-option label="午餐" :value="2" />
            <el-option label="晚餐" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="已接单" :value="1" />
            <el-option label="配送中" :value="2" />
            <el-option label="已送达" :value="4" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 批量操作按钮 -->
      <div class="batch-actions" v-if="selectedRows.length > 0">
        <el-alert
          :title="`已选择 ${selectedRows.length} 条记录`"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <el-button type="success" size="small" @click="handleBatchDeliver" :disabled="!hasPendingSelected">
              批量配送
            </el-button>
            <el-button type="danger" size="small" @click="handleBatchCancel" :disabled="!hasPendingSelected">
              批量取消
            </el-button>
            <el-button type="danger" size="small" @click="handleBatchDelete">
              批量删除
            </el-button>
          </template>
        </el-alert>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        stripe
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="编号" width="80" align="center" />
        <el-table-column prop="realName" label="住客姓名" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" align="center" />
        <el-table-column prop="mealType" label="餐别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getMealTypeTag(row.mealType)" size="small">
              {{ getMealTypeText(row.mealType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预订菜品" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="menu-items-cell">
              <el-image
                v-for="(img, idx) in getMenuImages(row.menuItems)"
                :key="idx"
                :src="img"
                :preview-src-list="getMenuImages(row.menuItems)"
                class="menu-thumb"
                fit="cover"
                :preview-teleported="true"
              />
              <span class="menu-names">{{ formatMenuItems(row.menuItems) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="specialRequirements" label="特殊要求" min-width="150" show-overflow-tooltip />
        <el-table-column prop="orderDate" label="订餐日期" width="120" align="center" />
        <el-table-column prop="totalAmount" label="金额" width="100" align="right">
          <template #default="{ row }">
            <span class="amount">¥{{ calculateAmount(row.menuItems).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryTime" label="配送时间" width="150" align="center">
          <template #default="{ row }">
            {{ row.deliveryTime ? formatDateTime(row.deliveryTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="success"
              size="small"
              @click="handleDeliver(row)"
            >
              <el-icon><Check /></el-icon>
              配送
            </el-button>
            <el-button
              v-if="row.status === 2"
              link
              type="warning"
              size="small"
              @click="handleComplete(row)"
            >
              <el-icon><CircleCheck /></el-icon>
              送达
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 2"
              link
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              <el-icon><Close /></el-icon>
              取消
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.id }}</el-descriptions-item>
        <el-descriptions-item label="订餐状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="住客姓名">{{ currentOrder.realName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentOrder.roomNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订餐日期">{{ currentOrder.orderDate }}</el-descriptions-item>
        <el-descriptions-item label="餐别">
          <el-tag :type="getMealTypeTag(currentOrder.mealType)">
            {{ getMealTypeText(currentOrder.mealType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额" :span="2">
          <span class="detail-amount">¥{{ calculateAmount(currentOrder.menuItems).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="特殊要求" :span="2">
          {{ currentOrder.specialRequirements || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentOrder.remark || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ formatDateTime(currentOrder.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="配送时间" :span="2" v-if="currentOrder.deliveryTime">
          {{ formatDateTime(currentOrder.deliveryTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="detail-menu-section" v-if="currentOrder">
        <h4>预订菜品</h4>
        <div class="detail-menu-list">
          <div v-for="menu in getMenuDetails(currentOrder.menuItems)" :key="menu.id" class="detail-menu-item">
            <el-image :src="menu.image" fit="cover" class="detail-menu-image" />
            <div class="detail-menu-info">
              <div class="detail-menu-name">{{ menu.name }}</div>
              <div class="detail-menu-price">¥{{ menu.price?.toFixed(2) || '0.00' }}</div>
              <div class="detail-menu-desc" v-if="menu.ingredients">食材：{{ menu.ingredients }}</div>
              <div class="detail-menu-suitable" v-if="menu.suitable">适宜：{{ menu.suitable }}</div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentOrder && currentOrder.status === 1"
          type="success"
          @click="handleDeliver(currentOrder); detailDialogVisible = false"
        >
          确认配送
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量配送对话框 -->
    <el-dialog v-model="batchDeliverDialogVisible" title="批量配送确认" width="500px">
      <el-alert
        title="确认批量配送以下订单？"
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 15px"
      />
      <el-table :data="selectedRows.filter(r => r.status === 1)" size="small" max-height="300">
        <el-table-column prop="realName" label="住客姓名" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="80" />
        <el-table-column prop="mealType" label="餐别" width="80">
          <template #default="{ row }">
            {{ getMealTypeText(row.mealType) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="日期" width="100" />
      </el-table>
      <template #footer>
        <el-button @click="batchDeliverDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmBatchDeliver" :loading="batchLoading">
          确认配送 ({{ selectedRows.filter(r => r.status === 1).length }}单)
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Search, View, Check, Close, Download, CircleCheck, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMealOrderList, deliverMealOrder, cancelMealOrder, completeMealOrder, deleteMealOrder, getAllMealMenu } from '@/api'

// 工具函数 - 放在前面
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const formatDateTime = (datetime) => {
  if (!datetime) return ''
  const d = new Date(datetime)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const loading = ref(false)
const batchLoading = ref(false)
const detailDialogVisible = ref(false)
const batchDeliverDialogVisible = ref(false)
const currentOrder = ref(null)
const selectedRows = ref([])

const searchForm = reactive({
  elderlyName: '',
  roomNumber: '',
  orderDate: '',
  mealType: null,
  status: null
})

const tableData = ref([])
const menuList = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 统计数据
const stats = reactive({
  todayTotal: 0,
  todayPending: 0,
  todayDelivered: 0,
  todayAmount: 0,
  breakfastCount: 0,
  breakfastAmount: 0,
  lunchCount: 0,
  lunchAmount: 0,
  dinnerCount: 0,
  dinnerAmount: 0
})

// 计算是否有已接单的选中项（可以配送的订单）
const hasPendingSelected = computed(() => {
  return selectedRows.value.some(row => row.status === 1)
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getMealOrderList({
      page: pagination.page,
      size: pagination.size,
      elderlyName: searchForm.elderlyName || null,
      roomNumber: searchForm.roomNumber || null,
      orderDate: searchForm.orderDate || null,
      mealType: searchForm.mealType,
      status: searchForm.status
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
      calculateStats(res.data.records)
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载菜品列表
const loadMenuList = async () => {
  try {
    const res = await getAllMealMenu()
    if (res.code === 200) {
      menuList.value = res.data
    }
  } catch (error) {
    console.error('加载菜品列表失败', error)
  }
}

// 计算统计数据
const calculateStats = (records) => {
  // 重置统计
  stats.todayTotal = records.length
  stats.todayPending = records.filter(r => r.status === 1).length
  stats.todayDelivered = records.filter(r => r.status === 2 || r.status === 4).length
  stats.todayAmount = records.reduce((sum, r) => sum + calculateAmount(r.menuItems), 0)

  // 按餐别统计
  stats.breakfastCount = records.filter(r => r.mealType === 1).length
  stats.breakfastAmount = records
    .filter(r => r.mealType === 1)
    .reduce((sum, r) => sum + calculateAmount(r.menuItems), 0)

  stats.lunchCount = records.filter(r => r.mealType === 2).length
  stats.lunchAmount = records
    .filter(r => r.mealType === 2)
    .reduce((sum, r) => sum + calculateAmount(r.menuItems), 0)

  stats.dinnerCount = records.filter(r => r.mealType === 3).length
  stats.dinnerAmount = records
    .filter(r => r.mealType === 3)
    .reduce((sum, r) => sum + calculateAmount(r.menuItems), 0)
}

// 计算订单金额
const calculateAmount = (menuItems) => {
  if (!menuItems || !menuList.value.length) return 0
  try {
    const parsed = JSON.parse(menuItems)
    // 兼容两种格式：ID数组 [1,2,3] 或 对象数组 [{id, name, quantity, price}]
    if (Array.isArray(parsed) && parsed.length > 0) {
      if (typeof parsed[0] === 'object' && parsed[0] !== null) {
        // 对象数组格式：计算 price * quantity
        return parsed.reduce((sum, item) => {
          return sum + ((item.price || 0) * (item.quantity || 1))
        }, 0)
      } else {
        // ID数组格式
        return parsed.reduce((sum, id) => {
          const menu = menuList.value.find(m => m.id === id)
          return sum + (menu?.price || 0)
        }, 0)
      }
    }
    return 0
  } catch {
    return 0
  }
}

// 获取菜品图片列表
const getMenuImages = (menuItems) => {
  if (!menuItems || !menuList.value.length) return []
  try {
    const parsed = JSON.parse(menuItems)
    // 兼容两种格式：ID数组 [1,2,3] 或 对象数组 [{id, name, quantity, price}]
    if (Array.isArray(parsed) && parsed.length > 0) {
      if (typeof parsed[0] === 'object' && parsed[0] !== null) {
        // 对象数组格式
        return parsed
          .map(item => menuList.value.find(m => m.id === item.id)?.image)
          .filter(Boolean)
          .slice(0, 3)
      } else {
        // ID数组格式
        return parsed
          .map(id => menuList.value.find(m => m.id === id)?.image)
          .filter(Boolean)
          .slice(0, 3)
      }
    }
    return []
  } catch {
    return []
  }
}

// 获取菜品详情
const getMenuDetails = (menuItems) => {
  if (!menuItems || !menuList.value.length) return []
  try {
    const parsed = JSON.parse(menuItems)
    // 兼容两种格式：ID数组 [1,2,3] 或 对象数组 [{id, name, quantity, price}]
    if (Array.isArray(parsed) && parsed.length > 0) {
      if (typeof parsed[0] === 'object' && parsed[0] !== null) {
        // 对象数组格式：合并对象数组中的信息和菜单列表中的信息
        return parsed.map(item => {
          const menu = menuList.value.find(m => m.id === item.id)
          return menu ? { ...menu, quantity: item.quantity || 1 } : null
        }).filter(Boolean)
      } else {
        // ID数组格式
        return parsed
          .map(id => menuList.value.find(m => m.id === id))
          .filter(Boolean)
      }
    }
    return []
  } catch {
    return []
  }
}

// 格式化菜品名称
const formatMenuItems = (items) => {
  if (!items) return ''
  try {
    const parsed = JSON.parse(items)
    // 兼容两种格式：ID数组 [1,2,3] 或 对象数组 [{id, name, quantity, price}]
    if (Array.isArray(parsed) && parsed.length > 0) {
      if (typeof parsed[0] === 'object' && parsed[0] !== null) {
        // 对象数组格式：直接使用对象中的 name
        const names = parsed.map(item => item.name).filter(Boolean)
        return names.join('、') || items
      } else {
        // ID数组格式：从菜单列表中查找名称
        const names = parsed.map(id => menuList.value.find(m => m.id === id)?.name).filter(Boolean)
        return names.join('、') || items
      }
    }
    return items
  } catch {
    return items
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.elderlyName = ''
  searchForm.roomNumber = ''
  searchForm.orderDate = ''
  searchForm.mealType = null
  searchForm.status = null
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查看详情
const handleViewDetail = (row) => {
  currentOrder.value = row
  detailDialogVisible.value = true
}

// 配送
const handleDeliver = async (row) => {
  try {
    await ElMessageBox.confirm('确认该订单开始配送？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deliverMealOrder(row.id)
    if (res.code === 200) {
      ElMessage.success('配送成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('配送失败')
    }
  }
}

// 送达
const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确认该订单已送达？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await completeMealOrder(row.id)
    if (res.code === 200) {
      ElMessage.success('送达成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('送达失败')
    }
  }
}

// 取消订单
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await cancelMealOrder(row.id)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

// 批量配送
const handleBatchDeliver = () => {
  const pendingCount = selectedRows.value.filter(r => r.status === 1).length
  if (pendingCount === 0) {
    ElMessage.warning('没有可配送的订单')
    return
  }
  batchDeliverDialogVisible.value = true
}

// 确认批量配送
const confirmBatchDeliver = async () => {
  batchLoading.value = true
  const pendingRows = selectedRows.value.filter(r => r.status === 1)
  let successCount = 0
  let failCount = 0

  for (const row of pendingRows) {
    try {
      const res = await deliverMealOrder(row.id)
      if (res.code === 200) {
        successCount++
      } else {
        failCount++
      }
    } catch {
      failCount++
    }
  }

  batchLoading.value = false
  batchDeliverDialogVisible.value = false

  if (failCount === 0) {
    ElMessage.success(`成功配送 ${successCount} 单`)
  } else {
    ElMessage.warning(`成功 ${successCount} 单，失败 ${failCount} 单`)
  }
  loadData()
}

// 批量取消
const handleBatchCancel = async () => {
  const pendingRows = selectedRows.value.filter(r => r.status === 1 || r.status === 2)
  if (pendingRows.length === 0) {
    ElMessage.warning('没有可取消的订单')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要取消选中的 ${pendingRows.length} 个订单吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    let successCount = 0
    let failCount = 0

    for (const row of pendingRows) {
      try {
        const res = await cancelMealOrder(row.id)
        if (res.code === 200) {
          successCount++
        } else {
          failCount++
        }
      } catch {
        failCount++
      }
    }

    if (failCount === 0) {
      ElMessage.success(`成功取消 ${successCount} 单`)
    } else {
      ElMessage.warning(`成功 ${successCount} 单，失败 ${failCount} 单`)
    }
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  }
}

// 删除订单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteMealOrder(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的订单')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个订单吗？删除后无法恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    let successCount = 0
    let failCount = 0

    for (const row of selectedRows.value) {
      try {
        const res = await deleteMealOrder(row.id)
        if (res.code === 200) {
          successCount++
        } else {
          failCount++
        }
      } catch {
        failCount++
      }
    }

    if (failCount === 0) {
      ElMessage.success(`成功删除 ${successCount} 单`)
    } else {
      ElMessage.warning(`成功 ${successCount} 单，失败 ${failCount} 单`)
    }
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 导出
const handleExport = () => {
  const data = tableData.value.map(row => ({
    '订单编号': row.id,
    '住客姓名': row.realName,
    '房间号': row.roomNumber,
    '订餐日期': row.orderDate,
    '餐别': getMealTypeText(row.mealType),
    '菜品': formatMenuItems(row.menuItems),
    '金额': calculateAmount(row.menuItems).toFixed(2),
    '特殊要求': row.specialRequirements || '',
    '状态': getStatusText(row.status),
    '配送时间': row.deliveryTime ? formatDateTime(row.deliveryTime) : ''
  }))

  // 创建 CSV
  const headers = Object.keys(data[0] || {})
  const csvContent = [
    headers.join(','),
    ...data.map(row => headers.map(h => `"${(row[h] || '').toString().replace(/"/g, '""')}"`).join(','))
  ].join('\n')

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `订餐记录_${searchForm.orderDate || formatDate(new Date())}.csv`
  link.click()

  ElMessage.success('导出成功')
}

// 分页
const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getMealTypeText = (type) => {
  const map = { 1: '早餐', 2: '午餐', 3: '晚餐' }
  return map[type] || '未知'
}

const getMealTypeTag = (type) => {
  const map = { 1: 'success', 2: 'warning', 3: 'info' }
  return map[type] || 'info'
}

const getStatusText = (status) => {
  const map = { 1: '已接单', 2: '配送中', 3: '已取消', 4: '已送达' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 1: 'primary', 2: 'warning', 3: 'info', 4: 'success' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
  loadMenuList()
})
</script>

<style scoped>
.meal-order-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.stats-row {
  margin-bottom: 10px;
}

.meal-type-stats {
  margin: 15px 0;
}

.meal-card {
  text-align: center;
  margin-bottom: 10px;
}

.meal-card.breakfast {
  border-top: 3px solid #67c23a;
}

.meal-card.lunch {
  border-top: 3px solid #e6a23c;
}

.meal-card.dinner {
  border-top: 3px solid #409eff;
}

.meal-title {
  font-size: 16px;
  font-weight: bold;
  color: #606266;
  margin-bottom: 8px;
}

.meal-count {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.meal-amount {
  font-size: 14px;
  color: #909399;
}

.search-form {
  margin-bottom: 20px;
}

.batch-actions {
  margin-bottom: 15px;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
}

.menu-items-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #ebeef5;
}

.menu-names {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.detail-menu-section {
  margin-top: 20px;
}

.detail-menu-section h4 {
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
}

.detail-menu-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-menu-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.detail-menu-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
}

.detail-menu-info {
  flex: 1;
}

.detail-menu-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.detail-menu-price {
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 5px;
}

.detail-menu-desc,
.detail-menu-suitable {
  font-size: 12px;
  color: #909399;
  margin-top: 3px;
}

:deep(.el-pagination) {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-statistic__number) {
  font-size: 24px;
  font-weight: bold;
}
</style>
