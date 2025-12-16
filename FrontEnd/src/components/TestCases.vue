<template>
  <div class="bg-white rounded-lg shadow-md overflow-hidden p-6 border-2 border-gray-300">
    <h2 class="text-xl font-bold mb-6">测试用例</h2>
    
    <!-- 筛选器 -->
    <div class="mb-6 p-4 bg-gray-50 rounded-lg">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- 参数1筛选 -->
        <div>
          <label class="block text-sm font-medium mb-2">参数1</label>
          <div class="relative">
            <input 
              type="text" 
              v-model="filter.param1" 
              @input="onFilterChange" 
              placeholder="输入参数名或值" 
              class="w-full p-2 border border-gray-300 rounded pl-3 pr-10"
            >
            <button 
              v-if="filter.param1" 
              @click="filter.param1 = ''; onFilterChange()" 
              class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              type="button"
            >
              <i class="fa fa-times"></i>
            </button>
          </div>
        </div>
        
        <!-- 参数2筛选 -->
        <div>
          <label class="block text-sm font-medium mb-2">参数2</label>
          <div class="relative">
            <input 
              type="text" 
              v-model="filter.param2" 
              @input="onFilterChange" 
              placeholder="输入参数名或值" 
              class="w-full p-2 border border-gray-300 rounded pl-3 pr-10"
            >
            <button 
              v-if="filter.param2" 
              @click="filter.param2 = ''; onFilterChange()" 
              class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              type="button"
            >
              <i class="fa fa-times"></i>
            </button>
          </div>
        </div>
        
        <!-- 是否触发漏洞筛选 -->
        <div>
          <label class="block text-sm font-medium mb-2">是否触发漏洞</label>
          <select v-model="filter.triggeredBug" @change="onFilterChange" class="w-full p-2 border border-gray-300 rounded">
            <option value="">所有</option>
            <option value="true">是</option>
            <option value="false">否</option>
          </select>
        </div>
        
        <!-- 搜索按钮 -->
        <div class="flex items-end">
          <button 
            @click="onFilterChange" 
            class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-all"
          >
            <i class="fa fa-search mr-1"></i> 搜索
          </button>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mb-4"></div>
      <div class="text-gray-600">加载测试用例数据中...</div>
    </div>
    
    <!-- 测试用例列表 -->
    <div v-else-if="testCases.length > 0" class="overflow-x-auto">
      <table class="min-w-full border-2 border-gray-300">
        <thead>
          <tr class="bg-gray-100">
            <th class="py-2 px-4 border border-gray-300 text-left">测试时间</th>
            <th class="py-2 px-4 border border-gray-300 text-left">Oracle类型</th>
            <th class="py-2 px-4 border border-gray-300 text-left">参数组合</th>
            <th class="py-2 px-4 border border-gray-300 text-left">权重值</th>
            <th class="py-2 px-4 border border-gray-300 text-left">SQL语句</th>
            <th class="py-2 px-4 border border-gray-300 text-left">是否触发漏洞</th>
            <th class="py-2 px-4 border border-gray-300 text-left">Bug ID</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(testCase, index) in testCases" :key="testCase.caseId" :class="index%2===0 ? 'bg-white' : 'bg-gray-50'">
            <td class="py-2 px-4 border border-gray-300 text-sm">{{ testCase.testTime }}</td>
            <td class="py-2 px-4 border border-gray-300">{{ testCase.oracleType }}</td>
            <td class="py-2 px-4 border border-gray-300 max-w-xs overflow-hidden text-ellipsis whitespace-nowrap" :title="testCase.paramCombo">
              {{ testCase.paramCombo }}
            </td>
            <td class="py-2 px-4 border border-gray-300">{{ testCase.weightValue.toFixed(4) }}</td>
            <td class="py-2 px-4 border border-gray-300">
              <button 
                @click="showSqlDialog(testCase.sqlStatement)" 
                class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 border border-blue-600 text-sm transition-all flex items-center"
              >
                <i class="fa fa-eye mr-1"></i> 显示代码
              </button>
            </td>
            <td class="py-2 px-4 border border-gray-300">
              <span :class="testCase.triggeredBug ? 'text-red-600 font-medium' : 'text-green-600 font-medium'">
                {{ testCase.triggeredBug ? '是' : '否' }}
              </span>
            </td>
            <td class="py-2 px-4 border border-gray-300">{{ testCase.bugId || '-' }}</td>
          </tr>
        </tbody>
      </table>
      
      <!-- 分页 -->
      <div class="mt-4">
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
      <div class="text-gray-500 text-lg">暂无测试用例数据</div>
      <div class="text-gray-400 text-sm mt-2">
        运行模糊测试后将显示测试用例信息
      </div>
    </div>
  </div>

  <!-- SQL语句弹窗 -->
  <div v-if="showSql" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-lg shadow-lg max-w-4xl w-full max-h-[80vh] overflow-auto">
      <div class="p-4 border-b flex items-center justify-between">
        <h3 class="font-bold text-lg text-gray-800">SQL语句详情</h3>
        <button @click="showSql = false" class="text-gray-500 hover:text-gray-700 text-xl">×</button>
      </div>
      <div class="p-4">
        <pre class="whitespace-pre-wrap text-sm bg-gray-50 p-4 rounded border border-gray-200 overflow-auto max-h-[60vh]">{{ currentSql }}</pre>
      </div>
      <div class="p-4 border-t text-right">
        <button @click="showSql = false" class="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700 transition-colors">
          关闭
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { testCaseApi, TestCase } from '../api/testCaseApi'
import Pagination from './Pagination.vue'

// 筛选条件
const filter = ref({
  triggeredBug: '',
  param1: '',
  param2: ''
})

// SQL弹窗控制
const showSql = ref(false)
const currentSql = ref('')

// 显示SQL语句弹窗
const showSqlDialog = (sql: string) => {
  currentSql.value = sql
  showSql.value = true
}

// 测试用例数据
const testCases = ref<TestCase[]>([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(20)
const totalPages = ref(0)
const totalElements = ref(0)

// 获取测试用例数据
const fetchTestCases = async (page: number = 0) => {
  loading.value = true
  try {
    const triggeredBug = filter.value.triggeredBug === '' ? undefined : filter.value.triggeredBug === 'true'
    const param1 = filter.value.param1 || undefined
    const param2 = filter.value.param2 || undefined
    
    // 添加时间倒序排序参数
    const response = await testCaseApi.getTestCases(page, pageSize.value, triggeredBug, param1, param2, 'testTime,desc')
    testCases.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
    currentPage.value = page
  } catch (error) {
    console.error('获取测试用例失败:', error)
  } finally {
    loading.value = false
  }
}

// 筛选条件变化
const onFilterChange = () => {
  fetchTestCases(0)
}

// 分页变化
const onPageChange = (page: number) => {
  fetchTestCases(page)
}

// 刷新测试用例
const refreshTestCases = () => {
  fetchTestCases(currentPage.value)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchTestCases()
})

// 暴露方法给父组件
defineExpose({
  refreshTestCases
})
</script>

<style scoped>
/* 可以在这里添加组件特定的样式 */
</style>
