<template>
  <div class="flex justify-center items-center space-x-2 mt-4">
    <!-- 上一页按钮 -->
    <button 
      @click="goToPage(currentPage - 1)"
      :disabled="currentPage <= 0"
      class="px-3 py-1 border border-gray-300 rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      上一页
    </button>
    
    <!-- 页码按钮 -->
    <template v-for="page in visiblePages" :key="page">
      <button
        v-if="page !== '...'"
        @click="goToPage(typeof page === 'number' ? page - 1 : 0)"
        :class="pageButtonClass(typeof page === 'number' ? page - 1 : 0)"
        class="px-3 py-1 border rounded min-w-[2.5rem]"
      >
        {{ page }}
      </button>
      <span v-else class="px-2 py-1 text-gray-500">...</span>
    </template>
    
    <!-- 下一页按钮 -->
    <button 
      @click="goToPage(currentPage + 1)"
      :disabled="currentPage >= totalPages - 1"
      class="px-3 py-1 border border-gray-300 rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      下一页
    </button>
    
    <!-- 页面信息 -->
    <div class="ml-4 text-sm text-gray-600">
      第 {{ currentPage + 1 }} 页，共 {{ totalPages }} 页，总计 {{ totalElements }} 条
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  currentPage: number
  totalPages: number
  totalElements: number
  maxVisiblePages?: number
}

interface Emits {
  (e: 'page-change', page: number): void
}

const props = withDefaults(defineProps<Props>(), {
  maxVisiblePages: 7
})

const emit = defineEmits<Emits>()

// 计算可见页码
const visiblePages = computed(() => {
  const { currentPage, totalPages, maxVisiblePages } = props
  const pages: (number | string)[] = []
  
  if (totalPages <= maxVisiblePages) {
    // 总页数不超过最大显示页数，显示所有页码
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i)
    }
  } else {
    // 总页数超过最大显示页数，需要省略
    const halfVisible = Math.floor(maxVisiblePages / 2)
    let startPage = Math.max(1, currentPage + 1 - halfVisible)
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1)
    
    // 调整起始页，确保显示足够的页码
    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1)
    }
    
    // 添加第一页和省略号
    if (startPage > 1) {
      pages.push(1)
      if (startPage > 2) {
        pages.push('...')
      }
    }
    
    // 添加中间页码
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i)
    }
    
    // 添加省略号和最后一页
    if (endPage < totalPages) {
      if (endPage < totalPages - 1) {
        pages.push('...')
      }
      pages.push(totalPages)
    }
  }
  
  return pages
})

// 页码按钮样式
const pageButtonClass = (page: number) => {
  const isActive = page === props.currentPage
  return {
    'bg-blue-600 text-white border-blue-600': isActive,
    'border-gray-300 hover:bg-gray-100': !isActive
  }
}

// 跳转到指定页
const goToPage = (page: number) => {
  if (page >= 0 && page < props.totalPages && page !== props.currentPage) {
    emit('page-change', page)
  }
}
</script>