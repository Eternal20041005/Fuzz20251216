package com.fuzz.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Bug报告数据传输对象
 * 用于前端展示的Bug报告信息
 */
public class BugReportDto {

    private Long id;
    private String bugType; // "崩溃" 或 "逻辑"
    private String targetDatabase; // MySQL, PostgreSQL, Oracle, SQL Server等
    private String oracleType; // TLP, RBR, CBR
    private LocalDateTime testTime;
    private String testCase; // 测试样例SQL
    private String errorMessage;
    private String formattedParameterSettings; // 格式化的参数设置字符串
    private Map<String, String> parameterSettings; // 参数设置Map
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BugReportDto() {
    }

    // 构造函数
    public BugReportDto(Long id, String bugType, String targetDatabase, String oracleType,
                       LocalDateTime testTime, String testCase, String errorMessage,
                       String formattedParameterSettings, Map<String, String> parameterSettings,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bugType = bugType;
        this.targetDatabase = targetDatabase;
        this.oracleType = oracleType;
        this.testTime = testTime;
        this.testCase = testCase;
        this.errorMessage = errorMessage;
        this.formattedParameterSettings = formattedParameterSettings;
        this.parameterSettings = parameterSettings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFormattedParameterSettings() {
        return formattedParameterSettings;
    }

    public void setFormattedParameterSettings(String formattedParameterSettings) {
        this.formattedParameterSettings = formattedParameterSettings;
    }

    public Map<String, String> getParameterSettings() {
        return parameterSettings;
    }

    public void setParameterSettings(Map<String, String> parameterSettings) {
        this.parameterSettings = parameterSettings;
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
        return "BugReportDto{" +
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
