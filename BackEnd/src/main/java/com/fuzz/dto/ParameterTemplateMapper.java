package com.fuzz.dto;

import com.fuzz.entity.ParameterTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数模板实体与DTO之间的转换器
 */
@Component
public class ParameterTemplateMapper {
    
    /**
     * 将实体转换为DTO
     * @param entity 参数模板实体
     * @return 参数模板DTO
     */
    public ParameterTemplateDto toDto(ParameterTemplate entity) {
        if (entity == null) {
            return null;
        }
        
        ParameterTemplateDto dto = new ParameterTemplateDto();
        dto.setId(entity.getId());
        dto.setParamName(entity.getParamName());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setDefaultValue(entity.getDefaultValue());
        dto.setParamType(entity.getParamType());
        dto.setIsTestDefault(entity.getIsTestDefault());
        dto.setMinValue(entity.getMinValue());
        dto.setMaxValue(entity.getMaxValue());
        dto.setValueRange(entity.getValueRange());
        dto.setCandidateValues(entity.getCandidateValues());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        dto.setWeight(entity.getWeight());
        dto.setCoverage(entity.getCoverage());
        
        // 构建约束信息
        dto.buildConstraints();
        
        return dto;
    }
    
    /**
     * 将DTO转换为实体
     * @param dto 参数模板DTO
     * @return 参数模板实体
     */
    public ParameterTemplate toEntity(ParameterTemplateDto dto) {
        if (dto == null) {
            return null;
        }
        
        ParameterTemplate entity = new ParameterTemplate();
        entity.setId(dto.getId());
        entity.setParamName(dto.getParamName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setDefaultValue(dto.getDefaultValue());
        entity.setParamType(dto.getParamType());
        entity.setIsTestDefault(dto.getIsTestDefault());
        entity.setMinValue(dto.getMinValue());
        entity.setMaxValue(dto.getMaxValue());
        entity.setValueRange(dto.getValueRange());
        entity.setCandidateValues(dto.getCandidateValues());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        
        return entity;
    }
    
    /**
     * 批量转换实体列表为DTO列表
     * @param entities 实体列表
     * @return DTO列表
     */
    public List<ParameterTemplateDto> toDtoList(List<ParameterTemplate> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 批量转换DTO列表为实体列表
     * @param dtos DTO列表
     * @return 实体列表
     */
    public List<ParameterTemplate> toEntityList(List<ParameterTemplateDto> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新实体的字段值（用于部分更新）
     * @param entity 要更新的实体
     * @param dto 包含新值的DTO
     */
    public void updateEntityFromDto(ParameterTemplate entity, ParameterTemplateDto dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            entity.setCategory(dto.getCategory());
        }
        if (dto.getDefaultValue() != null) {
            entity.setDefaultValue(dto.getDefaultValue());
        }
        if (dto.getParamType() != null) {
            entity.setParamType(dto.getParamType());
        }
        if (dto.getIsTestDefault() != null) {
            entity.setIsTestDefault(dto.getIsTestDefault());
        }
        if (dto.getMinValue() != null) {
            entity.setMinValue(dto.getMinValue());
        }
        if (dto.getMaxValue() != null) {
            entity.setMaxValue(dto.getMaxValue());
        }
        if (dto.getValueRange() != null) {
            entity.setValueRange(dto.getValueRange());
        }
        if (dto.getCandidateValues() != null) {
            entity.setCandidateValues(dto.getCandidateValues());
        }
    }
}