package com.fuzz.controller;

import com.fuzz.dto.FuzzTestConfigDto;
import com.fuzz.service.FuzzTestConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 模糊测试配置控制器
 * 处理模糊测试参数的配置管理
 */
@RestController
@RequestMapping("/fuzz-configs")
public class FuzzTestConfigController {

    private static final Logger logger = LoggerFactory.getLogger(FuzzTestConfigController.class);

    @Autowired
    private FuzzTestConfigService configService;

    /**
     * 获取默认配置
     */
    @GetMapping("/default")
    public ResponseEntity<FuzzTestConfigDto> getDefaultConfig() {
        logger.info("获取默认模糊测试配置");

        FuzzTestConfigDto config = configService.getDefaultConfig();
        return ResponseEntity.ok(config);
    }

    /**
     * 保存默认配置
     */
    @PostMapping("/default")
    public ResponseEntity<FuzzTestConfigDto> saveDefaultConfig(@RequestBody Map<String, Object> configMap) {
        logger.info("保存默认模糊测试配置");

        FuzzTestConfigDto savedConfig = configService.saveDefaultConfig(configMap);
        return ResponseEntity.ok(savedConfig);
    }

    /**
     * 更新默认配置
     */
    @PutMapping("/default")
    public ResponseEntity<FuzzTestConfigDto> updateDefaultConfig(@RequestBody Map<String, Object> configMap) {
        logger.info("更新默认模糊测试配置");

        FuzzTestConfigDto updatedConfig = configService.updateDefaultConfig(configMap);
        return ResponseEntity.ok(updatedConfig);
    }

    /**
     * 重置默认配置
     */
    @PostMapping("/default/reset")
    public ResponseEntity<FuzzTestConfigDto> resetDefaultConfig() {
        logger.info("重置默认模糊测试配置");

        FuzzTestConfigDto resetConfig = configService.resetDefaultConfig();
        return ResponseEntity.ok(resetConfig);
    }

    /**
     * 获取所有配置
     */
    @GetMapping
    public ResponseEntity<List<FuzzTestConfigDto>> getAllConfigs() {
        logger.info("获取所有模糊测试配置");

        List<FuzzTestConfigDto> configs = configService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    /**
     * 根据ID获取配置
     */
    @GetMapping("/{id}")
    public ResponseEntity<FuzzTestConfigDto> getConfigById(@PathVariable Long id) {
        logger.info("根据ID获取模糊测试配置: {}", id);

        FuzzTestConfigDto config = configService.getConfigById(id);
        return ResponseEntity.ok(config);
    }

    /**
     * 根据名称获取配置
     */
    @GetMapping("/name/{configName}")
    public ResponseEntity<FuzzTestConfigDto> getConfigByName(@PathVariable String configName) {
        logger.info("根据名称获取模糊测试配置: {}", configName);

        FuzzTestConfigDto config = configService.getConfigByName(configName);
        return ResponseEntity.ok(config);
    }

    /**
     * 创建新配置
     */
    @PostMapping
    public ResponseEntity<FuzzTestConfigDto> createConfig(@RequestBody CreateConfigRequest request) {
        logger.info("创建新的模糊测试配置: {}", request.getConfigName());

        FuzzTestConfigDto createdConfig = configService.createConfig(
                request.getConfigName(),
                request.getDescription(),
                request.getParameters()
        );

        return ResponseEntity.ok(createdConfig);
    }

    /**
     * 更新配置
     */
    @PutMapping("/{id}")
    public ResponseEntity<FuzzTestConfigDto> updateConfig(
            @PathVariable Long id,
            @RequestBody Map<String, Object> configMap) {

        logger.info("更新模糊测试配置: {}", id);

        FuzzTestConfigDto updatedConfig = configService.updateConfig(id, configMap);
        return ResponseEntity.ok(updatedConfig);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        logger.info("删除模糊测试配置: {}", id);

        configService.deleteConfig(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 检查配置名称是否存在
     */
    @GetMapping("/exists/{configName}")
    public ResponseEntity<Boolean> configNameExists(@PathVariable String configName) {
        boolean exists = configService.configNameExists(configName);
        return ResponseEntity.ok(exists);
    }

    // 请求DTO类
    public static class CreateConfigRequest {
        private String configName;
        private String description;
        private Map<String, Object> parameters;

        public String getConfigName() { return configName; }
        public void setConfigName(String configName) { this.configName = configName; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
}
