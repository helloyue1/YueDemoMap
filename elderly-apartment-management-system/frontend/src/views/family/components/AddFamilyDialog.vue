<template>
  <el-dialog
    v-model="visible"
    title="添加家属"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="family-form"
    >
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="form.name"
          placeholder="请输入家属姓名"
          maxlength="50"
        />
      </el-form-item>

      <el-form-item label="关系" prop="relationship">
        <el-select
          v-model="form.relationship"
          placeholder="请选择关系"
          style="width: 100%"
        >
          <el-option
            v-for="item in relationshipOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="form.phone"
          placeholder="请输入手机号"
          maxlength="20"
        />
      </el-form-item>

      <el-form-item label="身份证号" prop="idCard">
        <el-input
          v-model="form.idCard"
          placeholder="请输入身份证号（选填）"
          maxlength="18"
        />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio :label="1">男</el-radio>
          <el-radio :label="0">女</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="家庭住址" prop="address">
        <el-input
          v-model="form.address"
          type="textarea"
          :rows="2"
          placeholder="请输入家庭住址（选填）"
          maxlength="255"
        />
      </el-form-item>

      <el-form-item label="工作单位" prop="workUnit">
        <el-input
          v-model="form.workUnit"
          placeholder="请输入工作单位（选填）"
          maxlength="100"
        />
      </el-form-item>

      <!-- 设为紧急联系人开关 -->
      <el-form-item>
        <div class="switch-item">
          <div class="switch-left">
            <el-icon class="switch-icon"><Warning /></el-icon>
            <span class="switch-label">设为紧急联系人</span>
          </div>
          <el-switch
            v-model="form.isEmergencyContact"
            :active-value="1"
            :inactive-value="0"
            active-color="#ff6b6b"
          />
        </div>
      </el-form-item>

      <!-- 设为默认联系人开关 -->
      <el-form-item>
        <div class="switch-item">
          <div class="switch-left">
            <el-icon class="switch-icon"><Star /></el-icon>
            <span class="switch-label">设为默认联系人</span>
          </div>
          <el-switch
            v-model="form.isPrimaryContact"
            :active-value="1"
            :inactive-value="0"
            active-color="#ffa502"
          />
        </div>
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="2"
          placeholder="请输入备注（选填）"
          maxlength="500"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, Star } from '@element-plus/icons-vue'
import { createFamily, updateFamily, getRelationshipOptions } from '@/api/family'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  elderlyId: {
    type: [Number, String],
    required: true
  },
  editData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const loading = ref(false)
const formRef = ref(null)
const relationshipOptions = ref([])

const form = reactive({
  name: '',
  relationship: '',
  phone: '',
  idCard: '',
  gender: 1,
  address: '',
  workUnit: '',
  isEmergencyContact: 0,
  isPrimaryContact: 0,
  remark: '',
  elderlyId: null,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入家属姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  relationship: [
    { required: true, message: '请选择关系', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

// 加载关系选项
const loadRelationshipOptions = async () => {
  try {
    const res = await getRelationshipOptions()
    if (res.code === 200 || res.success) {
      relationshipOptions.value = res.data || []
    }
  } catch (error) {
    console.error('加载关系选项失败:', error)
    // 使用默认选项
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

// 重置表单
const resetForm = () => {
  form.name = ''
  form.relationship = ''
  form.phone = ''
  form.idCard = ''
  form.gender = 1
  form.address = ''
  form.workUnit = ''
  form.isEmergencyContact = 0
  form.isPrimaryContact = 0
  form.remark = ''
  form.elderlyId = props.elderlyId
  form.status = 1
}

// 设置编辑数据
const setEditData = (data) => {
  if (data) {
    form.name = data.name || ''
    form.relationship = data.relationship || ''
    form.phone = data.phone || ''
    form.idCard = data.idCard || ''
    form.gender = data.gender ?? 1
    form.address = data.address || ''
    form.workUnit = data.workUnit || ''
    form.isEmergencyContact = data.isEmergencyContact ?? 0
    form.isPrimaryContact = data.isPrimaryContact ?? 0
    form.remark = data.remark || ''
    form.elderlyId = data.elderlyId || props.elderlyId
    form.status = data.status ?? 1
  }
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  resetForm()
  emit('update:modelValue', false)
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    form.elderlyId = props.elderlyId

    let res
    if (props.editData?.id) {
      // 编辑模式
      res = await updateFamily(props.editData.id, form)
    } else {
      // 新增模式
      res = await createFamily(form)
    }

    if (res.code === 200 || res.success) {
      ElMessage.success(props.editData?.id ? '修改成功' : '添加成功')
      emit('success')
      handleClose()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

// 监听弹窗显示
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    loadRelationshipOptions()
    if (props.editData) {
      setEditData(props.editData)
    } else {
      resetForm()
    }
  }
})

// 监听编辑数据变化
watch(() => props.editData, (val) => {
  if (val && visible.value) {
    setEditData(val)
  }
})

onMounted(() => {
  loadRelationshipOptions()
})
</script>

<style scoped lang="scss">
.family-form {
  padding: 10px 0;

  .switch-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 8px 0;

    .switch-left {
      display: flex;
      align-items: center;
      gap: 8px;

      .switch-icon {
        font-size: 16px;
        color: #909399;
      }

      .switch-label {
        font-size: 14px;
        color: #606266;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
