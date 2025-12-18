// API 基础配置

const API_BASE_URL = 'http://localhost:8080/api'

export interface ApiResponse<T> {
    data: T
    status: number
    message?: string
}

export class ApiError extends Error {
    constructor(
        message: string,
        public status: number,
        public code?: string
    ) {
        super(message)
        this.name = 'ApiError'
    }
}

export async function apiRequest<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`
    console.log('API请求URL:', url)
    console.log('API请求选项:', options)
    console.log('API请求数据:', options.body ? JSON.parse(options.body as string) : null)

    const defaultOptions: RequestInit = {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
        ...options,
    }

    try {
        const response = await fetch(url, defaultOptions)
        console.log('API响应状态:', response.status)
        console.log('API响应状态文本:', response.statusText)

        if (!response.ok) {
            let errorMessage = `HTTP ${response.status}: ${response.statusText}`

            try {
                const errorData = await response.json()
                console.log('API错误响应数据:', errorData)
                errorMessage = errorData.message || errorMessage
            } catch {
                // 忽略JSON解析错误，使用默认错误消息
                const errorText = await response.text()
                console.log('API错误响应文本:', errorText)
            }

            throw new ApiError(errorMessage, response.status)
        }

        // 处理204 No Content响应
        if (response.status === 204) {
            console.log('API响应: 204 No Content')
            return null as T
        }

        const responseData = await response.json()
        console.log('API响应数据:', responseData)
        return responseData
    } catch (error) {
        console.error('API请求错误:', error)
        if (error instanceof ApiError) {
            throw error
        }

        // 网络错误或其他错误
        if (error instanceof TypeError && error.message.includes('Failed to fetch')) {
            // 这是真正的网络连接错误
            throw new ApiError('网络连接错误', 0)
        } else {
            // 其他类型的错误
            throw new ApiError(
                error instanceof Error ? error.message : '未知错误',
                0
            )
        }
    }
}

export const api = {
    get: <T>(endpoint: string) => apiRequest<T>(endpoint),

    post: <T>(endpoint: string, data?: any) =>
        apiRequest<T>(endpoint, {
            method: 'POST',
            body: data ? JSON.stringify(data) : undefined,
        }),

    put: <T>(endpoint: string, data?: any, options?: { params?: Record<string, any> }) => {
        let url = endpoint
        if (options?.params) {
            const params = new URLSearchParams()
            Object.entries(options.params).forEach(([key, value]) => {
                if (value !== undefined && value !== null) {
                    params.append(key, String(value))
                }
            })
            const queryString = params.toString()
            if (queryString) {
                url += (url.includes('?') ? '&' : '?') + queryString
            }
        }
        return apiRequest<T>(url, {
            method: 'PUT',
            body: data ? JSON.stringify(data) : undefined,
        })
    },

    delete: <T>(endpoint: string) =>
        apiRequest<T>(endpoint, { method: 'DELETE' }),
}

// 导出所有API模块
export { parameterApi } from './parameterApi'
export { databaseConfigApi } from './databaseConfigApi'
export { bugReportApi } from './bugReportApi'
export { fuzzConfigApi } from './fuzzConfigApi'
export { testCaseApi } from './testCaseApi'
export { reportApi } from './reportApi'