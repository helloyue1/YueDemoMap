<template>
  <div class="visitor-visit-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>访客登记</span>
        </div>
      </template>

      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-divider>访客信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="访客姓名" prop="visitorName">
              <el-input v-model="formData.visitorName" placeholder="请输入访客姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="visitorPhone">
              <el-input v-model="formData.visitorPhone" placeholder="请输入联系电话" />
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
            <el-form-item label="与住客关系" prop="relationship">
              <el-select v-model="formData.relationship" placeholder="请选择关系" style="width: 100%">
                <el-option label="子女" :value="1" />
                <el-option label="亲属" :value="2" />
                <el-option label="朋友" :value="3" />
                <el-option label="其他" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>被访住客信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住客姓名" prop="elderlyName">
              <el-input v-model="formData.elderlyName" placeholder="请输入住客姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="formData.roomNumber" placeholder="请输入房间号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>访问信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="访问日期" prop="visitDate">
              <el-date-picker v-model="formData.visitDate" type="date" placeholder="选择访问日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访问时间" prop="visitTime">
              <el-time-picker v-model="formData.visitTime" is-range range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="访问事由" prop="reason">
          <el-input v-model="formData.reason" type="textarea" :rows="3" placeholder="请输入访问事由" />
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="formData.notes" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交登记</el-button>
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
  visitorName: '',
  visitorPhone: '',
  idCard: '',
  relationship: null,
  elderlyName: '',
  roomNumber: '',
  visitDate: '',
  visitTime: '',
  reason: '',
  notes: ''
})

const rules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitorPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  relationship: [{ required: true, message: '请选择与住客关系', trigger: 'change' }],
  elderlyName: [{ required: true, message: '请输入住客姓名', trigger: 'blur' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  visitDate: [{ required: true, message: '请选择访问日期', trigger: 'change' }],
  visitTime: [{ required: true, message: '请选择访问时间', trigger: 'change' }],
  reason: [{ required: true, message: '请输入访问事由', trigger: 'blur' }]
}

const formRef = ref(null)

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    ElMessage.success('登记成功')
    handleReset()
  } catch (error) {
    ElMessage.error('登记失败')
  }
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.visitor-visit-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-divider {
  margin: 20px 0;
}
</style>
