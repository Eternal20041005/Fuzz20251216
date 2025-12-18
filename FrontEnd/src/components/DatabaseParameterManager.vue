<template>
  <div class="database-parameter-manager">
    <!-- 状态栏 -->
    <div class="mb-4 bg-gray-50 rounded-lg p-4 border">
      <div class="grid grid-cols-2 md:grid-cols-7 gap-4 text-sm">
        <div>
          <span class="font-medium text-gray-700">当前数据库：</span>
          <span class="text-gray-900">{{ currentTestDb.name || '无' }}</span>
        </div>
        <div>
          <span class="font-medium text-gray-700">版本：</span>
          <span class="text-gray-900">{{ currentTestDb.version || '无' }}</span>
        </div>
        <div>
          <span class="font-medium text-gray-700">状态：</span>
          <span :class="currentTestStatus.color" class="font-medium">{{ currentTestStatus.text }}</span>
        </div>
        <div>
          <span class="font-medium text-gray-700">运行时间：</span>
          <span class="text-gray-900">{{ currentTestTime || '无' }}</span>
        </div>
        <div>
          <span class="font-medium text-gray-700">参数组合：</span>
          <span class="text-gray-900">{{ currentParamCombo || '无' }}</span>
        </div>
        <div>
          <span class="font-medium text-gray-700">覆盖率：</span>
          <span class="text-gray-900">{{ currentCoverage || '0%' }}</span>
        </div>
        <div>
          <span class="font-medium text-gray-700">发现Bug：</span>
          <span class="text-gray-900">{{ currentBugCount || '0' }}</span>
        </div>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="mb-6 space-y-4">
      <!-- 数据库选择和导入 -->
      <div class="flex items-center space-x-4">
        <div class="flex-1">
          <select
            v-model="selectedDatabase"
            class="w-full p-2 border border-gray-300 rounded focus:border-blue-500 focus:outline-none"
            @change="onDatabaseChange"
          >
            <option value="">请选择测试的数据库</option>
            <option value="mysql-9.5.0">MySQL (v9.5.0)</option>
            <option value="mysql-9.4.0">MySQL (v9.4.0)</option>
            <option value="postgresql-19">PostgreSQL (v19)</option>
            <option value="postgresql-18">PostgreSQL (v18)</option>
            <option value="mariadb-11.8">MariaDB (v11.8 LTS)</option>
            <option value="mariadb-11.7">MariaDB (v11.7 LTS)</option>
            <option value="oceanbase-4.4">OceanBase (v4.4)</option>
            <option value="oceanbase-4.3">OceanBase (v4.3)</option>
            <option value="tidb-7.5.7">TiDB (v7.5.7)</option>
            <option value="tidb-7.5.6">TiDB (v7.5.6)</option>
          </select>
        </div>


      </div>
      
      <!-- 搜索和过滤 -->
      <div class="space-y-4">
        <!-- 筛选行：搜索框 + 三个下拉框 + 清空筛选按钮 -->
        <div class="flex items-center space-x-2 bg-gray-50 p-2 rounded-lg border border-gray-200">
          <!-- 搜索框 -->
          <div class="flex-1 relative">
            <div class="relative">
              <input
                v-model="searchKeyword"
                type="text"
                placeholder="搜索参数名、描述、候选值..."
                class="w-full p-2 pl-10 border border-gray-300 rounded focus:border-blue-500 focus:outline-none bg-white"
                @input="onSearchInput"
                @focus="showSuggestions = searchSuggestions.length > 0"
                @blur="hideSuggestions"
              />
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
              </div>
            </div>
            
            <!-- 搜索建议下拉框 -->
            <div 
              v-if="showSuggestions && searchSuggestions.length > 0"
              class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto"
            >
              <div
                v-for="(suggestion, index) in searchSuggestions"
                :key="index"
                @mousedown="selectSuggestion(suggestion)"
                class="px-3 py-2 cursor-pointer hover:bg-gray-100 text-sm"
              >
                <span class="font-medium">{{ suggestion }}</span>
              </div>
            </div>
          </div>

          <!-- 参数类别 -->
          <div class="w-36">
            <select 
              v-model="selectedCategory" 
              class="w-full p-2 border border-gray-300 rounded focus:border-blue-500 focus:outline-none text-sm bg-white"
              @change="loadParameters"
            >
              <option value="">所有类别</option>
              <option 
                v-for="category in categories" 
                :key="category" 
                :value="category"
              >
                {{ category }}
              </option>
            </select>
          </div>

          <!-- 设置范围 -->
          <div class="w-36">
            <select 
              v-model="selectedValueRange" 
              class="w-full p-2 border border-gray-300 rounded focus:border-blue-500 focus:outline-none text-sm bg-white"
              @change="loadParameters"
            >
              <option value="">所有范围</option>
              <option 
                v-for="range in valueRanges" 
                :key="range" 
                :value="range"
              >
                {{ range }}
              </option>
            </select>
          </div>

          <!-- 测试状态 -->
          <div class="w-28">
            <select 
              v-model="selectedTestStatus" 
              class="w-full p-2 border border-gray-300 rounded focus:border-blue-500 focus:outline-none text-sm bg-white"
              @change="loadParameters"
            >
              <option value="">全部</option>
              <option value="true">启用测试</option>
              <option value="false">禁用测试</option>
            </select>
          </div>

          <!-- 清空筛选按钮 -->
          <button
            @click="clearFilters"
            class="py-2 px-3 border border-gray-300 rounded hover:bg-gray-100 text-sm whitespace-nowrap bg-white"
          >
            清空筛选
          </button>
        </div>

        <!-- 操作按钮行 -->
        <div class="flex justify-end space-x-2">
          <button
            @click="() => { console.log('按钮被点击'); saveConfiguration() }"
            :disabled="parameters.length === 0 || saving"
            class="py-2 px-4 border rounded text-green-600 border-green-400 hover:bg-green-50 disabled:opacity-50 disabled:cursor-not-allowed text-sm"
          >
            {{ saving ? '保存中...' : '保存配置' }}
          </button>
          <button
            @click="resetAllParametersToDefault"
            :disabled="parameters.length === 0 || resetting"
            class="py-2 px-4 border rounded text-red-600 border-red-400 hover:bg-red-50 disabled:opacity-50 disabled:cursor-not-allowed text-sm"
          >
            {{ resetting ? '重置参数中...' : '恢复默认' }}
          </button>
        </div>
      </div>
    </div>







    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      <div class="mt-2 text-gray-600">加载中...</div>
    </div>



    <!-- 参数表格 -->
    <div v-else-if="parameters.length > 0" class="bg-white rounded-lg shadow-md overflow-hidden">
      <div class="overflow-x-auto table-container">
        <table class="min-w-full">
          <thead>
            <tr class="bg-gray-100">
              <th class="py-3 px-4 border border-gray-300 text-left">
                参数名
              </th>
              <th class="py-3 px-4 border border-gray-300 text-left">描述</th>
              <th class="py-3 px-4 border border-gray-300 text-left">类别</th>
              <th class="py-3 px-4 border border-gray-300 text-left">设置范围</th>
              <th class="py-3 px-4 border border-gray-300 text-left">约束信息</th>
              <th class="py-3 px-4 border border-gray-300 text-left">默认值</th>
              <th class="py-3 px-4 border border-gray-300 text-center">权重</th>
              <th class="py-3 px-4 border border-gray-300 text-center">是否测试</th>
              <th class="py-3 px-4 border border-gray-300 text-center">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="(param, index) in parameters" 
              :key="param.id"
              :class="index % 2 === 0 ? 'bg-white' : 'bg-gray-50'"
              @mouseenter="hoveredParam = param.id"
              @mouseleave="hoveredParam = null"
            >
              <!-- 参数名 -->
              <td class="py-3 px-4 border border-gray-300">
                <div class="flex flex-col">
                  <span class="font-medium">{{ param.paramName }}</span>
                  <span class="text-xs text-gray-500">{{ param.paramType }}</span>
                </div>
              </td>
              
              <!-- 描述 -->
              <td class="py-3 px-4 border border-gray-300">
                <div class="max-w-xs">
                  <span 
                    class="text-sm text-gray-600 cursor-help" 
                    :title="param.description"
                  >
                    {{ truncateText(param.description, 50) || '-' }}
                  </span>
                </div>
              </td>
              
              <!-- 类别 -->
              <td class="py-3 px-4 border border-gray-300">
                <span class="inline-block px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded">
                  {{ param.category }}
                </span>
              </td>

              <!-- 设置范围 -->
              <td class="py-3 px-4 border border-gray-300">
                <span 
                  v-if="param.valueRange"
                  class="inline-block px-2 py-1 text-xs rounded"
                  :class="getValueRangeClass(param.valueRange)"
                  :title="getValueRangeTooltip(param.valueRange)"
                >
                  {{ param.valueRange }}
                </span>
                <span v-else class="text-gray-400 text-xs">-</span>
              </td>

              <!-- 约束信息 -->
              <td class="py-3 px-4 border border-gray-300">
                <div class="flex flex-wrap gap-1">
                  <!-- 候选值标识 -->
                  <span 
                    v-if="hasCandidateValues(param)"
                    class="inline-flex items-center px-2 py-1 text-xs bg-green-100 text-green-800 rounded cursor-help"
                    :title="getCandidateValuesPreview(param)"
                  >
                    <span class="mr-1">📋</span>
                    {{ param.candidateValues?.length || param.allowedValues?.length }} 个选项
                  </span>

                  <!-- 范围约束标识 -->
                  <span 
                    v-if="hasRangeConstraint(param)"
                    class="inline-flex items-center px-2 py-1 text-xs bg-orange-100 text-orange-800 rounded cursor-help"
                    :title="getRangeConstraintPreview(param)"
                  >
                    <span class="mr-1">📏</span>
                    范围限制
                  </span>

                  <!-- 无约束 -->
                  <span 
                    v-if="!hasCandidateValues(param) && !hasRangeConstraint(param)"
                    class="text-gray-400 text-xs"
                  >
                    无约束
                  </span>
                </div>
              </td>
              
              <!-- 默认值编辑器 -->
              <td class="py-3 px-4 border border-gray-300">
                <ParameterEditor
                  :parameter="param"
                  :always-editing="false"
                  :show-constraints="false"
                  @save="(value) => updateParameterValue(param, value)"
                />
              </td>
              
              <!-- 权重 -->
              <td class="py-3 px-4 border border-gray-300 text-center">
                <div class="flex items-center justify-center space-x-2">
                  <input 
                    type="number" 
                    v-model.number="param.weight"
                    min="0"
                    max="10"
                    step="0.1"
                    @blur="updateParameterWeight(param)"
                    class="w-20 p-1 border border-gray-300 rounded text-center text-sm"
                    :disabled="updatingWeights.has(param.id)"
                  />
                  <span v-if="updatingWeights.has(param.id)" class="text-xs text-gray-500">保存中...</span>
                </div>
              </td>
              
              <!-- 是否测试 -->
              <td class="py-3 px-4 border border-gray-300 text-center">
                <input 
                  type="checkbox" 
                  v-model="param.isTestDefault"
                  @change="updateParameterTestStatus(param)"
                  class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                />
              </td>
              
              <!-- 操作 -->
              <td class="py-3 px-4 border border-gray-300 text-center">
                <div class="flex items-center justify-center space-x-2">
                  <button
                    @click="showParameterDetails(param)"
                    class="text-blue-600 hover:text-blue-800 text-sm"
                    title="查看详情"
                  >
                    详情
                  </button>
                  <button
                    @click="deleteParameter(param)"
                    class="text-red-600 hover:text-red-800 text-sm"
                    title="删除参数"
                  >
                    删除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 分页组件 -->
      <div class="p-4 border-t">
        <Pagination
          :current-page="currentPage"
          :total-pages="totalPages"
          :total-elements="totalElements"
          @page-change="onPageChange"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-12">
      <div class="text-gray-500 text-lg">暂无参数数据</div>
      <div class="text-gray-400 text-sm mt-2">
        请选择测试的数据库
      </div>
    </div>

    <!-- 消息提示 -->
    <div 
      v-if="message.text" 
      :class="messageClass"
      class="fixed top-4 right-4 px-4 py-2 rounded shadow-lg z-50"
    >
      {{ message.text }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { parameterApi } from '../api/parameterApi'
import { databaseConfigApi } from '../api/databaseConfigApi'
import ParameterEditor from './ParameterEditor.vue'
import Pagination from './Pagination.vue'
import type { 
  ParameterItem, 
  DatabaseConfig, 
  ConnectionTestResult,
  ImportResult,
  UpdateParameterRequest
} from '../types'

// 响应式数据
const parameters = ref<ParameterItem[]>([])
const dbConfigs = ref<DatabaseConfig[]>([])
const categories = ref<string[]>([])
const valueRanges = ref<string[]>([])

// 状态栏相关数据
const currentTestDb = ref({
  name: '',
  version: ''
})
const currentTestStatus = ref({
  text: '无',
  color: 'text-gray-500'
})
const currentTestTime = ref('无')
const currentParamCombo = ref('无')
const currentCoverage = ref('0%')
const currentBugCount = ref('0')

const selectedDbConfig = ref<number | ''>('')
const selectedDatabase = ref('')
const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedValueRange = ref('')
const selectedConstraintType = ref('')
const selectedTestStatus = ref('')
const hoveredParam = ref<number | null>(null)

// 快速筛选选项
const quickFilters = ref([
  { key: 'innodb', label: 'InnoDB参数', category: 'INNODB' },
  { key: 'memory', label: '内存相关', category: 'MEMORY' },
  { key: 'boolean', label: '布尔类型', paramType: 'BOOLEAN' },
  { key: 'with-candidates', label: '有候选值', constraintType: 'candidates' },
  { key: 'global', label: '全局设置', valueRange: 'Global' },
  { key: 'test-enabled', label: '启用测试', testStatus: true }
])

const activeQuickFilter = ref<string | null>(null)



const currentPage = ref(0)
const pageSize = ref(20)
const totalPages = ref(0)
const totalElements = ref(0)

const loading = ref(false)
const testing = ref(false)
const importing = ref(false)
const resetting = ref(false)
const saving = ref(false)
const updatingWeights = ref<Set<number>>(new Set())

// 记录首次从后端加载时的"默认状态"，用于一键恢复
const originalParameterSnapshot = ref<Record<number, { defaultValue: string; isTestDefault: boolean; weight: number }>>({})



const message = ref({ text: '', type: 'info' as 'success' | 'error' | 'info' })

// 计算属性

const messageClass = computed(() => {
  const baseClass = 'transition-all duration-300'
  switch (message.value.type) {
    case 'success':
      return `${baseClass} bg-green-100 text-green-800 border border-green-200`
    case 'error':
      return `${baseClass} bg-red-100 text-red-800 border border-red-200`
    default:
      return `${baseClass} bg-blue-100 text-blue-800 border border-blue-200`
  }
})

// 防抖搜索
let searchTimeout: number | null = null
const debouncedSearch = () => {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    currentPage.value = 0
    loadParameters()
  }, 300)
}

// 高级搜索功能
const performAdvancedSearch = (params: ParameterItem[], searchTerm: string): ParameterItem[] => {
  if (!searchTerm.trim()) return params
  
  const term = searchTerm.toLowerCase().trim()
  
  return params.filter(param => {
    // 基本字段搜索
    const basicMatch = 
      param.paramName.toLowerCase().includes(term) ||
      (param.description && param.description.toLowerCase().includes(term)) ||
      param.category.toLowerCase().includes(term) ||
      (param.defaultValue && param.defaultValue.toLowerCase().includes(term))
    
    // 候选值搜索
    const candidateMatch = (param.candidateValues || param.allowedValues || [])
      .some(value => value.toLowerCase().includes(term))
    
    // 约束信息搜索
    const constraintMatch = 
      (param.minValue && param.minValue.includes(term)) ||
      (param.maxValue && param.maxValue.includes(term)) ||
      (param.valueRange && param.valueRange.toLowerCase().includes(term))
    
    return basicMatch || candidateMatch || constraintMatch
  })
}

// 搜索建议功能
const searchSuggestions = ref<string[]>([])
const showSuggestions = ref(false)

const generateSearchSuggestions = (term: string) => {
  if (!term.trim() || term.length < 2) {
    searchSuggestions.value = []
    showSuggestions.value = false
    return
  }
  
  const suggestions = new Set<string>()
  const lowerTerm = term.toLowerCase()
  
  parameters.value.forEach(param => {
    // 参数名建议
    if (param.paramName.toLowerCase().includes(lowerTerm)) {
      suggestions.add(param.paramName)
    }
    
    // 类别建议
    if (param.category.toLowerCase().includes(lowerTerm)) {
      suggestions.add(param.category)
    }
    
    // 候选值建议
    const candidates = param.candidateValues || param.allowedValues || []
    candidates.forEach(value => {
      if (value.toLowerCase().includes(lowerTerm)) {
        suggestions.add(value)
      }
    })
  })
  
  searchSuggestions.value = Array.from(suggestions).slice(0, 8)
  showSuggestions.value = suggestions.size > 0
}

const selectSuggestion = (suggestion: string) => {
  searchKeyword.value = suggestion
  showSuggestions.value = false
  debouncedSearch()
}

const onSearchInput = () => {
  generateSearchSuggestions(searchKeyword.value)
  debouncedSearch()
}

const hideSuggestions = () => {
  // 延迟隐藏，允许点击建议项
  setTimeout(() => {
    showSuggestions.value = false
  }, 200)
}

// 显示消息
const showMessage = (text: string, type: 'success' | 'error' | 'info' = 'info') => {
  message.value = { text, type }
  setTimeout(() => {
    message.value.text = ''
  }, 3000)
}

// 加载数据库配置
const loadDatabaseConfigs = async () => {
  try {
    dbConfigs.value = await databaseConfigApi.getDatabaseConfigs()
  } catch (error) {
    console.error('加载数据库配置失败:', error)
    showMessage('加载数据库配置失败', 'error')
  }
}

// 加载参数类别
const loadCategories = async () => {
  try {
    categories.value = await parameterApi.getCategories()
  } catch (error) {
    console.error('加载参数类别失败:', error)
  }
}

// 加载设置范围
const loadValueRanges = async () => {
  try {
    valueRanges.value = await parameterApi.getValueRanges()
  } catch (error) {
    console.error('加载设置范围失败:', error)
  }
}

// 保存一份参数"默认状态"快照（仅首次记录每个参数的初始值）
const takeParameterSnapshot = (list: ParameterItem[]) => {
  const snapshot = { ...originalParameterSnapshot.value }
  list.forEach((p) => {
    if (!snapshot[p.id]) {
      snapshot[p.id] = {
        defaultValue: p.defaultValue,
        isTestDefault: p.isTestDefault,
        weight: p.weight || 5.0,
      }
    }
  })
  originalParameterSnapshot.value = snapshot
}

// 加载参数列表
const loadParameters = async () => {
  loading.value = true
  try {
    // 使用增强API支持valueRange筛选
    const response = await parameterApi.getEnhancedParameters({
      page: currentPage.value,
      size: pageSize.value,
      search: searchKeyword.value || undefined,
      category: selectedCategory.value || undefined,
      valueRange: selectedValueRange.value || undefined,
      // 暂时不传递testStatus参数，后端暂不支持
    })
    
    // 每次从后端获取最新数据后，更新"默认状态"快照
    takeParameterSnapshot(response.content)
    
    // 确保每个参数都有权重，如果为null或undefined，设置默认权重5.0
    response.content.forEach(param => {
      if (param.weight === null || param.weight === undefined) {
        param.weight = 5.0
      }
    })
    
    // 注意：不再进行前端筛选和排序
    // 后端已经按权重全局排序，前端筛选会破坏全局排序的一致性
    // 如果需要约束类型和测试状态筛选，应该在后端实现
    // 目前只使用后端返回的数据，保证全局排序正确
    
    parameters.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
    
  } catch (error) {
    console.error('加载参数失败:', error)
    showMessage('加载参数失败', 'error')
  } finally {
    loading.value = false
  }
}



// 保存当前参数为一个新方案


// 打开/关闭方案列表弹窗



// 保存配置到数据库
const saveConfiguration = async () => {
  console.log('saveConfiguration函数被调用')
  console.log('parameters.value长度:', parameters.value.length)
  console.log('parameters.value内容:', parameters.value)
  if (!parameters.value.length) {
    showMessage('当前没有可保存的参数', 'info')
    return
  }

  // 构造批量更新请求
  const requests: UpdateParameterRequest[] = parameters.value
    .map((param) => {
      return {
        id: param.id,
        defaultValue: param.defaultValue,
        isTestDefault: param.isTestDefault,
        weight: param.weight,
      } as UpdateParameterRequest
    })

  saving.value = true
  try {
    console.log('保存配置请求数据:', requests)
    console.log('调用batchUpdateParameters前')
    const result = await parameterApi.batchUpdateParameters(requests)
    console.log('保存配置成功结果:', result)
    showMessage('保存配置成功', 'success')
    // 重新加载，确保与后端完全一致
    await loadParameters()
  } catch (error) {
    console.error('保存配置失败:', error)
    if (error instanceof Error) {
      console.error('错误详情:', error.message)
      console.error('错误堆栈:', error.stack)
    } else {
      console.error('未知错误类型:', typeof error, error)
    }
    showMessage(`保存配置失败: ${error instanceof Error ? error.message : '网络连接错误'}`, 'error')
  } finally {
    console.log('保存配置操作完成')
    saving.value = false
  }
}

// 一键将当前所有参数恢复到“默认状态”
const resetAllParametersToDefault = async () => {
  if (!parameters.value.length) {
    showMessage('当前没有可重置的参数', 'info')
    return
  }

  const snapshot = originalParameterSnapshot.value
  if (!snapshot || Object.keys(snapshot).length === 0) {
    showMessage('暂无默认状态快照，无法重置', 'info')
    return
  }

  // 构造批量更新请求
  const requests: UpdateParameterRequest[] = parameters.value
    .map((param) => {
      const snap = snapshot[param.id]
      if (!snap) return null
      return {
        id: param.id,
        defaultValue: snap.defaultValue,
        isTestDefault: snap.isTestDefault,
        weight: snap.weight,
      } as UpdateParameterRequest
    })
    .filter((item): item is UpdateParameterRequest => item !== null)

  if (!requests.length) {
    showMessage('没有需要重置的参数', 'info')
    return
  }

  resetting.value = true
  try {
    await parameterApi.batchUpdateParameters(requests)

    // 本地同步为快照中的默认值
    parameters.value.forEach((param) => {
      const snap = snapshot[param.id]
      if (snap) {
        param.defaultValue = snap.defaultValue
        param.isTestDefault = snap.isTestDefault
        param.weight = snap.weight
      }
    })

    showMessage('参数已恢复为默认状态', 'success')
    // 重新加载，确保与后端完全一致
    await loadParameters()
  } catch (error) {
    console.error('重置参数失败:', error)
    showMessage('重置参数失败', 'error')
  } finally {
    resetting.value = false
  }
}

// 数据库选择变化
const onDatabaseChange = async () => {
    // 初始化变量
    let dbName = '';
    let dbVersion = '';
    
    if (!selectedDatabase.value) {
        parameters.value = []
        currentTestDb.value = { name: '', version: '' }
        currentTestStatus.value = { text: '无', color: 'text-gray-500' }
        
        // 清空本地存储
        localStorage.setItem('selectedDb', JSON.stringify({ name: '未选择', version: '' }))
        
        // 触发全局事件，同步到顶部状态栏
        window.dispatchEvent(new CustomEvent('dbChanged', {
            detail: {
                name: '未选择',
                version: ''
            }
        }));
        
        return
    }

    // 解析数据库类型和版本
    const [dbType, version] = selectedDatabase.value.split('-')
    dbName = getDatabaseDisplayName(dbType);
    dbVersion = version || '';
    
    // 更新页面内状态栏
    currentTestDb.value = {
        name: dbName,
        version: dbVersion
    };
    
    // 保存到本地存储
    localStorage.setItem('selectedDb', JSON.stringify({
        name: currentTestDb.value.name,
        version: currentTestDb.value.version
    }));
    
    // 触发全局事件，同步到顶部状态栏
    window.dispatchEvent(new CustomEvent('dbChanged', {
        detail: {
            name: dbName,
            version: dbVersion
        }
    }));
    
    // 自动加载参数
    importing.value = true;
    try {
        // 检查是否支持该数据库的参数数据
        if (!isDatabaseSupported(dbType, dbVersion)) {
            // 对于不支持的数据库，显示暂无参数数据
            parameters.value = [];
            currentTestStatus.value = {
                text: '无参数数据',
                color: 'text-orange-600'
            };
            showMessage(`暂无 ${dbName} ${dbVersion} 的参数数据`, 'info');
            return;
        }

        // 对于支持的数据库，加载参数数据
        await loadParameters();
        currentTestStatus.value = {
            text: '测试中',
            color: 'text-blue-600'
        };
        showMessage(`已加载 ${dbName} ${dbVersion} 的参数列表`, 'success');
    } catch (error) {
        console.error('加载参数失败:', error);
        showMessage('加载参数失败', 'error');
        currentTestStatus.value = {
            text: '加载失败',
            color: 'text-red-600'
        };
    } finally {
        importing.value = false;
    }
};

// 获取数据库显示名称（标准化格式）
const getDatabaseDisplayName = (dbType: string): string => {
    const nameMap: Record<string, string> = {
        'mysql': 'MySQL',
        'postgresql': 'PostgreSQL',
        'mariadb': 'MariaDB',
        'oceanbase': 'OceanBase',
        'tidb': 'TiDB'
    };
    return nameMap[dbType] || dbType;
};

// 检查是否支持参数数据（目前只有MySQL 9.5.0有参数数据）
const isDatabaseSupported = (dbType: string, version: string): boolean => {
    return dbType === 'mysql' && version === '9.5.0';
};

// 初始化状态栏（模拟从后端获取测试状态）
const initializeStatusBar = () => {
  // 这里应该从后端API获取当前测试状态
  // 暂时使用默认值
  currentTestStatus.value = {
    text: '无',
    color: 'text-gray-500'
  }
  currentTestTime.value = '无'
  currentParamCombo.value = '无'
  currentCoverage.value = '0%'
  currentBugCount.value = '0'
}

// 数据库配置变化（保留原有逻辑）
const onDbConfigChange = () => {
  // 可以在这里添加额外的逻辑
}

// 测试数据库连接
const testConnection = async () => {
  if (!selectedDbConfig.value) return
  
  testing.value = true
  try {
    const result: ConnectionTestResult = await databaseConfigApi.testConnection(selectedDbConfig.value as number)
    
    if (result.success) {
      showMessage(`连接成功！数据库版本: ${result.dbVersion}`, 'success')
    } else {
      showMessage(`连接失败: ${result.message}`, 'error')
    }
  } catch (error) {
    console.error('测试连接失败:', error)
    showMessage('测试连接失败', 'error')
  } finally {
    testing.value = false
  }
}



// 更新参数值
const updateParameterValue = async (param: ParameterItem, value: string) => {
  try {
    await parameterApi.updateParameter(param.id, {
      defaultValue: value
    })
    
    // 更新本地数据
    param.defaultValue = value
    showMessage('默认值编辑成功', 'success')
    
  } catch (error) {
    console.error('更新参数失败:', error)
    showMessage('更新参数失败', 'error')
  }
}

// 更新参数权重
const updateParameterWeight = async (param: ParameterItem) => {
  // 验证权重范围
  if (param.weight === undefined || param.weight === null) {
    param.weight = 5.0 // 设置默认权重
  }
  
  const weightValue = param.weight
  
  if (weightValue < 0 || weightValue > 10) {
    showMessage('权重必须在0-10之间', 'error')
    param.weight = 5.0 // 恢复为默认值
    return
  }

  updatingWeights.value.add(param.id)
  const originalWeight = param.weight
  
  try {
    await parameterApi.updateParameterWeight(param.id, weightValue)
    showMessage('权重更新成功', 'success')
    // 更新本地权重值
    param.weight = weightValue
    // 重新加载参数列表，让后端按新的权重排序（保证跨页排序一致性）
    await loadParameters()
  } catch (error) {
    console.error('更新权重失败:', error)
    showMessage('更新权重失败', 'error')
    // 回滚权重
    param.weight = originalWeight
  } finally {
    updatingWeights.value.delete(param.id)
  }
}

// 更新参数测试状态
const updateParameterTestStatus = async (param: ParameterItem) => {
  try {
    await parameterApi.updateParameter(param.id, {
      isTestDefault: param.isTestDefault
    })
    
    showMessage('测试状态更新成功', 'success')
    
  } catch (error) {
    console.error('更新测试状态失败:', error)
    showMessage('更新测试状态失败', 'error')
    // 回滚状态
    param.isTestDefault = !param.isTestDefault
  }
}



// 删除参数
const deleteParameter = async (param: ParameterItem) => {
  if (!confirm(`确定要删除参数 "${param.paramName}" 吗？`)) {
    return
  }
  
  try {
    await parameterApi.deleteParameter(param.id)
    showMessage('参数删除成功', 'success')
    await loadParameters()
  } catch (error) {
    console.error('删除参数失败:', error)
    showMessage('删除参数失败', 'error')
  }
}

// 清空筛选
const clearFilters = () => {
  searchKeyword.value = ''
  selectedCategory.value = ''
  selectedValueRange.value = ''
  selectedConstraintType.value = ''
  selectedTestStatus.value = ''
  activeQuickFilter.value = null
  showSuggestions.value = false
  searchSuggestions.value = []
  currentPage.value = 0
  loadParameters()
}

// 工具方法
const truncateText = (text: string | undefined, maxLength: number): string => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const hasCandidateValues = (param: ParameterItem): boolean => {
  const candidates = param.candidateValues || param.allowedValues || []
  return candidates.length > 0
}

const hasRangeConstraint = (param: ParameterItem): boolean => {
  return !!(param.minValue || param.maxValue)
}

const getCandidateValuesPreview = (param: ParameterItem): string => {
  const candidates = param.candidateValues || param.allowedValues || []
  if (candidates.length <= 3) {
    return `候选值: ${candidates.join(', ')}`
  }
  return `候选值 (${candidates.length} 个): ${candidates.slice(0, 3).join(', ')}...`
}

const getRangeConstraintPreview = (param: ParameterItem): string => {
  const { minValue, maxValue } = param
  if (minValue && maxValue) {
    return `范围: ${minValue} - ${maxValue}`
  } else if (minValue) {
    return `范围: ≥ ${minValue}`
  } else if (maxValue) {
    return `范围: ≤ ${maxValue}`
  }
  return '有范围限制'
}

const getValueRangeClass = (valueRange: string): string => {
  const classMap: Record<string, string> = {
    'Global': 'bg-purple-100 text-purple-800',
    'Session': 'bg-green-100 text-green-800',
    'Both': 'bg-yellow-100 text-yellow-800'
  }
  return classMap[valueRange] || 'bg-gray-100 text-gray-800'
}

const getValueRangeTooltip = (valueRange: string): string => {
  const tooltipMap: Record<string, string> = {
    'Global': '全局设置，影响整个MySQL服务器',
    'Session': '会话设置，仅影响当前连接',
    'Both': '可以设置为全局或会话级别'
  }
  return tooltipMap[valueRange] || valueRange
}

const filterByConstraintType = (params: ParameterItem[], constraintType: string): ParameterItem[] => {
  switch (constraintType) {
    case 'candidates':
      return params.filter(param => hasCandidateValues(param))
    case 'range':
      return params.filter(param => hasRangeConstraint(param))
    case 'both':
      return params.filter(param => hasCandidateValues(param) || hasRangeConstraint(param))
    case 'none':
      return params.filter(param => !hasCandidateValues(param) && !hasRangeConstraint(param))
    default:
      return params
  }
}

const showParameterDetails = (param: ParameterItem) => {
  // 这里可以打开一个详情弹窗或导航到详情页面
  console.log('显示参数详情:', param)
  // 临时实现：显示alert
  const details = [
    `参数名: ${param.paramName}`,
    `类型: ${param.paramType}`,
    `类别: ${param.category}`,
    `设置范围: ${param.valueRange || '未知'}`,
    `默认值: ${param.defaultValue || '未设置'}`,
    `描述: ${param.description || '无描述'}`
  ]
  
  if (hasCandidateValues(param)) {
    const candidates = param.candidateValues || param.allowedValues || []
    details.push(`候选值: ${candidates.join(', ')}`)
  }
  
  if (hasRangeConstraint(param)) {
    details.push(`范围约束: ${getRangeConstraintPreview(param)}`)
  }
  
  alert(details.join('\n'))
}

// 快速筛选相关方法
const applyQuickFilter = (filter: any) => {
  // 如果点击的是当前激活的筛选，则取消筛选
  if (activeQuickFilter.value === filter.key) {
    clearFilters()
    activeQuickFilter.value = null
    return
  }
  
  // 清空其他筛选条件
  searchKeyword.value = ''
  selectedCategory.value = ''
  selectedValueRange.value = ''
  selectedConstraintType.value = ''
  selectedTestStatus.value = ''
  
  // 应用快速筛选
  activeQuickFilter.value = filter.key
  
  if (filter.category) {
    selectedCategory.value = filter.category
  }
  if (filter.valueRange) {
    selectedValueRange.value = filter.valueRange
  }
  if (filter.constraintType) {
    selectedConstraintType.value = filter.constraintType
  }
  if (filter.testStatus !== undefined) {
    selectedTestStatus.value = filter.testStatus.toString()
  }
  if (filter.paramType) {
    // 对于参数类型筛选，我们需要在客户端进行筛选
    // 这里可以扩展后端API支持参数类型筛选
  }
  
  currentPage.value = 0
  loadParameters()
}

const isQuickFilterActive = (filter: any): boolean => {
  return activeQuickFilter.value === filter.key
}

// 统计方法
const getCategoryCount = (category: string): number => {
  return parameters.value.filter(p => p.category === category).length
}

const getValueRangeCount = (range: string): number => {
  return parameters.value.filter(p => p.valueRange === range).length
}

const getConstraintTypeCount = (type: string): number => {
  return filterByConstraintType(parameters.value, type).length
}

const getTestStatusCount = (status: boolean): number => {
  return parameters.value.filter(p => p.isTestDefault === status).length
}









const formatDateTime = (dateTimeStr: string): string => {
  try {
    const date = new Date(dateTimeStr)
    return date.toLocaleString('zh-CN')
  } catch (error) {
    return dateTimeStr
  }
}

// 页面变化
const onPageChange = (page: number) => {
  currentPage.value = page
  // 新增：清空搜索建议，避免分页时弹窗干扰
  showSuggestions.value = false
  searchSuggestions.value = []
  // 原有逻辑不变
  loadParameters()
}

// 组件挂载
onMounted(async () => {
  // 初始化状态栏
  initializeStatusBar()

  await Promise.all([
    loadDatabaseConfigs(),
    loadCategories(),
    loadValueRanges(),
    loadParameters()
  ])
})
</script>