<template>
	<div class="flex flex-col h-screen bg-slate-50">
		<!-- 顶部Header：渐变背景+精致样式 + 校徽 -->
		<header class="bg-gradient-to-r from-indigo-600 to-blue-500 text-white p-4 flex items-center justify-between shadow-lg">
			<div class="flex items-center gap-4">
				<!-- 插入校徽：尺寸控制在60px，圆角+描边，不突兀 -->
				<img 
					src="https://p3-flow-imagex-sign.byteimg.com/tos-cn-i-a9rns2rl98/37b0bf50748045f796734f85864ece84.png~tplv-a9rns2rl98-image.png?rcl=2025121521204253EE4916776B1A1F0CE5&rk3s=8e244e95&rrcfp=dafada99&x-expires=2082028843&x-signature=J7QV1J0z%2FDv2FRLZ6HVmfvpUxJw%3D" 
					alt="校徽" 
					class="w-15 h-15 rounded-full border-2 border-white shadow-md object-contain"
				/>
				<h1 class="text-2xl font-bold tracking-tight">参数敏感数据库模糊测试平台</h1>
			</div>
			<div class="text-right text-sm opacity-90">
				<div>SQLancer</div>
				<div>版本 1.0</div>
			</div>
		</header>
    
    <GlobalStatusBar />
		<div class="flex flex-1 overflow-hidden">
			<!-- 侧边栏：深色主题+hover动效 -->
			<aside class="bg-indigo-700 w-64 flex-shrink-0 shadow-md">
				<nav class="py-4 text-white">
				<ul>
					<li>
						<button 
							id="test-settings-btn" 
							class="w-full text-left p-4 transition-all duration-200" 
							:class="activePanel==='settings' ? 'bg-indigo-600 text-white font-medium' : 'text-indigo-100 hover:bg-indigo-600/50'" 
							@click="switchPanel('settings')"
						>
							<i class="fa fa-cog mr-2"></i> 测试配置
						</button>
					</li>
					<li>
						<button 
							id="status-info-btn" 
							class="w-full text-left p-4 transition-all duration-200" 
							:class="activePanel==='status' ? 'bg-indigo-600 text-white font-medium' : 'text-indigo-100 hover:bg-indigo-600/50'" 
							@click="switchPanel('status')"
						>
							<i class="fa fa-bar-chart mr-2"></i> 状态监测
						</button>
					</li>
					
					<!-- 测试结果折叠菜单 -->
					<li class="has-submenu">
						<button 
							class="w-full text-left p-4 flex justify-between items-center text-indigo-100 hover:bg-indigo-600/50 transition-all duration-200"
							@click="toggleSubmenu('testResults')"
						>
							<span><i class="fa fa-list-alt mr-2"></i> 测试结果</span>
							<i class="fa fa-chevron-up" v-if="expandedSubmenu === 'testResults'"></i>
							<i class="fa fa-chevron-down" v-else></i>
						</button>
						<ul v-show="expandedSubmenu === 'testResults'" class="pl-8 bg-indigo-800/50">
							<li>
								<button 
									id="test-cases-btn" 
									class="w-full text-left p-4 transition-all duration-200" 
									:class="activePanel==='testCases' ? 'bg-indigo-600 text-white font-medium' : 'text-indigo-100 hover:bg-indigo-600/50'" 
									@click="switchPanel('testCases')"
								>
									测试用例
								</button>
							</li>
							<li>
								<button 
									id="test-report-btn" 
									class="w-full text-left p-4 transition-all duration-200" 
									:class="activePanel==='report' ? 'bg-indigo-600 text-white font-medium' : 'text-indigo-100 hover:bg-indigo-600/50'" 
									@click="switchPanel('report')"
								>
									测试报告
								</button>
							</li>
						</ul>
					</li>
				</ul>
			</nav>
			</aside>

			<!-- 主内容区：卡片化+呼吸感间距 -->
			<main class="flex-1 overflow-auto p-6 space-y-6">
				<!-- 测试设置页面 -->
				<div v-show="activePanel==='settings'" class="space-y-6">
					<div class="bg-white rounded-xl shadow-md overflow-hidden border border-slate-100">
						<div class="p-6">
							<h2 class="text-xl font-bold mb-6 text-indigo-700">测试设置</h2>
							<div class="flex items-center justify-between border-b-2 border-indigo-100 mb-6 pb-2">
								<div>
									<button
										id="fuzz-params-tab"
										class="px-4 py-2 font-medium transition-all"
										:class="activeSubTab==='fuzz' ? 'text-indigo-700 border-b-2 border-indigo-700' : 'text-slate-500 hover:text-indigo-600'"
										@click="activeSubTab='fuzz'"
									>
										模糊测试
									</button>
									<button
										id="db-params-tab"
										class="px-4 py-2 font-medium transition-all"
										:class="activeSubTab==='db' ? 'text-indigo-700 border-b-2 border-indigo-700' : 'text-slate-500 hover:text-indigo-600'"
										@click="activeSubTab='db'"
									>
										数据库参数
									</button>
								</div>
							</div>

							<!-- 数据库参数子页面 -->
							<div v-show="activeSubTab==='db'">
								<DatabaseParameterManager />
							</div>

							<!-- 模糊测试参数子页面 -->
							<div v-show="activeSubTab==='fuzz'" class="space-y-6">
								<div>
									<!-- 模糊测试参数操作按钮 -->
									<div class="flex gap-3 mb-4 flex-wrap">
										<button
											class="py-2 px-4 bg-green-600 text-white rounded-lg hover:bg-green-700 text-sm transition-all shadow-sm"
											@click="saveConfigToDatabase"
										>
											<i class="fa fa-save mr-1"></i> 保存配置
										</button>

										<button
											class="px-4 py-2 text-sm border border-red-200 text-red-600 rounded-lg hover:bg-red-50 transition-colors"
											@click="resetFuzzParams"
										>
											<i class="fa fa-refresh mr-1"></i> 重置为默认
										</button>
									</div>
									<h3 class="text-lg font-semibold mb-4 text-indigo-700">基础参数</h3>
									<div class="grid grid-cols-1 md:grid-cols-2 gap-5">
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">测试Oracle</label>
											<select v-model="form.testOracle" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all">
												<option value="TLP">TLP (Test Language Platform)</option>
												<option value="NoREC">NoREC</option>
												<option value="PQS">PQS</option>
											</select>
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">随机种子</label>
											<input type="number" v-model.number="form.randomSeed" min="-1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">最大表达式深度</label>
											<input type="number" v-model.number="form.maxExpressionDepth" min="1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">查询数量</label>
											<input type="number" v-model.number="form.numQueries" min="1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">最大插入数量</label>
											<input type="number" v-model.number="form.maxNumInserts" min="1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">尝试次数</label>
											<input type="number" v-model.number="form.numTries" min="1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">超时时间(秒)</label>
											<input type="number" v-model.number="form.timeoutSeconds" min="-1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">最大生成数据库数</label>
											<input type="number" v-model.number="form.maxGeneratedDatabases" min="1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>

									</div>
								</div>

								<div class="border-t-2 border-indigo-100 pt-5">
									<h3 class="text-lg font-semibold mb-4 text-indigo-700">数据库登录凭据</h3>
									<div class="grid grid-cols-1 md:grid-cols-2 gap-5">
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">用户名</label>
											<input type="text" v-model="form.username" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">密码</label>
											<input type="password" v-model="form.password" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">主机</label>
											<input type="text" v-model="form.host" placeholder="localhost" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
										<div>
											<label class="block text-sm font-medium mb-2 text-slate-700">端口</label>
											<input type="number" v-model.number="form.port" min="-1" @keydown.enter="saveAsDefaultConfig" class="w-full p-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition-all" />
										</div>
									</div>
								</div>

							</div>
							
						</div>
					</div>
				</div>

				<!-- 状态监测页 -->
				<div v-show="activePanel==='status'" class="h-full w-full">
					<!-- 左右分栏 -->
					<div class="flex h-full gap-6">
						<!-- 左侧：参数组合权重表格 -->
						<div class="w-1/2 bg-white rounded-xl shadow-md p-6 border border-slate-100 overflow-hidden flex flex-col">
							<div class="flex justify-between items-center mb-4">
								<h2 class="text-lg font-bold text-indigo-700">参数组合权重</h2>
								<button 
									class="bg-indigo-600 text-white px-3 py-1 rounded-lg hover:bg-indigo-700 text-sm transition-all shadow-sm"
									@click="getParamComboWeights"
								>
									<i class="fa fa-refresh mr-1"></i> 刷新组合权重
								</button>
							</div>
							<!-- 参数组合表格 -->
							<div class="flex-1 overflow-y-auto">
								<table class="min-w-full border border-slate-200 table-fixed rounded-lg overflow-hidden">
									<thead>
										<tr class="bg-indigo-50 font-medium">
											<th class="py-3 px-4 border border-slate-200 text-left w-24 text-indigo-700">序号</th>
											<th class="py-3 px-4 border border-slate-200 text-left w-48 text-indigo-700">参数组合名</th>
											<th class="py-3 px-4 border border-slate-200 text-left w-24 text-indigo-700">权重值</th>
										</tr>
									</thead>
									<tbody>
										<tr v-for="(combo, index) in sortedParamCombos" :key="combo.id" class="hover:bg-slate-50 transition-all">
											<td class="py-2 px-4 border border-slate-200">{{ index + 1 }}</td>
											<td class="py-2 px-4 border border-slate-200">{{ combo.paramName }}</td>
											<td class="py-2 px-4 border border-slate-200">{{ combo.weightValue }}</td>
										</tr>
										<tr v-if="paramComboWeights.length === 0">
											<td colspan="3" class="py-4 px-4 border border-slate-200 text-center text-slate-500">
												暂无参数组合数据
											</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>

						<!-- 右侧：数据看板 -->
						<div class="w-1/2 flex flex-col gap-6">
							<!-- 数据库信息卡片 -->
							<div class="bg-white rounded-xl shadow-md p-6 border border-slate-100">
								<h2 class="text-lg font-bold mb-4 text-indigo-700">数据库信息</h2>
								<div class="space-y-3">
									<div class="flex"><span class="font-semibold w-24 text-slate-700">数据库：</span><span class="text-slate-800">{{ statusMonitorTestStatus.currentDatabase || '无' }}</span></div>
									<div class="flex"><span class="font-semibold w-24 text-slate-700">版本：</span><span class="text-slate-800">{{ statusMonitorTestStatus.dbVersion || '无' }}</span></div>
								</div>
							</div>

							<!-- 测试状态信息卡片 -->
							<div class="bg-white rounded-xl shadow-md p-6 border border-slate-100">
								<h2 class="text-lg font-bold mb-4 text-indigo-700">测试状态信息</h2>
								<div class="grid grid-two-cols gap-4">
									<div class="flex justify-between"><span class="font-semibold text-slate-700">测试Oracle：</span><span class="text-slate-800">{{ statusMonitorTestStatus.testOracle || '无' }}</span></div>
									<div class="flex justify-between"><span class="font-semibold text-slate-700">Bug数量：</span><span class="text-red-600 font-medium">{{ statusMonitorTestStatus.bugCount || '0' }}</span></div>
									<div class="flex justify-between"><span class="font-semibold text-slate-700">吞吐量：</span><span class="text-slate-800">{{ statusMonitorTestStatus.throughput || '0' }} qps</span></div>
									<div class="flex justify-between"><span class="font-semibold text-slate-700">测试时间：</span><span class="text-slate-800">{{ statusMonitorTestStatus.runTime || '0秒' }}</span></div>
									<div class="flex justify-between"><span class="font-semibold text-slate-700">执行次数：</span><span class="text-slate-800">{{ statusMonitorTestStatus.executionCount || '0' }}</span></div>
								</div>
							</div>

							<!-- 覆盖率曲线卡片 -->
							<div class="bg-white rounded-xl shadow-md p-6 border border-slate-100 flex-1">
								<h2 class="text-lg font-bold mb-4 text-indigo-700">覆盖率变化曲线</h2>
								<div class="h-[300px] overflow-hidden rounded-lg border border-slate-200">
									<canvas id="coverage-chart"></canvas>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- 测试报告页面 -->
				<div v-show="activePanel==='report'">
					<div class="bg-white rounded-xl shadow-md p-6 border border-slate-100">
						<div id="test-report-container">
							<h2 class="text-xl font-bold mb-6 text-center text-indigo-700">参数敏感数据库模糊测试报告-{{ currentDatabase.name }}</h2>
							
							<!-- 加载状态 -->
							<div v-if="reportLoading" class="text-center py-8">
								<div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
								<div class="mt-2 text-slate-600">加载报告数据中...</div>
							</div>
							
							<!-- 报告数据列表 -->
							<div v-else class="overflow-x-auto">
								<!-- 表格容器，确保表格居中且宽度合适 -->
								<div class="flex justify-center">
									<table class="min-w-[500px] border border-slate-200 rounded-lg overflow-hidden">
										<tbody>
											<tr class="bg-indigo-50">
												<td class="py-3 px-4 border border-slate-200 font-medium text-slate-700">测试Oracle</td>
												<td class="py-3 px-4 border border-slate-200 text-slate-800">{{ testStatus.testOracle }}</td>
											</tr>
											<tr class="bg-white">
												<td class="py-3 px-4 border border-slate-200 font-medium text-slate-700">测试运行时间</td>
												<td class="py-3 px-4 border border-slate-200 text-slate-800">{{ testStatus.testRuntime }}</td>
											</tr>
											<tr class="bg-indigo-50">
												<td class="py-3 px-4 border border-slate-200 font-medium text-slate-700">测试执行次数</td>
												<td class="py-3 px-4 border border-slate-200 text-slate-800">{{ testStatus.testExecutionCount }}</td>
											</tr>
											<tr class="bg-white">
												<td class="py-3 px-4 border border-slate-200 font-medium text-slate-700">覆盖率</td>
												<td class="py-3 px-4 border border-slate-200 text-slate-800">{{ testStatus.coverage }}%</td>
											</tr>
											<tr class="bg-indigo-50">
												<td class="py-3 px-4 border border-slate-200 font-medium text-slate-700">发现bug数量</td>
												<td class="py-3 px-4 border border-slate-200 text-red-600 font-medium">{{ testStatus.bugCount }}</td>
											</tr>
											<tr class="bg-white">
												<td class="py-3 px-4 border border-slate-200 font-medium text-slate-700">吞吐量</td>
												<td class="py-3 px-4 border border-slate-200 text-slate-800">{{ testStatus.throughput }}qps</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						
						<!-- 导出PDF按钮 -->
						<div class="mt-6 flex justify-center">
							<button class="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition-colors shadow-sm" @click="exportPdf">
								<i class="fa fa-download mr-1"></i> 导出pdf
							</button>
						</div>
					</div>
				</div>

				<!-- 测试用例页面 -->
				<div v-show="activePanel==='testCases'">
					<TestCases ref="testCasesRef" />
				</div>
			</main>
		</div>

		<!-- 代码显示折叠面板 -->
		<div id="code-collapse-panel" class="fixed bottom-0 left-0 right-0 bg-white border-t-2 border-slate-200 rounded-t-xl shadow-xl" v-show="collapseVisible">
			<div class="flex justify-between items-center p-4 cursor-pointer bg-indigo-50">
				<h3 class="text-lg font-bold text-indigo-700">{{ collapseTitle }}</h3>
				<button class="text-slate-500 hover:text-slate-700" @click.stop="collapseVisible=false">
					<i class="fa fa-times text-xl"></i>
				</button>
			</div>
			<div class="p-4 max-h-96 overflow-auto">
				<pre class="bg-slate-50 p-4 rounded-lg text-slate-800 whitespace-pre-wrap border border-slate-200">{{ collapseContent }}</pre>
			</div>
		</div>
	</div>

	<!-- 消息提示：更精致的样式 -->
	<div
		v-if="message.text"
		:class="messageClass"
		class="fixed top-4 right-4 px-4 py-3 rounded-lg shadow-lg z-50 flex items-center gap-2"
	>
		<i v-if="message.type==='success'" class="fa fa-check-circle text-green-500"></i>
		<i v-if="message.type==='error'" class="fa fa-exclamation-circle text-red-500"></i>
		<i v-if="message.type==='info'" class="fa fa-info-circle text-blue-500"></i>
		{{ message.text }}
	</div>

	
	

	

</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch, nextTick } from 'vue'
import DatabaseParameterManager from './components/DatabaseParameterManager.vue'
import TestCases from './components/TestCases.vue'
import GlobalStatusBar from './components/GlobalStatusBar.vue'
import axios from 'axios'
import { bugReportApi } from './api/bugReportApi'
import { fuzzConfigApi } from './api/fuzzConfigApi'
import { reportApi } from './api/reportApi'
import html2pdf from 'html2pdf.js'
import type { BugReportItem, PagedBugReports, FuzzTestConfig } from './types'

// 声明全局类型
declare global {
  interface Window {
    coverageChart: any
  }
}

// 新增：定义参数类型（适配后端返回的字段，id/paramName/weight 必须和后端一致）
interface Parameter {
  id: number;         // 参数ID（后端是Long类型，前端用number兼容）
  paramName: string;  // 参数名称（后端字段名是paramName，前端对应一致）
  weight: number;     // 参数权重（后端是Double类型，前端用number兼容）
  inputWeight: number; // 输入框中的权重（用于临时存储用户输入）
  isSaving?: boolean; // 是否正在保存（用于显示加载状态）
}



// 新增：存储参数列表（响应式数据，用ref包裹数组，类型指定为Parameter[]）
const parameterList = ref<Parameter[]>([])
// 👇 新增：分页核心变量（复制粘贴）
const currentPage = ref(1) // 当前页码（默认第1页）
const pageSize = ref(10)   // 每页显示10条（固定）
const totalParams = computed(() => parameterList.value.length) // 总参数数
const totalPages = computed(() => Math.ceil(totalParams.value / pageSize.value)) // 总页数

// 👇 新增：计算当前页要显示的参数（核心分页逻辑）
const currentPageParams = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return parameterList.value.slice(start, end)
})

// Bug报告相关状态
const bugReports = ref<BugReportItem[]>([])
const bugReportsLoading = ref(false)
const bugReportsPage = ref(0)
const bugReportsSize = ref(20)
const totalBugReports = ref(0)
const totalBugPages = ref(0)

// 测试报告相关状态
const testStatus = ref({
  testOracle: '',
  testRuntime: '',
  testExecutionCount: 0,
  coverage: 0,
  bugCount: 0,
  throughput: 0
})
const currentDatabase = ref({
  name: ''
})
const reportLoading = ref(false)

// 模糊配置相关状态
const savingConfig = ref(false)
const loadingConfig = ref(false)

// 消息提示系统
const message = ref({ text: '', type: 'info' as 'success' | 'error' | 'info' })
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

// -------------- 状态监测页独立变量（不影响其他页面）--------------
// 1. 参数组合权重数据（对应param_combo_weight表）
const paramComboWeights = ref<any[]>([]) // 只存组合权重
// 2. 排序后的组合权重（按权重从高到低）
const sortedParamCombos = computed(() => {
  return [...paramComboWeights.value].sort((a, b) => b.weightValue - a.weightValue)
})
// 3. 状态监测页专用测试状态（独立于测试配置页）
const statusMonitorTestStatus = ref({
  currentDatabase: '', // 当前数据库
  dbVersion: '', // 数据库版本
  runTime: '0秒', // 运行时间
  currentParamCombo: '', // 正在测试的参数组合
  coverageRate: '0.00', // 覆盖率
  bugCount: '0', // Bug数量
  executionCount: '0', // 执行次数
  testOracle: '', // 测试Oracle
  throughput: '0' // 吞吐量
})

// -------------- 状态监测页独立接口（不碰测试配置页的接口）--------------
// 1. 获取参数组合权重（只查param_combo_weight表）
const getParamComboWeights = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/status-monitor/param-weights')
    paramComboWeights.value = res.data // 只存组合权重数据
  } catch (err) {
    console.error('获取参数组合权重失败:', err)
    showMessage('获取参数组合权重失败', 'error')
  }
}

// 获取测试状态（只查test_status表）
const getTestStatus = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/status-monitor/test-status')
    statusMonitorTestStatus.value = {
      currentDatabase: 'MySQL',
      dbVersion: '8.0.26',
      runTime: `${res.data.runTime || 0}秒`,
      currentParamCombo: res.data.currentParamCombo || '无',
      coverageRate: res.data.coverageRate || '0.00',
      bugCount: res.data.bugCount || '0',
      executionCount: res.data.executionCount || '0',
      testOracle: res.data.testOracle || '无',
      throughput: res.data.throughput || '0'
    }
  } catch (err) {
    console.error('获取测试状态失败:', err)
    showMessage('获取测试状态失败', 'error')
  }
}

// 修复后的代码：刷新保留页面状态
const activePanel = ref<'settings'|'status'|'report'|'testCases'>(
  // 优先读取本地存储的状态，没有就默认显示测试设置页
  (localStorage.getItem('activePanel') as 'settings'|'status'|'report'|'testCases') || 'settings'
)

// 监听 activePanel 变化，同步保存到本地存储
watch(activePanel, (newVal) => {
  localStorage.setItem('activePanel', newVal)
}, { immediate: true }) // immediate: true 确保初始状态也会保存

const activeSubTab = ref<'db'|'fuzz'>('fuzz')

// 新增：折叠菜单状态管理
const expandedSubmenu = ref<string | null>(null)

// 测试用例组件引用
const testCasesRef = ref<InstanceType<typeof TestCases> | null>(null)

// 切换子菜单展开/折叠
const toggleSubmenu = (menu: string) => {
  expandedSubmenu.value = expandedSubmenu.value === menu ? null : menu
}

// 👇 新增：页码输入后校验（防止输入超出范围）
const handlePageChange = () => {
  // 处理空值/非数字
  if (isNaN(currentPage.value) || currentPage.value < 1) {
    currentPage.value = 1
  }
  // 处理超出最大页数
  if (currentPage.value > totalPages.value && totalPages.value > 0) {
    currentPage.value = totalPages.value
  }
  // 无数据时重置为1
  if (totalPages.value === 0) {
    currentPage.value = 1
  }
}

// 模糊测试参数默认值
const defaultForm = {
	testOracle: 'TLP',
	randomSeed: -1,
	maxExpressionDepth: 3,
	numQueries: 10000,
	maxNumInserts: 30,
	numTries: 100,
	timeoutSeconds: 30,
	maxGeneratedDatabases: 1,
	username: 'sqlancer',
	password: 'sqlancer',
	host: '',
	port: -1,
}

// 当前模糊测试参数表单
const form = reactive({ ...defaultForm })



const collapseVisible = ref(false)
const collapseTitle = ref('参数设置')
const collapseContent = ref('')

function openCollapse(title: string, content: string) {
	collapseTitle.value = title
	collapseContent.value = content
	collapseVisible.value = true
}

// 唯一的switchPanel函数（无重复）
// 定位到 switchPanel 函数
function switchPanel(panel: 'settings'|'status'|'report'|'testCases') {
  // 仅在从非状态页切换到状态页时，才初始化图表
  const isSwitchToStatus = activePanel.value !== 'status' && panel === 'status';

  activePanel.value = panel; // 这一步触发 DOM 更新（v-show 切换）

  if (panel === 'status') {
    // 加载状态监测页的独立数据
    getParamComboWeights() 
    getTestStatus()
    // 保留原有逻辑
    getParameterList();

    if (isSwitchToStatus) { 
      // [!修改处] 使用 nextTick 等待 DOM 更新完成（元素真正显示且有了宽度）后再初始化图表
      nextTick(() => {
        initCoverageChart();
      });
    }
  } else if (panel === 'report') {
    getTestReportData();
  } else if (panel === 'testCases' && testCasesRef.value) {
    testCasesRef.value.refreshTestCases();
  }
}

const getParameterList = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/parameters/all');
    parameterList.value = response.data.map((item: any) => ({
      ...item,
      inputWeight: item.weight || 0,
      isSaving: false
    }));
    // 👇 新增：刷新后回到第1页
    currentPage.value = 1
  } catch (error) {
    console.error('获取参数列表失败:', error);
    showMessage('获取参数列表失败', 'error');
  }
};

// 获取Bug报告列表
const getBugReports = async () => {
  bugReportsLoading.value = true
  try {
    const response: PagedBugReports = await bugReportApi.getBugReports({
      page: bugReportsPage.value,
      size: bugReportsSize.value
    })
    bugReports.value = response.content
    totalBugReports.value = response.totalElements
    totalBugPages.value = response.totalPages
    console.log('Bug报告列表：', bugReports.value)
  } catch (err) {
    console.error('获取Bug报告失败：', err)
    bugReports.value = []
  } finally {
    bugReportsLoading.value = false
  }
}

// 获取测试报告数据
const getTestReportData = async () => {
  reportLoading.value = true
  try {
    // 从localStorage读取当前数据库信息，与全局状态栏保持一致
    const savedDb = localStorage.getItem('selectedDb')
    if (savedDb) {
      const dbData = JSON.parse(savedDb)
      currentDatabase.value = {
        name: `${dbData.name} (v${dbData.version})`
      }
    } else {
      // 默认值
      currentDatabase.value = {
        name: 'MySQL (v8.0.44)'
      }
    }
    
    // 获取测试状态数据
    const statusResponse = await reportApi.getTestStatus()
    
    // 将后端返回的数据映射到前端使用的字段名
    if (statusResponse) {
      testStatus.value = {
        testOracle: statusResponse.testOracle,
        testRuntime: `${Math.floor(statusResponse.runTime / 3600)}h ${Math.floor((statusResponse.runTime % 3600) / 60)}m ${statusResponse.runTime % 60}s`,
        testExecutionCount: statusResponse.executionCount,
        coverage: statusResponse.coverageRate,
        bugCount: statusResponse.bugCount,
        // 直接使用后端test_status表中的throughput字段值
        throughput: statusResponse.throughput
      }
    }
    
  } catch (err) {
    console.error('获取测试报告数据失败：', err)
  } finally {
    reportLoading.value = false
  }
}

// 显示Bug的参数设置
const showBugParameterSettings = (bug: BugReportItem) => {
  const title = `Bug #${bug.id} - 参数设置 (${bug.targetDatabase})`
  const content = bug.formattedParameterSettings || '// 无参数设置信息'
  openCollapse(title, content)
}

// 显示Bug的测试样例
const showBugTestCase = (bug: BugReportItem) => {
  const title = `Bug #${bug.id} - 测试样例 (${bug.oracleType})`
  let content = `// 测试时间: ${new Date(bug.testTime).toLocaleString('zh-CN')}\n`
  content += `// 错误信息: ${bug.errorMessage || '无'}\n\n`
  content += bug.testCase
  openCollapse(title, content)
}

// 显示消息提示
const showMessage = (text: string, type: 'success' | 'error' | 'info' = 'info') => {
  message.value = { text, type }
  setTimeout(() => {
    message.value.text = ''
  }, 3000)
}

// 加载默认模糊配置
const loadDefaultConfig = async () => {
  loadingConfig.value = true
  try {
    const config: FuzzTestConfig = await fuzzConfigApi.getDefaultConfig()
    // 将配置应用到表单
    Object.assign(form, {
      testOracle: config.testOracle,
      randomSeed: config.randomSeed,
      maxExpressionDepth: config.maxExpressionDepth,
      numQueries: config.numQueries,
      maxNumInserts: config.maxNumInserts,
      numTries: config.numTries,
      timeoutSeconds: config.timeoutSeconds,
      maxGeneratedDatabases: config.maxGeneratedDatabases,
      username: config.username,
      password: config.password,
      host: config.host,
      port: config.port
    })
    console.log('已加载默认配置:', config)
  } catch (err) {
    console.error('加载默认配置失败:', err)
    // 如果加载失败，使用前端默认值
    console.log('使用前端默认配置')
  } finally {
    loadingConfig.value = false
  }
}

// 保存当前配置为默认值
const saveAsDefaultConfig = async () => {
  savingConfig.value = true
  try {
    await fuzzConfigApi.saveDefaultConfig(form)
    showMessage('默认值修改成功！', 'success')
    console.log('配置保存成功')
  } catch (err) {
    console.error('保存配置失败:', err)
    showMessage('保存配置失败，请检查网络连接', 'error')
  } finally {
    savingConfig.value = false
  }
}

// 保存配置到数据库
const saveConfigToDatabase = async () => {
  // 验证输入合法性
  if (!validateFuzzConfig(form)) {
    alert('输入不合法，请检查参数设置！')
    return
  }

  savingConfig.value = true
  try {
    await fuzzConfigApi.saveDefaultConfig(form)
    showMessage('配置保存成功！', 'success')
    console.log('配置保存到数据库成功')
  } catch (err) {
    console.error('保存配置到数据库失败:', err)
    showMessage('保存配置失败，请检查网络连接', 'error')
  } finally {
    savingConfig.value = false
  }
}

// 验证模糊测试配置合法性
const validateFuzzConfig = (config: any): boolean => {
  // 检查必需的参数是否存在且有效
  if (!config.testOracle || config.testOracle.trim() === '') {
    return false
  }
  if (config.randomSeed < -1) {
    return false
  }
  if (config.maxExpressionDepth < 1) {
    return false
  }
  if (config.numQueries < 1) {
    return false
  }
  if (config.maxNumInserts < 1) {
    return false
  }
  if (config.numTries < 1) {
    return false
  }
  if (config.timeoutSeconds < -1) {
    return false
  }
  if (config.maxGeneratedDatabases < 1) {
    return false
  }
  return true
}

// 重置为系统默认值
const resetToSystemDefaults = async () => {
  if (!confirm('确定要重置为系统默认值吗？这将清除所有自定义设置。')) {
    return
  }

  try {
    const defaultConfig: FuzzTestConfig = await fuzzConfigApi.resetDefaultConfig()
    // 应用重置后的配置
    Object.assign(form, {
      testOracle: defaultConfig.testOracle,
      randomSeed: defaultConfig.randomSeed,
      maxExpressionDepth: defaultConfig.maxExpressionDepth,
      numQueries: defaultConfig.numQueries,
      maxNumInserts: defaultConfig.maxNumInserts,
      numTries: defaultConfig.numTries,
      timeoutSeconds: defaultConfig.timeoutSeconds,
      maxGeneratedDatabases: defaultConfig.maxGeneratedDatabases,
      username: defaultConfig.username,
      password: defaultConfig.password,
      host: defaultConfig.host,
      port: defaultConfig.port
    })
    showMessage('已重置为系统默认值！', 'success')
    console.log('配置重置成功')
  } catch (err) {
    console.error('重置配置失败:', err)
    showMessage('重置配置失败，请检查网络连接', 'error')
  }
}

// 新增：更新参数权重（点击保存按钮时调用）
// 保存参数权重
const updateWeight = async (param: Parameter) => {
  // 验证输入是否有效
  if (isNaN(param.inputWeight) || param.inputWeight < 0 || param.inputWeight > 10) {
    showMessage('权重必须是0-10之间的数字', 'error');
    return;
  }

  // 如果输入的权重和原来的一样，不需要保存
  if (param.inputWeight === param.weight) {
    showMessage('权重未变化，无需保存', 'info');
    return;
  }

  try {
    // 显示保存中状态
    param.isSaving = true;
    
    // 调用后端正确的权重更新接口（PUT方式 + URL参数）
    await axios.put(`http://localhost:8080/api/parameters/${param.id}/weight`, {}, {
      params: { weight: param.inputWeight } // 权重放在URL参数里
    });

    // 保存成功后更新显示的权重
    param.weight = param.inputWeight;
    showMessage('权重保存成功', 'success');
  } catch (error) {
    console.error('保存权重失败:', error);
    showMessage('保存权重失败，请重试', 'error');
    // 失败时恢复输入框的值为原来的权重
    param.inputWeight = param.weight;
  } finally {
    // 无论成功失败，都结束保存状态
    param.isSaving = false;
  }
};

function startTest() {
	const payload = { ...form }
	console.log('测试参数:', payload)
	alert('测试已启动！参数已记录到控制台。')
	switchPanel('status')
}

// 重置模糊测试参数为默认值
function resetFuzzParams() {
	Object.assign(form, defaultForm)
}

function initCoverageChart() {
  const canvas = document.getElementById('coverage-chart') as HTMLCanvasElement | null;
  if (!canvas) return;
  // @ts-ignore - Chart 是全局变量
  const { Chart } = window as any;
  if (!Chart) return;

  // 关键：先销毁已存在的图表，避免重复渲染
  if (window.coverageChart) {
    window.coverageChart.destroy();
    window.coverageChart = null; // 清空引用
  }

  // 强制设置画布高度（防止无限变长）
  canvas.style.height = '300px'; // 固定高度，可根据需求调整

  // 初始化新图表（色调改为靛蓝色系）
  window.coverageChart = new Chart(canvas.getContext('2d'), {
    type: 'line',
    data: {
      labels: ['0h','0.5h','1h','1.5h','2h','2.5h','3h'],
      datasets: [{
        label: '代码覆盖率',
        data: [10,25,42,58,65,72,80],
        borderColor: '#4F46E5', // 靛蓝色
        backgroundColor: 'rgba(79, 70, 229, 0.1)',
        tension: 0.4,
        fill: true,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false, // 配合固定高度使用
      scales: {
        y: {
          beginAtZero: true,
          max: 100,
          ticks: { callback: (value: number) => `${value}%` },
          title: { display: true, text: '覆盖率 (%)', color: '#4B5563' },
        },
        x: { title: { display: true, text: '测试时间', color: '#4B5563' } },
      },
      plugins: {
        tooltip: { callbacks: { label: (ctx: any) => `覆盖率: ${ctx.parsed.y}%` } },
      },
    },
  });
}

// 导出PDF功能
const exportPdf = () => {
  try {
    // 选择要导出的容器
    const element = document.getElementById('test-report-container')
    if (!element) {
      console.error('找不到要导出的元素')
      return
    }

    // 生成文件名
    const fileName = `参数敏感数据库模糊测试报告-${currentDatabase.value.name}.pdf`

    // 配置导出选项
    const options = {
      margin: 10, // 使用统一的边距值
      filename: fileName,
      image: { type: 'jpeg' as const, quality: 0.98 },
      html2canvas: { scale: 2 },
      jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' as const }
    }

    // 执行导出
    html2pdf().set(options).from(element).save()
  } catch (error) {
    console.error('导出PDF失败:', error)
    showMessage('导出PDF失败', 'error')
  }
}

onMounted(async () => {
	// 初始化
	activeSubTab.value = 'fuzz'
	await loadDefaultConfig() // 加载默认配置

	if (activePanel.value === 'status') {
    getParameterList()
	initCoverageChart()
  }
})
</script>

<style scoped>
/* 1. 主背景：极浅灰，自然不刺眼 */
.flex-col.h-screen {
  background: #f8fafc; /* 浅灰替代纯白，更柔和 */
  position: relative;
}

/* 2. 校徽样式：完全保留原有尺寸，不改动 */
:deep(.w-15.h-15) {
  width: 60px;
  height: 60px;
}

/* 3. 侧边栏：浅蓝灰渐变，替代深色紫蓝 */
:deep(aside.bg-indigo-700) {
  background: linear-gradient(to bottom, #e0f2fe, #bae6fd) !important; /* 浅蓝渐变 */
  color: #0c4a6e !important; /* 深色文字保证可读性 */
  position: relative;
}
:deep(aside.bg-indigo-700)::after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, #38bdf8, #0ea5e9); /* 浅蓝装饰线 */
}
/* 侧边栏按钮配色（浅色系适配） */
:deep(aside button) {
  color: #0c4a6e !important;
}
:deep(aside button:hover) {
  background: #bae6fd80 !important;
}
:deep(aside button.bg-indigo-600) {
  background: #38bdf8 !important;
  color: white !important;
}

/* 4. 状态栏（GlobalStatusBar）：浅色系优化 */
:deep(.GlobalStatusBar) {
  background: #ffffff !important;
  border-bottom: 1px solid #e2e8f0 !important;
  color: #0f766e !important;
  padding: 8px 16px !important;
  box-shadow: 0 1px 2px rgba(0,0,0,0.02) !important;
}

/* 5. 内容卡片：浅白+淡阴影，更自然 */
:deep(.bg-white.rounded-xl) {
  position: relative;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03) !important; /* 更淡的阴影 */
  border: 1px solid #e8f4f8 !important; /* 极浅边框 */
  background: #ffffff !important;
}
:deep(.bg-white.rounded-xl)::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 40px;
  height: 40px;
  border-top-left-radius: 0.75rem;
  background: linear-gradient(135deg, #38bdf8, #14b8a6); /* 浅蓝绿装饰角 */
  opacity: 0.08; /* 低透明度，不突兀 */
}

/* 6. 顶部Header：浅蓝绿渐变，柔和不刺眼 */
:deep(header.bg-gradient-to-r) {
  background: linear-gradient(to right, #e0f2fe, #ccfbf1) !important; /* 浅蓝绿渐变 */
  color: #0c4a6e !important; /* 深色文字 */
  box-shadow: 0 2px 8px rgba(0,0,0,0.05) !important;
}

/* 7. 按钮配色：浅色系优化，保留功能 */
:deep(button.bg-indigo-600) {
  background: #38bdf8 !important; /* 浅蓝按钮 */
  color: white !important;
}
:deep(button.bg-indigo-600:hover) {
  background: #0ea5e9 !important;
}
:deep(button.bg-green-600) {
  background: #4ade80 !important; /* 浅绿按钮 */
  color: #065f46 !important;
}
:deep(button.bg-green-600:hover) {
  background: #34d399 !important;
}
:deep(button.border-red-200) {
  border-color: #fecdd3 !important; /* 浅红边框 */
  color: #b91c1c !important;
}
:deep(button.border-red-200:hover) {
  background: #fef2f2 !important; /* 浅红背景 */
}
</style>