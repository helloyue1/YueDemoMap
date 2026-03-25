<template>
  <div class="check-in-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>住客入住</span>
        </div>
      </template>

      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住客姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入住客姓名" />
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
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择出生日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="formData.roomNumber" placeholder="请输入房间号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="家属姓名" prop="contactName">
              <el-input v-model="formData.contactName" placeholder="请输入家属姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家属电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入家属电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker v-model="formData.checkInDate" type="date" placeholder="选择入住日期" style="width: 100%" />
        </el-form-item>

        <el-form-item label="健康状况" prop="healthStatus">
          <el-select v-model="formData.healthStatus" placeholder="请选择健康状况" style="width: 100%">
            <el-option label="良好" :value="1" />
            <el-option label="一般" :value="2" />
            <el-option label="较差" :value="3" />
            <el-option label="危急" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="病史" prop="medicalHistory">
          <el-input v-model="formData.medicalHistory" type="textarea" :rows="3" placeholder="请输入病史" />
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input v-model="formData.notes" type="textarea" :rows="2" placeholder="请输入备注信息" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交入住</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const formData = reactive({
  name: '',
  gender: 1,
  idCard: '',
  birthDate: '',
  phone: '',
  roomNumber: '',
  contactName: '',
  contactPhone: '',
  checkInDate: '',
  healthStatus: 1,
  medicalHistory: '',
  notes: ''
})

const rules = {
  name: [{ required: true, message: '请输入住客姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入家属姓名', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入家属电话', trigger: 'blur' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  healthStatus: [{ required: true, message: '请选择健康状况', trigger: 'change' }]
}

const formRef = ref(null)

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    ElMessage.success('入住信息提交成功')
    handleReset()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.check-in-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
