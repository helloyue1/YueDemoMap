<template>
  <div class="medicine-records-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用药记录</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增记录
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable />
        </el-form-item>
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.medicineName" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="记录日期">
          <el-date-picker v-model="searchForm.recordDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="elderlyName" label="住客姓名" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="medicineName" label="药品名称" width="150" />
        <el-table-column prop="dosage" label="剂量" width="100" />
        <el-table-column prop="frequency" label="频次" width="100" />
        <el-table-column prop="recordDate" label="记录日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已服用' : '待服用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="success" size="small" @click="handleConfirm(row)" v-if="row.status === 0">确认</el-button>
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
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  elderlyName: '',
  medicineName: '',
  recordDate: ''
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
      { id: 1, elderlyName: '张大爷', roomNumber: '101', medicineName: '阿司匹林', dosage: '100mg', frequency: '每日1次', recordDate: '2024-01-16', status: 1 },
      { id: 2, elderlyName: '李奶奶', roomNumber: '102', medicineName: '降压药', dosage: '5mg', frequency: '每日2次', recordDate: '2024-01-16', status: 0 }
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
  searchForm.elderlyName = ''
  searchForm.medicineName = ''
  searchForm.recordDate = ''
  handleSearch()
}

const handleAdd = () => {
  ElMessage.info('新增记录功能开发中')
}

const handleView = (row) => {
  ElMessage.info('查看详情功能开发中')
}

const handleConfirm = (row) => {
  ElMessage.success('确认成功')
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.medicine-records-container {
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
