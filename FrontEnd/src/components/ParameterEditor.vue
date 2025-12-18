<template>
  <div class="parameter-editor">
    <!-- 编辑模式切换 -->
    <div class="editor-header" v-if="showHeader">
      <div class="editor-title">
        <span class="param-name">{{ parameter.paramName }}</span>
        <span class="param-type">{{ parameter.paramType }}</span>
      </div>
      <div class="editor-actions">
        <button 
          v-if="!isEditing"
          @click="startEditing"
          class="edit-btn"
          :disabled="disabled"
        >
          编辑
        </button>
        <div v-else class="edit-actions">
          <button v-if="!props.autoSave || !hasCandidateValues" @click="handleSave" class="save-btn" :disabled="!hasChanges">
            保存
          </button>
          <button v-if="!props.autoSave || !hasCandidateValues" @click="handleCancel" class="cancel-btn">
            取消
          </button>
        </div>
      </div>
    </div>

    <!-- 当前值显示 -->
    <div v-if="!isEditing && !alwaysEditing" class="current-value-display">
      <span class="current-value">{{ displayCurrentValue }}</span>
      <button 
        @click="startEditing"
        class="inline-edit-btn"
        :disabled="disabled"
        title="点击编辑"
      >
        ✏️
      </button>
    </div>

    <!-- 编辑器 -->
    <div v-if="isEditing || alwaysEditing" class="editor-content" ref="rootRef">
      <!-- 第一行：显示当前值 -->
      <div class="current-value-row">
        <span class="current-value-label">当前值：</span>
        <span class="current-value-text">{{ displayCurrentValue }}</span>
      </div>

      <!-- 第二行：下拉选择框（如果有候选值） -->
      <div v-if="hasCandidateValues" class="selector-row">
        <CandidateValueSelector
          v-model="editValue"
          :candidate-values="candidateValues"
          :parameter="parameter"
          :allow-custom="allowCustomValues"
          :disabled="disabled"
          :validate-value="validateValue"
          @change="onValueChange"
          @validate="onValidationResult"
          @save="onCandidateValueSave"
        />
      </div>

      <!-- 第二行：输入框（如果没有候选值） -->
      <div v-else class="selector-row">
        <!-- 数值输入（整数类型） -->
        <input 
          v-if="parameter.paramType === 'INTEGER'"
          type="number"
          v-model="editValue"
          :min="parameter.minValue"
          :max="parameter.maxValue"
          :placeholder="parameter.defaultValue || '请输入数值'"
          :disabled="disabled"
          class="number-input"
          @blur="onInputBlur"
          @keyup.enter="handleSave"
          @input="onValueChange"
        />        
        <!-- 数值输入（小数类型） -->
        <input 
          v-else-if="parameter.paramType === 'DECIMAL'"
          type="number"
          step="0.01"
          v-model="editValue"
          :min="parameter.minValue"
          :max="parameter.maxValue"
          :placeholder="parameter.defaultValue || '请输入小数'"
          :disabled="disabled"
          class="number-input"
          @blur="onInputBlur"
          @keyup.enter="handleSave"
          @input="onValueChange"
        />
        
        <!-- 文本输入 -->
        <input 
          v-else
          type="text"
          v-model="editValue"
          :placeholder="parameter.defaultValue || '请输入文本'"
          :disabled="disabled"
          class="text-input"
          @blur="onInputBlur"
          @keyup.enter="handleSave"
          @input="onValueChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted, onBeforeUnmount } from 'vue'
import CandidateValueSelector from './CandidateValueSelector.vue'
import type { ParameterItem, ValidationResult } from '../types'

interface Props {
  parameter: ParameterItem
  autoSave?: boolean
  alwaysEditing?: boolean
  showHeader?: boolean
  showConstraints?: boolean
  disabled?: boolean
  allowCustomValues?: boolean
}

interface Emits {
  (e: 'save', value: string): void
  (e: 'cancel'): void
  (e: 'startEdit'): void
  (e: 'change', value: string): void
}

const props = withDefaults(defineProps<Props>(), {
  autoSave: true,
  alwaysEditing: false,
  showHeader: false,
  showConstraints: true,
  disabled: false,
  allowCustomValues: true
})

const emit = defineEmits<Emits>()

// 响应式数据
const editValue = ref<string>('')
const originalValue = ref<string>('')
const isEditing = ref(props.alwaysEditing)
const rootRef = ref<HTMLElement | null>(null)

const saveStatus = ref<'saving' | 'success' | 'error' | null>(null)
const saveMessage = ref('')
const validationResult = ref<ValidationResult | null>(null)

// 计算属性
const candidateValues = computed(() => {
  return props.parameter.candidateValues || props.parameter.allowedValues || []
})

const hasCandidateValues = computed(() => {
  return candidateValues.value.length > 0
})

const hasRangeConstraint = computed(() => {
  return !!(props.parameter.minValue || props.parameter.maxValue)
})

const displayCurrentValue = computed(() => {
  const value = props.parameter.currentValue || props.parameter.defaultValue || ''
  if (!value) return '未设置'
  
  // 格式化显示值
  if (props.parameter.paramType === 'BOOLEAN') {
    const booleanMap: Record<string, string> = {
      '1': 'ON',
      '0': 'OFF',
      'true': 'ON',
      'false': 'OFF'
    }
    return booleanMap[value] || value
  }
  
  return value
})

const hasChanges = computed(() => {
  return String(editValue.value) !== originalValue.value
})

// 初始化编辑值
const initEditValue = () => {
  const value = props.parameter.currentValue || props.parameter.defaultValue || ''
  originalValue.value = value
  
  if (hasCandidateValues.value) {
    // 对于候选值，下拉默认显示“请选择值”
    editValue.value = ''
  } else {
    editValue.value = value
  }
}

// 监听参数变化，重新初始化编辑值
watch(() => props.parameter, initEditValue, { immediate: true })

// 监听alwaysEditing属性变化
watch(() => props.alwaysEditing, (newValue) => {
  isEditing.value = newValue
})

// 保存状态样式
const saveStatusClass = computed(() => {
  switch (saveStatus.value) {
    case 'saving':
      return 'text-blue-600'
    case 'success':
      return 'text-green-600'
    case 'error':
      return 'text-red-600'
    default:
      return ''
  }
})

// 保存状态文本
const saveStatusText = computed(() => {
  switch (saveStatus.value) {
    case 'saving':
      return '保存中...'
    case 'success':
      return '保存成功'
    case 'error':
      return saveMessage.value || '保存失败'
    default:
      return ''
  }
})

// 方法
const startEditing = () => {
  isEditing.value = true
  // 点击进入编辑时重置下拉占位
  if (hasCandidateValues.value) {
    editValue.value = ''
  }
  emit('startEdit')
}

const getRangeDescription = (): string => {
  const { minValue, maxValue } = props.parameter
  
  if (minValue && maxValue) {
    return `${minValue} - ${maxValue}`
  } else if (minValue) {
    return `≥ ${minValue}`
  } else if (maxValue) {
    return `≤ ${maxValue}`
  }
  
  return ''
}

const getHelpText = (): string => {
  const parts: string[] = []
  
  if (hasRangeConstraint.value) {
    parts.push(`范围: ${getRangeDescription()}`)
  }
  
  if (props.parameter.valueRange) {
    parts.push(`作用域: ${props.parameter.valueRange}`)
  }
  
  return parts.join(' | ')
}

// 验证输入值
const validateValue = (value: string): ValidationResult => {
  if (value === '' || value === null || value === undefined) {
    return { valid: true, message: '' } // 允许空值
  }
  
  const strValue = String(value).trim()
  
  switch (props.parameter.paramType) {
    case 'INTEGER':
      if (!/^-?\d+$/.test(strValue)) {
        return { valid: false, message: '请输入有效的整数' }
      }
      const intValue = Number(strValue)
      if (props.parameter.minValue && intValue < Number(props.parameter.minValue)) {
        return { valid: false, message: `值不能小于 ${props.parameter.minValue}` }
      }
      if (props.parameter.maxValue && intValue > Number(props.parameter.maxValue)) {
        return { valid: false, message: `值不能大于 ${props.parameter.maxValue}` }
      }
      break
      
    case 'DECIMAL':
      if (!/^-?\d+(\.\d+)?$/.test(strValue)) {
        return { valid: false, message: '请输入有效的小数' }
      }
      const decimalValue = Number(strValue)
      if (props.parameter.minValue && decimalValue < Number(props.parameter.minValue)) {
        return { valid: false, message: `值不能小于 ${props.parameter.minValue}` }
      }
      if (props.parameter.maxValue && decimalValue > Number(props.parameter.maxValue)) {
        return { valid: false, message: `值不能大于 ${props.parameter.maxValue}` }
      }
      break
      
    case 'BOOLEAN':
      const validBoolValues = ['ON', 'OFF']
      if (!validBoolValues.some(v => v.toLowerCase() === strValue.toLowerCase())) {
        return { valid: false, message: '布尔类型只能为 ON 或 OFF' }
      }
      break
      
    case 'STRING':
      if (candidateValues.value.length > 0 && !props.allowCustomValues) {
        if (!candidateValues.value.includes(strValue)) {
          return { valid: false, message: '请选择允许的值' }
        }
      }
      if (strValue.length > 1000) {
        return { valid: false, message: '文本长度不能超过1000个字符' }
      }
      break
  }
  
  return { valid: true, message: '' }
}

const onValueChange = () => {
  const result = validateValue(String(editValue.value))
  validationResult.value = result
  emit('change', String(editValue.value))

  // 如果启用了自动保存且有候选值，当值发生变化时自动保存
  if (props.autoSave && hasCandidateValues.value && hasChanges.value) {
    handleSave()
  }
}

const onValidationResult = (result: { valid: boolean; message?: string }) => {
  validationResult.value = { 
    valid: result.valid, 
    message: result.message || '' 
  }
}

const onCandidateValueSave = async (value: string) => {
  // 直接保存选中的候选值
  editValue.value = value
  await handleSave()
}

const onInputBlur = () => {
  if (props.autoSave && hasChanges.value) {
    handleSave()
  }
}

// 处理保存
const handleSave = async () => {
  const value = String(editValue.value).trim()
  
  // 如果值没有变化，不需要保存
  if (value === originalValue.value) {
    if (!props.alwaysEditing) {
      isEditing.value = false
    }
    return
  }
  
  // 验证输入值
  const validation = validateValue(value)
  if (!validation.valid) {
    if (props.parameter.paramType === 'BOOLEAN') {
      alert(validation.message || '布尔类型只能为 ON 或 OFF')
    }
    saveStatus.value = 'error'
    saveMessage.value = validation.message || '验证失败'
    setTimeout(() => {
      saveStatus.value = null
    }, 3000)
    return
  }
  
  try {
    saveStatus.value = 'saving'
    emit('save', value)

    // 更新原始值
    originalValue.value = value

    // 立即退出编辑模式（实际的保存结果由父组件处理）
    if (!props.alwaysEditing) {
      isEditing.value = false
    }

  } catch (error) {
    console.error('保存失败:', error)
    saveStatus.value = 'error'
    saveMessage.value = '保存失败'
    setTimeout(() => {
      saveStatus.value = null
    }, 3000)
  }
}

// 处理取消
const handleCancel = () => {
  initEditValue()
  validationResult.value = null
  if (!props.alwaysEditing) {
    isEditing.value = false
  }
  emit('cancel')
}

// 暴露方法给父组件
defineExpose({
  handleSave,
  handleCancel,
  validateValue,
  startEditing
})

// 点击组件外部退出编辑（未保存时直接取消）
const handleClickOutside = (event: MouseEvent) => {
  if (props.alwaysEditing) return
  if (!isEditing.value) return
  const target = event.target as Node
  if (rootRef.value && !rootRef.value.contains(target)) {
    handleCancel()
  }
}

onMounted(() => {
  document.addEventListener('mousedown', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleClickOutside)
})
</script>

<style scoped>
.parameter-editor {
  @apply space-y-2;
}

.editor-header {
  @apply flex items-center justify-between p-2 bg-gray-50 rounded-t border;
}

.editor-title {
  @apply flex items-center space-x-2;
}

.param-name {
  @apply font-medium text-gray-900;
}

.param-type {
  @apply text-xs px-2 py-1 bg-blue-100 text-blue-800 rounded-full;
}

.editor-actions {
  @apply flex space-x-2;
}

.edit-btn {
  @apply px-3 py-1 text-sm bg-blue-600 text-white rounded;
  @apply hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed;
}

.edit-actions {
  @apply flex space-x-2;
}

.save-btn {
  @apply px-3 py-1 text-sm bg-green-600 text-white rounded;
  @apply hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed;
}

.cancel-btn {
  @apply px-3 py-1 text-sm bg-gray-300 text-gray-700 rounded;
  @apply hover:bg-gray-400;
}

.current-value-display {
  @apply flex items-center space-x-2 p-2 bg-white border rounded;
}

.current-value {
  @apply flex-1 font-mono text-sm;
}

.inline-edit-btn {
  @apply text-gray-400 hover:text-gray-600 cursor-pointer;
  @apply disabled:opacity-50 disabled:cursor-not-allowed;
}

.editor-content {
  @apply space-y-2;
}

.current-value-row {
  @apply flex items-center space-x-2 p-2 bg-gray-50 border border-gray-200 rounded;
}

.current-value-label {
  @apply text-sm font-medium text-gray-700;
}

.current-value-text {
  @apply text-sm font-mono text-gray-900;
}

.selector-row {
  @apply w-full;
}

.number-input-wrapper,
.text-input-wrapper {
  @apply space-y-1;
}

.number-input,
.text-input {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md;
  @apply focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
  @apply disabled:bg-gray-100 disabled:cursor-not-allowed;
}

.range-hint {
  @apply text-xs text-gray-500;
}

.validation-error {
  @apply flex items-center text-sm text-red-600;
}

.error-icon {
  @apply mr-1;
}

.save-status {
  @apply flex items-center text-sm;
}

.save-status.text-blue-600 {
  @apply text-blue-600;
}

.save-status.text-green-600 {
  @apply text-green-600;
}

.save-status.text-red-600 {
  @apply text-red-600;
}

.status-icon::before {
  content: "ℹ️";
  @apply mr-1;
}

.save-status.text-blue-600 .status-icon::before {
  content: "⏳";
}

.save-status.text-green-600 .status-icon::before {
  content: "✅";
}

.save-status.text-red-600 .status-icon::before {
  content: "❌";
}
</style>