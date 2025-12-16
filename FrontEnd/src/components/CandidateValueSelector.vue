<template>
  <div class="candidate-value-selector">
    <!-- 单选模式 -->
    <div v-if="!multiple" class="single-select">
      <!-- 下拉选择器（只显示下拉框，不显示按钮组） -->
      <div v-if="candidateValues.length > 0" class="select-wrapper">
        <select 
          v-model="selectedValue"
          @change="onSelectionChange"
          class="candidate-select"
          :disabled="disabled"
        >
          <option value="" disabled>请选择值</option>
          <option 
            v-for="value in candidateValues" 
            :key="value"
            :value="value"
          >
            {{ formatDisplayValue(value) }}
          </option>
          <option v-if="allowCustom" value="__custom__">自定义值...</option>
        </select>
      </div>

      <!-- 自定义值输入 -->
      <div v-if="showCustomInput" class="custom-input-wrapper">
        <input
          v-model="customValue"
          @blur="onCustomValueBlur"
          @keyup.enter="onCustomValueEnter"
          @keyup.escape="cancelCustomInput"
          type="text"
          placeholder="输入自定义值"
          class="custom-input"
          ref="customInputRef"
        />
        <div class="custom-input-actions">
          <button @click="confirmCustomValue" class="confirm-btn" :disabled="!customValue.trim()">
            确认
          </button>
          <button @click="cancelCustomInput" class="cancel-btn">
            取消
          </button>
        </div>
      </div>
    </div>

    <!-- 多选模式 -->
    <div v-if="multiple" class="multi-select">
      <div class="selected-values" v-if="selectedValues.length > 0">
        <span class="selected-label">已选择:</span>
        <div class="selected-tags">
          <span 
            v-for="value in selectedValues" 
            :key="value"
            class="selected-tag"
          >
            {{ formatDisplayValue(value) }}
            <button 
              @click="removeValue(value)"
              class="remove-btn"
              :disabled="disabled"
            >
              ×
            </button>
          </span>
        </div>
      </div>

      <div class="candidate-checkboxes">
        <label 
          v-for="value in candidateValues" 
          :key="value"
          class="checkbox-label"
          :class="{ 'is-disabled': disabled }"
        >
          <input
            type="checkbox"
            :value="value"
            :checked="selectedValues.includes(value)"
            @change="onCheckboxChange(value, $event)"
            :disabled="disabled"
            class="checkbox-input"
          />
          <span class="checkbox-text">{{ formatDisplayValue(value) }}</span>
        </label>
      </div>

      <div v-if="allowCustom" class="custom-multi-input">
        <input
          v-model="customValue"
          @keyup.enter="addCustomValue"
          type="text"
          placeholder="输入自定义值并按回车添加"
          class="custom-input"
          :disabled="disabled"
        />
        <button 
          @click="addCustomValue"
          class="add-btn"
          :disabled="!customValue.trim() || disabled"
        >
          添加
        </button>
      </div>
    </div>

    <!-- 验证提示（已移除，根据用户要求不显示） -->
    <!-- <div v-if="validationMessage" class="validation-message" :class="validationClass">
      <i class="validation-icon"></i>
      {{ validationMessage }}
    </div> -->

    <!-- 帮助文本（已移除，根据用户要求不显示） -->
    <!-- <div v-if="helpText" class="help-text">
      {{ helpText }}
    </div> -->
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, nextTick } from 'vue'
import type { ParameterItem } from '../types'

interface Props {
  modelValue: string | string[]
  candidateValues: string[]
  parameter?: ParameterItem
  multiple?: boolean
  allowCustom?: boolean
  disabled?: boolean
  maxButtonCount?: number
  helpText?: string
  validateValue?: (value: string) => { valid: boolean; message?: string }
}

interface Emits {
  (e: 'update:modelValue', value: string | string[]): void
  (e: 'change', value: string | string[]): void
  (e: 'validate', result: { valid: boolean; message?: string }): void
  (e: 'save', value: string): void
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  allowCustom: false,
  disabled: false,
  maxButtonCount: 4
})

const emit = defineEmits<Emits>()

// 响应式数据
const selectedValue = ref<string>('')
const customValue = ref<string>('')
const showCustomInput = ref<boolean>(false)
const validationMessage = ref<string>('')
const validationClass = ref<string>('')
const customInputRef = ref<HTMLInputElement>()

// 计算属性
const selectedValues = computed<string[]>(() => {
  if (props.multiple) {
    return Array.isArray(props.modelValue) ? props.modelValue : []
  }
  return []
})

// 监听器
watch(() => props.modelValue, (newValue) => {
  if (!props.multiple && typeof newValue === 'string') {
    selectedValue.value = newValue || ''
  }
}, { immediate: true })

watch(showCustomInput, async (show) => {
  if (show) {
    await nextTick()
    customInputRef.value?.focus()
  }
})

// 方法
const formatDisplayValue = (value: string): string => {
  // 格式化显示值，可以根据参数类型进行特殊处理
  if (props.parameter?.paramType === 'BOOLEAN') {
    const booleanMap: Record<string, string> = {
      '1': 'ON (1)',
      '0': 'OFF (0)',
      'true': 'ON (true)',
      'false': 'OFF (false)',
      'ON': 'ON',
      'OFF': 'OFF'
    }
    return booleanMap[value] || value
  }
  
  return value
}

const getValueTooltip = (value: string): string => {
  if (props.parameter) {
    if (value === props.parameter.defaultValue) {
      return `${value} (默认值)`
    }
    
    // 可以添加更多的工具提示逻辑
    if (props.parameter.paramType === 'BOOLEAN') {
      return value === '1' || value === 'ON' || value === 'true' ? '启用' : '禁用'
    }
  }
  
  return value
}

const validateInput = (value: string): { valid: boolean; message?: string } => {
  if (props.validateValue) {
    return props.validateValue(value)
  }
  
  // 默认验证逻辑
  if (!value.trim()) {
    return { valid: false, message: '值不能为空' }
  }
  
  // 如果有候选值限制，检查是否在候选值中
  if (props.candidateValues.length > 0 && !props.allowCustom) {
    if (!props.candidateValues.includes(value)) {
      return { valid: false, message: '值必须是候选值之一' }
    }
  }
  
  return { valid: true }
}

const updateValidation = (value: string) => {
  const result = validateInput(value)
  validationMessage.value = result.message || ''
  validationClass.value = result.valid ? 'valid' : 'invalid'
  emit('validate', result)
}

const selectValue = (value: string) => {
  if (props.disabled) return

  selectedValue.value = value
  emit('update:modelValue', value)
  emit('change', value)
  emit('save', value) // 添加保存事件
  updateValidation(value)
}

const onSelectionChange = () => {
  if (selectedValue.value === '__custom__') {
    showCustomInput.value = true
    selectedValue.value = ''
  } else {
    selectValue(selectedValue.value)
    // 立即触发保存
    emit('save', selectedValue.value)
  }
}

const onCustomValueBlur = () => {
  // 延迟隐藏，允许点击确认按钮
  setTimeout(() => {
    if (!customValue.value.trim()) {
      showCustomInput.value = false
    }
  }, 200)
}

const onCustomValueEnter = () => {
  confirmCustomValue()
}

const confirmCustomValue = () => {
  if (customValue.value.trim()) {
    selectValue(customValue.value.trim())
    customValue.value = ''
    showCustomInput.value = false
  }
}

const cancelCustomInput = () => {
  customValue.value = ''
  showCustomInput.value = false
}

// 多选模式方法
const onCheckboxChange = (value: string, event: Event) => {
  if (props.disabled) return
  
  const target = event.target as HTMLInputElement
  const newSelectedValues = [...selectedValues.value]
  
  if (target.checked) {
    if (!newSelectedValues.includes(value)) {
      newSelectedValues.push(value)
    }
  } else {
    const index = newSelectedValues.indexOf(value)
    if (index > -1) {
      newSelectedValues.splice(index, 1)
    }
  }
  
  emit('update:modelValue', newSelectedValues)
  emit('change', newSelectedValues)
}

const removeValue = (value: string) => {
  if (props.disabled) return
  
  const newSelectedValues = selectedValues.value.filter(v => v !== value)
  emit('update:modelValue', newSelectedValues)
  emit('change', newSelectedValues)
}

const addCustomValue = () => {
  if (!customValue.value.trim() || props.disabled) return
  
  const value = customValue.value.trim()
  const newSelectedValues = [...selectedValues.value]
  
  if (!newSelectedValues.includes(value)) {
    newSelectedValues.push(value)
    emit('update:modelValue', newSelectedValues)
    emit('change', newSelectedValues)
  }
  
  customValue.value = ''
}
</script>

<style scoped>
.candidate-value-selector {
  @apply space-y-3;
}

/* 单选模式样式 */
.single-select {
  @apply space-y-2;
}

.select-wrapper {
  @apply relative;
}

.candidate-select {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md;
  @apply focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
  @apply disabled:bg-gray-100 disabled:cursor-not-allowed;
}

.button-group {
  @apply flex flex-wrap gap-2;
}

.candidate-button {
  @apply px-3 py-2 text-sm border border-gray-300 rounded-md;
  @apply hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500;
  @apply transition-colors duration-200;
}

.candidate-button.is-selected {
  @apply bg-blue-100 border-blue-500 text-blue-700;
}

.candidate-button.is-disabled {
  @apply opacity-50 cursor-not-allowed;
}

.custom-button {
  @apply border-dashed border-gray-400 text-gray-600;
  @apply hover:border-gray-500 hover:text-gray-700;
}

.custom-input-wrapper {
  @apply space-y-2 p-3 border border-gray-200 rounded-md bg-gray-50;
}

.custom-input {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md;
  @apply focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
}

.custom-input-actions {
  @apply flex space-x-2;
}

.confirm-btn {
  @apply px-3 py-1 text-sm bg-blue-600 text-white rounded;
  @apply hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed;
}

.cancel-btn {
  @apply px-3 py-1 text-sm bg-gray-300 text-gray-700 rounded;
  @apply hover:bg-gray-400;
}

/* 多选模式样式 */
.multi-select {
  @apply space-y-3;
}

.selected-values {
  @apply space-y-2;
}

.selected-label {
  @apply text-sm font-medium text-gray-700;
}

.selected-tags {
  @apply flex flex-wrap gap-2;
}

.selected-tag {
  @apply inline-flex items-center px-2 py-1 text-sm bg-blue-100 text-blue-800 rounded-full;
}

.remove-btn {
  @apply ml-1 text-blue-600 hover:text-blue-800 font-bold;
  @apply disabled:opacity-50 disabled:cursor-not-allowed;
}

.candidate-checkboxes {
  @apply space-y-2 max-h-40 overflow-y-auto;
}

.checkbox-label {
  @apply flex items-center space-x-2 cursor-pointer;
  @apply hover:bg-gray-50 p-2 rounded;
}

.checkbox-label.is-disabled {
  @apply opacity-50 cursor-not-allowed;
}

.checkbox-input {
  @apply rounded border-gray-300 text-blue-600;
  @apply focus:ring-blue-500 focus:ring-2;
}

.checkbox-text {
  @apply text-sm text-gray-700;
}

.custom-multi-input {
  @apply flex space-x-2;
}

.add-btn {
  @apply px-3 py-2 text-sm bg-green-600 text-white rounded;
  @apply hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed;
}

/* 验证和帮助文本样式 */
.validation-message {
  @apply flex items-center text-sm;
}

.validation-message.valid {
  @apply text-green-600;
}

.validation-message.invalid {
  @apply text-red-600;
}

.validation-icon::before {
  content: "ℹ️";
  @apply mr-1;
}

.validation-message.valid .validation-icon::before {
  content: "✅";
}

.validation-message.invalid .validation-icon::before {
  content: "❌";
}

.help-text {
  @apply text-xs text-gray-500;
}
</style>