package com.fuzz.service;

import com.fuzz.dto.ParameterConstraintDto;
import com.fuzz.entity.ParameterTemplate;
import com.fuzz.entity.ParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 参数约束验证服务
 * 负责验证MySQL参数值是否符合约束条件
 */
@Service
public class ParameterConstraintValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(ParameterConstraintValidator.class);
    
    // 常用的数值格式正则表达式
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^-?\\d+$");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(true|false|on|off|1|0)$", Pattern.CASE_INSENSITIVE);
    
    /**
     * 验证参数值是否符合所有约束条件
     * @param parameter 参数模板
     * @param value 要验证的值
     * @return 验证结果
     */
    public ValidationResult validateParameterValue(ParameterTemplate parameter, String value) {
        if (parameter == null) {
            return ValidationResult.error("参数模板不能为空");
        }
        
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error("参数值不能为空");
        }
        
        String trimmedValue = value.trim();
        
        // 1. 验证参数类型格式
        ValidationResult typeResult = validateParameterType(parameter.getParamType(), trimmedValue);
        if (!typeResult.isValid()) {
            return typeResult;
        }
        
        // 2. 验证候选取值约束
        ValidationResult candidateResult = validateCandidateValues(parameter, trimmedValue);
        if (!candidateResult.isValid()) {
            return candidateResult;
        }
        
        // 3. 验证数值范围约束
        ValidationResult rangeResult = validateRangeConstraint(parameter, trimmedValue);
        if (!rangeResult.isValid()) {
            return rangeResult;
        }
        
        return ValidationResult.success("参数值验证通过");
    }
    
    /**
     * 验证参数类型格式
     * @param paramType 参数类型
     * @param value 参数值
     * @return 验证结果
     */
    public ValidationResult validateParameterType(ParameterType paramType, String value) {
        switch (paramType) {
            case INTEGER:
                if (!INTEGER_PATTERN.matcher(value).matches()) {
                    return ValidationResult.error("参数值必须是整数格式");
                }
                try {
                    Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return ValidationResult.error("参数值超出整数范围");
                }
                break;
                
            case DECIMAL:
                if (!DECIMAL_PATTERN.matcher(value).matches()) {
                    return ValidationResult.error("参数值必须是数值格式");
                }
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    return ValidationResult.error("参数值超出数值范围");
                }
                break;
                
            case BOOLEAN:
                if (!BOOLEAN_PATTERN.matcher(value).matches()) {
                    return ValidationResult.error("参数值必须是布尔格式 (true/false, on/off, 1/0)");
                }
                break;
                
            case STRING:
                // 字符串类型通常没有格式限制
                break;
                
            default:
                return ValidationResult.error("不支持的参数类型: " + paramType);
        }
        
        return ValidationResult.success("参数类型验证通过");
    }
    
    /**
     * 验证候选取值约束
     * @param parameter 参数模板
     * @param value 参数值
     * @return 验证结果
     */
    public ValidationResult validateCandidateValues(ParameterTemplate parameter, String value) {
        List<String> candidateValues = parameter.getCandidateValues();
        
        if (candidateValues == null || candidateValues.isEmpty()) {
            return ValidationResult.success("无候选取值约束");
        }
        
        // 对于布尔类型，进行特殊处理
        if (parameter.getParamType() == ParameterType.BOOLEAN) {
            String normalizedValue = normalizeBooleanValue(value);
            for (String candidate : candidateValues) {
                if (normalizedValue.equals(normalizeBooleanValue(candidate))) {
                    return ValidationResult.success("候选取值验证通过");
                }
            }
        } else {
            if (candidateValues.contains(value)) {
                return ValidationResult.success("候选取值验证通过");
            }
        }
        
        return ValidationResult.error("参数值必须是以下候选值之一: " + String.join(", ", candidateValues));
    }
    
    /**
     * 验证数值范围约束
     * @param parameter 参数模板
     * @param value 参数值
     * @return 验证结果
     */
    public ValidationResult validateRangeConstraint(ParameterTemplate parameter, String value) {
        String minValue = parameter.getMinValue();
        String maxValue = parameter.getMaxValue();
        
        if ((minValue == null || minValue.isEmpty()) && (maxValue == null || maxValue.isEmpty())) {
            return ValidationResult.success("无范围约束");
        }
        
        try {
            if (parameter.getParamType() == ParameterType.INTEGER) {
                long longValue = Long.parseLong(value);
                
                if (minValue != null && !minValue.isEmpty()) {
                    long minLong = Long.parseLong(minValue);
                    if (longValue < minLong) {
                        return ValidationResult.error("参数值不能小于 " + minValue);
                    }
                }
                
                if (maxValue != null && !maxValue.isEmpty()) {
                    long maxLong = Long.parseLong(maxValue);
                    if (longValue > maxLong) {
                        return ValidationResult.error("参数值不能大于 " + maxValue);
                    }
                }
                
            } else if (parameter.getParamType() == ParameterType.DECIMAL) {
                double doubleValue = Double.parseDouble(value);
                
                if (minValue != null && !minValue.isEmpty()) {
                    double minDouble = Double.parseDouble(minValue);
                    if (doubleValue < minDouble) {
                        return ValidationResult.error("参数值不能小于 " + minValue);
                    }
                }
                
                if (maxValue != null && !maxValue.isEmpty()) {
                    double maxDouble = Double.parseDouble(maxValue);
                    if (doubleValue > maxDouble) {
                        return ValidationResult.error("参数值不能大于 " + maxValue);
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            return ValidationResult.error("参数值格式错误，无法进行范围验证");
        }
        
        return ValidationResult.success("范围约束验证通过");
    }
    
    /**
     * 解析候选取值字符串
     * @param candidateValuesStr 候选取值字符串，支持多种格式
     * @return 候选取值列表
     */
    public List<String> parseCandidateValues(String candidateValuesStr) {
        List<String> values = new ArrayList<>();
        
        if (candidateValuesStr == null || candidateValuesStr.trim().isEmpty()) {
            return values;
        }
        
        // 移除可能的引号和空格
        String cleaned = candidateValuesStr.trim().replaceAll("^[\"']|[\"']$", "");
        
        // 支持多种分隔符：逗号、空格、中文逗号
        String[] parts = cleaned.split("[,，\\s]+");
        
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
        
        return values;
    }
    
    /**
     * 获取参数的约束信息
     * @param parameter 参数模板
     * @return 约束信息DTO
     */
    public ParameterConstraintDto getParameterConstraints(ParameterTemplate parameter) {
        if (parameter == null) {
            return new ParameterConstraintDto();
        }
        
        return new ParameterConstraintDto(
            parameter.getMinValue(),
            parameter.getMaxValue(),
            parameter.getCandidateValues(),
            parameter.getValueRange()
        );
    }
    
    /**
     * 标准化布尔值
     * @param value 原始值
     * @return 标准化后的值
     */
    private String normalizeBooleanValue(String value) {
        if (value == null) {
            return "";
        }
        
        String lower = value.toLowerCase().trim();
        switch (lower) {
            case "true":
            case "on":
            case "1":
                return "true";
            case "false":
            case "off":
            case "0":
                return "false";
            default:
                return lower;
        }
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final List<String> errors;
        
        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
            this.errors = new ArrayList<>();
            if (!valid && message != null) {
                this.errors.add(message);
            }
        }
        
        public static ValidationResult success(String message) {
            return new ValidationResult(true, message);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
        
        public void addError(String error) {
            this.errors.add(error);
        }
        
        @Override
        public String toString() {
            return "ValidationResult{" +
                    "valid=" + valid +
                    ", message='" + message + '\'' +
                    ", errors=" + errors +
                    '}';
        }
    }
}