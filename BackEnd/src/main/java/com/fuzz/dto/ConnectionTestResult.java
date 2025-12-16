package com.fuzz.dto;

/**
 * 数据库连接测试结果
 */
public class ConnectionTestResult {
    
    private boolean success;
    private String message;
    private String dbVersion;
    private Long responseTime; // 毫秒
    
    public ConnectionTestResult() {
    }
    
    public ConnectionTestResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ConnectionTestResult(boolean success, String message, String dbVersion, Long responseTime) {
        this.success = success;
        this.message = message;
        this.dbVersion = dbVersion;
        this.responseTime = responseTime;
    }
    
    // 静态工厂方法
    public static ConnectionTestResult success(String dbVersion, Long responseTime) {
        return new ConnectionTestResult(true, "连接成功", dbVersion, responseTime);
    }
    
    public static ConnectionTestResult failure(String message) {
        return new ConnectionTestResult(false, message);
    }
    
    // Getter和Setter
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDbVersion() {
        return dbVersion;
    }
    
    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }
    
    public Long getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }
}