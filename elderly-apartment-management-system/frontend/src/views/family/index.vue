<template>
  <div class="family-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">家属管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加家属
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="家属姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入家属姓名"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="关系">
          <el-select
            v-model="searchForm.relationship"
            placeholder="请选择关系"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in relationshipOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="relationship" label="关系" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">
              {{ getRelationshipLabel(row.relationship) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="elderlyName" label="关联老人" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="80" />

        <!-- 紧急联系人开关列 -->
        <el-table-column label="紧急联系人" width="110" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.isEmergencyContact"
              :active-value="1"
              :inactive-value="0"
              active-color="#ff6b6b"
              @change="(val) => handleEmergencyChange(row, val)"
            />
          </template>
        </el-table-column>

        <!-- 默认联系人开关列 -->
        <el-table-column label="默认联系人" width="110" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.isPrimaryContact"
              :active-value="1"
              :inactive-value="0"
              active-color="#ffa502"
              @change="(val) => handlePrimaryChange(row, val)"
            />
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="添加时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
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
        class="pagination"
      />
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <AddFamilyDialog
      v-model="dialogVisible"
      :elderly-id="currentElderlyId"
      :edit-data="editData"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import {
  getFamilyList,
  deleteFamily,
  setEmergencyContact,
  setPrimaryContact,
  getRelationshipOptions
} from '@/api/family'
import AddFamilyDialog from './components/AddFamilyDialog.vue'

// 搜索表单
const searchForm = reactive({
  name: '',
  phone: '',
  relationship: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const relationshipOptions = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 弹窗控制
const dialogVisible = ref(false)
const currentElderlyId = ref(1) // 默认老人ID，实际应从路由或状态获取
const editData = ref(null)

// 加载关系选项
const loadRelationshipOptions = async () => {
  try {
    const res = await getRelationshipOptions()
    if (res.code === 200 || res.success) {
      relationshipOptions.value = res.data || []
    }
  } catch (error) {
    console.error('加载关系选项失败:', error)
    relationshipOptions.value = [
      { value: 'spouse', label: '配偶' },
      { value: 'child', label: '子女' },
      { value: 'sibling', label: '兄弟姐妹' },
      { value: 'parent', label: '父母' },
      { value: 'grandchild', label: '孙辈' },
      { value: 'other', label: '其他' }
    ]
  }
}

// 获取关系标签
const getRelationshipLabel = (value) => {
  const option = relationshipOptions.value.find(item => item.value === value)
  return option?.label || value
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      name: searchForm.name,
      phone: searchForm.phone,
      relationship: searchForm.relationship
    }
    const res = await getFamilyList(params)
    if (res.code === 200 || res.success) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '加载数据失败')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.phone = ''
  searchForm.relationship = ''
  pagination.page = 1
  loadData()
}

// 添加
const handleAdd = () => {
  editData.value = null
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  editData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除家属 "${row.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await deleteFamily(row.id)
    if (res.code === 200 || res.success) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 紧急联系人开关变化
const handleEmergencyChange = async (row, val) => {
  try {
    const res = await setEmergencyContact(row.id, val === 1)
    if (res.code === 200 || res.success) {
      ElMessage.success(val === 1 ? '已设为紧急联系人' : '已取消紧急联系人')
    } else {
      ElMessage.error(res.message || '操作失败')
      // 恢复原状态
      row.isEmergencyContact = val === 1 ? 0 : 1
    }
  } catch (error) {
    console.error('设置紧急联系人失败:', error)
    ElMessage.error('操作失败')
    // 恢复原状态
    row.isEmergencyContact = val === 1 ? 0 : 1
  }
}

// 默认联系人开关变化
const handlePrimaryChange = async (row, val) => {
  try {
    const res = await setPrimaryContact(row.id, val === 1)
    if (res.code === 200 || res.success) {
      ElMessage.success(val === 1 ? '已设为默认联系人' : '已取消默认联系人')
      // 如果设为默认联系人，刷新列表以更新其他行的状态
      if (val === 1) {
        loadData()
      }
    } else {
      ElMessage.error(res.message || '操作失败')
      // 恢复原状态
      row.isPrimaryContact = val === 1 ? 0 : 1
    }
  } catch (error) {
    console.error('设置默认联系人失败:', error)
    ElMessage.error('操作失败')
    // 恢复原状态
    row.isPrimaryContact = val === 1 ? 0 : 1
  }
}

// 添加/编辑成功回调
const handleSuccess = () => {
  loadData()
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

onMounted(() => {
  loadRelationshipOptions()
  loadData()
})
</script>

<style scoped lang="scss">
.family-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .page-title {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
      color: #303133;
    }
  }

  .search-card {
    margin-bottom: 20px;
    border: none;

    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }
  }

  .table-card {
    border: none;

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
