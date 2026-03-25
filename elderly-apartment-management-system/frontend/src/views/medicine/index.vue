<template>
  <div class="medicine-index-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药品管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增药品
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.medicineName" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="药品类型">
          <el-select v-model="searchForm.medicineType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="处方药" :value="1" />
            <el-option label="非处方药" :value="2" />
            <el-option label="保健品" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="medicineName" label="药品名称" width="150" />
        <el-table-column prop="medicineType" label="药品类型" width="100">
          <template #default="{ row }">
            {{ getMedicineTypeText(row.medicineType) }}
          </template>
        </el-table-column>
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="manufacturer" label="生产厂家" width="150" />
        <el-table-column prop="stock" label="库存数量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="expiryDate" label="有效期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
  medicineName: '',
  medicineType: null
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
      { id: 1, medicineName: '阿司匹林', medicineType: 1, specification: '100mg/片', manufacturer: '某制药厂', stock: 500, unit: '片', expiryDate: '2025-12-31', status: 1 },
      { id: 2, medicineName: '降压药', medicineType: 1, specification: '5mg/片', manufacturer: '某制药厂', stock: 300, unit: '片', expiryDate: '2025-06-30', status: 1 }
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
  searchForm.medicineType = null
  handleSearch()
}

const handleAdd = () => {
  ElMessage.info('新增药品功能开发中')
}

const handleView = (row) => {
  ElMessage.info('查看详情功能开发中')
}

const handleEdit = (row) => {
  ElMessage.info('编辑功能开发中')
}

const handleDelete = (row) => {
  ElMessage.success('删除成功')
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

const getMedicineTypeText = (type) => {
  const map = { 1: '处方药', 2: '非处方药', 3: '保健品' }
  return map[type] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.medicine-index-container {
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
