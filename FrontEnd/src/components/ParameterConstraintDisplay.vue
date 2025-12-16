<template>
  <div class="parameter-constraint-display">
    <!-- 约束信息概览 -->
    <div v-if="hasAnyConstraint" class="constraint-overview">
      <div class="constraint-badges">
        <span 
          v-if="constraint.hasRangeConstraint" 
          class="constraint-badge range-badge"
          :title="getRangeTooltip()"
        >
          <i class="icon-range"></i>
          范围约束
        </span>
        
        <span 
          v-if="constraint.hasCandidateConstraint" 
          class="constraint-badge candidate-badge"
          :title="getCandidateTooltip()"
        >
          <i class="icon-list"></i>
          候选值
        </span>
        
        <span 
          v-if="parameter.valueRange" 
          class="constraint-badge scope-badge"
          :title="getScopeTooltip()"
        >
          <i class="icon-scope"></i>
          {{ parameter.valueRange }}
        </span>
      </div>
    </div>

    <!-- 详细约束信息 -->
    <div v-if="showDetails" class="constraint-details">
      <!-- 数值范围约束 -->
      <div v-if="constraint.hasRangeConstraint" class="constraint-section">
        <h4 class="constraint-title">
          <i class="icon-range"></i>
          数值范围
        </h4>
        <div class="range-display">
          <div class="range-info">
            <span class="range-text">{{ constraint.rangeDescription }}</span>
            <div class="range-visual" v-if="canShowRangeVisual">
              <div class="range-bar">
                <div class="range-indicator" :style="getRangeIndicatorStyle()"></div>
                <div class="range-markers">
                  <span v-if="constraint.minValue" class="marker min-marker">{{ constraint.minValue }}</span>
                  <span v-if="constraint.maxValue" class="marker max-marker">{{ constraint.maxValue }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 候选取值约束 -->
      <div v-if="constraint.hasCandidateConstraint" class="constraint-section">
        <h4 class="constraint-title">
          <i class="icon-list"></i>
          候选取值
          <span class="candidate-count">({{ constraint.candidateValues?.length || 0 }} 个)</span>
        </h4>
        <div class="candidate-display">
          <div class="candidate-tags">
            <span 
              v-for="(value, index) in displayCandidateValues" 
              :key="index"
              class="candidate-tag"
              :class="{ 'is-current': value === parameter.defaultValue }"
              :title="getCandidateValueTooltip(value)"
            >
              {{ value }}
            </span>
            <span 
              v-if="hasMoreCandidates" 
              class="candidate-tag more-tag"
              @click="toggleShowAllCandidates"
              title="点击查看更多"
            >
              +{{ remainingCandidatesCount }} 更多
            </span>
          </div>
          
          <button 
            v-if="constraint.candidateValues && constraint.candidateValues.length > maxDisplayCandidates"
            @click="toggleShowAllCandidates"
            class="toggle-candidates-btn"
          >
            {{ showAllCandidates ? '收起' : '展开全部' }}
          </button>
        </div>
      </div>

      <!-- 设置范围信息 -->
      <div v-if="parameter.valueRange" class="constraint-section">
        <h4 class="constraint-title">
          <i class="icon-scope"></i>
          作用域
        </h4>
        <div class="scope-display">
          <div class="scope-info">
            <span class="scope-value">{{ parameter.valueRange }}</span>
            <span class="scope-description">{{ getScopeDescription() }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 切换详情显示按钮 -->
    <button 
      v-if="hasAnyConstraint"
      @click="toggleDetails"
      class="toggle-details-btn"
      :class="{ 'is-expanded': showDetails }"
    >
      <i :class="showDetails ? 'icon-chevron-up' : 'icon-chevron-down'"></i>
      {{ showDetails ? '收起详情' : '查看详情' }}
    </button>

    <!-- 无约束提示 -->
    <div v-if="!hasAnyConstraint" class="no-constraints">
      <i class="icon-info"></i>
      <span>此参数无特殊约束条件</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { ParameterItem, ParameterConstraint } from '../types'

interface Props {
  parameter: ParameterItem
  showDetailsDefault?: boolean
  maxDisplayCandidates?: number
}

const props = withDefaults(defineProps<Props>(), {
  showDetailsDefault: false,
  maxDisplayCandidates: 5
})

// 响应式数据
const showDetails = ref(props.showDetailsDefault)
const showAllCandidates = ref(false)

// 计算属性
const constraint = computed((): ParameterConstraint => {
  if (props.parameter.constraints) {
    return props.parameter.constraints
  }
  
  // 如果没有约束对象，从其他字段构建
  const candidateValues = props.parameter.candidateValues || props.parameter.allowedValues || []
  return {
    minValue: props.parameter.minValue,
    maxValue: props.parameter.maxValue,
    candidateValues,
    valueRange: props.parameter.valueRange,
    hasRangeConstraint: !!(props.parameter.minValue || props.parameter.maxValue),
    hasCandidateConstraint: candidateValues.length > 0,
    rangeDescription: getRangeDescription(),
    candidateValuesDisplay: candidateValues.join(', '),
    hasAnyConstraint: !!(props.parameter.minValue || props.parameter.maxValue || candidateValues.length > 0)
  }
})

const hasAnyConstraint = computed(() => constraint.value.hasAnyConstraint)

const displayCandidateValues = computed(() => {
  const candidates = constraint.value.candidateValues || []
  if (showAllCandidates.value || candidates.length <= props.maxDisplayCandidates) {
    return candidates
  }
  return candidates.slice(0, props.maxDisplayCandidates)
})

const hasMoreCandidates = computed(() => {
  const candidates = constraint.value.candidateValues || []
  return !showAllCandidates.value && candidates.length > props.maxDisplayCandidates
})

const remainingCandidatesCount = computed(() => {
  const candidates = constraint.value.candidateValues || []
  return Math.max(0, candidates.length - props.maxDisplayCandidates)
})

const canShowRangeVisual = computed(() => {
  return props.parameter.paramType === 'INTEGER' || props.parameter.paramType === 'DECIMAL'
})

// 方法
const toggleDetails = () => {
  showDetails.value = !showDetails.value
}

const toggleShowAllCandidates = () => {
  showAllCandidates.value = !showAllCandidates.value
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

const getRangeTooltip = (): string => {
  const desc = getRangeDescription()
  return desc ? `数值范围: ${desc}` : '有数值范围限制'
}

const getCandidateTooltip = (): string => {
  const candidates = constraint.value.candidateValues || []
  const count = candidates.length
  if (count <= 3) {
    return `候选值: ${candidates.join(', ')}`
  }
  return `候选值 (${count} 个): ${candidates.slice(0, 3).join(', ')}...`
}

const getScopeTooltip = (): string => {
  const scope = props.parameter.valueRange
  const descriptions: Record<string, string> = {
    'Global': '全局设置，影响整个MySQL服务器',
    'Session': '会话设置，仅影响当前连接',
    'Both': '可以设置为全局或会话级别'
  }
  return descriptions[scope || ''] || `作用域: ${scope}`
}

const getCandidateValueTooltip = (value: string): string => {
  if (value === props.parameter.defaultValue) {
    return `${value} (当前默认值)`
  }
  return value
}

const getScopeDescription = (): string => {
  const scope = props.parameter.valueRange
  const descriptions: Record<string, string> = {
    'Global': '全局设置，重启后生效',
    'Session': '会话设置，立即生效',
    'Both': '支持全局和会话设置'
  }
  return descriptions[scope || ''] || ''
}

const getRangeIndicatorStyle = () => {
  // 这里可以根据当前值在范围中的位置计算样式
  // 简化实现，仅作为示例
  return {
    left: '20%',
    width: '60%'
  }
}
</script>

<style scoped>
.parameter-constraint-display {
  @apply space-y-3;
}

.constraint-overview {
  @apply mb-3;
}

.constraint-badges {
  @apply flex flex-wrap gap-2;
}

.constraint-badge {
  @apply inline-flex items-center px-2 py-1 text-xs font-medium rounded-full cursor-help;
}

.range-badge {
  @apply bg-blue-100 text-blue-800 border border-blue-200;
}

.candidate-badge {
  @apply bg-green-100 text-green-800 border border-green-200;
}

.scope-badge {
  @apply bg-purple-100 text-purple-800 border border-purple-200;
}

.constraint-details {
  @apply space-y-4 p-3 bg-gray-50 rounded-lg border;
}

.constraint-section {
  @apply space-y-2;
}

.constraint-title {
  @apply flex items-center text-sm font-semibold text-gray-700 mb-2;
}

.constraint-title i {
  @apply mr-2 text-gray-500;
}

.candidate-count {
  @apply ml-1 text-xs font-normal text-gray-500;
}

.range-display {
  @apply space-y-2;
}

.range-info {
  @apply space-y-2;
}

.range-text {
  @apply text-sm font-mono bg-white px-2 py-1 rounded border;
}

.range-visual {
  @apply mt-2;
}

.range-bar {
  @apply relative h-2 bg-gray-200 rounded-full;
}

.range-indicator {
  @apply absolute h-full bg-blue-500 rounded-full;
}

.range-markers {
  @apply flex justify-between mt-1;
}

.marker {
  @apply text-xs text-gray-600 font-mono;
}

.candidate-display {
  @apply space-y-2;
}

.candidate-tags {
  @apply flex flex-wrap gap-1;
}

.candidate-tag {
  @apply inline-block px-2 py-1 text-xs bg-white border rounded cursor-default;
  @apply hover:bg-gray-50 transition-colors;
}

.candidate-tag.is-current {
  @apply bg-blue-100 border-blue-300 text-blue-800 font-medium;
}

.more-tag {
  @apply bg-gray-100 border-gray-300 text-gray-600 cursor-pointer;
  @apply hover:bg-gray-200;
}

.toggle-candidates-btn {
  @apply text-xs text-blue-600 hover:text-blue-800 underline cursor-pointer;
}

.scope-display {
  @apply space-y-1;
}

.scope-info {
  @apply flex items-center space-x-2;
}

.scope-value {
  @apply text-sm font-mono bg-white px-2 py-1 rounded border;
}

.scope-description {
  @apply text-xs text-gray-600;
}

.toggle-details-btn {
  @apply flex items-center justify-center w-full px-3 py-2 text-sm text-gray-600;
  @apply border border-gray-300 rounded hover:bg-gray-50 transition-colors;
}

.toggle-details-btn.is-expanded {
  @apply text-blue-600 border-blue-300;
}

.toggle-details-btn i {
  @apply mr-1;
}

.no-constraints {
  @apply flex items-center justify-center py-4 text-sm text-gray-500;
}

.no-constraints i {
  @apply mr-2;
}

/* 图标样式 (使用CSS类名，实际项目中可能需要图标字体或SVG) */
.icon-range::before { content: "📏"; }
.icon-list::before { content: "📋"; }
.icon-scope::before { content: "🎯"; }
.icon-info::before { content: "ℹ️"; }
.icon-chevron-up::before { content: "▲"; }
.icon-chevron-down::before { content: "▼"; }
</style>