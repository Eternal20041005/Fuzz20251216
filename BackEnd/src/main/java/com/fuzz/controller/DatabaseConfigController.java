package com.fuzz.controller;

import com.fuzz.dto.ConnectionTestResult;
import com.fuzz.dto.DatabaseConfigDto;
import com.fuzz.entity.DatabaseConfig;
import com.fuzz.service.DatabaseConfigService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库配置控制器
 */
@RestController
@RequestMapping("/database-configs")
public class DatabaseConfigController {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigController.class);
    
    @Autowired
    private DatabaseConfigService databaseConfigService;
    
    /**
     * 获取数据库配置列表
     */
    @GetMapping
    public ResponseEntity<List<DatabaseConfigDto>> getDatabaseConfigs() {
        logger.info("获取数据库配置列表");
        
        List<DatabaseConfigDto> configs = databaseConfigService.getAllDatabaseConfigs();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * 根据ID获取数据库配置
     */
    @GetMapping("/{id}")
    public ResponseEntity<DatabaseConfigDto> getDatabaseConfig(@PathVariable Long id) {
        logger.info("获取数据库配置: id={}", id);
        
        DatabaseConfigDto config = databaseConfigService.getDatabaseConfigById(id);
        return ResponseEntity.ok(config);
    }
    
    /**
     * 测试数据库连接
     */
    @PostMapping("/{id}/test-connection")
    public ResponseEntity<ConnectionTestResult> testConnection(@PathVariable Long id) {
        logger.info("测试数据库连接: id={}", id);
        
        ConnectionTestResult result = databaseConfigService.testConnection(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 创建数据库配置
     */
    @PostMapping
    public ResponseEntity<DatabaseConfigDto> createDatabaseConfig(
            @Valid @RequestBody DatabaseConfig config) {
        
        logger.info("创建数据库配置: name={}", config.getName());
        
        DatabaseConfigDto created = databaseConfigService.createDatabaseConfig(config);
        return ResponseEntity.ok(created);
    }
    
    /**
     * 更新数据库配置
     */
    @PutMapping("/{id}")
    public ResponseEntity<DatabaseConfigDto> updateDatabaseConfig(
            @PathVariable Long id,
            @Valid @RequestBody DatabaseConfig config) {
        
        logger.info("更新数据库配置: id={}, name={}", id, config.getName());
        
        DatabaseConfigDto updated = databaseConfigService.updateDatabaseConfig(id, config);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 删除数据库配置
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDatabaseConfig(@PathVariable Long id) {
        logger.info("删除数据库配置: id={}", id);
        
        databaseConfigService.deleteDatabaseConfig(id);
        return ResponseEntity.noContent().build();
    }
}