// 数据库配置API

import { api } from './index'
import type {
    DatabaseConfig,
    ConnectionTestResult
} from '../types'

export const databaseConfigApi = {
    // 获取数据库配置列表
    getDatabaseConfigs(): Promise<DatabaseConfig[]> {
        return api.get('/database-configs')
    },

    // 获取数据库配置详情
    getDatabaseConfig(id: number): Promise<DatabaseConfig> {
        return api.get(`/database-configs/${id}`)
    },

    // 测试数据库连接
    testConnection(id: number): Promise<ConnectionTestResult> {
        return api.post(`/database-configs/${id}/test-connection`)
    },

    // 创建数据库配置
    createDatabaseConfig(config: Omit<DatabaseConfig, 'id' | 'createTime' | 'updateTime'>): Promise<DatabaseConfig> {
        return api.post('/database-configs', config)
    },

    // 更新数据库配置
    updateDatabaseConfig(id: number, config: Omit<DatabaseConfig, 'id' | 'createTime' | 'updateTime'>): Promise<DatabaseConfig> {
        return api.put(`/database-configs/${id}`, config)
    },

    // 删除数据库配置
    deleteDatabaseConfig(id: number): Promise<void> {
        return api.delete(`/database-configs/${id}`)
    }
}