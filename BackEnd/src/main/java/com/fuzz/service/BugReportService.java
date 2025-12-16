package com.fuzz.service;

import com.fuzz.dto.BugReportDto;
import com.fuzz.dto.PagedResponse;
import com.fuzz.entity.BugReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Bug报告服务接口
 */
public interface BugReportService {

    /**
     * 分页获取Bug报告列表
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页的Bug报告列表
     */
    PagedResponse<BugReportDto> getBugReports(int page, int size);

    /**
     * 根据ID获取Bug报告详情
     * @param id Bug报告ID
     * @return Bug报告DTO
     */
    BugReportDto getBugReportById(Long id);

    /**
     * 创建新的Bug报告
     * @param bugType Bug类型（"崩溃" 或 "逻辑"）
     * @param targetDatabase 目标数据库
     * @param oracleType Oracle类型
     * @param testCase 测试样例
     * @param parameterSettings 参数设置
     * @param errorMessage 错误信息（可选）
     * @return 创建的Bug报告DTO
     */
    BugReportDto createBugReport(String bugType, String targetDatabase, String oracleType,
                                String testCase, Map<String, String> parameterSettings,
                                String errorMessage);

    /**
     * 更新Bug报告
     * @param id Bug报告ID
     * @param bugType Bug类型
     * @param targetDatabase 目标数据库
     * @param oracleType Oracle类型
     * @param testCase 测试样例
     * @param parameterSettings 参数设置
     * @param errorMessage 错误信息
     * @return 更新后的Bug报告DTO
     */
    BugReportDto updateBugReport(Long id, String bugType, String targetDatabase,
                                String oracleType, String testCase,
                                Map<String, String> parameterSettings, String errorMessage);

    /**
     * 删除Bug报告
     * @param id Bug报告ID
     */
    void deleteBugReport(Long id);

    /**
     * 批量删除Bug报告
     * @param ids Bug报告ID列表
     */
    void batchDeleteBugReports(List<Long> ids);

    /**
     * 根据Bug类型筛选Bug报告
     * @param bugType Bug类型
     * @return Bug报告列表
     */
    List<BugReportDto> getBugReportsByType(String bugType);

    /**
     * 根据目标数据库筛选Bug报告
     * @param targetDatabase 目标数据库
     * @return Bug报告列表
     */
    List<BugReportDto> getBugReportsByDatabase(String targetDatabase);

    /**
     * 根据Oracle类型筛选Bug报告
     * @param oracleType Oracle类型
     * @return Bug报告列表
     */
    List<BugReportDto> getBugReportsByOracleType(String oracleType);

    /**
     * 获取最近的Bug报告
     * @param limit 限制数量
     * @return 最近的Bug报告列表
     */
    List<BugReportDto> getRecentBugReports(int limit);

    /**
     * 统计不同类型的Bug数量
     * @return Bug类型统计结果
     */
    Map<String, Long> getBugStatisticsByType();

    /**
     * 统计不同数据库的Bug数量
     * @return 数据库Bug统计结果
     */
    Map<String, Long> getBugStatisticsByDatabase();

    /**
     * 获取Bug总数
     * @return Bug总数
     */
    long getTotalBugCount();

    /**
     * 导出Bug报告数据
     * @param format 导出格式（json, csv等）
     * @return 导出数据
     */
    String exportBugReports(String format);
}
