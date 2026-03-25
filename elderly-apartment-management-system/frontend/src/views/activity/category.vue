<template>
  <div class="activity-category-container">
    <!-- 页面标题 -->
    <el-card class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2>活动分类管理</h2>
          <p class="subtitle">管理老年公寓活动分类，便于活动组织和统计</p>
        </div>
        <el-button type="primary" size="large" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增分类
        </el-button>
      </div>
    </el-card>

    <!-- 分类列表 -->
    <el-card class="category-list-card">
      <template #header>
        <div class="card-header">
          <span>分类列表</span>
          <div class="header-actions">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索分类名称"
              style="width: 250px;"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch" style="margin-left: 10px;">
              <el-icon><Search /></el-icon> 搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon> 重置
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="categoryList"
        style="width: 100%"
        border
        stripe
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="分类名称" min-width="180">
          <template #default="{ row }">
            <div class="category-name">
              <el-icon v-if="row.icon" :size="18" style="margin-right: 8px;">
                <component :is="row.icon" />
              </el-icon>
              <span :style="{ color: row.color }">{{ row.name }}</span>
              <el-tag v-if="row.level === 1" type="primary" size="small" style="margin-left: 8px;">一级</el-tag>
              <el-tag v-else type="info" size="small" style="margin-left: 8px;">二级</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类编码" prop="code" width="120" align="center" />
        <el-table-column label="颜色标识" width="100" align="center">
          <template #default="{ row }">
            <div class="color-preview" :style="{ backgroundColor: row.color }"></div>
          </template>
        </el-table-column>
        <el-table-column label="活动数量" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.activityCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="80" align="center" prop="sortOrder" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button v-if="row.level === 1" type="success" link size="small" @click="handleAddSub(row)">
              <el-icon><Plus /></el-icon> 添加子类
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 分类表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增分类' : dialogType === 'edit' ? '编辑分类' : '添加子分类'"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级分类" v-if="form.parentId">
          <el-input v-model="form.parentName" disabled />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入分类编码，如：sports" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="颜色标识" prop="color">
          <el-color-picker v-model="form.color" show-alpha />
          <span class="form-tip">选择分类标识颜色</span>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="form.icon" placeholder="选择图标" clearable style="width: 200px;">
            <el-option
              v-for="icon in iconOptions"
              :key="icon.value"
              :label="icon.label"
              :value="icon.value"
            >
              <el-icon style="margin-right: 8px;">
                <component :is="icon.value" />
              </el-icon>
              {{ icon.label }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
          <span class="form-tip">数字越小排序越靠前</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Refresh, Edit, Delete
} from '@element-plus/icons-vue'
import {
  getCategoryList, createCategory, updateCategory, deleteCategory,
  updateCategoryStatus
} from '@/api/activity'

// 图标选项
const iconOptions = [
  { label: '运动', value: 'Basketball' },
  { label: '音乐', value: 'Headset' },
  { label: '书画', value: 'EditPen' },
  { label: '游戏', value: 'Cpu' },
  { label: '健康', value: 'FirstAidKit' },
  { label: '社交', value: 'UserFilled' },
  { label: '学习', value: 'Reading' },
  { label: '节日', value: 'Calendar' },
  { label: '户外', value: 'Sunny' },
  { label: '室内', value: 'House' },
  { label: '手工', value: 'Scissors' },
  { label: '烹饪', value: 'Food' }
]

// 搜索表单
const searchForm = reactive({
  keyword: ''
})

// 分类列表
const categoryList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogType = ref('add') // add, edit, sub
const formRef = ref(null)
const submitLoading = ref(false)

// 表单
const form = reactive({
  id: null,
  parentId: null,
  parentName: '',
  name: '',
  code: '',
  color: '#409EFF',
  icon: '',
  sortOrder: 0,
  status: 1,
  description: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入分类编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '只能包含字母、数字、下划线和横线', trigger: 'blur' }
  ],
  color: [
    { required: true, message: '请选择颜色标识', trigger: 'change' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序号', trigger: 'blur' }
  ]
}

// 获取分类列表
const fetchCategoryList = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    // 将 isActive 映射到 status，供前端 switch 组件使用
    const list = (res.data || []).map(item => ({
      ...item,
      status: item.isActive
    }))
    categoryList.value = buildTree(list)
    pagination.total = res.data?.length || 0
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 构建树形结构
const buildTree = (list) => {
  const map = {}
  const tree = []
  
  // 先构建map
  list.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  
  // 再构建树
  list.forEach(item => {
    if (item.parentId && map[item.parentId]) {
      map[item.parentId].children.push(map[item.id])
    } else {
      tree.push(map[item.id])
    }
  })
  
  return tree
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchCategoryList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  pagination.page = 1
  fetchCategoryList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchCategoryList()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  fetchCategoryList()
}

// 新增分类
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

// 添加子分类
const handleAddSub = (row) => {
  dialogType.value = 'sub'
  resetForm()
  form.parentId = row.id
  form.parentName = row.name
  form.color = row.color
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  dialogType.value = 'edit'
  resetForm()
  Object.assign(form, row)
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.parentId = null
  form.parentName = ''
  form.name = ''
  form.code = ''
  form.color = '#409EFF'
  form.icon = ''
  form.sortOrder = 0
  form.status = 1
  form.description = ''
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = { ...form }
    delete data.parentName
    
    let res
    if (dialogType.value === 'edit') {
      res = await updateCategory(form.id, data)
    } else {
      res = await createCategory(data)
    }
    
    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'edit' ? '修改成功' : '添加成功')
      dialogVisible.value = false
      fetchCategoryList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除分类
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${row.name}" 吗？删除后该分类下的活动将变为未分类状态。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await deleteCategory(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchCategoryList()
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

// 状态变更
const handleStatusChange = async (row) => {
  try {
    const res = await updateCategoryStatus(row.id, row.status)
    if (res.code === 200) {
      ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
    } else {
      // 恢复原状态
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    console.error('状态变更失败:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchCategoryList()
})
</script>

<style scoped lang="scss">
.activity-category-container {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title-section {
        h2 {
          margin: 0 0 8px 0;
          font-size: 24px;
          color: #303133;
        }

        .subtitle {
          margin: 0;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }

  .category-list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      span {
        font-size: 16px;
        font-weight: bold;
      }

      .header-actions {
        display: flex;
        align-items: center;
      }
    }

    .category-name {
      display: flex;
      align-items: center;
    }

    .color-preview {
      width: 30px;
      height: 30px;
      border-radius: 4px;
      margin: 0 auto;
      border: 1px solid #dcdfe6;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .form-tip {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }
}
</style>
