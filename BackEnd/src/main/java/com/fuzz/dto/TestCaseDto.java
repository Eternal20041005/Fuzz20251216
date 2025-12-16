package com.fuzz.dto;

import java.math.BigDecimal;

/**
 * 测试用例数据传输对象
 * 用于前端展示的测试用例信息
 */
public class TestCaseDto {

    private String caseId;
    private String testTime;
    private String targetDb;
    private String oracleType;
    private boolean triggeredBug;
    private String bugId;
    private String paramCombo;
    private BigDecimal weightValue;
    private String sqlStatement;

    public TestCaseDto() {
    }

    // 构造函数
    public TestCaseDto(String caseId, String testTime, String targetDb, String oracleType, boolean triggeredBug,
                      String bugId, String paramCombo, BigDecimal weightValue, String sqlStatement) {
        this.caseId = caseId;
        this.testTime = testTime;
        this.targetDb = targetDb;
        this.oracleType = oracleType;
        this.triggeredBug = triggeredBug;
        this.bugId = bugId;
        this.paramCombo = paramCombo;
        this.weightValue = weightValue;
        this.sqlStatement = sqlStatement;
    }

    // Getter 和 Setter 方法
    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getTargetDb() {
        return targetDb;
    }

    public void setTargetDb(String targetDb) {
        this.targetDb = targetDb;
    }

    public String getOracleType() {
        return oracleType;
    }

    public void setOracleType(String oracleType) {
        this.oracleType = oracleType;
    }

    public boolean isTriggeredBug() {
        return triggeredBug;
    }

    public void setTriggeredBug(boolean triggeredBug) {
        this.triggeredBug = triggeredBug;
    }

    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public String getParamCombo() {
        return paramCombo;
    }

    public void setParamCombo(String paramCombo) {
        this.paramCombo = paramCombo;
    }

    public BigDecimal getWeightValue() {
        return weightValue;
    }

    public void setWeightValue(BigDecimal weightValue) {
        this.weightValue = weightValue;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }
}
