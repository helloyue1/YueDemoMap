<template>
  <div class="access-record-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>访问记录</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="访客姓名">
          <el-input v-model="searchForm.visitorName" placeholder="请输入访客姓名" clearable />
        </el-form-item>
        <el-form-item label="访问类型">
          <el-select v-model="searchForm.accessType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="探视" :value="1" />
            <el-option label="快递" :value="2" />
            <el-option label="维修" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
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
        <el-table-column prop="accessType" label="访问类型" width="100">
          <template #default="{ row }">
            {{ getAccessTypeText(row.accessType) }}
          </template>
        </el-table-column>
        <el-table-column prop="visitPurpose" label="访问目的" width="150" />
        <el-table-column prop="accessTime" label="进入时间" width="160" />
        <el-table-column prop="exitTime" label="离开时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : 'success'">
              {{ row.status === 1 ? '未离开' : '已离开' }}
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
  accessType: null,
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
    const params = {
      page: pagination.page,
      size: pagination.size,
      visitorName: searchForm.visitorName,
      accessType: searchForm.accessType
    }
    tableData.value = [
      { id: 1, visitorName: '张三', visitorPhone: '13800138000', accessType: 1, visitPurpose: '探望住客', accessTime: '2024-01-15 10:00', exitTime: '2024-01-15 12:00', status: 0 },
      { id: 2, visitorName: '李四', visitorPhone: '13900139000', accessType: 2, visitPurpose: '快递配送', accessTime: '2024-01-15 14:00', exitTime: null, status: 1 }
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
  searchForm.accessType = null
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

const getAccessTypeText = (type) => {
  const map = { 1: '探视', 2: '快递', 3: '维修', 4: '其他' }
  return map[type] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.access-record-container {
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
