<template>
  <div class="emergency-container">
    <el-card class="header-card" shadow="never">
      <div class="header-content">
        <div class="title-section">
          <h2 class="page-title">
            <el-icon class="title-icon" color="#FF4D4F"><PhoneFilled /></el-icon>
            紧急呼叫管理
          </h2>
          <p class="page-subtitle">管理和响应住客的紧急呼叫请求</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card pending" shadow="hover" @click="handleStatClick(1)">
          <div class="stat-content">
            <div class="stat-icon-wrapper" style="background: #fff1f0;">
              <el-icon :size="28" color="#ff4d4f"><BellFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value" style="color: #ff4d4f;">{{ stats.pending }}</div>
              <div class="stat-label">待响应</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card responded" shadow="hover" @click="handleStatClick(2)">
          <div class="stat-content">
            <div class="stat-icon-wrapper" style="background: #e6f7ff;">
              <el-icon :size="28" color="#1890ff"><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value" style="color: #1890ff;">{{ stats.responded }}</div>
              <div class="stat-label">已响应</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card processing" shadow="hover" @click="handleStatClick(3)">
          <div class="stat-content">
            <div class="stat-icon-wrapper" style="background: #fff7e6;">
              <el-icon :size="28" color="#fa8c16"><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value" style="color: #fa8c16;">{{ stats.processing }}</div>
              <div class="stat-label">处理中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card completed" shadow="hover" @click="handleStatClick(4)">
          <div class="stat-content">
            <div class="stat-icon-wrapper" style="background: #f6ffed;">
              <el-icon :size="28" color="#52c41a"><CircleCheckFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value" style="color: #52c41a;">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>紧急呼叫记录</span>
              <el-radio-group v-model="listType" size="small" @change="handleListTypeChange">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button label="1">待响应</el-radio-button>
                <el-radio-button label="2">已响应</el-radio-button>
                <el-radio-button label="3">处理中</el-radio-button>
                <el-radio-button label="4">已完成</el-radio-button>
                <el-radio-button label="5">已取消</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="住客姓名">
              <el-input v-model="searchForm.elderlyName" placeholder="请输入住客姓名" clearable style="width: 140px" />
            </el-form-item>
            <el-form-item label="呼叫类型">
              <el-select v-model="searchForm.callType" placeholder="请选择" clearable style="width: 130px">
                <el-option label="紧急呼叫" value="emergency" />
                <el-option label="紧急" value="urgent" />
                <el-option label="普通" value="normal" />
                <el-option label="咨询" value="consult" />
              </el-select>
            </el-form-item>
            <el-form-item label="紧急程度">
              <el-select v-model="searchForm.urgencyLevel" placeholder="请选择" clearable style="width: 100px">
                <el-option label="低" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="高" :value="3" />
                <el-option label="紧急" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="房间号">
              <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable style="width: 120px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="elderlyName" label="住客姓名" width="120" />
            <el-table-column prop="roomNumber" label="房间号" width="100" />
            <el-table-column prop="phone" label="联系电话" width="120" />
            <el-table-column prop="callType" label="呼叫类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getCallTypeTag(row.callType)" effect="dark">
                  {{ getCallTypeText(row.callType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="urgencyLevel" label="紧急程度" width="100">
              <template #default="{ row }">
                <el-tag :type="getUrgencyTag(row.urgencyLevel)" effect="dark">
                  {{ getUrgencyText(row.urgencyLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="callReason" label="呼叫原因" min-width="150" show-overflow-tooltip />
            <el-table-column prop="assignedNurseName" label="负责护士" width="120">
              <template #default="{ row }">
                {{ row.assignedNurseName || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" effect="dark">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="呼叫时间" width="160" />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.status === 1" type="primary" size="small" @click="handleRespond(row)">
                  <el-icon><Phone /></el-icon>响应
                </el-button>
                <el-button v-if="row.status === 1" type="warning" size="small" @click="handleAssign(row)">
                  <el-icon><User /></el-icon>分配
                </el-button>
                <el-button v-if="row.status === 2" type="warning" size="small" @click="handleProcessing(row)">
                  <el-icon><Loading /></el-icon>处理中
                </el-button>
                <el-button v-if="row.status === 3" type="success" size="small" @click="handleComplete(row)">
                  <el-icon><CircleCheck /></el-icon>完成
                </el-button>
                <el-button v-if="row.status === 1 || row.status === 2" type="danger" size="small" @click="handleCancel(row)">
                  <el-icon><CircleClose /></el-icon>取消
                </el-button>
                <el-button link type="info" size="small" @click="handleView(row)">详情</el-button>
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
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="contact-card">
          <template #header>
            <div class="card-header">
              <span class="contact-title">
                <el-icon><Phone /></el-icon>
                紧急联系人
              </span>
              <el-button type="primary" size="small" @click="handleAddContact">
                <el-icon><Plus /></el-icon>
                添加
              </el-button>
            </div>
          </template>

          <div class="contact-list" v-loading="contactLoading">
            <div
              v-for="item in contactList"
              :key="item.id"
              class="contact-item"
            >
              <div class="contact-info">
                <div class="contact-name">{{ item.name }}</div>
                <div class="contact-phone">{{ item.phone }}</div>
                <div class="contact-type">
                  <el-tag size="small" :type="getContactTypeTag(item.contactType)">
                    {{ getContactTypeText(item.contactType) }}
                  </el-tag>
                </div>
              </div>
              <div class="contact-actions">
                <el-button
                  size="small"
                  type="primary"
                  @click="handleContactCommand('edit', item)"
                >
                  编辑
                </el-button>
                <el-button
                  size="small"
                  :type="item.status === 1 ? 'warning' : 'success'"
                  @click="handleContactCommand('toggle', item)"
                >
                  {{ item.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="handleContactCommand('delete', item)"
                >
                  删除
                </el-button>
              </div>
            </div>
            <el-empty v-if="contactList.length === 0" description="暂无紧急联系人" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="viewDialogVisible" title="呼叫详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="住客姓名">{{ currentRow.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentRow.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.phone }}</el-descriptions-item>
        <el-descriptions-item label="呼叫类型">
          <el-tag :type="getCallTypeTag(currentRow.callType)" effect="dark">{{ getCallTypeText(currentRow.callType) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyTag(currentRow.urgencyLevel)" effect="dark">{{ getUrgencyText(currentRow.urgencyLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentRow.status)" effect="dark">{{ getStatusText(currentRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="呼叫原因" :span="2">{{ currentRow.callReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="负责护士">{{ currentRow.assignedNurseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="响应时间">{{ currentRow.responseTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentRow.completeTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="呼叫时间" :span="2">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="completeDialogVisible" title="完成处理" width="500px">
      <el-form :model="completeForm" label-width="80px">
        <el-form-item label="处理备注">
          <el-input v-model="completeForm.remark" type="textarea" :rows="4" placeholder="请输入处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignDialogVisible" title="分配护士" width="500px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="选择护士" required>
          <el-select v-model="assignForm.nurseId" placeholder="请选择护士" style="width: 100%">
            <el-option
              v-for="item in nurseList"
              :key="item.id"
              :label="item.realName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定分配</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="contactDialogVisible" :title="contactForm.id ? '编辑紧急联系人' : '添加紧急联系人'" width="500px">
      <el-form :model="contactForm" label-width="100px" :rules="contactRules" ref="contactFormRef">
        <el-form-item label="联系人名称" prop="name">
          <el-input v-model="contactForm.name" placeholder="请输入联系人名称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="contactForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系人类型" prop="contactType">
          <el-select v-model="contactForm.contactType" placeholder="请选择联系人类型" style="width: 100%">
            <el-option label="公寓前台" value="front_desk" />
            <el-option label="医护人员" value="medical" />
            <el-option label="保安室" value="security" />
            <el-option label="管理处" value="management" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述说明">
          <el-input v-model="contactForm.description" type="textarea" :rows="2" placeholder="请输入描述说明（可选）" />
        </el-form-item>
        <el-form-item label="排序顺序">
          <el-input-number v-model="contactForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitContact">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  getCallCaregiverList, respondToCall, completeCall, cancelCall, assignNurseToCall,
  getCallCaregiverStats, getNurseList,
  getEmergencyContactList, createEmergencyContact, updateEmergencyContact,
  deleteEmergencyContact, toggleEmergencyContactStatus
} from '@/api'

export default {
  name: 'EmergencyManagement',
  data() {
    return {
      searchForm: {
        elderlyName: '',
        callType: '',
        urgencyLevel: null,
        roomNumber: ''
      },
      listType: '',
      tableData: [],
      loading: false,
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      stats: {
        pending: 0,
        responded: 0,
        processing: 0,
        completed: 0
      },
      viewDialogVisible: false,
      currentRow: null,
      completeDialogVisible: false,
      completeForm: {
        id: null,
        remark: ''
      },
      assignDialogVisible: false,
      assignForm: {
        id: null,
        nurseId: null
      },
      nurseList: [],
      contactList: [],
      contactLoading: false,
      contactDialogVisible: false,
      contactForm: {
        id: null,
        name: '',
        phone: '',
        contactType: 'other',
        description: '',
        sortOrder: 0
      },
      contactRules: {
        name: [{ required: true, message: '请输入联系人名称', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        contactType: [{ required: true, message: '请选择联系人类型', trigger: 'change' }]
      }
    }
  },
  created() {
    this.fetchData()
    this.fetchStats()
    this.fetchNurseList()
    this.fetchContactList()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page,
          size: this.pagination.size,
          elderlyName: this.searchForm.elderlyName || undefined,
          callType: this.searchForm.callType || undefined,
          urgencyLevel: this.searchForm.urgencyLevel || undefined,
          status: this.listType ? parseInt(this.listType) : undefined
        }
        const res = await getCallCaregiverList(params)
        if (res.code === 200) {
          this.tableData = res.data.records || []
          this.pagination.total = res.data.total || 0
          this.updateStatsFromData()
        }
      } catch (error) {
        console.error('获取数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    updateStatsFromData() {
      this.stats.pending = this.tableData.filter(t => t.status === 1).length
      this.stats.responded = this.tableData.filter(t => t.status === 2).length
      this.stats.processing = this.tableData.filter(t => t.status === 3).length
      this.stats.completed = this.tableData.filter(t => t.status === 4).length
    },
    async fetchStats() {
      try {
        const res = await getCallCaregiverStats()
        if (res.code === 200) {
          const data = res.data
          console.log('统计数据:', data)
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    },
    async fetchNurseList() {
      try {
        const res = await getNurseList({ page: 1, size: 100 })
        if (res.code === 200) {
          this.nurseList = res.data.records || []
        }
      } catch (error) {
        console.error('获取护士列表失败:', error)
      }
    },
    async fetchContactList() {
      this.contactLoading = true
      try {
        const res = await getEmergencyContactList({ page: 1, size: 100 })
        if (res.code === 200) {
          this.contactList = res.data.records || []
        }
      } catch (error) {
        console.error('获取紧急联系人列表失败:', error)
      } finally {
        this.contactLoading = false
      }
    },
    handleSearch() {
      this.pagination.page = 1
      this.fetchData()
    },
    handleReset() {
      this.searchForm = {
        elderlyName: '',
        callType: '',
        urgencyLevel: null,
        roomNumber: ''
      }
      this.listType = ''
      this.handleSearch()
    },
    handleListTypeChange() {
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchData()
    },
    handlePageChange(val) {
      this.pagination.page = val
      this.fetchData()
    },
    handleStatClick(status) {
      this.listType = status.toString()
      this.handleSearch()
    },
    handleRefresh() {
      this.fetchData()
      this.fetchStats()
      this.fetchContactList()
    },
    handleView(row) {
      this.currentRow = row
      this.viewDialogVisible = true
    },
    async handleRespond(row) {
      try {
        await this.$confirm('确认响应该紧急呼叫?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await respondToCall(row.id)
        if (res.code === 200) {
          this.$message.success('响应成功')
          this.fetchData()
          this.fetchStats()
        } else {
          this.$message.error(res.data || '响应失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('响应失败')
        }
      }
    },
    async handleProcessing(row) {
      try {
        await this.$confirm('确认开始处理?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await respondToCall(row.id)
        if (res.code === 200) {
          this.$message.success('已开始处理')
          this.fetchData()
          this.fetchStats()
        } else {
          this.$message.error(res.data || '操作失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('操作失败')
        }
      }
    },
    handleComplete(row) {
      this.completeForm = {
        id: row.id,
        remark: ''
      }
      this.completeDialogVisible = true
    },
    async submitComplete() {
      try {
        const res = await completeCall(this.completeForm.id, { remark: this.completeForm.remark })
        if (res.code === 200) {
          this.$message.success('处理完成')
          this.completeDialogVisible = false
          this.fetchData()
          this.fetchStats()
        } else {
          this.$message.error(res.data || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    handleCancel(row) {
      this.$confirm('确认取消该呼叫?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await cancelCall(row.id)
        if (res.code === 200) {
          this.$message.success('已取消')
          this.fetchData()
          this.fetchStats()
        } else {
          this.$message.error(res.data || '操作失败')
        }
      }).catch(() => {})
    },
    handleAssign(row) {
      this.assignForm = {
        id: row.id,
        nurseId: null
      }
      this.assignDialogVisible = true
    },
    async submitAssign() {
      if (!this.assignForm.nurseId) {
        this.$message.warning('请选择护士')
        return
      }
      const nurse = this.nurseList.find(n => n.id === this.assignForm.nurseId)
      try {
        const res = await assignNurseToCall(this.assignForm.id, {
          nurseId: this.assignForm.nurseId,
          nurseName: nurse ? nurse.realName : ''
        })
        if (res.code === 200) {
          this.$message.success('分配成功')
          this.assignDialogVisible = false
          this.fetchData()
          this.fetchStats()
        } else {
          this.$message.error(res.data || '分配失败')
        }
      } catch (error) {
        this.$message.error('分配失败')
      }
    },
    getCallTypeText(type) {
      const map = {
        emergency: '紧急呼叫',
        urgent: '紧急',
        normal: '普通',
        consult: '咨询'
      }
      return map[type] || type
    },
    getCallTypeTag(type) {
      const map = {
        emergency: 'danger',
        urgent: 'warning',
        normal: 'info',
        consult: 'success'
      }
      return map[type] || ''
    },
    getUrgencyText(level) {
      const map = {
        1: '低',
        2: '中',
        3: '高',
        4: '紧急'
      }
      return map[level] || level
    },
    getUrgencyTag(level) {
      const map = {
        1: 'info',
        2: 'warning',
        3: 'danger',
        4: 'danger'
      }
      return map[level] || ''
    },
    getStatusText(status) {
      const map = {
        1: '待响应',
        2: '已响应',
        3: '处理中',
        4: '已完成',
        5: '已取消'
      }
      return map[status] || status
    },
    getStatusTag(status) {
      const map = {
        1: 'danger',
        2: 'primary',
        3: 'warning',
        4: 'success',
        5: 'info'
      }
      return map[status] || ''
    },
    getContactTypeText(type) {
      const map = {
        front_desk: '公寓前台',
        medical: '医护人员',
        security: '保安室',
        management: '管理处',
        other: '其他'
      }
      return map[type] || type
    },
    getContactTypeTag(type) {
      const map = {
        front_desk: 'primary',
        medical: 'success',
        security: 'warning',
        management: 'info',
        other: ''
      }
      return map[type] || ''
    },
    handleAddContact() {
      this.contactForm = {
        id: null,
        name: '',
        phone: '',
        contactType: 'other',
        description: '',
        sortOrder: 0
      }
      this.contactDialogVisible = true
    },
    handleContactCommand(command, item) {
      switch (command) {
        case 'edit':
          this.contactForm = { ...item }
          this.contactDialogVisible = true
          break
        case 'toggle':
          this.handleToggleContact(item)
          break
        case 'delete':
          this.handleDeleteContact(item)
          break
      }
    },
    async handleToggleContact(item) {
      try {
        const res = await toggleEmergencyContactStatus(item.id)
        if (res.code === 200) {
          this.$message.success(item.status === 1 ? '已禁用' : '已启用')
          this.fetchContactList()
        } else {
          this.$message.error(res.data || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    handleDeleteContact(item) {
      this.$confirm(`确认删除紧急联系人 "${item.name}"?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await deleteEmergencyContact(item.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.fetchContactList()
        } else {
          this.$message.error(res.data || '删除失败')
        }
      }).catch(() => {})
    },
    async submitContact() {
      this.$refs.contactFormRef.validate(async (valid) => {
        if (!valid) return

        try {
          let res
          if (this.contactForm.id) {
            res = await updateEmergencyContact(this.contactForm.id, this.contactForm)
          } else {
            res = await createEmergencyContact(this.contactForm)
          }
          if (res.code === 200) {
            this.$message.success(this.contactForm.id ? '更新成功' : '创建成功')
            this.contactDialogVisible = false
            this.fetchContactList()
          } else {
            this.$message.error(res.data || '操作失败')
          }
        } catch (error) {
          this.$message.error('操作失败')
        }
      })
    }
  }
}
</script>

<style scoped>
.emergency-container {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  display: flex;
  flex-direction: column;
}

.page-title {
  margin: 0;
  font-size: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-subtitle {
  margin: 5px 0 0;
  color: #909399;
  font-size: 14px;
}

.title-icon {
  color: #ff4d4f;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 15px;
}

.contact-card {
  height: calc(100vh - 340px);
  min-height: 400px;
}

.contact-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.contact-list {
  max-height: calc(100vh - 420px);
  min-height: 320px;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.2s;
  gap: 12px;
}

.contact-item:hover {
  background-color: #f5f7fa;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.contact-phone {
  font-size: 14px;
  color: #606266;
  margin-bottom: 6px;
}

.contact-type {
  display: flex;
  gap: 8px;
}

.contact-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}
</style>
