package com.fuzz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 测试用例实体类
 * 记录所有经过测试的测试用例信息
 */
@Entity
@Table(name = "test_case")
public class TestCase {

    @Id
    @NotBlank(message = "测试用例ID不能为空")
    @Size(max = 36, message = "测试用例ID长度不能超过36个字符")
    @Column(name = "case_id", nullable = false, length = 36, unique = true)
    private String caseId;

    @NotBlank(message = "测试时间不能为空")
    @Size(max = 20, message = "测试时间长度不能超过20个字符")
    @Column(name = "test_time", nullable = false, length = 20)
    private String testTime;

    @NotBlank(message = "目标数据库不能为空")
    @Size(max = 20, message = "目标数据库长度不能超过20个字符")
    @Column(name = "target_db", nullable = false, length = 20)
    private String targetDb;

    @NotBlank(message = "Oracle类型不能为空")
    @Size(max = 50, message = "Oracle类型长度不能超过50个字符")
    @Column(name = "oracle_type", nullable = false, length = 50)
    private String oracleType;

    @NotNull(message = "是否触发漏洞不能为空")
    @Column(name = "triggered_bug", nullable = false)
    private boolean triggeredBug;

    @Size(max = 50, message = "Bug ID长度不能超过50个字符")
    @Column(name = "bug_id", length = 50)
    private String bugId;

    @Lob
    @Column(name = "param_combo", columnDefinition = "TEXT")
    private String paramCombo;

    @NotNull(message = "权重值不能为空")
    @DecimalMin(value = "0.0000", message = "权重值必须大于等于0")
    @Column(name = "weight_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal weightValue;

    @NotBlank(message = "SQL语句不能为空")
    @Lob
    @Column(name = "sql_statement", nullable = false, columnDefinition = "TEXT")
    private String sqlStatement;

    // 构造函数
    public TestCase() {
    }

    // Getters and Setters
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
