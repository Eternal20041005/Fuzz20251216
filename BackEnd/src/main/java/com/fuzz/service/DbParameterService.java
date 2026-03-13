package com.fuzz.service;

import com.fuzz.dto.DbParameterDto;
import com.fuzz.dto.PagedResponse;

import java.util.List;

/**
 * 数据库参数管理服务接口
 */
public interface DbParameterService {

    /**
     * 分页查询数据库参数
     * @param dbType 数据库类型（如 mysql, postgresql）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param search 搜索关键词
     * @param category 参数类别筛选
     * @param testStatus 测试状态筛选
     * @return 分页结果
     */
    PagedResponse<DbParameterDto> getParameters(String dbType, int page, int size, String search, String category, Boolean testStatus);

    /**
     * 根据ID获取参数
     * @param id 参数ID
     * @return 参数DTO
     */
    DbParameterDto getParameterById(Long id);

    /**
     * 根据数据库类型和参数名获取参数
     * @param dbType 数据库类型
     * @param paramName 参数名
     * @return 参数DTO
     */
    DbParameterDto getParameterByName(String dbType, String paramName);

    /**
     * 更新参数
     * @param id 参数ID
     * @param defaultValue 默认值
     * @param isTest 是否测试
     * @param weight 权重
     * @return 更新后的参数DTO
     */
    DbParameterDto updateParameter(Long id, String defaultValue, Boolean isTest, Double weight);

    /**
     * 获取所有数据库类型
     * @return 数据库类型列表
     */
    List<String> getAllDbTypes();

    /**
     * 获取指定数据库类型的参数类别
     * @param dbType 数据库类型
     * @return 类别列表
     */
    List<String> getCategoriesByDbType(String dbType);

    /**
     * 删除参数
     * @param id 参数ID
     */
    void deleteParameter(Long id);

    /**
     * 获取所有参数（无分页）
     * @param dbType 数据库类型
     * @return 参数列表
     */
    List<DbParameterDto> getAllParameters(String dbType);

    /**
     * 更新参数权重
     * @param id 参数ID
     * @param weight 新权重
     * @return 更新后的参数DTO
     */
    DbParameterDto updateParameterWeight(Long id, Double weight);

    /**
     * 根据覆盖率调整权重
     * @param paramId 参数ID
     * @param coverage 覆盖率
     */
    void adjustWeightByCoverage(Long paramId, Double coverage);

    /**
     * 获取参数的权重和覆盖率
     * @param id 参数ID
     * @return 包含权重和覆盖率的Map
     */
    java.util.Map<String, Double> getParameterWeightAndCoverage(Long id);

    /**
     * 根据ID查询参数
     * @param id 参数ID
     * @return 参数实体
     */
    com.fuzz.entity.DbParameter getById(Long id);

    /**
     * 批量更新参数
     * @param requests 更新请求列表
     * @return 批量更新结果
     */
    com.fuzz.dto.BatchUpdateResult batchUpdateParameters(java.util.List<com.fuzz.dto.UpdateParameterRequest> requests);
}
