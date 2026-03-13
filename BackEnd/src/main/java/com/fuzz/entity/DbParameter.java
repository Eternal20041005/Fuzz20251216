package com.fuzz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数据库参数实体类
 * 支持多种数据库的参数配置信息
 */
@Entity
@Table(name = "db_parameter",
       uniqueConstraints = @UniqueConstraint(columnNames = {"db_type", "param_name"}))
public class DbParameter {

    private static final Logger logger = LoggerFactory.getLogger(DbParameter.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "数据库类型不能为空")
    @Size(max = 20, message = "数据库类型长度不能超过20个字符")
    @Column(name = "db_type", nullable = false, length = 20)
    private String dbType;

    @NotBlank(message = "参数名不能为空")
    @Size(max = 100, message = "参数名长度不能超过100个字符")
    @Column(name = "param_name", nullable = false, length = 100)
    private String paramName;

    @NotBlank(message = "参数类型不能为空")
    @Size(max = 20, message = "参数类型长度不能超过20个字符")
    @Column(name = "param_type", nullable = false, length = 20)
    private String paramType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 255, message = "默认值长度不能超过255个字符")
    @Column(name = "default_value", length = 255)
    private String defaultValue;

    @Size(max = 500, message = "取值范围长度不能超过500个字符")
    @Column(name = "value_range", length = 500)
    private String valueRange;

    @NotNull(message = "权重不能为空")
    @DecimalMin(value = "0.0000", message = "权重不能小于0")
    @DecimalMax(value = "10.0000", message = "权重不能大于10")
    @Column(name = "weight", nullable = false, precision = 10, scale = 4)
    private BigDecimal weight = new BigDecimal("5.0000");

    @Column(name = "coverage", precision = 5, scale = 2)
    private BigDecimal coverage = new BigDecimal("0.00");

    @NotNull(message = "是否测试不能为空")
    @Column(name = "is_test", nullable = false)
    private Boolean isTest = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 构造函数
    public DbParameter() {
    }

    public DbParameter(String dbType, String paramName, String paramType, String description,
                      String defaultValue, String valueRange, BigDecimal weight, Boolean isTest) {
        this.dbType = dbType;
        this.paramName = paramName;
        this.paramType = paramType;
        this.description = description;
        this.defaultValue = defaultValue;
        this.valueRange = valueRange;
        this.weight = weight != null ? weight : new BigDecimal("5.0000");
        this.isTest = isTest != null ? isTest : false;
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
    }

    public BigDecimal getCoverage() {
        return coverage;
    }

    public void setCoverage(BigDecimal coverage) {
        this.coverage = coverage;
    }

    public Boolean getIsTest() {
        return isTest;
    }

    public void setIsTest(Boolean isTest) {
        this.isTest = isTest;
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

    @Override
    public String toString() {
        return "DbParameter{" +
                "id=" + id +
                ", dbType='" + dbType + '\'' +
                ", paramName='" + paramName + '\'' +
                ", paramType='" + paramType + '\'' +
                ", description='" + description + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", valueRange='" + valueRange + '\'' +
                ", weight=" + weight +
                ", isTest=" + isTest +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbParameter that = (DbParameter) o;
        return dbType != null ? dbType.equals(that.dbType) : that.dbType == null &&
               paramName != null ? paramName.equals(that.paramName) : that.paramName == null;
    }

    @Override
    public int hashCode() {
        int result = dbType != null ? dbType.hashCode() : 0;
        result = 31 * result + (paramName != null ? paramName.hashCode() : 0);
        return result;
    }
}