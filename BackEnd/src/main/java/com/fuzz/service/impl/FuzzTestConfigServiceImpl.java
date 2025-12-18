package com.fuzz.service.impl;

import com.fuzz.dto.FuzzTestConfigDto;
import com.fuzz.entity.FuzzTestConfig;
import com.fuzz.repository.FuzzTestConfigRepository;
import com.fuzz.service.FuzzTestConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模糊测试配置服务实现类
 */
@Service
@Transactional
public class FuzzTestConfigServiceImpl implements FuzzTestConfigService {

    private static final Logger logger = LoggerFactory.getLogger(FuzzTestConfigServiceImpl.class);
    private static final String DEFAULT_CONFIG_NAME = "default";

    @Autowired
    private FuzzTestConfigRepository configRepository;

    @Override
    public FuzzTestConfigDto getDefaultConfig() {
        logger.info("获取默认模糊测试配置");

        try {
            return convertToDto(configRepository.findDefaultConfig(DEFAULT_CONFIG_NAME)
                    .orElseGet(() -> createDefaultConfigIfNotExists()));
        } catch (Exception e) {
            logger.error("获取默认配置失败，可能存在重复记录: {}", e.getMessage());
            // 如果发现重复记录，清理并保留最新的一条
            return convertToDto(cleanupDuplicateDefaultConfigs());
        }
    }

    @Override
    public FuzzTestConfigDto saveDefaultConfig(Map<String, Object> configMap) {
        logger.info("保存默认模糊测试配置");

        FuzzTestConfig config = configRepository.findDefaultConfig(DEFAULT_CONFIG_NAME)
                .orElseGet(() -> {
                    FuzzTestConfig newConfig = new FuzzTestConfig(DEFAULT_CONFIG_NAME);
                    newConfig.setDescription("默认模糊测试配置");
                    return configRepository.save(newConfig);
                });

        config.updateFromMap(configMap);
        FuzzTestConfig savedConfig = configRepository.save(config);

        logger.info("默认配置保存成功: {}", savedConfig.getId());
        return convertToDto(savedConfig);
    }

    @Override
    public FuzzTestConfigDto updateDefaultConfig(Map<String, Object> configMap) {
        logger.info("更新默认模糊测试配置");

        FuzzTestConfig config = configRepository.findDefaultConfig(DEFAULT_CONFIG_NAME)
                .orElseThrow(() -> new RuntimeException("默认配置不存在"));

        config.updateFromMap(configMap);
        FuzzTestConfig updatedConfig = configRepository.save(config);

        logger.info("默认配置更新成功: {}", updatedConfig.getId());
        return convertToDto(updatedConfig);
    }

    @Override
    public FuzzTestConfigDto resetDefaultConfig() {
        logger.info("重置默认模糊测试配置");

        FuzzTestConfig config = configRepository.findDefaultConfig(DEFAULT_CONFIG_NAME)
                .orElseGet(() -> new FuzzTestConfig(DEFAULT_CONFIG_NAME));

        config.setDefaultValues();
        config.setDescription("默认模糊测试配置（已重置）");

        FuzzTestConfig savedConfig = configRepository.save(config);

        logger.info("默认配置重置成功: {}", savedConfig.getId());
        return convertToDto(savedConfig);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuzzTestConfigDto> getAllConfigs() {
        logger.info("获取所有模糊测试配置");

        List<FuzzTestConfig> configs = configRepository.findAllByOrderByUpdatedAtDesc();
        return configs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FuzzTestConfigDto getConfigById(Long id) {
        logger.info("根据ID获取模糊测试配置: {}", id);

        FuzzTestConfig config = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置不存在: " + id));

        return convertToDto(config);
    }

    @Override
    @Transactional(readOnly = true)
    public FuzzTestConfigDto getConfigByName(String configName) {
        logger.info("根据名称获取模糊测试配置: {}", configName);

        FuzzTestConfig config = configRepository.findByConfigName(configName)
                .orElseThrow(() -> new RuntimeException("配置不存在: " + configName));

        return convertToDto(config);
    }

    @Override
    public FuzzTestConfigDto createConfig(String configName, String description, Map<String, Object> configMap) {
        logger.info("创建新的模糊测试配置: {}", configName);

        if (configRepository.existsByConfigName(configName)) {
            throw new RuntimeException("配置名称已存在: " + configName);
        }

        FuzzTestConfig config = new FuzzTestConfig(configName);
        config.setDescription(description);
        config.updateFromMap(configMap);

        FuzzTestConfig savedConfig = configRepository.save(config);

        logger.info("配置创建成功: {}", savedConfig.getId());
        return convertToDto(savedConfig);
    }

    @Override
    public FuzzTestConfigDto updateConfig(Long id, Map<String, Object> configMap) {
        logger.info("更新模糊测试配置: {}", id);

        FuzzTestConfig config = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置不存在: " + id));

        config.updateFromMap(configMap);
        FuzzTestConfig updatedConfig = configRepository.save(config);

        logger.info("配置更新成功: {}", updatedConfig.getId());
        return convertToDto(updatedConfig);
    }

    @Override
    public void deleteConfig(Long id) {
        logger.info("删除模糊测试配置: {}", id);

        if (!configRepository.existsById(id)) {
            throw new RuntimeException("配置不存在: " + id);
        }

        // 不允许删除默认配置
        FuzzTestConfig config = configRepository.findById(id).get();
        if (DEFAULT_CONFIG_NAME.equals(config.getConfigName())) {
            throw new RuntimeException("不能删除默认配置");
        }

        configRepository.deleteById(id);
        logger.info("配置删除成功: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean configNameExists(String configName) {
        return configRepository.existsByConfigName(configName);
    }

    /**
     * 清理重复的默认配置，只保留最新的一条
     */
    private FuzzTestConfig cleanupDuplicateDefaultConfigs() {
        logger.info("清理重复的默认配置");
        
        // 查询所有名为"default"的配置记录
        List<FuzzTestConfig> allConfigs = configRepository.findAll();
        List<FuzzTestConfig> defaultConfigs = allConfigs.stream()
                .filter(config -> DEFAULT_CONFIG_NAME.equals(config.getConfigName()))
                .sorted(Comparator.comparing(FuzzTestConfig::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
        
        if (defaultConfigs.isEmpty()) {
            // 如果没有默认配置，创建一个新的
            return createDefaultConfigIfNotExists();
        }
        
        // 保留最新的一条配置
        FuzzTestConfig latestConfig = defaultConfigs.get(0);
        
        // 删除其他重复的配置
        if (defaultConfigs.size() > 1) {
            for (int i = 1; i < defaultConfigs.size(); i++) {
                FuzzTestConfig duplicateConfig = defaultConfigs.get(i);
                logger.info("删除重复的默认配置: {}", duplicateConfig.getId());
                configRepository.delete(duplicateConfig);
            }
        }
        
        return latestConfig;
    }
    
    /**
     * 如果默认配置不存在，则创建一个
     */
    private FuzzTestConfig createDefaultConfigIfNotExists() {
        logger.info("创建默认模糊测试配置");

        FuzzTestConfig defaultConfig = new FuzzTestConfig(DEFAULT_CONFIG_NAME);
        defaultConfig.setDescription("系统默认模糊测试配置");
        defaultConfig.setDefaultValues();

        return configRepository.save(defaultConfig);
    }

    /**
     * 将FuzzTestConfig实体转换为FuzzTestConfigDto
     */
    private FuzzTestConfigDto convertToDto(FuzzTestConfig config) {
        FuzzTestConfigDto dto = new FuzzTestConfigDto();

        dto.setId(config.getId());
        dto.setConfigName(config.getConfigName());
        dto.setDescription(config.getDescription());
        dto.setTestOracle(config.getTestOracle());
        dto.setRandomSeed(config.getRandomSeed());
        dto.setMaxExpressionDepth(config.getMaxExpressionDepth());
        dto.setNumQueries(config.getNumQueries());
        dto.setMaxNumInserts(config.getMaxNumInserts());
        dto.setNumTries(config.getNumTries());
        dto.setTimeoutSeconds(config.getTimeoutSeconds());
        dto.setMaxGeneratedDatabases(config.getMaxGeneratedDatabases());

        dto.setUsername(config.getUsername());
        dto.setPassword(config.getPassword());
        dto.setHost(config.getHost());
        dto.setPort(config.getPort());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());

        return dto;
    }
}
