import { api } from './index'

// 测试状态数据接口（对应test_status表）
export interface TestStatus {
    taskStatus: string
    testOracle: string
    runTime: number
    coverageRate: number
    bugCount: number
    executionCount: number
    currentParamCombo: string
    throughput: number
    createdAt: string
    updatedAt: string
}



// 获取测试状态数据
export const getTestStatus = async (): Promise<TestStatus | null> => {
    try {
        // 使用后端提供的专门获取最新记录的接口，提高效率
        const response = await api.get<TestStatus>('/test-status/latest')
        return response
    } catch (error) {
        console.error('获取最新测试状态失败，尝试使用备用方法:', error)
        // 备用方法：获取所有记录并在前端排序
        const response = await api.get<TestStatus[]>('/test-status')
        if (response && response.length > 0) {
            // 优先按updatedAt排序，确保获取最近修改的记录
            return response.sort((a, b) => 
                new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
            )[0]
        }
        return null
    }
}



export const reportApi = { getTestStatus }
