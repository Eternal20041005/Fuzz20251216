package com.fuzz.service.impl;

import com.fuzz.dto.ConnectionTestResult;
import com.fuzz.dto.DatabaseParameter;
import com.fuzz.entity.DatabaseConfig;
import com.fuzz.entity.ParameterType;
import com.fuzz.service.DatabaseConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

/**
 * 数据库连接服务实现类
 */
@Service
public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionServiceImpl.class);
    
    // 布尔类型参数列表
    private static final Set<String> BOOLEAN_PARAMS = Set.of(
        "autocommit", "sql_auto_is_null", "sql_big_selects", "sql_buffer_result",
        "sql_log_bin", "sql_quote_show_create", "sql_safe_updates", "sql_select_limit",
        "unique_checks", "foreign_key_checks", "query_cache_type", "log_queries_not_using_indexes"
    );
    
    @Override
    public ConnectionTestResult testConnection(DatabaseConfig config) {
        long startTime = System.currentTimeMillis();
        
        try (Connection connection = getConnection(config)) {
            if (connection != null && !connection.isClosed()) {
                String version = getDatabaseVersion(config);
                long responseTime = System.currentTimeMillis() - startTime;
                
                logger.info("数据库连接测试成功: {} - {}", config.getName(), version);
                return ConnectionTestResult.success(version, responseTime);
            } else {
                return ConnectionTestResult.failure("无法建立数据库连接");
            }
        } catch (Exception e) {
            logger.error("数据库连接测试失败: {}", config.getName(), e);
            return ConnectionTestResult.failure("连接失败: " + e.getMessage());
        }
    }
    
    @Override
    public Connection getConnection(DatabaseConfig config) throws Exception {
        try {
            // 根据数据库类型加载驱动
            String driverClass = getDriverClass(config.getDbType());
            Class.forName(driverClass);
            
            // 创建连接
            Properties props = new Properties();
            props.setProperty("user", config.getUsername());
            props.setProperty("password", config.getPassword());
            props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "utf8");
            props.setProperty("useSSL", "false");
            props.setProperty("serverTimezone", "Asia/Shanghai");
            props.setProperty("connectTimeout", "10000");
            props.setProperty("socketTimeout", "30000");
            
            return DriverManager.getConnection(config.getConnectionUrl(), props);
        } catch (Exception e) {
            logger.error("创建数据库连接失败: {}", config.getName(), e);
            throw new Exception("数据库连接失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, DatabaseParameter> readDatabaseParameters(DatabaseConfig config) {
        Map<String, DatabaseParameter> parameters = new HashMap<>();
        
        try (Connection connection = getConnection(config)) {
            if ("MySQL".equalsIgnoreCase(config.getDbType())) {
                parameters.putAll(readMySQLParameters(connection));
            } else if ("PostgreSQL".equalsIgnoreCase(config.getDbType())) {
                parameters.putAll(readPostgreSQLParameters(connection));
            }
            
            logger.info("成功读取数据库参数: {} 个", parameters.size());
        } catch (Exception e) {
            logger.error("读取数据库参数失败: {}", config.getName(), e);
        }
        
        return parameters;
    }
    
    @Override
    public String getDatabaseVersion(DatabaseConfig config) {
        try (Connection connection = getConnection(config)) {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
        } catch (Exception e) {
            logger.error("获取数据库版本失败: {}", config.getName(), e);
            return "未知版本";
        }
    }
    
    /**
     * 读取MySQL数据库参数
     */
    private Map<String, DatabaseParameter> readMySQLParameters(Connection connection) throws SQLException {
        Map<String, DatabaseParameter> parameters = new HashMap<>();
        
        // 读取系统变量
        String systemVarSql = """
            SELECT VARIABLE_NAME as param_name, VARIABLE_VALUE as param_value, 'SYSTEM' as category
            FROM INFORMATION_SCHEMA.GLOBAL_VARIABLES 
            WHERE VARIABLE_NAME IN (
                'max_connections', 'wait_timeout', 'innodb_buffer_pool_size',
                'max_allowed_packet', 'tmp_table_size', 'key_buffer_size',
                'query_cache_size', 'thread_cache_size', 'sort_buffer_size',
                'read_buffer_size', 'innodb_log_file_size', 'innodb_log_buffer_size',
                'table_open_cache', 'thread_stack', 'query_cache_type',
                'autocommit', 'sql_mode', 'character_set_server', 'collation_server'
            )
            ORDER BY VARIABLE_NAME
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(systemVarSql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String name = rs.getString("param_name");
                String value = rs.getString("param_value");
                String category = rs.getString("category");
                
                DatabaseParameter param = new DatabaseParameter(name, value, category);
                param.setType(identifyParameterType(name, value));
                param.setDescription(getParameterDescription(name));
                
                parameters.put(name, param);
            }
        }
        
        return parameters;
    }
    
    /**
     * 读取PostgreSQL数据库参数
     */
    private Map<String, DatabaseParameter> readPostgreSQLParameters(Connection connection) throws SQLException {
        Map<String, DatabaseParameter> parameters = new HashMap<>();
        
        String sql = """
            SELECT name as param_name, setting as param_value, 'SYSTEM' as category
            FROM pg_settings 
            WHERE name IN (
                'max_connections', 'shared_buffers', 'work_mem', 'maintenance_work_mem',
                'effective_cache_size', 'checkpoint_completion_target', 'wal_buffers',
                'default_statistics_target', 'random_page_cost', 'effective_io_concurrency'
            )
            ORDER BY name
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String name = rs.getString("param_name");
                String value = rs.getString("param_value");
                String category = rs.getString("category");
                
                DatabaseParameter param = new DatabaseParameter(name, value, category);
                param.setType(identifyParameterType(name, value));
                param.setDescription(getParameterDescription(name));
                
                parameters.put(name, param);
            }
        }
        
        return parameters;
    }
    
    /**
     * 识别参数类型
     */
    private ParameterType identifyParameterType(String paramName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return ParameterType.STRING;
        }
        
        String lowerParamName = paramName.toLowerCase();
        String lowerValue = value.toLowerCase().trim();
        
        // 布尔类型参数
        if (BOOLEAN_PARAMS.contains(lowerParamName) || 
            "on".equals(lowerValue) || "off".equals(lowerValue) ||
            "true".equals(lowerValue) || "false".equals(lowerValue) ||
            "yes".equals(lowerValue) || "no".equals(lowerValue) ||
            "1".equals(lowerValue) || "0".equals(lowerValue)) {
            return ParameterType.BOOLEAN;
        }
        
        // 数值类型参数
        if (value.matches("\\d+")) {
            return ParameterType.INTEGER;
        }
        
        if (value.matches("\\d+\\.\\d+")) {
            return ParameterType.DECIMAL;
        }
        
        // 带单位的数值（如 128MB, 1G）
        if (value.matches("\\d+[KMGT]?B?")) {
            return ParameterType.STRING; // 作为字符串处理，但可以转换为数值
        }
        
        // 默认为字符串类型
        return ParameterType.STRING;
    }
    
    /**
     * 获取参数描述
     */
    private String getParameterDescription(String paramName) {
        Map<String, String> descriptions = Map.ofEntries(
            Map.entry("max_connections", "最大并发连接数"),
            Map.entry("wait_timeout", "非交互连接超时时间（秒）"),
            Map.entry("innodb_buffer_pool_size", "InnoDB缓冲池大小"),
            Map.entry("max_allowed_packet", "最大数据包大小"),
            Map.entry("tmp_table_size", "临时表最大大小"),
            Map.entry("key_buffer_size", "MyISAM索引缓冲区大小"),
            Map.entry("query_cache_size", "查询缓存大小"),
            Map.entry("thread_cache_size", "线程缓存大小"),
            Map.entry("sort_buffer_size", "排序缓冲区大小"),
            Map.entry("read_buffer_size", "读缓冲区大小"),
            Map.entry("shared_buffers", "共享缓冲区大小"),
            Map.entry("work_mem", "工作内存大小"),
            Map.entry("maintenance_work_mem", "维护工作内存大小"),
            Map.entry("effective_cache_size", "有效缓存大小")
        );
        
        return descriptions.getOrDefault(paramName, "数据库参数");
    }
    
    /**
     * 根据数据库类型获取驱动类名
     */
    private String getDriverClass(String dbType) {
        return switch (dbType.toLowerCase()) {
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "postgresql" -> "org.postgresql.Driver";
            case "oracle" -> "oracle.jdbc.driver.OracleDriver";
            case "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            default -> "com.mysql.cj.jdbc.Driver";
        };
    }
}