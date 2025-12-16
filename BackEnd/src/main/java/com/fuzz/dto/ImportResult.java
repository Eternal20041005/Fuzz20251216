package com.fuzz.dto;

import java.util.List;

/**
 * 导入结果
 */
public class ImportResult {
    
    private boolean success;
    private String message;
    private int totalCount;
    private int importedCount;
    private int updatedCount;
    private int skippedCount;
    private List<String> errorMessages;
    
    public ImportResult() {
    }
    
    public ImportResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ImportResult(boolean success, String message, int totalCount, 
                       int importedCount, int updatedCount) {
        this.success = success;
        this.message = message;
        this.totalCount = totalCount;
        this.importedCount = importedCount;
        this.updatedCount = updatedCount;
    }
    
    // 静态工厂方法
    public static ImportResult success(int totalCount, int importedCount, int updatedCount) {
        return new ImportResult(true, "导入成功", totalCount, importedCount, updatedCount);
    }
    
    public static ImportResult failure(String message) {
        return new ImportResult(false, message);
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
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getImportedCount() {
        return importedCount;
    }
    
    public void setImportedCount(int importedCount) {
        this.importedCount = importedCount;
    }
    
    public int getUpdatedCount() {
        return updatedCount;
    }
    
    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }
    
    public int getSkippedCount() {
        return skippedCount;
    }
    
    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    // 链式调用方法
    public ImportResult withMessage(String message) {
        this.message = message;
        return this;
    }
}