// 模糊测试配置API
import { api } from './index'

// 模糊测试配置接口定义
export interface FuzzTestConfig {
  id?: number
  configName: string
  description?: string
  testOracle: string
  randomSeed: number
  maxExpressionDepth: number
  numQueries: number
  maxNumInserts: number
  numTries: number
  timeoutSeconds: number
  maxGeneratedDatabases: number
  username?: string
  password?: string
  host?: string
  port?: number
  createdAt?: string
  updatedAt?: string
}

export interface CreateConfigRequest {
  configName: string
  description?: string
  parameters: Record<string, any>
}

export const fuzzConfigApi = {
  // 获取默认配置
  getDefaultConfig(): Promise<FuzzTestConfig> {
    return api.get('/fuzz-configs/default')
  },

  // 保存默认配置
  saveDefaultConfig(configData: Record<string, any>): Promise<FuzzTestConfig> {
    return api.post('/fuzz-configs/default', configData)
  },

  // 更新默认配置
  updateDefaultConfig(configData: Record<string, any>): Promise<FuzzTestConfig> {
    return api.put('/fuzz-configs/default', configData)
  },

  // 重置默认配置
  resetDefaultConfig(): Promise<FuzzTestConfig> {
    return api.post('/fuzz-configs/default/reset')
  },

  // 获取所有配置
  getAllConfigs(): Promise<FuzzTestConfig[]> {
    return api.get('/fuzz-configs')
  },

  // 根据ID获取配置
  getConfigById(id: number): Promise<FuzzTestConfig> {
    return api.get(`/fuzz-configs/${id}`)
  },

  // 根据名称获取配置
  getConfigByName(configName: string): Promise<FuzzTestConfig> {
    return api.get(`/fuzz-configs/name/${encodeURIComponent(configName)}`)
  },

  // 创建新配置
  createConfig(request: CreateConfigRequest): Promise<FuzzTestConfig> {
    return api.post('/fuzz-configs', request)
  },

  // 更新配置
  updateConfig(id: number, configData: Record<string, any>): Promise<FuzzTestConfig> {
    return api.put(`/fuzz-configs/${id}`, configData)
  },

  // 删除配置
  deleteConfig(id: number): Promise<void> {
    return api.delete(`/fuzz-configs/${id}`)
  },

  // 检查配置名称是否存在
  configNameExists(configName: string): Promise<boolean> {
    return api.get(`/fuzz-configs/exists/${encodeURIComponent(configName)}`)
  }
}
