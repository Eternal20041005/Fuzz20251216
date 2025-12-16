package com.fuzz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试状态实体类
 */
@Entity
@Table(name = "test_status")
public class TestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "任务状态不能为空")
    @Size(max = 20, message = "任务状态长度不能超过20个字符")
    @Column(name = "task_status", nullable = false, length = 20)
    private String taskStatus;

    @NotBlank(message = "测试Oracle不能为空")
    @Size(max = 50, message = "测试Oracle长度不能超过50个字符")
    @Column(name = "test_oracle", nullable = false, length = 50)
    private String testOracle;

    @NotNull(message = "测试时间不能为空")
    @Min(value = 0, message = "测试时间不能小于0")
    @Column(name = "run_time", nullable = false)
    private Integer runTime;

    @NotNull(message = "覆盖率不能为空")
    @DecimalMin(value = "0.0", message = "覆盖率不能小于0")
    @DecimalMax(value = "100.0", message = "覆盖率不能大于100")
    @Column(name = "coverage_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal coverageRate;

    @NotNull(message = "Bug数量不能为空")
    @Min(value = 0, message = "Bug数量不能小于0")
    @Column(name = "bug_count", nullable = false)
    private Integer bugCount;

    @NotNull(message = "执行次数不能为空")
    @Min(value = 0, message = "执行次数不能小于0")
    @Column(name = "execution_count", nullable = false)
    private Integer executionCount;

    @Size(max = 500, message = "当前参数组合长度不能超过500个字符")
    @Column(name = "current_param_combo", length = 500)
    private String currentParamCombo;

    @NotNull(message = "吞吐量不能为空")
    @DecimalMin(value = "0.0", message = "吞吐量不能小于0")
    @Column(name = "throughput", nullable = false, precision = 8, scale = 2)
    private BigDecimal throughput;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // getter and setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTestOracle() {
        return testOracle;
    }

    public void setTestOracle(String testOracle) {
        this.testOracle = testOracle;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public BigDecimal getCoverageRate() {
        return coverageRate;
    }

    public void setCoverageRate(BigDecimal coverageRate) {
        this.coverageRate = coverageRate;
    }

    public Integer getBugCount() {
        return bugCount;
    }

    public void setBugCount(Integer bugCount) {
        this.bugCount = bugCount;
    }

    public Integer getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(Integer executionCount) {
        this.executionCount = executionCount;
    }

    public String getCurrentParamCombo() {
        return currentParamCombo;
    }

    public void setCurrentParamCombo(String currentParamCombo) {
        this.currentParamCombo = currentParamCombo;
    }

    public BigDecimal getThroughput() {
        return throughput;
    }

    public void setThroughput(BigDecimal throughput) {
        this.throughput = throughput;
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
}