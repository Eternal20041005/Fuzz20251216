package com.fuzz.dto;

import com.fuzz.entity.ParameterType;

import java.util.List;

/**
 * 数据库参数值对象
 */
public class DatabaseParameter {
    
    private String name;
    private String value;
    private String description;
    private ParameterType type;
    private String minValue;
    private String maxValue;
    private List<String> allowedValues;
    private String category;
    
    public DatabaseParameter() {
    }
    
    public DatabaseParameter(String name, String value, String category) {
        this.name = name;
        this.value = value;
        this.category = category;
    }
    
    public DatabaseParameter(String name, String value, String description, 
                           ParameterType type, String category) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.type = type;
        this.category = category;
    }
    
    // Getter和Setter
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ParameterType getType() {
        return type;
    }
    
    public void setType(ParameterType type) {
        this.type = type;
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
    
    public List<String> getAllowedValues() {
        return allowedValues;
    }
    
    public void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}