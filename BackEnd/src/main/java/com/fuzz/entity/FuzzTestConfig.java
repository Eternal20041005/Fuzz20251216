package com.fuzz.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * 模糊测试配置实体类
 * 存储模糊测试参数的默认配置
 */
@Entity
@Table(name = "fuzz_config")
public class FuzzTestConfig {

    private static final Logger logger = LoggerFactory.getLogger(FuzzTestConfig.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过100个字符")
    @Column(name = "config_name", nullable = false, length = 100, unique = true)
    private String configName;

    @Size(max = 200, message = "配置描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    // 测试Oracle类型
    @NotBlank(message = "测试Oracle不能为空")
    @Size(max = 50, message = "测试Oracle长度不能超过50个字符")
    @Column(name = "test_oracle", nullable = false, length = 50)
    private String testOracle;

    // 基础参数
    @NotNull(message = "随机种子不能为空")
    @Column(name = "random_seed", nullable = false)
    private Long randomSeed;

    @NotNull(message = "最大表达式深度不能为空")
    @Column(name = "max_expr_depth", nullable = false)
    private Integer maxExpressionDepth;

    @NotNull(message = "查询数量不能为空")
    @Column(name = "max_queries", nullable = false)
    private Integer numQueries;

    @NotNull(message = "尝试次数不能为空")
    @Column(name = "max_retries", nullable = false)
    private Integer numTries;

    @NotNull(message = "超时时间不能为空")
    @Column(name = "timeout_seconds", nullable = false)
    private Integer timeoutSeconds;

    @NotNull(message = "最大插入数量不能为空")
    @Column(name = "max_insert_rows", nullable = false)
    private Integer maxNumInserts;

    @NotNull(message = "最大生成数据库数不能为空")
    @Column(name = "max_data_ordinal", nullable = false)
    private Integer maxGeneratedDatabases;

    // 数据库连接参数
    @Size(max = 100, message = "数据库用户名长度不能超过100个字符")
    @Column(name = "db_username", length = 100)
    private String username;

    @Size(max = 500, message = "数据库密码长度不能超过500个字符")
    @Column(name = "db_password", length = 500)
    private String password;

    @Size(max = 100, message = "数据库主机长度不能超过100个字符")
    @Column(name = "db_host", length = 100)
    private String host;

    @Column(name = "db_port")
    private Integer port;

    // 时间戳
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 构造函数
    public FuzzTestConfig() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public FuzzTestConfig(String configName) {
        this();
        this.configName = configName;
        // 设置默认值
        setDefaultValues();
    }

    // 设置默认值的方法
    public void setDefaultValues() {
        this.testOracle = "TLP";
        this.randomSeed = -1L;
        this.maxExpressionDepth = 3;
        this.numQueries = 10000;
        this.maxNumInserts = 30;
        this.numTries = 100;
        this.timeoutSeconds = 30;
        this.maxGeneratedDatabases = 0;
        this.username = "sqlancer";
        this.password = "sqlancer";
        this.host = "";
        this.port = -1;
    }

    // 从Map更新配置
    public void updateFromMap(java.util.Map<String, Object> configMap) {
        if (configMap.containsKey("testOracle"))
            this.testOracle = (String) configMap.get("testOracle");
        if (configMap.containsKey("randomSeed"))
            this.randomSeed = ((Number) configMap.get("randomSeed")).longValue();
        if (configMap.containsKey("maxExpressionDepth"))
            this.maxExpressionDepth = ((Number) configMap.get("maxExpressionDepth")).intValue();
        if (configMap.containsKey("numQueries"))
            this.numQueries = ((Number) configMap.get("numQueries")).intValue();
        if (configMap.containsKey("maxNumInserts"))
            this.maxNumInserts = ((Number) configMap.get("maxNumInserts")).intValue();
        if (configMap.containsKey("numTries"))
            this.numTries = ((Number) configMap.get("numTries")).intValue();
        if (configMap.containsKey("timeoutSeconds"))
            this.timeoutSeconds = ((Number) configMap.get("timeoutSeconds")).intValue();
        if (configMap.containsKey("maxGeneratedDatabases"))
            this.maxGeneratedDatabases = ((Number) configMap.get("maxGeneratedDatabases")).intValue();
        if (configMap.containsKey("username"))
            this.username = (String) configMap.get("username");
        if (configMap.containsKey("password"))
            this.password = (String) configMap.get("password");
        if (configMap.containsKey("host"))
            this.host = (String) configMap.get("host");
        if (configMap.containsKey("port"))
            this.port = ((Number) configMap.get("port")).intValue();

        this.updatedAt = LocalDateTime.now();
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

    public Long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(Long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public Integer getMaxExpressionDepth() {
        return maxExpressionDepth;
    }

    public void setMaxExpressionDepth(Integer maxExpressionDepth) {
        this.maxExpressionDepth = maxExpressionDepth;
    }

    public Integer getNumQueries() {
        return numQueries;
    }

    public void setNumQueries(Integer numQueries) {
        this.numQueries = numQueries;
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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "FuzzTestConfig{" +
                "id=" + id +
                ", configName='" + configName + '\'' +
                ", testOracle='" + testOracle + '\'' +
                ", numQueries=" + numQueries +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
