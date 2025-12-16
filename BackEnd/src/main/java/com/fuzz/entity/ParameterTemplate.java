package com.fuzz.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 参数模板实体类
 * 支持MySQL参数的完整配置信息，包括约束条件和候选取值
 */
@Entity
@Table(name = "parameter_template", 
       uniqueConstraints = @UniqueConstraint(columnNames = "param_name"))
public class ParameterTemplate {
    
    private static final Logger logger = LoggerFactory.getLogger(ParameterTemplate.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "参数名不能为空")
    @Size(max = 100, message = "参数名长度不能超过100个字符")
    @Column(name = "param_name", nullable = false, unique = true, length = 100)
    private String paramName;

    @Size(max = 200, message = "描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    @NotBlank(message = "类别不能为空")
    @Size(max = 50, message = "类别长度不能超过50个字符")
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Size(max = 100, message = "默认值长度不能超过100个字符")
    @Column(name = "default_value", length = 100)
    private String defaultValue;

    @NotNull(message = "参数类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "param_type", nullable = false, length = 20)
    private ParameterType paramType;

    @Column(name = "is_test_default")
    private Boolean isTestDefault = true;

    @Size(max = 50, message = "最小值长度不能超过50个字符")
    @Column(name = "min_value", length = 50)
    private String minValue;

    @Size(max = 50, message = "最大值长度不能超过50个字符")
    @Column(name = "max_value", length = 50)
    private String maxValue;

    @Size(max = 1000, message = "允许值长度不能超过1000个字符")
    @Column(name = "allowed_values", length = 1000)
    private String allowedValues; // JSON格式存储候选取值

    @Size(max = 200, message = "设置范围描述长度不能超过200个字符")
    @Column(name = "value_range", length = 200)
    private String valueRange; // 设置范围描述，如"Global", "Both", "Session"

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 权重字段（假设为整数类型，可根据实际需求调整类型）
    @Column(name = "weight")
    private Double weight;

    // 覆盖率字段（假设为百分比的小数形式，如0.95表示95%）
    @Column(name = "coverage")
    private Double coverage;


    // 构造函数
    public ParameterTemplate() {
    }

    public ParameterTemplate(String paramName, String description, String category, 
                           String defaultValue, ParameterType paramType) {
        this.paramName = paramName;
        this.description = description;
        this.category = category;
        this.defaultValue = defaultValue;
        this.paramType = paramType;
    }

    public ParameterTemplate(String paramName, String description, String category, 
                           String defaultValue, ParameterType paramType, String valueRange,
                           List<String> candidateValues) {
        this.paramName = paramName;
        this.description = description;
        this.category = category;
        this.defaultValue = defaultValue;
        this.paramType = paramType;
        this.valueRange = valueRange;
        this.setCandidateValues(candidateValues);
    }

    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ParameterType getParamType() {
        return paramType;
    }

    public void setParamType(ParameterType paramType) {
        this.paramType = paramType;
    }

    public Boolean getIsTestDefault() {
        return isTestDefault;
    }

    public void setIsTestDefault(Boolean isTestDefault) {
        this.isTestDefault = isTestDefault;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(String allowedValues) {
        this.allowedValues = allowedValues;
    }

    public String getValueRange() {
        return valueRange;
    }

    public void setValueRange(String valueRange) {
        this.valueRange = valueRange;
    }
    
    public Double getWeight() {
    return weight;
    }

    public void setWeight(Double weight) {
    this.weight = weight;
    }

    public Double getCoverage() {
    return coverage;
    }

    public void setCoverage(Double coverage) {
    this.coverage = coverage;
    }
    /**
     * 获取候选取值列表
     * @return 候选取值列表，如果解析失败则返回空列表
     */
    public List<String> getCandidateValues() {
        if (allowedValues == null || allowedValues.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return objectMapper.readValue(allowedValues, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse candidate values for parameter {}: {}", paramName, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 设置候选取值列表
     * @param candidateValues 候选取值列表
     */
    public void setCandidateValues(List<String> candidateValues) {
        if (candidateValues == null || candidateValues.isEmpty()) {
            this.allowedValues = null;
            return;
        }
        
        try {
            this.allowedValues = objectMapper.writeValueAsString(candidateValues);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize candidate values for parameter {}: {}", paramName, e.getMessage());
            this.allowedValues = null;
        }
    }

    /**
     * 检查给定值是否在候选取值范围内
     * @param value 要检查的值
     * @return 如果值有效则返回true，否则返回false
     */
    public boolean isValidCandidateValue(String value) {
        if (value == null) {
            return false;
        }
        
        List<String> candidates = getCandidateValues();
        if (candidates.isEmpty()) {
            return true; // 如果没有候选取值限制，则认为任何值都有效
        }
        
        return candidates.contains(value);
    }

    /**
     * 检查给定值是否在数值范围内
     * @param value 要检查的值
     * @return 如果值在范围内则返回true，否则返回false
     */
    public boolean isValidRangeValue(String value) {
        if (value == null || (minValue == null && maxValue == null)) {
            return true;
        }
        
        try {
            if (paramType == ParameterType.INTEGER) {
                long longValue = Long.parseLong(value);
                if (minValue != null && longValue < Long.parseLong(minValue)) {
                    return false;
                }
                if (maxValue != null && longValue > Long.parseLong(maxValue)) {
                    return false;
                }
            } else if (paramType == ParameterType.DECIMAL) {
                double doubleValue = Double.parseDouble(value);
                if (minValue != null && doubleValue < Double.parseDouble(minValue)) {
                    return false;
                }
                if (maxValue != null && doubleValue > Double.parseDouble(maxValue)) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format for parameter {}: {}", paramName, value);
            return false;
        }
    }

    /**
     * 验证参数值的完整性
     * @param value 要验证的值
     * @return 如果值有效则返回true，否则返回false
     */
    public boolean validateValue(String value) {
        return isValidCandidateValue(value) && isValidRangeValue(value);
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ParameterTemplate{" +
                "id=" + id +
                ", paramName='" + paramName + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", paramType=" + paramType +
                ", isTestDefault=" + isTestDefault +
                ", valueRange='" + valueRange + '\'' +
                ", minValue='" + minValue + '\'' +
                ", maxValue='" + maxValue + '\'' +
                ", candidateValuesCount=" + getCandidateValues().size() +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ParameterTemplate that = (ParameterTemplate) o;
        return paramName != null ? paramName.equals(that.paramName) : that.paramName == null;
    }

    @Override
    public int hashCode() {
        return paramName != null ? paramName.hashCode() : 0;
    }
}