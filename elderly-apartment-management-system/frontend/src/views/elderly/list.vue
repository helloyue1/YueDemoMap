<template>
  <div class="elderly-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>住客列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加住客
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="在住" :value="1" />
            <el-option label="已退房" :value="2" />
            <el-option label="请假" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="healthStatus" label="健康状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHealthStatusType(row.healthStatus)">
              {{ getHealthStatusText(row.healthStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="入住状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入姓名" maxlength="50" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="formData.birthday"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
                @change="calculateAge"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="formData.age" :min="0" :max="150" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="民族" prop="nationality">
              <el-select v-model="formData.nationality" placeholder="请选择民族" style="width: 100%">
                <el-option v-for="item in nationalityOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" maxlength="18" @blur="parseIdCard" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-select
                v-model="formData.roomNumber"
                placeholder="请选择房间"
                style="width: 100%"
                filterable
                :loading="roomLoading"
              >
                <el-option
                  v-for="room in availableRooms"
                  :key="room.roomNumber"
                  :label="`${room.roomNumber} (剩余${room.capacity - (room.currentOccupancy || 0)}床位)`"
                  :value="room.roomNumber"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="formData.emergencyPhone" placeholder="请输入紧急联系电话" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住日期" prop="checkInDate">
              <el-date-picker
                v-model="formData.checkInDate"
                type="date"
                placeholder="请选择入住日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状态" prop="healthStatus">
              <el-select v-model="formData.healthStatus" placeholder="请选择健康状态" style="width: 100%">
                <el-option label="良好" :value="1" />
                <el-option label="一般" :value="2" />
                <el-option label="较差" :value="3" />
                <el-option label="危急" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入家庭住址" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input v-model="formData.notes" type="textarea" :rows="3" placeholder="请输入备注信息" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="住客详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ viewData.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ viewData.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ viewData.birthday }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ viewData.age }}</el-descriptions-item>
        <el-descriptions-item label="民族">{{ viewData.nationality }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ viewData.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ viewData.phone }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ viewData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ viewData.emergencyContact }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系电话">{{ viewData.emergencyPhone }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ viewData.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="健康状态">{{ getHealthStatusText(viewData.healthStatus) }}</el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="2">{{ viewData.address }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.notes }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getElderlyList, createElderly, updateElderly, deleteElderly } from '@/api/elderly'
import { getAvailableRooms } from '@/api/room'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({
  name: '',
  roomNumber: '',
  status: null
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('添加住客')
const submitLoading = ref(false)
const roomLoading = ref(false)
const availableRooms = ref([])

const viewData = reactive({
  name: '',
  gender: 1,
  birthday: '',
  age: 0,
  nationality: '汉族',
  idCard: '',
  phone: '',
  roomNumber: '',
  emergencyContact: '',
  emergencyPhone: '',
  checkInDate: '',
  healthStatus: 1,
  address: '',
  notes: ''
})

const formData = reactive({
  id: null,
  name: '',
  gender: 1,
  birthday: '',
  age: 0,
  nationality: '汉族',
  idCard: '',
  phone: '',
  roomNumber: '',
  emergencyContact: '',
  emergencyPhone: '',
  checkInDate: '',
  healthStatus: 1,
  address: '',
  notes: ''
})

const nationalityOptions = [
  '汉族', '满族', '蒙古族', '回族', '藏族', '维吾尔族', '苗族', '彝族', '壮族', '布依族',
  '朝鲜族', '侗族', '瑶族', '白族', '土家族', '哈尼族', '哈萨克族', '傣族', '黎族', '傈僳族',
  '佤族', '畲族', '高山族', '拉祜族', '水族', '东乡族', '纳西族', '景颇族', '柯尔克孜族',
  '土族', '达斡尔族', '仫佬族', '羌族', '布朗族', '撒拉族', '毛南族', '仡佬族', '锡伯族',
  '阿昌族', '普米族', '塔吉克族', '怒族', '乌孜别克族', '俄罗斯族', '鄂温克族', '德昂族',
  '保安族', '裕固族', '京族', '塔塔尔族', '独龙族', '鄂伦春族', '赫哲族', '门巴族', '珞巴族', '基诺族'
]

const validateIdCard = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入身份证号'))
    return
  }
  // 18位身份证号正则：地址码(6位) + 年份(4位) + 月份(2位) + 日期(2位) + 顺序码(3位) + 校验码(1位)
  // 月份: 01-12
  // 日期: 01-31
  const reg = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/
  if (!reg.test(value)) {
    callback(new Error('身份证号格式不正确'))
    return
  }
  
  // 进一步校验日期合法性
  const year = parseInt(value.substring(6, 10))
  const month = parseInt(value.substring(10, 12))
  const day = parseInt(value.substring(12, 14))
  
  // 检查日期是否合法
  const date = new Date(year, month - 1, day)
  if (date.getFullYear() !== year || date.getMonth() !== month - 1 || date.getDate() !== day) {
    callback(new Error('身份证号中的日期不合法'))
    return
  }
  
  callback()
}

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入联系电话'))
    return
  }
  const reg = /^1[3-9]\d{9}$/
  if (!reg.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
}

const validateEmergencyPhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入紧急联系电话'))
    return
  }
  const reg = /^1[3-9]\d{9}$/
  if (!reg.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
}

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在2-50个字符之间', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  nationality: [{ required: true, message: '请选择民族', trigger: 'change' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { validator: validateIdCard, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  roomNumber: [{ required: true, message: '请选择房间', trigger: 'change' }],
  emergencyContact: [
    { required: true, message: '请输入紧急联系人', trigger: 'blur' },
    { min: 2, max: 50, message: '联系人姓名长度在2-50个字符之间', trigger: 'blur' }
  ],
  emergencyPhone: [
    { required: true, message: '请输入紧急联系电话', trigger: 'blur' },
    { validator: validateEmergencyPhone, trigger: 'blur' }
  ],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  healthStatus: [{ required: true, message: '请选择健康状态', trigger: 'change' }]
}

const formRef = ref(null)

const calculateAge = () => {
  if (formData.birthday) {
    const birthDate = new Date(formData.birthday)
    const today = new Date()
    let age = today.getFullYear() - birthDate.getFullYear()
    const monthDiff = today.getMonth() - birthDate.getMonth()
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--
    }
    formData.age = age > 0 ? age : 0
  }
}

const parseIdCard = () => {
  if (formData.idCard && formData.idCard.length === 18) {
    const birthStr = formData.idCard.substring(6, 14)
    const year = birthStr.substring(0, 4)
    const month = birthStr.substring(4, 6)
    const day = birthStr.substring(6, 8)
    formData.birthday = `${year}-${month}-${day}`
    calculateAge()

    const genderCode = parseInt(formData.idCard.substring(16, 17))
    formData.gender = genderCode % 2 === 1 ? 1 : 2
  }
}

const loadAvailableRooms = async () => {
  roomLoading.value = true
  try {
    const res = await getAvailableRooms()
    if (res.code === 200) {
      availableRooms.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载房间列表失败')
  } finally {
    roomLoading.value = false
  }
}

const loadData = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      realName: searchForm.realName,
      roomNumber: searchForm.roomNumber,
      status: searchForm.status
    }
    const res = await getElderlyList(params)
    console.log('API Response:', res)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
      console.log('Total set to:', pagination.total, 'Type:', typeof res.data.total)
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.realName = ''
  searchForm.roomNumber = ''
  searchForm.status = null
  handleSearch()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    realName: '',
    gender: 1,
    birthday: '',
    age: 0,
    nationality: '汉族',
    idCard: '',
    phone: '',
    roomNumber: '',
    emergencyContact: '',
    emergencyPhone: '',
    checkInDate: new Date().toISOString().split('T')[0],
    healthStatus: 1,
    address: '',
    notes: ''
  })
}

const handleAdd = () => {
  dialogTitle.value = '添加住客'
  resetForm()
  loadAvailableRooms()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑住客'
  resetForm()
  loadAvailableRooms()
  Object.assign(formData, {
    ...row,
    birthday: row.birthday || '',
    nationality: row.nationality || '汉族',
    emergencyContact: row.emergencyContact || '',
    emergencyPhone: row.emergencyPhone || '',
    checkInDate: row.checkInDate || '',
    address: row.address || ''
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  Object.assign(viewData, row)
  viewDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该住客信息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteElderly(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    const submitData = { ...formData }

    // 根据 roomNumber 查找对应的 roomId
    if (formData.roomNumber) {
      const selectedRoom = availableRooms.value.find(room => room.roomNumber === formData.roomNumber)
      if (selectedRoom) {
        submitData.roomId = selectedRoom.id
        submitData.roomNumber = selectedRoom.roomNumber
      }
    }

    if (formData.id) {
      await updateElderly(formData.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createElderly(submitData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('操作失败，请检查表单填写是否正确')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const getHealthStatusText = (status) => {
  const map = { 1: '良好', 2: '一般', 3: '较差', 4: '危急' }
  return map[status] || '未知'
}

const getHealthStatusType = (status) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 1: '在住', 2: '已退房', 3: '请假' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 1: 'success', 2: 'info', 3: 'warning' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.elderly-list-container {
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
