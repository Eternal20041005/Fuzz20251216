package com.fuzz.service;

import com.fuzz.dto.FuzzTestConfigDto;
import com.fuzz.entity.FuzzTestConfig;

import java.util.List;
import java.util.Map;

/**
 * 模糊测试配置服务接口
 */
public interface FuzzTestConfigService {

    /**
     * 获取默认配置
     * @return 默认配置DTO
     */
    FuzzTestConfigDto getDefaultConfig();

    /**
     * 保存默认配置
     * @param configMap 配置参数Map
     * @return 保存后的配置DTO
     */
    FuzzTestConfigDto saveDefaultConfig(Map<String, Object> configMap);

    /**
     * 更新默认配置
     * @param configMap 配置参数Map
     * @return 更新后的配置DTO
     */
    FuzzTestConfigDto updateDefaultConfig(Map<String, Object> configMap);

    /**
     * 重置默认配置为系统默认值
     * @return 重置后的配置DTO
     */
    FuzzTestConfigDto resetDefaultConfig();

    /**
     * 获取所有配置列表
     * @return 配置列表
     */
    List<FuzzTestConfigDto> getAllConfigs();

    /**
     * 根据ID获取配置
     * @param id 配置ID
     * @return 配置DTO
     */
    FuzzTestConfigDto getConfigById(Long id);

    /**
     * 根据名称获取配置
     * @param configName 配置名称
     * @return 配置DTO
     */
    FuzzTestConfigDto getConfigByName(String configName);

    /**
     * 创建新配置
     * @param configName 配置名称
     * @param description 配置描述
     * @param configMap 配置参数Map
     * @return 创建的配置DTO
     */
    FuzzTestConfigDto createConfig(String configName, String description, Map<String, Object> configMap);

    /**
     * 更新配置
     * @param id 配置ID
     * @param configMap 配置参数Map
     * @return 更新后的配置DTO
     */
    FuzzTestConfigDto updateConfig(Long id, Map<String, Object> configMap);

    /**
     * 删除配置
     * @param id 配置ID
     */
    void deleteConfig(Long id);

    /**
     * 检查配置名称是否存在
     * @param configName 配置名称
     * @return 是否存在
     */
    boolean configNameExists(String configName);
}
