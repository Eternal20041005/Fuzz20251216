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

// 当前测试数据库接口（对应db_parameter表，通过Is_test筛选）
export interface DbParameter {
    id: number
    dbType: string
    paramName: string
    paramType: string
    description: string
    defaultValue: string
    valueRange: string
    weight: number
    isTest: boolean
    createdAt: string
    updatedAt: string
}

// 前端使用的当前数据库信息
export interface CurrentDatabase {
    name: string
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

// 获取当前正在测试的数据库
export const getCurrentTestDatabase = async (): Promise<CurrentDatabase> => {
    const response = await api.get<DbParameter[]>('/current-test-database')
    // 后端返回参数数组，提取数据库类型作为名称
    if (response && response.length > 0) {
        // 取第一个参数的dbType作为数据库名称
        return {
            name: response[0].dbType || 'Unknown'
        }
    }
    return {
        name: 'Unknown'
    }
}

export const reportApi = { getTestStatus, getCurrentTestDatabase }
