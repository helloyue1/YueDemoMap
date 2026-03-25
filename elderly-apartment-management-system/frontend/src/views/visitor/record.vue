<template>
  <div class="visitor-record-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>访客记录</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="访客姓名">
          <el-input v-model="searchForm.visitorName" placeholder="请输入访客姓名" clearable />
        </el-form-item>
        <el-form-item label="被访住客">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="visitorName" label="访客姓名" width="120" />
        <el-table-column prop="visitorPhone" label="联系电话" width="130" />
        <el-table-column prop="elderlyName" label="被访住客" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="visitDate" label="访问日期" width="120" />
        <el-table-column prop="visitTime" label="访问时间" width="120" />
        <el-table-column prop="checkInTime" label="签到时间" width="160" />
        <el-table-column prop="checkOutTime" label="签退时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  visitorName: '',
  elderlyName: '',
  dateRange: []
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadData = async () => {
  try {
    tableData.value = [
      { id: 1, visitorName: '王先生', visitorPhone: '13900139000', elderlyName: '张大爷', roomNumber: '101', visitDate: '2024-01-16', visitTime: '10:00-12:00', checkInTime: '2024-01-16 10:00', checkOutTime: '2024-01-16 12:00', status: 1 },
      { id: 2, visitorName: '李女士', visitorPhone: '13800138000', elderlyName: '李奶奶', roomNumber: '102', visitDate: '2024-01-16', visitTime: '14:00-16:00', checkInTime: '2024-01-16 14:00', checkOutTime: '', status: 0 }
    ]
    pagination.total = 2
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.visitorName = ''
  searchForm.elderlyName = ''
  searchForm.dateRange = []
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getStatusText = (status) => {
  const map = { 0: '进行中', 1: '已完成', 2: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'info' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.visitor-record-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
