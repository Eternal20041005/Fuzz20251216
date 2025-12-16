package com.fuzz.service;

import com.fuzz.dto.ConnectionTestResult;
import com.fuzz.dto.DatabaseParameter;
import com.fuzz.entity.DatabaseConfig;

import java.sql.Connection;
import java.util.Map;

/**
 * 数据库连接服务接口
 */
public interface DatabaseConnectionService {
    
    /**
     * 测试数据库连接
     * @param config 数据库配置
     * @return 连接测试结果
     */
    ConnectionTestResult testConnection(DatabaseConfig config);
    
    /**
     * 获取数据库连接
     * @param config 数据库配置
     * @return 数据库连接
     * @throws Exception 连接异常
     */
    Connection getConnection(DatabaseConfig config) throws Exception;
    
    /**
     * 读取数据库参数
     * @param config 数据库配置
     * @return 参数映射表
     */
    Map<String, DatabaseParameter> readDatabaseParameters(DatabaseConfig config);
    
    /**
     * 获取数据库版本信息
     * @param config 数据库配置
     * @return 版本信息
     */
    String getDatabaseVersion(DatabaseConfig config);
}