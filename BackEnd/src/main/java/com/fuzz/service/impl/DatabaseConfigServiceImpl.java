package com.fuzz.service.impl;

import com.fuzz.dto.ConnectionTestResult;
import com.fuzz.dto.DatabaseConfigDto;
import com.fuzz.entity.DatabaseConfig;
import com.fuzz.repository.DatabaseConfigRepository;
import com.fuzz.service.DatabaseConfigService;
import com.fuzz.service.DatabaseConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库配置服务实现类
 */
@Service
@Transactional
public class DatabaseConfigServiceImpl implements DatabaseConfigService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigServiceImpl.class);
    
    @Autowired
    private DatabaseConfigRepository databaseConfigRepository;
    
    @Autowired
    private DatabaseConnectionService databaseConnectionService;
    
    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable("databaseConfigs")
    public List<DatabaseConfigDto> getAllDatabaseConfigs() {
        List<DatabaseConfig> configs = databaseConfigRepository.findAllOrderByCreateTimeDesc();
        return configs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public DatabaseConfigDto getDatabaseConfigById(Long id) {
        DatabaseConfig config = databaseConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("数据库配置不存在: " + id));
        return convertToDto(config);
    }
    
    @Override
    public ConnectionTestResult testConnection(Long id) {
        DatabaseConfig config = databaseConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("数据库配置不存在: " + id));
        
        ConnectionTestResult result = databaseConnectionService.testConnection(config);
        
        // 更新配置状态
        if (result.isSuccess()) {
            config.setStatus("连接成功");
        } else {
            config.setStatus("连接失败");
        }
        databaseConfigRepository.save(config);
        
        logger.info("数据库连接测试完成: {} - {}", config.getName(), config.getStatus());
        return result;
    }
    
    @Override
    public DatabaseConfigDto createDatabaseConfig(DatabaseConfig config) {
        // 检查名称是否已存在
        if (databaseConfigRepository.existsByName(config.getName())) {
            throw new RuntimeException("数据库配置名称已存在: " + config.getName());
        }
        
        DatabaseConfig saved = databaseConfigRepository.save(config);
        logger.info("数据库配置创建成功: {}", saved.getName());
        
        return convertToDto(saved);
    }
    
    @Override
    public DatabaseConfigDto updateDatabaseConfig(Long id, DatabaseConfig config) {
        DatabaseConfig existing = databaseConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("数据库配置不存在: " + id));
        
        // 检查名称冲突（排除自己）
        if (!existing.getName().equals(config.getName()) && 
            databaseConfigRepository.existsByName(config.getName())) {
            throw new RuntimeException("数据库配置名称已存在: " + config.getName());
        }
        
        // 更新字段
        existing.setName(config.getName());
        existing.setDbType(config.getDbType());
        existing.setConnectionUrl(config.getConnectionUrl());
        existing.setUsername(config.getUsername());
        existing.setPassword(config.getPassword());
        existing.setStatus("未测试"); // 重置状态
        
        DatabaseConfig saved = databaseConfigRepository.save(existing);
        logger.info("数据库配置更新成功: {}", saved.getName());
        
        return convertToDto(saved);
    }
    
    @Override
    public void deleteDatabaseConfig(Long id) {
        if (!databaseConfigRepository.existsById(id)) {
            throw new RuntimeException("数据库配置不存在: " + id);
        }
        
        databaseConfigRepository.deleteById(id);
        logger.info("数据库配置删除成功: {}", id);
    }
    
    /**
     * 转换为DTO（不包含密码）
     */
    private DatabaseConfigDto convertToDto(DatabaseConfig entity) {
        DatabaseConfigDto dto = new DatabaseConfigDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDbType(entity.getDbType());
        dto.setConnectionUrl(entity.getConnectionUrl());
        dto.setUsername(entity.getUsername());
        // 不返回密码
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}