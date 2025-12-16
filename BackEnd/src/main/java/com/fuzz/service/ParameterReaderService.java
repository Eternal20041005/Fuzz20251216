package com.fuzz.service;

import com.fuzz.dto.DatabaseParameter;
import com.fuzz.entity.DatabaseConfig;
import com.fuzz.entity.ParameterType;

import java.util.List;
import java.util.Map;

/**
 * 参数读取服务接口
 */
public interface ParameterReaderService {
    
    /**
     * 从数据库读取所有参数
     * @param config 数据库配置
     * @return 参数列表
     */
    List<DatabaseParameter> readAllParameters(DatabaseConfig config);
    
    /**
     * 读取指定类别的参数
     * @param config 数据库配置
     * @param category 参数类别
     * @return 参数列表
     */
    List<DatabaseParameter> readParametersByCategory(DatabaseConfig config, String category);
    
    /**
     * 验证参数值的有效性
     * @param paramName 参数名
     * @param value 参数值
     * @param type 参数类型
     * @return 验证结果
     */
    boolean validateParameterValue(String paramName, String value, ParameterType type);
    
    /**
     * 获取参数的推荐值范围
     * @param paramName 参数名
     * @param type 参数类型
     * @return 推荐值范围
     */
    Map<String, Object> getParameterRecommendations(String paramName, ParameterType type);
}