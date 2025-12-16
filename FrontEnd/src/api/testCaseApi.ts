import { api, ApiResponse } from './index'

// 测试用例类型定义
export interface TestCase {
  testTime: string
  caseId: string
  targetDb: string
  oracleType: string
  triggeredBug: boolean
  bugId?: string
  paramCombo: string
  weightValue: number
  sqlStatement: string
}

// 分页响应类型
export interface PagedResponse<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      sorted: boolean
      unsorted: boolean
      empty: boolean
    }
    offset: number
    paged: boolean
    unpaged: boolean
  }
  last: boolean
  totalPages: number
  totalElements: number
  size: number
  number: number
  sort: {
    sorted: boolean
    unsorted: boolean
    empty: boolean
  }
  first: boolean
  numberOfElements: number
  empty: boolean
}

// 测试用例API
export const testCaseApi = {
  // 获取测试用例列表
  getTestCases: (page: number = 0, size: number = 20, triggeredBug?: boolean, param1?: string, param2?: string, sort?: string) => {
    let url = `/test-cases?page=${page}&size=${size}`
    if (triggeredBug !== undefined) {
      url += `&triggeredBug=${triggeredBug}`
    }
    if (param1) {
      url += `&param1=${encodeURIComponent(param1)}`
    }
    if (param2) {
      url += `&param2=${encodeURIComponent(param2)}`
    }
    if (sort) {
      url += `&sort=${encodeURIComponent(sort)}`
    }
    return api.get<PagedResponse<TestCase>>(url)
  }
}
