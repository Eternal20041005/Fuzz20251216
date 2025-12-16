package com.fuzz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 更新参数请求对象
 */
public class UpdateParameterRequest {
    
    @NotNull(message = "参数ID不能为空")
    private Long id;
    
    private String defaultValue;
    
    private Boolean isTestDefault;
    
    private String description;

    private Double weight;

    public UpdateParameterRequest() {
    }
    
    public UpdateParameterRequest(Long id, String defaultValue, Boolean isTestDefault) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.isTestDefault = isTestDefault;
    }
    
    // Getter和Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public Boolean getIsTestDefault() {
        return isTestDefault;
    }
    
    public void setIsTestDefault(Boolean isTestDefault) {
        this.isTestDefault = isTestDefault;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}