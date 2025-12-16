// 参数管理API

import { api } from './index'
import type {
    ParameterItem,
    PagedResponse,
    UpdateParameterRequest,
    BatchUpdateResult,
    ImportResult,
    ParameterConstraint,
    ValidationResult,
    ParameterSearchParams
} from '../types'

export interface GetParametersParams extends ParameterSearchParams {
    // 继承ParameterSearchParams接口
}

export const parameterApi = {
    // 获取参数列表
    getParameters(params: GetParametersParams = {}): Promise<PagedResponse<ParameterItem>> {
        const searchParams = new URLSearchParams()

        if (params.page !== undefined) searchParams.set('page', params.page.toString())
        if (params.size !== undefined) searchParams.set('size', params.size.toString())
        if (params.search) searchParams.set('search', params.search)
        if (params.category) searchParams.set('category', params.category)

        const query = searchParams.toString()
        return api.get(`/parameters${query ? `?${query}` : ''}`)
    },

    // 获取参数详情
    getParameter(id: number): Promise<ParameterItem> {
        return api.get(`/parameters/${id}`)
    },

    // 更新参数
    updateParameter(id: number, data: Omit<UpdateParameterRequest, 'id'>): Promise<ParameterItem> {
        return api.put(`/parameters/${id}`, { id, ...data })
    },

    // 批量更新参数
    batchUpdateParameters(requests: UpdateParameterRequest[]): Promise<BatchUpdateResult> {
        return api.put('/parameters/batch', requests)
    },

    // 从数据库导入参数
    importFromDatabase(dbConfigId: number): Promise<ImportResult> {
        return api.post(`/parameters/import/${dbConfigId}`)
    },

    // 获取所有类别
    getCategories(): Promise<string[]> {
        return api.get('/parameters/categories')
    },

    // 删除参数
    deleteParameter(id: number): Promise<void> {
        return api.delete(`/parameters/${id}`)
    },

    // 批量删除参数
    batchDeleteParameters(ids: number[]): Promise<void> {
        return api.delete('/parameters/batch')
    },

    // 根据参数名获取参数详情
    getParameterByName(paramName: string): Promise<ParameterItem> {
        return api.get(`/parameters/name/${encodeURIComponent(paramName)}`)
    },

    // 验证参数值
    validateParameterValue(paramName: string, value: string): Promise<ValidationResult> {
        const params = new URLSearchParams()
        params.set('paramName', paramName)
        params.set('value', value)
        return api.post(`/parameters/validate?${params.toString()}`)
    },

    // 获取参数的候选取值
    getParameterCandidateValues(id: number): Promise<string[]> {
        return api.get(`/parameters/${id}/candidates`)
    },

    // 获取参数的约束信息
    getParameterConstraints(id: number): Promise<ParameterConstraint> {
        return api.get(`/parameters/${id}/constraints`)
    },

    // 获取所有设置范围类型
    getValueRanges(): Promise<string[]> {
        return api.get('/parameters/value-ranges')
    },

    // 根据设置范围筛选参数
    getParametersByValueRange(params: { page?: number; size?: number; valueRange: string }): Promise<PagedResponse<ParameterItem>> {
        const searchParams = new URLSearchParams()
        if (params.page !== undefined) searchParams.set('page', params.page.toString())
        if (params.size !== undefined) searchParams.set('size', params.size.toString())
        searchParams.set('valueRange', params.valueRange)

        return api.get(`/parameters/by-value-range?${searchParams.toString()}`)
    },

    // 获取增强的参数列表（支持设置范围筛选）
    getEnhancedParameters(params: GetParametersParams = {}): Promise<PagedResponse<ParameterItem>> {
        const searchParams = new URLSearchParams()

        if (params.page !== undefined) searchParams.set('page', params.page.toString())
        if (params.size !== undefined) searchParams.set('size', params.size.toString())
        if (params.search) searchParams.set('search', params.search)
        if (params.category) searchParams.set('category', params.category)
        if (params.valueRange) searchParams.set('valueRange', params.valueRange)

        const query = searchParams.toString()
        return api.get(`/parameters/enhanced${query ? `?${query}` : ''}`)
    },

    // 更新参数权重
    updateParameterWeight(id: number, weight: number): Promise<ParameterItem> {
        return api.put(`/parameters/${id}/weight`, {}, {
            params: { weight }
        })
    }
}