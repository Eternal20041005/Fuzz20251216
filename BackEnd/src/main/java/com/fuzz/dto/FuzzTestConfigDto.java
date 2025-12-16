package com.fuzz.dto;

import java.time.LocalDateTime;

/**
 * 模糊测试配置数据传输对象
 */
public class FuzzTestConfigDto {

    private Long id;
    private String configName;
    private String description;

    // 测试Oracle类型
    private String testOracle;

    // 基础参数
    private Integer maxExpressionDepth;
    private Integer numTries;
    private Integer timeoutSeconds;
    private Long randomSeed;
    private Integer numQueries;
    private Integer maxNumInserts;
    private Integer maxGeneratedDatabases;

    // 数据库连接参数
    private String username;
    private String password;
    private String host;
    private Integer port;

    // 时间戳
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FuzzTestConfigDto() {
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTestOracle() {
        return testOracle;
    }

    public void setTestOracle(String testOracle) {
        this.testOracle = testOracle;
    }

    public Integer getMaxExpressionDepth() {
        return maxExpressionDepth;
    }

    public void setMaxExpressionDepth(Integer maxExpressionDepth) {
        this.maxExpressionDepth = maxExpressionDepth;
    }

    public Integer getNumTries() {
        return numTries;
    }

    public void setNumTries(Integer numTries) {
        this.numTries = numTries;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(Long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public Integer getNumQueries() {
        return numQueries;
    }

    public void setNumQueries(Integer numQueries) {
        this.numQueries = numQueries;
    }

    public Integer getMaxNumInserts() {
        return maxNumInserts;
    }

    public void setMaxNumInserts(Integer maxNumInserts) {
        this.maxNumInserts = maxNumInserts;
    }

    public Integer getMaxGeneratedDatabases() {
        return maxGeneratedDatabases;
    }

    public void setMaxGeneratedDatabases(Integer maxGeneratedDatabases) {
        this.maxGeneratedDatabases = maxGeneratedDatabases;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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
        return "FuzzTestConfigDto{" +
                "id=" + id +
                ", configName='" + configName + '\'' +
                ", testOracle='" + testOracle + '\'' +
                ", numQueries=" + numQueries +
                '}';
    }
}
