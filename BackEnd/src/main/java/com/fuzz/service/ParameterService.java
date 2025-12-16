package com.fuzz.service;

import com.fuzz.dto.*;
import com.fuzz.entity.DatabaseConfig;
import com.fuzz.service.ParameterConstraintValidator.ValidationResult;
import com.fuzz.entity.ParameterTemplate;
import com.fuzz.dto.ParameterTemplateDto;
import org.springframework.data.domain.Page;

import java.util.List;
/**
 * 参数管理服务接口
 */
public interface ParameterService {
    
    /**
     * 分页查询参数
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param search 搜索关键词
     * @param category 参数类别
     * @return 分页结果
     */
    PagedResponse<ParameterTemplateDto> getParameters(int page, int size, String search, String category);
    
    /**
     * 根据ID获取参数
     * @param id 参数ID
     * @return 参数DTO
     */
    ParameterTemplateDto getParameterById(Long id);
    
    /**
     * 更新参数
     * @param id 参数ID
     * @param request 更新请求
     * @return 更新后的参数DTO
     */
    ParameterTemplateDto updateParameter(Long id, UpdateParameterRequest request);
    
    /**
     * 批量更新参数
     * @param requests 更新请求列表
     * @return 批量更新结果
     */
    BatchUpdateResult batchUpdateParameters(List<UpdateParameterRequest> requests);
    
    /**
     * 从数据库导入参数
     * @param dbConfigId 数据库配置ID
     * @return 导入结果
     */
    ImportResult importParametersFromDatabase(Long dbConfigId);
    
    /**
     * 获取数据库实际参数值
     * @param dbConfig 数据库配置
     * @return 参数值映射
     */
    java.util.Map<String, String> getDatabaseParameterValues(DatabaseConfig dbConfig);
    
    /**
     * 获取所有参数类别
     * @return 类别列表
     */
    List<String> getAllCategories();
    
    /**
     * 删除参数
     * @param id 参数ID
     */
    void deleteParameter(Long id);
    
    /**
     * 批量删除参数
     * @param ids 参数ID列表
     */
    void batchDeleteParameters(List<Long> ids);
    
    /**
     * 验证参数约束条件
     * @param paramName 参数名
     * @param value 参数值
     * @return 验证结果
     */
    ValidationResult validateParameterConstraints(String paramName, String value);
    
    /**
     * 根据参数ID获取候选取值列表
     * @param parameterId 参数ID
     * @return 候选取值列表
     */
    List<String> getParameterCandidateValues(Long parameterId);
    
    /**
     * 获取参数的约束信息
     * @param parameterId 参数ID
     * @return 约束信息DTO
     */
    ParameterConstraintDto getParameterConstraints(Long parameterId);
    
    /**
     * 根据参数名获取参数详情
     * @param paramName 参数名
     * @return 参数DTO
     */
    ParameterTemplateDto getParameterByName(String paramName);
    
    /**
     * 获取所有设置范围类型
     * @return 设置范围列表
     */
    List<String> getAllValueRanges();
    
    /**
     * 根据设置范围筛选参数
     * @param page 页码
     * @param size 每页大小
     * @param valueRange 设置范围
     * @return 分页结果
     */
    PagedResponse<ParameterTemplateDto> getParametersByValueRange(int page, int size, String valueRange);
    
    ParameterTemplateDto updateParameterWeight(Long id, Double weight);

    void adjustWeightByCoverage(Long paramId, Double coverage);

    java.util.Map<String, Double> getParameterWeightAndCoverage(Long id);

    // 根据 ID 查询参数模板
    ParameterTemplate getById(Long id);

    List<ParameterTemplateDto> getAllParameters();
}