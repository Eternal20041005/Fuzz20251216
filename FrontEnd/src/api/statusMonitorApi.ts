import axios from 'axios';
// 后端根路径（必须加/api，匹配你的Tomcat配置）
const baseURL = 'http://localhost:8080/api';

// 创建axios实例（避免跨域/重复请求问题）
const request = axios.create({
  baseURL,
  timeout: 5000
});

// 状态监测相关接口
export const statusMonitorApi = {
  // 获取最新测试状态（顶部状态栏+右侧测试状态）
  getTestStatus: async () => {
    const res = await request.get('/status-monitor/test-status');
    return res.data;
  },
  // 获取参数组合权重列表（左侧表格）
  getParamWeights: async () => {
    const res = await request.get('/status-monitor/param-weights');
    return res.data;
  },
  // 保存参数权重（可选，图一的“保存”按钮）
  saveParamWeight: async (id: number, weightValue: number) => {
    const res = await request.post(`/status-monitor/save-weight/${id}`, {}, {
      params: { weightValue }
    });
    return res.data;
  }
};