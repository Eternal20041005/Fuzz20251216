package com.fuzz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据库参数数据传输对象
 * 包含数据库参数的完整信息
 */
public class DbParameterDto {

    private Long id;
    private String dbType;
    private String paramName;
    private String paramType;
    private String description;
    private String defaultValue;
    private String valueRange;
    private BigDecimal weight;
    private BigDecimal coverage;
    private Boolean isTest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 为兼容前端而添加的字段（从现有数据结构推导）
    private String category; // 从参数名推导或设置为默认值
    private Boolean isTestDefault; // 对应 isTest
    private List<String> candidateValues; // 从 valueRange 解析
    private List<String> allowedValues; // 保持向后兼容
    private Double weightAsDouble; // 转换为 Double 类型
    private Double coverageAsDouble; // 转换为 Double 类型

    public DbParameterDto() {
    }

    public DbParameterDto(Long id, String dbType, String paramName, String paramType,
                         String description, String defaultValue, String valueRange,
                         BigDecimal weight, BigDecimal coverage, Boolean isTest,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.dbType = dbType;
        this.paramName = paramName;
        this.paramType = normalizeParamType(paramType); // 规范化参数类型
        this.description = description;
        this.defaultValue = defaultValue;
        this.valueRange = valueRange;
        this.weight = weight;
        this.coverage = coverage;
        this.isTest = isTest;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        // 设置兼容字段
        this.isTestDefault = isTest;
        this.weightAsDouble = weight != null ? weight.doubleValue() : 5.0;
        this.category = this.paramType; // 使用规范化的数据类型作为类别
        this.candidateValues = parseCandidateValues(valueRange);
        this.allowedValues = candidateValues;
    }

    /**
     * 规范化参数类型为标准MySQL参数类型
     */
    public static String normalizeParamType(String paramType) {
        if (paramType == null) return "String";

        switch (paramType.toUpperCase()) {
            case "INTEGER":
            case "INT":
                return "Integer";
            case "BOOLEAN":
            case "BOOL":
                return "Boolean";
            case "DECIMAL":
            case "DOUBLE":
            case "FLOAT":
            case "NUMERIC":
                return "Numeric";
            case "STRING":
            case "VARCHAR":
            case "TEXT":
                return "String";
            case "ENUM":
            case "ENUMERATION":
                return "Enumeration";
            case "SET":
                return "Set";
            case "BITMAP":
                return "Bitmap";
            case "FILE":
            case "FILENAME":
                return "File name";
            case "DIRECTORY":
            case "DIR":
                return "Directory name";
            default:
                return "String"; // 默认归为String类型
        }
    }

    /**
     * 从参数名推导类别
     */
    public static String inferCategory(String paramName) {
        if (paramName == null) return "OTHER";

        String lowerName = paramName.toLowerCase();

        // InnoDB相关
        if (lowerName.startsWith("innodb_")) return "INNODB";

        // 内存相关
        if (lowerName.contains("buffer") || lowerName.contains("cache") ||
            lowerName.contains("size") || lowerName.contains("memory")) return "MEMORY";

        // 连接相关
        if (lowerName.contains("connect") || lowerName.contains("thread") ||
            lowerName.contains("timeout")) return "CONNECTION";

        // 查询优化相关
        if (lowerName.contains("optimizer") || lowerName.contains("query") ||
            lowerName.startsWith("eq_") || lowerName.contains("search")) return "OPTIMIZER";

        // 系统相关
        if (lowerName.contains("table") || lowerName.contains("key") ||
            lowerName.contains("sort") || lowerName.contains("read")) return "SYSTEM";

        // SQL模式相关
        if (lowerName.contains("sql_") || lowerName.contains("mode")) return "SQL_MODE";

        // 存储引擎相关
        if (lowerName.contains("engine") || lowerName.contains("myisam") ||
            lowerName.contains("storage")) return "ENGINE";

        return "OTHER";
    }

    /**
     * 从 valueRange 解析候选取值
     */
    private List<String> parseCandidateValues(String valueRange) {
        // 这里可以根据实际的 valueRange 格式来解析
        // 暂时返回空列表，前端可以根据需要处理
        return java.util.Collections.emptyList();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueRange() {
        return valueRange;
    }

    public void setValueRange(String valueRange) {
        this.valueRange = valueRange;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
        this.weightAsDouble = weight != null ? weight.doubleValue() : 5.0;
    }

    public BigDecimal getCoverage() {
        return coverage;
    }

    public void setCoverage(BigDecimal coverage) {
        this.coverage = coverage;
    }

    public Double getCoverageAsDouble() {
        return coverage != null ? coverage.doubleValue() : 0.0;
    }

    public void setCoverageAsDouble(Double coverageAsDouble) {
        this.coverage = coverageAsDouble != null ? BigDecimal.valueOf(coverageAsDouble) : BigDecimal.ZERO;
    }

    public Boolean getIsTest() {
        return isTest;
    }

    public void setIsTest(Boolean isTest) {
        this.isTest = isTest;
        this.isTestDefault = isTest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 兼容性字段
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsTestDefault() {
        return isTestDefault;
    }

    public void setIsTestDefault(Boolean isTestDefault) {
        this.isTestDefault = isTestDefault;
        this.isTest = isTestDefault;
    }

    public List<String> getCandidateValues() {
        return candidateValues;
    }

    public void setCandidateValues(List<String> candidateValues) {
        this.candidateValues = candidateValues;
        this.allowedValues = candidateValues;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues;
        this.candidateValues = allowedValues;
    }

    public Double getWeightAsDouble() {
        return weightAsDouble;
    }

    public void setWeightAsDouble(Double weightAsDouble) {
        this.weightAsDouble = weightAsDouble;
        this.weight = weightAsDouble != null ? BigDecimal.valueOf(weightAsDouble) : null;
    }

}
