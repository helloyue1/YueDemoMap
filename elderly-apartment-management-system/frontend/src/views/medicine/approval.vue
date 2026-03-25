<template>
  <div class="medicine-approval-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药品审批</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.medicineName" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请日期">
          <el-date-picker v-model="searchForm.applyDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="medicineName" label="药品名称" width="150" />
        <el-table-column prop="elderlyName" label="申请人" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="quantity" label="申请数量" width="100" />
        <el-table-column prop="applyDate" label="申请日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="success" size="small" @click="handleApprove(row)" v-if="row.status === 0">通过</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)" v-if="row.status === 0">拒绝</el-button>
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
  medicineName: '',
  status: null,
  applyDate: ''
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
      { id: 1, medicineName: '阿司匹林', elderlyName: '张大爷', roomNumber: '101', quantity: 10, applyDate: '2024-01-16', status: 0 },
      { id: 2, medicineName: '降压药', elderlyName: '李奶奶', roomNumber: '102', quantity: 20, applyDate: '2024-01-16', status: 1 }
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
  searchForm.medicineName = ''
  searchForm.status = null
  searchForm.applyDate = ''
  handleSearch()
}

const handleView = (row) => {
  ElMessage.info('查看详情功能开发中')
}

const handleApprove = (row) => {
  ElMessage.success('审批通过')
  loadData()
}

const handleReject = (row) => {
  ElMessage.success('审批拒绝')
  loadData()
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
  const map = { 0: '待审批', 1: '已通过', 2: '已拒绝' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.medicine-approval-container {
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
