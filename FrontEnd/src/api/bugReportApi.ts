// Bug报告API
import { api } from './index'

// Bug报告数据类型定义
export interface BugReportItem {
  id: number
  bugType: '崩溃' | '逻辑'
  targetDatabase: string
  oracleType: 'TLP' | 'RBR' | 'CBR'
  testTime: string
  testCase: string
  errorMessage?: string
  formattedParameterSettings: string
  parameterSettings: Record<string, string>
  createdAt: string
  updatedAt: string
}

export interface CreateBugReportRequest {
  bugType: string
  targetDatabase: string
  oracleType: string
  testCase: string
  parameterSettings: Record<string, string>
  errorMessage?: string
}

export interface UpdateBugReportRequest extends CreateBugReportRequest {
  // 继承所有字段
}

export interface BugStatistics {
  totalCount: number
  byType: Record<string, number>
  byDatabase: Record<string, number>
}

export interface PagedBugReports {
  content: BugReportItem[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: any
    offset: number
    paged: boolean
    unpaged: boolean
  }
  totalPages: number
  totalElements: number
  last: boolean
  first: boolean
  numberOfElements: number
  size: number
  number: number
  sort: any
  empty: boolean
}

export const bugReportApi = {
  // 获取Bug报告列表（分页）
  getBugReports(params: { page?: number; size?: number } = {}): Promise<PagedBugReports> {
    const searchParams = new URLSearchParams()
    if (params.page !== undefined) searchParams.set('page', params.page.toString())
    if (params.size !== undefined) searchParams.set('size', params.size.toString())

    const query = searchParams.toString()
    return api.get(`/bug-reports${query ? `?${query}` : ''}`)
  },

  // 根据ID获取Bug报告详情
  getBugReport(id: number): Promise<BugReportItem> {
    return api.get(`/bug-reports/${id}`)
  },

  // 创建Bug报告
  createBugReport(request: CreateBugReportRequest): Promise<BugReportItem> {
    return api.post('/bug-reports', request)
  },

  // 更新Bug报告
  updateBugReport(id: number, request: UpdateBugReportRequest): Promise<BugReportItem> {
    return api.put(`/bug-reports/${id}`, request)
  },

  // 删除Bug报告
  deleteBugReport(id: number): Promise<void> {
    return api.delete(`/bug-reports/${id}`)
  },

  // 批量删除Bug报告
  batchDeleteBugReports(ids: number[]): Promise<void> {
    return api.delete('/bug-reports/batch')
  },

  // 根据Bug类型获取Bug报告
  getBugReportsByType(bugType: string): Promise<BugReportItem[]> {
    return api.get(`/bug-reports/type/${encodeURIComponent(bugType)}`)
  },

  // 根据目标数据库获取Bug报告
  getBugReportsByDatabase(targetDatabase: string): Promise<BugReportItem[]> {
    return api.get(`/bug-reports/database/${encodeURIComponent(targetDatabase)}`)
  },

  // 根据Oracle类型获取Bug报告
  getBugReportsByOracleType(oracleType: string): Promise<BugReportItem[]> {
    return api.get(`/bug-reports/oracle/${encodeURIComponent(oracleType)}`)
  },

  // 获取最近的Bug报告
  getRecentBugReports(limit: number = 10): Promise<BugReportItem[]> {
    return api.get(`/bug-reports/recent?limit=${limit}`)
  },

  // 获取Bug统计信息
  getBugStatistics(): Promise<BugStatistics> {
    return api.get('/bug-reports/statistics')
  },

  // 导出Bug报告数据
  exportBugReports(format: string = 'json'): Promise<string> {
    return api.get(`/bug-reports/export?format=${format}`)
  }
}
