<template>
  <div class="global-status-bar">
    <!-- 左侧状态信息：滚动容器（内容多了横向滚动，不撑高状态栏） -->
    <div class="status-info-scroll">
      <div class="status-info">
        <!-- 测试状态标签 -->
        <span class="status-tag" :class="status.task_status === '测试中' ? 'testing' : status.task_status === '已结束' ? 'ended' : ''">
          测试状态：{{ status.task_status }}
        </span>
        <!-- 数据库信息 -->
        <span class="status-tag">当前数据库：{{ dbInfo.name }}（v{{ dbInfo.version }}）</span>
        <span class="status-tag">运行时间：{{ status.run_time }}</span>
        <span class="status-tag highlight">正在测试的参数组合：{{ status.current_param_combo }}</span>
        <span class="status-tag">覆盖率：{{ status.coverage_rate }}%</span>
        <span class="status-tag danger">发现bug数量：{{ status.bug_count }}</span>
        <span class="status-tag">执行次数：{{ status.execution_count }}</span>
      </div>
    </div>
    <!-- 右侧按钮（固定位置） -->
    <div class="status-buttons">
      <button class="btn start-btn" @click="startTest" :disabled="isTesting">开始测试</button>
      <button class="btn stop-btn" @click="stopTest" :disabled="!isTesting">结束测试</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

// 核心状态（强制0/无）
const status = ref({
  run_time: '0秒',
  current_param_combo: '无',
  coverage_rate: '0.00',
  bug_count: '0',
  execution_count: '0',
  task_status: '未开始'
})
const isTesting = ref(false)
let stompClient: Client | null = null

// 数据库信息
const dbInfo = ref({
  name: 'MySQL',
  version: '8.0.44'
})

// 读取本地数据库配置
const loadDbConfig = () => {
  const savedDb = localStorage.getItem('selectedDb')
  if (savedDb) {
    const dbData = JSON.parse(savedDb)
    dbInfo.value.name = dbData.name || 'MySQL'
    dbInfo.value.version = dbData.version || '8.0.44'
  }
  setTimeout(loadDbConfig, 1000)
}

// 初始化WebSocket
const initWebSocket = () => {
  const socket = new SockJS('http://localhost:8080/api/ws-test')
  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      stompClient?.subscribe('/topic/testStatus', (msg) => {
        const newData = JSON.parse(msg.body)
        status.value = {
          run_time: `${newData.run_time || 0}秒`,
          current_param_combo: newData.current_param_combo || '无',
          coverage_rate: newData.coverage_rate || '0.00',
          bug_count: newData.bug_count || '0',
          execution_count: newData.execution_count || '0',
          task_status: newData.task_status || '未开始'
        }
        isTesting.value = newData.task_status === '测试中'
      })
    },
    onStompError: (frame) => {
      console.error('WebSocket 错误:', frame.headers['message'])
    }
  })
  stompClient.activate()
}

// 开始测试
const startTest = async () => {
  try {
    await axios.post('http://localhost:8080/api/test/start')
    alert('测试启动成功！')
  } catch (err: any) {
    alert('启动失败：' + (err.response?.data?.message || err.message))
  }
}

// 结束测试
const stopTest = async () => {
  try {
    await axios.post('http://localhost:8080/api/test/stop')
    status.value = {
      run_time: '0秒',
      current_param_combo: '无',
      coverage_rate: '0.00',
      bug_count: '0',
      execution_count: '0',
      task_status: '已结束'
    }
    isTesting.value = false
    alert('测试已结束！')
  } catch (err: any) {
    alert('终止失败：' + (err.response?.data?.message || err.message))
  }
}

// 生命周期
onMounted(() => {
  initWebSocket()
  loadDbConfig()
  window.addEventListener('dbChanged', loadDbConfig)
})

onUnmounted(() => {
  stompClient?.deactivate()
  window.removeEventListener('dbChanged', loadDbConfig)
})
</script>

<style scoped>
/* 状态栏：固定高度+增强对比度（醒目但自然） */
.global-status-bar {
  display: flex;
  align-items: center;
  padding: 0 1.5rem;
  background: #f8fafc; /* 浅灰背景（比纯白深一点，更醒目） */
  border-bottom: 1px solid #cbd5e1; /* 稍深的边框，增强区分度 */
  color: #1e293b; /* 深灰文字（提高对比度） */
  font-size: 1rem; /* 放大字体 */
  box-shadow: 0 2px 4px rgba(0,0,0,0.08);
  position: sticky;
  top: 0;
  z-index: 999;
  height: 64px; /* 适当增高，更醒目 */
  overflow: hidden;
}

/* 状态信息：横向滚动容器 */
.status-info-scroll {
  flex: 1;
  overflow-x: auto;
  white-space: nowrap;
  padding: 14px 0;
}
.status-info {
  display: inline-flex;
  gap: 1rem; /* 适度放大间距 */
  align-items: center;
}

/* 单个标签：增强对比度+尺寸 */
.status-tag {
  padding: 0.5rem 1rem; /* 放大内边距 */
  border-radius: 8px;
  background: #e2e8f0; /* 稍深的浅灰，更醒目 */
  font-weight: 500; /* 加粗文字 */
  transition: all 0.2s;
}
.status-tag:hover {
  background: #cbd5e1;
}

/* 高亮标签（参数组合） */
.highlight {
  background: #bfdbfe; /* 稍深的浅蓝，更醒目 */
  color: #1e40af;
  font-weight: 600;
}

/* 危险标签（bug） */
.danger {
  background: #fecdd3; /* 稍深的浅红，更醒目 */
  color: #991b1b;
  font-weight: 600;
}

/* 测试状态标签 */
.testing {
  background: #bbf7d0; /* 稍深的浅绿，更醒目 */
  color: #065f46;
  font-weight: 600;
}
.ended {
  background: #e9d5ff; /* 稍深的浅紫，更醒目 */
  color: #5b21b6;
  font-weight: 600;
}

/* 按钮区域：放大按钮+增强对比度 */
.status-buttons {
  display: flex;
  gap: 1rem;
  margin-left: 1.2rem;
}
.btn {
  padding: 0.5rem 1.2rem; /* 放大按钮 */
  border-radius: 8px;
  border: none;
  font-weight: 600; /* 加粗按钮文字 */
  cursor: pointer;
  font-size: 1rem; /* 放大按钮字体 */
  transition: all 0.2s;
}
.start-btn {
  background: #16a34a; /* 稍深的绿，更醒目 */
  color: #ffffff;
}
.start-btn:hover {
  background: #15803d;
}
.stop-btn {
  background: #dc2626; /* 稍深的红，更醒目 */
  color: #ffffff;
}
.stop-btn:hover {
  background: #b91c1c;
}
/* 禁用按钮 */
.start-btn:disabled, .stop-btn:disabled {
  background: #94a3b8;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>