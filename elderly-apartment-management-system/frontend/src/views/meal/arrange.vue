<template>
  <div class="meal-arrange-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>餐饮安排</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增安排
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="安排日期">
          <el-date-picker v-model="searchForm.arrangeDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="餐别">
          <el-select v-model="searchForm.mealType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="早餐" :value="1" />
            <el-option label="午餐" :value="2" />
            <el-option label="晚餐" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="arrangeDate" label="安排日期" width="120" />
        <el-table-column prop="mealType" label="餐别" width="100">
          <template #default="{ row }">
            {{ getMealTypeText(row.mealType) }}
          </template>
        </el-table-column>
        <el-table-column prop="mealName" label="菜品名称" width="200" />
        <el-table-column prop="location" label="用餐地点" width="150" />
        <el-table-column prop="distributeTime" label="配送时间" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已完成' : '待配送' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="handleDistribute(row)" v-if="row.status === 0">配送</el-button>
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
  arrangeDate: '',
  mealType: null
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
      { id: 1, arrangeDate: '2024-01-16', mealType: 1, mealName: '小米粥、鸡蛋', location: '一楼餐厅', distributeTime: '07:30', status: 1 },
      { id: 2, arrangeDate: '2024-01-16', mealType: 2, mealName: '红烧肉、米饭', location: '一楼餐厅', distributeTime: '11:30', status: 0 }
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
  searchForm.arrangeDate = ''
  searchForm.mealType = null
  handleSearch()
}

const handleAdd = () => {
  ElMessage.info('新增安排功能开发中')
}

const handleView = (row) => {
  ElMessage.info('查看详情功能开发中')
}

const handleEdit = (row) => {
  ElMessage.info('编辑功能开发中')
}

const handleDistribute = (row) => {
  ElMessage.success('配送成功')
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

const getMealTypeText = (type) => {
  const map = { 1: '早餐', 2: '午餐', 3: '晚餐' }
  return map[type] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.meal-arrange-container {
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
