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
import java.util.HashMap;
import java.util.Map;

/**
 * Bug报告实体类
 * 记录模糊测试发现的Bug信息，包括参数设置和测试样例
 */
@Entity
@Table(name = "bug_report")
public class BugReport {

    private static final Logger logger = LoggerFactory.getLogger(BugReport.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bug类型不能为空")
    @Size(max = 20, message = "Bug类型长度不能超过20个字符")
    @Column(name = "bug_type", nullable = false, length = 20)
    private String bugType; // "崩溃" 或 "逻辑"

    @NotBlank(message = "目标数据库不能为空")
    @Size(max = 50, message = "目标数据库长度不能超过50个字符")
    @Column(name = "target_database", nullable = false, length = 50)
    private String targetDatabase; // MySQL, PostgreSQL, Oracle, SQL Server等

    @NotBlank(message = "Oracle类型不能为空")
    @Size(max = 10, message = "Oracle类型长度不能超过10个字符")
    @Column(name = "oracle_type", nullable = false, length = 10)
    private String oracleType; // TLP, RBR, CBR

    @NotNull(message = "测试时间不能为空")
    @Column(name = "test_time", nullable = false)
    private LocalDateTime testTime;

    @Lob
    @Column(name = "test_case", columnDefinition = "TEXT")
    private String testCase; // 测试样例SQL

    @Lob
    @Column(name = "parameter_settings", columnDefinition = "TEXT")
    private String parameterSettingsJson; // 存储为JSON字符串的参数设置

    @Size(max = 500, message = "错误信息长度不能超过500个字符")
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 构造函数
    public BugReport() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BugReport(String bugType, String targetDatabase, String oracleType,
                    String testCase, Map<String, String> parameterSettings) {
        this();
        this.bugType = bugType;
        this.targetDatabase = targetDatabase;
        this.oracleType = oracleType;
        this.testCase = testCase;
        this.testTime = LocalDateTime.now();
        this.setParameterSettings(parameterSettings);
    }

    // 参数设置的 getter/setter（JSON序列化）
    public Map<String, String> getParameterSettings() {
        if (parameterSettingsJson == null || parameterSettingsJson.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(parameterSettingsJson,
                    new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            logger.error("解析参数设置JSON失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public void setParameterSettings(Map<String, String> parameterSettings) {
        try {
            this.parameterSettingsJson = objectMapper.writeValueAsString(parameterSettings);
        } catch (JsonProcessingException e) {
            logger.error("序列化参数设置JSON失败: {}", e.getMessage());
            this.parameterSettingsJson = "{}";
        }
    }

    // 参数设置格式化的字符串表示（用于前端显示）
    public String getFormattedParameterSettings() {
        Map<String, String> settings = getParameterSettings();
        if (settings.isEmpty()) {
            return "// 无参数设置信息";
        }

        StringBuilder sb = new StringBuilder("// Bug发现时的参数设置\n");
        settings.forEach((key, value) -> {
            sb.append(key).append(" = ").append(value).append("\n");
        });
        return sb.toString();
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBugType() {
        return bugType;
    }

    public void setBugType(String bugType) {
        this.bugType = bugType;
    }

    public String getTargetDatabase() {
        return targetDatabase;
    }

    public void setTargetDatabase(String targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    public String getOracleType() {
        return oracleType;
    }

    public void setOracleType(String oracleType) {
        this.oracleType = oracleType;
    }

    public LocalDateTime getTestTime() {
        return testTime;
    }

    public void setTestTime(LocalDateTime testTime) {
        this.testTime = testTime;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getParameterSettingsJson() {
        return parameterSettingsJson;
    }

    public void setParameterSettingsJson(String parameterSettingsJson) {
        this.parameterSettingsJson = parameterSettingsJson;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "BugReport{" +
                "id=" + id +
                ", bugType='" + bugType + '\'' +
                ", targetDatabase='" + targetDatabase + '\'' +
                ", oracleType='" + oracleType + '\'' +
                ", testTime=" + testTime +
                ", testCase='" + (testCase != null ? testCase.substring(0, Math.min(50, testCase.length())) + "..." : null) + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
