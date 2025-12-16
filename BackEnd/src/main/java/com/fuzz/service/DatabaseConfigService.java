package com.fuzz.service;

import com.fuzz.dto.ConnectionTestResult;
import com.fuzz.dto.DatabaseConfigDto;
import com.fuzz.entity.DatabaseConfig;

import java.util.List;

/**
 * 数据库配置服务接口
 */
public interface DatabaseConfigService {
    
    /**
     * 获取所有数据库配置
     * @return 配置列表
     */
    List<DatabaseConfigDto> getAllDatabaseConfigs();
    
    /**
     * 根据ID获取数据库配置
     * @param id 配置ID
     * @return 配置DTO
     */
    DatabaseConfigDto getDatabaseConfigById(Long id);
    
    /**
     * 测试数据库连接
     * @param id 配置ID
     * @return 连接测试结果
     */
    ConnectionTestResult testConnection(Long id);
    
    /**
     * 创建数据库配置
     * @param config 数据库配置
     * @return 创建的配置DTO
     */
    DatabaseConfigDto createDatabaseConfig(DatabaseConfig config);
    
    /**
     * 更新数据库配置
     * @param id 配置ID
     * @param config 更新的配置
     * @return 更新后的配置DTO
     */
    DatabaseConfigDto updateDatabaseConfig(Long id, DatabaseConfig config);
    
    /**
     * 删除数据库配置
     * @param id 配置ID
     */
    void deleteDatabaseConfig(Long id);
}