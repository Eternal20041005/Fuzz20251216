package com.fuzz.dto;

import java.util.List;

/**
 * 批量更新结果
 */
public class BatchUpdateResult {
    
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<String> errorMessages;
    
    public BatchUpdateResult() {
    }
    
    public BatchUpdateResult(int totalCount, int successCount, int failureCount) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }
    
    // Getter和Setter
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    
    public int getFailureCount() {
        return failureCount;
    }
    
    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}