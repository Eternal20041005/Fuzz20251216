package com.fuzz.dto;

import java.util.List;

/**
 * 参数约束信息DTO
 * 封装参数的约束条件，包括范围限制和候选取值
 */
public class ParameterConstraintDto {
    
    private String minValue;
    private String maxValue;
    private List<String> candidateValues;
    private String valueRange; // 设置范围：Global, Both, Session
    private boolean hasRangeConstraint;
    private boolean hasCandidateConstraint;
    
    public ParameterConstraintDto() {
    }
    
    public ParameterConstraintDto(String minValue, String maxValue, List<String> candidateValues, String valueRange) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.candidateValues = candidateValues;
        this.valueRange = valueRange;
        this.hasRangeConstraint = (minValue != null && !minValue.isEmpty()) || (maxValue != null && !maxValue.isEmpty());
        this.hasCandidateConstraint = candidateValues != null && !candidateValues.isEmpty();
    }
    
    public String getMinValue() {
        return minValue;
    }
    
    public void setMinValue(String minValue) {
        this.minValue = minValue;
        updateConstraintFlags();
    }
    
    public String getMaxValue() {
        return maxValue;
    }
    
    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
        updateConstraintFlags();
    }
    
    public List<String> getCandidateValues() {
        return candidateValues;
    }
    
    public void setCandidateValues(List<String> candidateValues) {
        this.candidateValues = candidateValues;
        updateConstraintFlags();
    }
    
    public String getValueRange() {
        return valueRange;
    }
    
    public void setValueRange(String valueRange) {
        this.valueRange = valueRange;
    }
    
    public boolean isHasRangeConstraint() {
        return hasRangeConstraint;
    }
    
    public boolean isHasCandidateConstraint() {
        return hasCandidateConstraint;
    }
    
    /**
     * 获取范围描述字符串
     * @return 范围描述，如 "1 - 100" 或 "≥ 0"
     */
    public String getRangeDescription() {
        if (!hasRangeConstraint) {
            return null;
        }
        
        if (minValue != null && maxValue != null) {
            return minValue + " - " + maxValue;
        } else if (minValue != null) {
            return "≥ " + minValue;
        } else if (maxValue != null) {
            return "≤ " + maxValue;
        }
        
        return null;
    }
    
    /**
     * 获取候选取值的显示字符串
     * @return 候选取值字符串，如 "ON, OFF" 或 "1, 2, 3"
     */
    public String getCandidateValuesDisplay() {
        if (!hasCandidateConstraint) {
            return null;
        }
        
        return String.join(", ", candidateValues);
    }
    
    /**
     * 检查是否有任何约束条件
     * @return 如果有约束条件则返回true
     */
    public boolean hasAnyConstraint() {
        return hasRangeConstraint || hasCandidateConstraint;
    }
    
    private void updateConstraintFlags() {
        this.hasRangeConstraint = (minValue != null && !minValue.isEmpty()) || (maxValue != null && !maxValue.isEmpty());
        this.hasCandidateConstraint = candidateValues != null && !candidateValues.isEmpty();
    }
    
    @Override
    public String toString() {
        return "ParameterConstraintDto{" +
                "minValue='" + minValue + '\'' +
                ", maxValue='" + maxValue + '\'' +
                ", candidateValues=" + candidateValues +
                ", valueRange='" + valueRange + '\'' +
                ", hasRangeConstraint=" + hasRangeConstraint +
                ", hasCandidateConstraint=" + hasCandidateConstraint +
                '}';
    }
}