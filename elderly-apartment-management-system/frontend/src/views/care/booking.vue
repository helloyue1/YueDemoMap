<template>
  <div class="care-booking-container">
    <!-- 页面标题 -->
    <el-card class="header-card" shadow="never">
      <div class="header-content">
        <div class="title-section">
          <h2 class="page-title">
            <el-icon class="title-icon"><ShoppingCart /></el-icon>
            护理预约
          </h2>
          <p class="page-subtitle">住客/家属自主下单 + 付费预约服务管理</p>
        </div>
      </div>
    </el-card>

    <!-- 服务配置管理 -->
    <el-card class="service-config-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>服务配置管理</span>
        </div>
      </template>
      <el-row :gutter="20">
        <!-- 左侧分类和服务项树形列表 -->
        <el-col :span="24">
          <el-card shadow="never">
            <template #header>
              <div class="config-card-header">
                <span>服务分类</span>
                <el-button type="primary" link size="small" @click="handleAddCategory">
                  <el-icon><Plus /></el-icon>新增分类
                </el-button>
              </div>
            </template>
            <div class="category-tree-list">
              <div
                v-for="cat in categoryList"
                :key="cat.id"
                class="category-tree-item"
              >
                <!-- 一级分类 -->
                <div
                  :class="['category-header', { active: selectedCategory?.id === cat.id }]"
                >
                  <div class="category-header-left" @click="selectCategory(cat)">
                    <el-icon :size="18" class="category-icon">
                      <component :is="cat.icon || 'Folder'" />
                    </el-icon>
                    <span class="category-name">{{ cat.name }}</span>
                    <el-tag size="small" :type="cat.status === 1 ? 'success' : 'info'">
                      {{ cat.status === 1 ? '启' : '禁' }}
                    </el-tag>
                  </div>
                  <div class="category-header-right">
                    <el-button
                      type="primary"
                      link
                      size="small"
                      @click.stop="handleEditCategory(cat)"
                    >
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button
                      type="danger"
                      link
                      size="small"
                      @click.stop="handleDeleteCategory(cat)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                    <el-icon class="expand-icon" :class="{ expanded: selectedCategory?.id === cat.id }" @click="selectCategory(cat)">
                      <ArrowRight />
                    </el-icon>
                  </div>
                </div>
                <!-- 二级分类（服务项） -->
                <div v-if="selectedCategory?.id === cat.id" class="service-items-container">
                  <div
                    v-for="item in getItemsByCategory(cat.id)"
                    :key="item.id"
                    :class="['service-item-row', { 'disabled': item.status !== 1 }]"
                    @click="handleEditItem(item)"
                  >
                    <span class="service-item-name">{{ item.name }}</span>
                    <span class="service-item-price">¥{{ item.price }}/{{ item.unit }}</span>
                  </div>
                  <div v-if="getItemsByCategory(cat.id).length === 0" class="empty-service-items">
                    <el-empty description="暂无服务项" :image-size="60" />
                  </div>
                  <div class="add-service-item-row" @click="handleAddItem">
                    <el-icon><Plus /></el-icon>
                    <span>新增服务项</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>


      </el-row>
    </el-card>

    <!-- 预约列表 -->
    <el-card class="booking-list-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>预约订单列表</span>
            <el-radio-group v-model="listType" size="small" @change="handleListTypeChange">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="pending">待确认</el-radio-button>
              <el-radio-button label="confirmed">已确认</el-radio-button>
              <el-radio-button label="completed">已完成</el-radio-button>
              <el-radio-button label="cancelled">已取消</el-radio-button>
            </el-radio-group>
          </div>
          <div class="header-right">
            <el-button type="warning" size="small" @click="handleBatchAssign" :disabled="selectedBookings.length === 0">
              <el-icon><User /></el-icon>
              批量分配 ({{ selectedBookings.length }})
            </el-button>
            <el-button type="success" size="small" @click="exportData">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="住客姓名">
          <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="searchForm.serviceType" placeholder="请选择" clearable style="width: 150px">
            <el-option
              v-for="item in serviceItemOptions"
              :key="item.id"
              :label="item.name"
              :value="item.code || item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 预约表格 -->
      <el-table
        :data="tableData"
        stripe
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单编号" width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column prop="elderlyName" label="住客姓名" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="80" />
        <el-table-column prop="serviceName" label="服务项目" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="service-info">
              <span class="service-name">{{ row.serviceName }}</span>
              <el-tag size="small" type="info" v-if="row.isPackage">套餐</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentDate" label="预约日期" width="110" />
        <el-table-column prop="appointmentTime" label="预约时段" width="100" />
        <el-table-column prop="price" label="金额" width="90">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="支付状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getPaymentType(row.paymentStatus)" size="small" effect="plain">
              {{ getPaymentText(row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nurseName" label="分配护理员" width="100">
          <template #default="{ row }">
            <span v-if="row.nurseName">{{ row.nurseName }}</span>
            <el-tag v-else type="warning" size="small">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
            <!-- 待确认状态：显示确认和取消按钮 -->
            <el-button 
              link 
              type="success" 
              size="small" 
              @click="handleConfirm(row)"
              v-if="row.status === 'pending'"
            >
              确认
            </el-button>
            <!-- 已确认状态且未分配护理员：显示分配按钮 -->
            <el-button
              link
              type="primary"
              size="small"
              @click="handleAssign(row)"
              v-if="row.status === 'confirmed' && !row.nurseName"
            >
              分配
            </el-button>
            <!-- 已分配状态：显示完成按钮 -->
            <el-button 
              link 
              type="success" 
              size="small" 
              @click="handleComplete(row)"
              v-if="row.status === 'assigned'"
            >
              完成
            </el-button>
            <!-- 待确认和已确认状态可以取消 -->
            <el-button
              link
              type="danger"
              size="small"
              @click="handleCancel(row)"
              v-if="row.status === 'pending' || row.status === 'confirmed'"
            >
              取消
            </el-button>
            <!-- 删除按钮 -->
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

    <!-- 预约详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="700px">
      <el-descriptions :column="2" border v-if="selectedBooking">
        <el-descriptions-item label="订单编号" :span="2">{{ selectedBooking.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ selectedBooking.createTime }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(selectedBooking.status)">
            {{ getStatusText(selectedBooking.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="住客姓名">{{ selectedBooking.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ selectedBooking.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ selectedBooking.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家属姓名">{{ selectedBooking.familyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家属电话">{{ selectedBooking.familyPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="服务项目" :span="2">{{ selectedBooking.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ selectedBooking.appointmentDate }}</el-descriptions-item>
        <el-descriptions-item label="预约时段">{{ selectedBooking.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="服务金额">
          <span class="price-highlight">¥{{ selectedBooking.price }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="getPaymentType(selectedBooking.paymentStatus)" effect="plain">
            {{ getPaymentText(selectedBooking.paymentStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="分配护理员" :span="2">
          <span v-if="selectedBooking.nurseName">{{ selectedBooking.nurseName }}</span>
          <el-tag v-else type="warning">未分配</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务备注" :span="2">{{ selectedBooking.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          type="primary"
          @click="handleAssign(selectedBooking)"
          v-if="selectedBooking?.status === 'confirmed'"
        >
          分配护理员
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配护理员对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配护理员" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="预约订单">
          <div v-if="assignForm.bookings.length === 1" class="booking-preview">
            <span class="booking-name">{{ assignForm.bookings[0].serviceName }}</span>
            <span class="booking-time">{{ assignForm.bookings[0].appointmentDate }} {{ assignForm.bookings[0].appointmentTime }}</span>
          </div>
          <div v-else class="booking-preview">
            <el-tag type="primary">共 {{ assignForm.bookings.length }} 个订单</el-tag>
          </div>
        </el-form-item>
        <el-form-item label="选择护理员" prop="nurseId">
          <el-select v-model="assignForm.nurseId" placeholder="请选择护理员" style="width: 100%" filterable>
            <el-option
              v-for="nurse in availableNurses"
              :key="nurse.id"
              :label="nurse.name"
              :value="nurse.id"
            >
              <div class="nurse-option">
                <span class="nurse-name">{{ nurse.name }}</span>
                <span class="nurse-info">今日任务: {{ nurse.todayTasks }}/{{ nurse.maxTasks }}</span>
                <el-tag :type="nurse.status === 'available' ? 'success' : 'warning'" size="small">
                  {{ nurse.status === 'available' ? '可分配' : '忙碌' }}
                </el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分配说明">
          <el-input v-model="assignForm.remark" type="textarea" rows="3" placeholder="请输入分配说明（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign" :loading="assignLoading">确认分配</el-button>
      </template>
    </el-dialog>

    <!-- 代客预约对话框 -->
    <el-dialog v-model="bookingDialogVisible" title="代客预约" width="600px">
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
        <el-form-item label="选择住客" prop="elderlyId">
          <el-select v-model="bookingForm.elderlyId" placeholder="请选择住客" style="width: 100%" filterable>
            <el-option
              v-for="elderly in elderlyList"
              :key="elderly.id"
              :label="`${elderly.name} (${elderly.roomNumber})`"
              :value="elderly.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择服务" prop="serviceId">
          <el-select v-model="bookingForm.serviceId" placeholder="请选择服务项目" style="width: 100%">
            <el-option-group
              v-for="category in serviceCategories"
              :key="category.id"
              :label="category.name"
            >
              <el-option
                v-for="item in getItemsByCategory(category.id)"
                :key="item.id"
                :label="`${item.name} - ¥${item.price}/${item.unit || '次'}`"
                :value="item.id"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约日期" prop="appointmentDate">
              <el-date-picker v-model="bookingForm.appointmentDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约时段" prop="appointmentTime">
              <el-select v-model="bookingForm.appointmentTime" placeholder="选择时段" style="width: 100%">
                <el-option label="08:00-10:00" value="08:00-10:00" />
                <el-option label="10:00-12:00" value="10:00-12:00" />
                <el-option label="14:00-16:00" value="14:00-16:00" />
                <el-option label="16:00-18:00" value="16:00-18:00" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="bookingForm.remark" type="textarea" rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBooking" :loading="bookingLoading">提交预约</el-button>
      </template>
    </el-dialog>

    <!-- 分类表单对话框 -->
    <el-dialog v-model="categoryDialogVisible" :title="categoryDialogType === 'add' ? '新增分类' : '编辑分类'" width="450px" destroy-on-close>
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="categoryForm.code" placeholder="请输入分类编码" maxlength="50" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="categoryForm.icon" placeholder="选择图标" clearable style="width: 150px;">
            <el-option v-for="icon in iconOptions" :key="icon.value" :label="icon.label" :value="icon.value">
              <el-icon style="margin-right: 8px;"><component :is="icon.value" /></el-icon>
              {{ icon.label }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" :rows="2" placeholder="请输入分类描述" maxlength="255" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategoryForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 服务项表单对话框 -->
    <el-dialog v-model="itemDialogVisible" :title="itemDialogType === 'add' ? '新增服务项' : '编辑服务项'" width="500px" destroy-on-close>
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="90px">
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="itemForm.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="itemForm.name" placeholder="请输入服务名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="服务编码" prop="code">
          <el-input v-model="itemForm.code" placeholder="请输入服务编码" maxlength="50" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="itemForm.price" :min="0" :precision="2" :step="10" style="width: 120px;" />
          <span class="form-unit">元</span>
        </el-form-item>
        <el-form-item label="计价单位" prop="unit">
          <el-input v-model="itemForm.unit" placeholder="如：次、小时" maxlength="20" style="width: 120px;" />
        </el-form-item>
        <el-form-item label="服务时长" prop="duration">
          <el-input-number v-model="itemForm.duration" :min="0" :step="15" style="width: 120px;" />
          <span class="form-unit">分钟</span>
        </el-form-item>
        <el-form-item label="服务描述" prop="description">
          <el-input v-model="itemForm.description" type="textarea" :rows="3" placeholder="请输入服务描述" maxlength="500" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="itemForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="itemForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitItemForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, ShoppingCart, Calendar, Timer, Money, Star,
  Goods, User, Download, Setting, Folder, ArrowRight, Edit, Delete
} from '@element-plus/icons-vue'
import {
  getCareBookingList,
  createCareBooking,
  confirmCareBooking,
  completeCareBooking,
  cancelCareBooking,
  assignNurseToBooking,
  deleteCareBooking,
  getNurseList
} from '@/api/care'
import {
  getServiceCategoryList,
  getActiveServiceCategories,
  getAllServiceItems,
  createServiceCategory,
  updateServiceCategory,
  deleteServiceCategory,
  createServiceItem,
  updateServiceItem,
  deleteServiceItem
} from '@/api/serviceCategory'

const router = useRouter()

// 统计数据
const stats = reactive({
  todayBookings: 12,
  pendingBookings: 5,
  todayRevenue: 1580,
  avgRating: 4.8
})

// 列表类型
const listType = ref('all')

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  elderlyName: '',
  serviceType: '',
  dateRange: []
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const selectedBookings = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 对话框控制
const detailDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const assignLoading = ref(false)
const bookingDialogVisible = ref(false)
const bookingLoading = ref(false)
const selectedBooking = ref(null)
const submitting = ref(false)

// 分配表单
const assignForm = reactive({
  bookings: [],
  nurseId: null,
  remark: ''
})

// 可分配护理员列表（从后端获取）
const availableNurses = ref([])

// 住客列表
const elderlyList = ref([
  { id: 1, name: '张大爷', roomNumber: '101' },
  { id: 2, name: '李奶奶', roomNumber: '102' },
  { id: 3, name: '王大爷', roomNumber: '201' },
  { id: 4, name: '赵奶奶', roomNumber: '103' }
])

// 服务分类和服务项数据
const serviceCategories = ref([])
const serviceItemOptions = ref([])
const serviceItemsMap = ref({})

// 服务配置相关
const serviceConfigVisible = ref(false)
const categoryList = ref([])
const itemList = ref([])
const selectedCategory = ref(null)

// 图标选项
const iconOptions = [
  { label: '生活护理', value: 'HomeFilled' },
  { label: '医疗护理', value: 'FirstAidKit' },
  { label: '康复训练', value: 'UserFilled' },
  { label: '陪诊服务', value: 'Guide' },
  { label: '文件夹', value: 'Folder' },
  { label: '日历', value: 'Calendar' },
  { label: '收藏', value: 'Star' },
  { label: '奖杯', value: 'Trophy' }
]

// 分类对话框
const categoryDialogVisible = ref(false)
const categoryDialogType = ref('add')
const categoryFormRef = ref(null)
const categoryForm = reactive({
  id: null,
  name: '',
  code: '',
  icon: '',
  description: '',
  sortOrder: 0,
  status: 1
})
const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }]
}

// 服务项对话框
const itemDialogVisible = ref(false)
const itemDialogType = ref('add')
const itemFormRef = ref(null)
const itemForm = reactive({
  id: null,
  categoryId: null,
  name: '',
  code: '',
  description: '',
  price: 0,
  unit: '次',
  duration: null,
  sortOrder: 0,
  status: 1
})
const itemRules = {
  categoryId: [{ required: true, message: '请选择所属分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

// 预约表单
const bookingFormRef = ref(null)
const bookingForm = reactive({
  elderlyId: null,
  serviceId: '',
  appointmentDate: '',
  appointmentTime: '',
  remark: ''
})

const bookingRules = {
  elderlyId: [{ required: true, message: '请选择住客', trigger: 'change' }],
  serviceId: [{ required: true, message: '请选择服务', trigger: 'change' }],
  appointmentDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  appointmentTime: [{ required: true, message: '请选择时段', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      orderNo: searchForm.orderNo,
      elderlyName: searchForm.elderlyName,
      serviceType: searchForm.serviceType,
      status: listType.value === 'all' ? '' : listType.value
    }
    const res = await getCareBookingList(params)
    if (res.success) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '加载数据失败')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败: ' + (error.message || '未知错误'))
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 获取支付状态类型
const getPaymentType = (status) => {
  const map = { pending: 'warning', paid: 'success', refunded: 'info', failed: 'danger' }
  return map[status] || ''
}

// 获取支付状态文本
const getPaymentText = (status) => {
  const map = { pending: '未支付', paid: '已支付', refunded: '已退款', failed: '支付失败' }
  return map[status] || status
}

// 获取订单状态类型
const getStatusType = (status) => {
  const map = { pending: 'warning', confirmed: 'primary', assigned: 'success', in_progress: 'warning', completed: 'success', cancelled: 'info' }
  return map[status] || ''
}

// 获取订单状态文本
const getStatusText = (status) => {
  const map = { pending: '待确认', confirmed: '已确认', assigned: '已分配', in_progress: '服务中', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

// 列表类型切换
const handleListTypeChange = () => {
  handleSearch()
}

// 统计卡片点击
const handleStatClick = (type) => {
  if (type === 'today') {
    listType.value = 'all'
  } else if (type === 'pending') {
    listType.value = 'pending'
  }
  handleSearch()
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.elderlyName = ''
  searchForm.serviceType = ''
  searchForm.dateRange = []
  listType.value = 'all'
  handleSearch()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectedBookings.value = selection
}

// 判断是否可以取消订单
// 逻辑：待支付（未付款）或 已支付但待确认（未开始服务）可以取消
// 已支付且已确认/服务中/已完成 不能取消
const canCancel = (row) => {
  // 待支付 - 可以取消
  if (row.paymentStatus === 'pending') {
    return true
  }
  // 已支付 + 待确认 - 可以取消（自动退款）
  if (row.paymentStatus === 'paid' && row.status === 'pending') {
    return true
  }
  // 其他情况（已支付+已确认/服务中/已完成）不能取消
  return false
}

// 加载可用护理员列表
const loadNurseList = async () => {
  try {
    const res = await getNurseList()
    if (res.success || res.code === 200) {
      const nurses = res.data || []
      // 转换后端数据格式为前端需要的格式
      availableNurses.value = nurses.map(nurse => ({
        id: nurse.id,
        name: nurse.name,
        todayTasks: nurse.todayTasks || 0,
        maxTasks: nurse.maxTasks || 8,
        status: nurse.status || 'available',
        roles: nurse.roles || []
      }))
    }
  } catch (error) {
    console.error('加载护理员列表失败:', error)
  }
}

// 查看详情
const handleView = (row) => {
  selectedBooking.value = row
  detailDialogVisible.value = true
}

// 分配护理员
const handleAssign = (row) => {
  assignForm.bookings = [row]
  assignForm.nurseId = null
  assignForm.remark = ''
  // 打开弹窗前加载护理员列表
  loadNurseList()
  assignDialogVisible.value = true
}

// 批量分配
const handleBatchAssign = () => {
  if (selectedBookings.value.length === 0) {
    ElMessage.warning('请先选择要分配的订单')
    return
  }
  assignForm.bookings = selectedBookings.value
  assignForm.nurseId = null
  assignForm.remark = ''
  // 打开弹窗前加载护理员列表
  loadNurseList()
  assignDialogVisible.value = true
}

// 确认分配
const confirmAssign = async () => {
  if (!assignForm.nurseId) {
    ElMessage.warning('请选择护理员')
    return
  }
  assignLoading.value = true
  try {
    for (const booking of assignForm.bookings) {
      await assignNurseToBooking(booking.id, assignForm.nurseId)
    }
    ElMessage.success(`成功分配 ${assignForm.bookings.length} 个订单`)
    assignDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('分配护理员失败:', error)
    ElMessage.error('分配失败')
  } finally {
    assignLoading.value = false
  }
}



// 确认预约
const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm(`确认接受 ${row.elderlyName} 的预约申请？`, '确认预约', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    })
    const res = await confirmCareBooking(row.id)
    if (res.success) {
      ElMessage.success('预约已确认')
      loadData()
    } else {
      ElMessage.error(res.message || '确认失败')
    }
  } catch (error) {
    console.error('确认预约失败:', error)
    ElMessage.error('确认失败')
  }
}

// 完成服务
const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认 ${row.elderlyName} 的服务已完成？`, '完成服务', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    const res = await completeCareBooking(row.id)
    if (res.success) {
      ElMessage.success('服务已标记为完成')
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('完成服务失败:', error)
    ElMessage.error('操作失败')
  }
}

// 取消订单（自动退款）
const handleCancel = async (row) => {
  try {
    const isPaid = row.paymentStatus === 'paid'
    const message = isPaid
      ? `确定要取消 ${row.elderlyName} 的订单吗？已支付金额将自动退回。`
      : `确定要取消 ${row.elderlyName} 的订单吗？`

    await ElMessageBox.confirm(message, '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await cancelCareBooking(row.id)
    if (res.success) {
      const msg = res.data?.refunded ? '订单已取消，退款已自动处理' : '订单已取消'
      ElMessage.success(msg)
      loadData()
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch (error) {
    console.error('取消订单失败:', error)
    ElMessage.error('取消失败')
  }
}

// 删除订单
const handleDelete = (row) => {
  console.log('点击删除按钮，row:', row)
  ElMessageBox.confirm(
    `确定要删除 ${row.elderlyName} 的预约订单吗？此操作不可恢复。`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    console.log('确认删除，id:', row.id)
    const res = await deleteCareBooking(row.id)
    console.log('删除接口返回:', res)
    if (res.success) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  }).catch((error) => {
    console.log('删除操作取消或出错:', error)
    if (error !== 'cancel' && error?.message !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  })
}



// 代客预约
const handleAddBooking = () => {
  bookingForm.elderlyId = null
  bookingForm.serviceId = ''
  bookingForm.appointmentDate = ''
  bookingForm.appointmentTime = ''
  bookingForm.remark = ''
  bookingDialogVisible.value = true
}

// 提交预约
const submitBooking = async () => {
  await bookingFormRef.value.validate()
  bookingLoading.value = true
  try {
    const res = await createCareBooking(bookingForm)
    if (res.success) {
      ElMessage.success('代客预约成功')
      bookingDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '预约失败')
    }
  } catch (error) {
    console.error('创建预约失败:', error)
    ElMessage.error('预约失败')
  } finally {
    bookingLoading.value = false
  }
}

// 跳转服务套餐
const goToServicePackage = () => {
  router.push('/care/service-package')
}

// 导出数据
const exportData = () => {
  ElMessage.success('数据导出成功')
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

// 加载服务分类和项目
const loadServiceData = async () => {
  try {
    const [catRes, itemRes] = await Promise.all([
      getActiveServiceCategories(),
      getAllServiceItems()
    ])

    // 处理标准响应格式 { code: 200, data: [...] } 或 { success: true, data: [...] }
    if (catRes.code === 200) {
      serviceCategories.value = catRes.data || []
    } else if (catRes.success && Array.isArray(catRes.data)) {
      serviceCategories.value = catRes.data
    }

    let items = []
    if (itemRes.code === 200) {
      items = itemRes.data || []
    } else if (itemRes.success && Array.isArray(itemRes.data)) {
      items = itemRes.data
    }
    serviceItemOptions.value = items

    // 按分类组织服务项
    serviceItemsMap.value = items.reduce((map, item) => {
      if (!map[item.categoryId]) {
        map[item.categoryId] = []
      }
      map[item.categoryId].push(item)
      return map
    }, {})
  } catch (error) {
    console.error('加载服务数据失败:', error)
  }
}

// 获取分类下的服务项
const getItemsByCategory = (categoryId) => {
  return serviceItemsMap.value[categoryId] || []
}

// ==================== 服务配置管理 ====================
const showServiceConfig = () => {
  serviceConfigVisible.value = true
  loadConfigData()
}

const loadConfigData = async () => {
  try {
    const [catRes, itemRes] = await Promise.all([
      getServiceCategoryList(),
      getAllServiceItems()
    ])
    // 处理标准响应格式 { code: 200, data: [...] }
    if (catRes.code === 200) {
      categoryList.value = catRes.data || []
    } else if (catRes.success && Array.isArray(catRes.data)) {
      categoryList.value = catRes.data
    }
    if (itemRes.code === 200) {
      itemList.value = itemRes.data || []
    } else if (itemRes.success && Array.isArray(itemRes.data)) {
      itemList.value = itemRes.data
    }
  } catch (error) {
    console.error('加载配置数据失败:', error)
  }
}

const filteredItemList = computed(() => {
  if (!selectedCategory.value) return itemList.value
  return itemList.value.filter(item => item.categoryId === selectedCategory.value.id)
})

const selectCategory = (cat) => {
  selectedCategory.value = selectedCategory.value?.id === cat.id ? null : cat
}

// 分类操作
const handleAddCategory = () => {
  categoryDialogType.value = 'add'
  resetCategoryForm()
  categoryDialogVisible.value = true
}

const handleEditCategory = (cat) => {
  categoryDialogType.value = 'edit'
  Object.assign(categoryForm, cat)
  categoryDialogVisible.value = true
}

const handleDeleteCategory = (cat) => {
  ElMessageBox.confirm(`确定要删除分类 "${cat.name}" 吗？该分类下的所有服务项也将被删除。`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteServiceCategory(cat.id)
      if (res.code === 200 || res.success) {
        ElMessage.success('删除成功')
        // 如果删除的是当前选中的分类，清空选中状态
        if (selectedCategory.value?.id === cat.id) {
          selectedCategory.value = null
        }
        loadConfigData()
        loadServiceData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除分类失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

const resetCategoryForm = () => {
  categoryForm.id = null
  categoryForm.name = ''
  categoryForm.code = ''
  categoryForm.icon = ''
  categoryForm.description = ''
  categoryForm.sortOrder = 0
  categoryForm.status = 1
}

const submitCategoryForm = async () => {
  const valid = await categoryFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    let res
    if (categoryDialogType.value === 'add') {
      res = await createServiceCategory(categoryForm)
    } else {
      res = await updateServiceCategory(categoryForm.id, categoryForm)
    }
    if (res.code === 200 || res.success) {
      ElMessage.success(categoryDialogType.value === 'add' ? '创建成功' : '更新成功')
      categoryDialogVisible.value = false
      loadConfigData()
      loadServiceData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交分类失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 服务项操作
const handleAddItem = () => {
  itemDialogType.value = 'add'
  resetItemForm()
  if (selectedCategory.value) {
    itemForm.categoryId = selectedCategory.value.id
  }
  itemDialogVisible.value = true
}

const handleEditItem = (row) => {
  itemDialogType.value = 'edit'
  // 使用深拷贝，避免引用问题
  Object.assign(itemForm, {
    id: row.id,
    categoryId: row.categoryId,
    name: row.name,
    code: row.code,
    description: row.description,
    price: row.price,
    unit: row.unit,
    duration: row.duration,
    sortOrder: row.sortOrder,
    status: row.status
  })
  itemDialogVisible.value = true
}

const resetItemForm = () => {
  itemForm.id = null
  itemForm.categoryId = null
  itemForm.name = ''
  itemForm.code = ''
  itemForm.description = ''
  itemForm.price = 0
  itemForm.unit = '次'
  itemForm.duration = null
  itemForm.sortOrder = 0
  itemForm.status = 1
}

const submitItemForm = async () => {
  const valid = await itemFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const isAdd = itemDialogType.value === 'add'
    const api = isAdd ? createServiceItem : updateServiceItem
    
    console.log('提交服务项:', { isAdd, itemForm: { ...itemForm } })
    
    let res
    if (isAdd) {
      res = await api(itemForm)
    } else {
      // 编辑时需要传递 id 和 data
      const { id, ...data } = itemForm
      console.log('更新服务项:', { id, data })
      res = await api(id, data)
    }
    
    console.log('API响应:', res)
    
    if (res.code === 200 || res.success) {
      ElMessage.success(isAdd ? '创建成功' : '更新成功')
      itemDialogVisible.value = false
      loadConfigData()
      loadServiceData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交服务项失败:', error)
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const handleDeleteItem = (row) => {
  ElMessageBox.confirm(`确定要删除服务项 "${row.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteServiceItem(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadConfigData()
        loadServiceData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadData()
  loadServiceData()
  loadConfigData()
  loadNurseList()
})
</script>

<style scoped lang="scss">
.care-booking-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  .header-card {
    margin-bottom: 20px;
    border: none;

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;

      .title-section {
        .page-title {
          margin: 0 0 8px 0;
          font-size: 22px;
          font-weight: 600;
          color: #303133;
          display: flex;
          align-items: center;
          gap: 10px;

          .title-icon {
            color: #409eff;
          }
        }

        .page-subtitle {
          margin: 0;
          color: #909399;
          font-size: 14px;
        }
      }

      .header-actions {
        display: flex;
        gap: 12px;
      }
    }
  }

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      cursor: pointer;
      border: none;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0,0,0,0.1);
      }

      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon-wrapper {
          width: 48px;
          height: 48px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          .stat-value {
            font-size: 22px;
            font-weight: 600;
            color: #303133;
          }

          .stat-label {
            font-size: 13px;
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
  }

  .booking-list-card {
    border: none;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;

      .header-left {
        display: flex;
        align-items: center;
        gap: 16px;
        font-weight: 600;
        color: #303133;
      }

      .header-right {
        display: flex;
        gap: 12px;
      }
    }

    .search-form {
      margin: 20px 0;
      padding: 16px;
      background: #f5f7fa;
      border-radius: 8px;
    }

    .service-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .service-name {
        font-weight: 500;
      }
    }

    .price-text {
      color: #f56c6c;
      font-weight: 600;
    }

    .el-pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}

.price-highlight {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.booking-preview {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .booking-name {
    font-weight: 500;
    color: #303133;
  }

  .booking-time {
    font-size: 13px;
    color: #909399;
  }
}

.nurse-option {
  display: flex;
  align-items: center;
  gap: 12px;

  .nurse-name {
    font-weight: 500;
    width: 80px;
  }

  .nurse-info {
    flex: 1;
    color: #909399;
    font-size: 13px;
  }
}

// 服务配置相关样式
.config-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

// 树形分类列表样式
.category-tree-list {
  max-height: 600px;
  overflow-y: auto;

  .category-tree-item {
    margin-bottom: 4px;

    .category-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      background-color: #f5f7fa;

      &:hover {
        background-color: #e4e7ed;
      }

      &.active {
        background-color: #ecf5ff;
        border-left: 3px solid #409eff;
      }

      .category-header-left {
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
        cursor: pointer;

        .category-icon {
          color: #409eff;
        }

        .category-name {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
        }
      }

      .category-header-right {
        display: flex;
        align-items: center;
        gap: 4px;

        .el-button {
          padding: 4px;
          opacity: 0;
          transition: opacity 0.2s;
        }

        &:hover .el-button {
          opacity: 1;
        }
      }

      &:hover .category-header-right .el-button {
        opacity: 1;
      }

      .expand-icon {
        color: #909399;
        transition: transform 0.3s;
        cursor: pointer;
        padding: 4px;

        &.expanded {
          transform: rotate(90deg);
        }
      }
    }

    .service-items-container {
      padding: 8px 8px 8px 36px;
      background-color: #fafafa;
      border-radius: 0 0 8px 8px;

      .service-item-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 10px 12px;
        margin-bottom: 6px;
        border-radius: 6px;
        background-color: #fff;
        cursor: pointer;
        transition: all 0.2s;
        border: 1px solid #ebeef5;

        &:hover {
          border-color: #409eff;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
        }

        &.disabled {
          opacity: 0.6;

          .service-item-name {
            text-decoration: line-through;
            color: #c0c4cc;
          }
        }

        .service-item-name {
          font-size: 13px;
          color: #606266;
        }

        .service-item-price {
          font-size: 12px;
          color: #f56c6c;
          font-weight: 500;
        }
      }

      .empty-service-items {
        padding: 20px 0;
      }

      .add-service-item-row {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        padding: 10px;
        margin-top: 8px;
        border-radius: 6px;
        border: 1px dashed #dcdfe6;
        cursor: pointer;
        transition: all 0.2s;
        color: #909399;
        font-size: 13px;

        &:hover {
          border-color: #409eff;
          color: #409eff;
          background-color: #ecf5ff;
        }
      }
    }
  }
}

.category-list {
  max-height: 400px;
  overflow-y: auto;

  .category-item {
    display: flex;
    align-items: center;
    padding: 10px;
    margin-bottom: 6px;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background-color: #f5f7fa;
    }

    &.active {
      background-color: #ecf5ff;
      border-left: 3px solid #409eff;
    }

    .category-name {
      flex: 1;
      margin: 0 8px;
      font-size: 14px;
    }
  }
}

.form-unit {
  margin-left: 8px;
  color: #606266;
}

.text-disabled {
  color: #c0c4cc;
  text-decoration: line-through;
}

@media (max-width: 768px) {
  .care-booking-container {
    padding: 12px;

    .header-content {
      flex-direction: column;
      align-items: flex-start;
    }

    .booking-list-card .card-header {
      flex-direction: column;
      align-items: flex-start;
    }
  }
}
</style>
