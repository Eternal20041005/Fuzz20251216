package com.fuzz.service.impl;

import com.fuzz.dto.DatabaseParameter;
import com.fuzz.entity.DatabaseConfig;
import com.fuzz.entity.ParameterType;
import com.fuzz.service.DatabaseConnectionService;
import com.fuzz.service.ParameterReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 参数读取服务实现类
 */
@Service
public class ParameterReaderServiceImpl implements ParameterReaderService {
    
    private static final Logger logger = LoggerFactory.getLogger(ParameterReaderServiceImpl.class);
    
    @Autowired
    private DatabaseConnectionService databaseConnectionService;
    
    @Override
    public List<DatabaseParameter> readAllParameters(DatabaseConfig config) {
        try {
            Map<String, DatabaseParameter> parameterMap = 
                databaseConnectionService.readDatabaseParameters(config);
            
            List<DatabaseParameter> parameters = new ArrayList<>(parameterMap.values());
            
            // 为每个参数添加推荐配置
            parameters.forEach(param -> {
                Map<String, Object> recommendations = 
                    getParameterRecommendations(param.getName(), param.getType());
                
                if (recommendations.containsKey("minValue")) {
                    param.setMinValue(String.valueOf(recommendations.get("minValue")));
                }
                if (recommendations.containsKey("maxValue")) {
                    param.setMaxValue(String.valueOf(recommendations.get("maxValue")));
                }
                if (recommendations.containsKey("allowedValues")) {
                    @SuppressWarnings("unchecked")
                    List<String> allowedValues = (List<String>) recommendations.get("allowedValues");
                    param.setAllowedValues(allowedValues);
                }
            });
            
            logger.info("成功读取参数: {} 个", parameters.size());
            return parameters;
            
        } catch (Exception e) {
            logger.error("读取数据库参数失败", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<DatabaseParameter> readParametersByCategory(DatabaseConfig config, String category) {
        List<DatabaseParameter> allParameters = readAllParameters(config);
        return allParameters.stream()
                .filter(param -> category.equals(param.getCategory()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean validateParameterValue(String paramName, String value, ParameterType type) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        try {
            switch (type) {
                case INTEGER:
                    Long.parseLong(value);
                    return validateIntegerRange(paramName, Long.parseLong(value));
                    
                case DECIMAL:
                    Double.parseDouble(value);
                    return validateDecimalRange(paramName, Double.parseDouble(value));
                    
                case BOOLEAN:
                    return validateBooleanValue(value);
                    
                case STRING:
                    return validateStringValue(paramName, value);
                    
                default:
                    return true;
            }
        } catch (NumberFormatException e) {
            logger.warn("参数值格式错误: {} = {}", paramName, value);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getParameterRecommendations(String paramName, ParameterType type) {
        Map<String, Object> recommendations = new HashMap<>();
        
        // 根据参数名和类型提供推荐配置
        switch (paramName.toLowerCase()) {
            case "max_connections":
                recommendations.put("minValue", 1);
                recommendations.put("maxValue", 100000);
                recommendations.put("recommendedValue", 151);
                break;
                
            case "wait_timeout":
                recommendations.put("minValue", 1);
                recommendations.put("maxValue", 31536000); // 1年
                recommendations.put("recommendedValue", 28800);
                break;
                
            case "innodb_buffer_pool_size":
                recommendations.put("minValue", 5242880); // 5MB
                recommendations.put("maxValue", Long.MAX_VALUE);
                recommendations.put("recommendedValue", 134217728); // 128MB
                break;
                
            case "query_cache_type":
                recommendations.put("allowedValues", Arrays.asList("OFF", "ON", "DEMAND"));
                break;
                
            case "autocommit":
                recommendations.put("allowedValues", Arrays.asList("ON", "OFF", "0", "1"));
                break;
                
            case "sql_mode":
                recommendations.put("allowedValues", Arrays.asList(
                    "STRICT_TRANS_TABLES", "NO_ZERO_DATE", "NO_ZERO_IN_DATE", 
                    "ERROR_FOR_DIVISION_BY_ZERO", "NO_AUTO_CREATE_USER", "NO_ENGINE_SUBSTITUTION"
                ));
                break;
                
            default:
                // 根据类型提供通用推荐
                if (type == ParameterType.BOOLEAN) {
                    recommendations.put("allowedValues", Arrays.asList("ON", "OFF", "0", "1"));
                } else if (type == ParameterType.INTEGER) {
                    recommendations.put("minValue", 0);
                    recommendations.put("maxValue", Integer.MAX_VALUE);
                }
                break;
        }
        
        return recommendations;
    }
    
    /**
     * 验证整数范围
     */
    private boolean validateIntegerRange(String paramName, long value) {
        Map<String, Object> recommendations = getParameterRecommendations(paramName, ParameterType.INTEGER);
        
        if (recommendations.containsKey("minValue")) {
            long minValue = ((Number) recommendations.get("minValue")).longValue();
            if (value < minValue) {
                return false;
            }
        }
        
        if (recommendations.containsKey("maxValue")) {
            long maxValue = ((Number) recommendations.get("maxValue")).longValue();
            if (value > maxValue) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 验证小数范围
     */
    private boolean validateDecimalRange(String paramName, double value) {
        Map<String, Object> recommendations = getParameterRecommendations(paramName, ParameterType.DECIMAL);
        
        if (recommendations.containsKey("minValue")) {
            double minValue = ((Number) recommendations.get("minValue")).doubleValue();
            if (value < minValue) {
                return false;
            }
        }
        
        if (recommendations.containsKey("maxValue")) {
            double maxValue = ((Number) recommendations.get("maxValue")).doubleValue();
            if (value > maxValue) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 验证布尔值
     */
    private boolean validateBooleanValue(String value) {
        String lowerValue = value.toLowerCase().trim();
        return Arrays.asList("on", "off", "true", "false", "1", "0", "yes", "no")
                .contains(lowerValue);
    }
    
    /**
     * 验证字符串值
     */
    private boolean validateStringValue(String paramName, String value) {
        Map<String, Object> recommendations = getParameterRecommendations(paramName, ParameterType.STRING);
        
        if (recommendations.containsKey("allowedValues")) {
            @SuppressWarnings("unchecked")
            List<String> allowedValues = (List<String>) recommendations.get("allowedValues");
            return allowedValues.contains(value);
        }
        
        // 基本字符串验证
        return value.length() <= 1000; // 限制最大长度
    }
}