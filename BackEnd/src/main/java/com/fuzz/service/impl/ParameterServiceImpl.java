package com.fuzz.service.impl;

import com.fuzz.dto.*;
import com.fuzz.entity.DatabaseConfig;
import com.fuzz.entity.ParameterTemplate;
import com.fuzz.repository.DatabaseConfigRepository;
import com.fuzz.repository.ParameterTemplateRepository;
import com.fuzz.service.DatabaseConnectionService;
import com.fuzz.service.ParameterConstraintValidator;
import com.fuzz.service.ParameterConstraintValidator.ValidationResult;
import com.fuzz.service.ParameterReaderService;
import com.fuzz.service.ParameterService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.lang.IllegalArgumentException;
import java.util.List;
/**
 * 参数管理服务实现类
 */
@Service
@Transactional
public class ParameterServiceImpl implements ParameterService {
    
    private static final Logger logger = LoggerFactory.getLogger(ParameterServiceImpl.class);
    
    @Autowired
    private ParameterTemplateRepository parameterTemplateRepository;
    
    @Autowired
    private DatabaseConfigRepository databaseConfigRepository;
    
    @Autowired
    private DatabaseConnectionService databaseConnectionService;
    
    @Autowired
    private ParameterReaderService parameterReaderService;
    
    @Autowired
    private ParameterConstraintValidator parameterConstraintValidator;
    
    @Autowired
    private ParameterTemplateMapper parameterTemplateMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ParameterTemplateDto> getParameters(int page, int size, String search, String category) {
        Specification<ParameterTemplate> spec = buildSpecification(search, category);
        // 按权重降序排序，权重为null的排在最后
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by(Sort.Order.desc("weight").nullsLast(), Sort.Order.asc("paramName")));
        
        Page<ParameterTemplate> parameterPage = parameterTemplateRepository.findAll(spec, pageable);
        
        List<ParameterTemplateDto> dtoList = parameterPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
            dtoList,
            parameterPage.getTotalElements(),
            page,
            size
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ParameterTemplateDto getParameterById(Long id) {
        ParameterTemplate parameter = parameterTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + id));
        return convertToDto(parameter);
    }
    
    @Override
    public ParameterTemplateDto updateParameter(Long id, UpdateParameterRequest request) {
        ParameterTemplate parameter = parameterTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + id));
        
        // 验证参数值
        if (request.getDefaultValue() != null) {
            boolean isValid = parameterReaderService.validateParameterValue(
                parameter.getParamName(), 
                request.getDefaultValue(), 
                parameter.getParamType()
            );
            
            if (!isValid) {
                throw new RuntimeException("参数值无效: " + request.getDefaultValue());
            }
            
            parameter.setDefaultValue(request.getDefaultValue());
        }
        
        if (request.getIsTestDefault() != null) {
            parameter.setIsTestDefault(request.getIsTestDefault());
        }
        
        if (request.getDescription() != null) {
            parameter.setDescription(request.getDescription());
        }

        if (request.getWeight() != null) {
            parameter.setWeight(request.getWeight());
        }

        ParameterTemplate saved = parameterTemplateRepository.save(parameter);
        logger.info("参数更新成功: {}", saved.getParamName());
        
        return convertToDto(saved);
    }
    
    @Override
    public BatchUpdateResult batchUpdateParameters(List<UpdateParameterRequest> requests) {
        int totalCount = requests.size();
        int successCount = 0;
        int failureCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        for (UpdateParameterRequest request : requests) {
            try {
                updateParameter(request.getId(), request);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errorMessages.add("参数ID " + request.getId() + ": " + e.getMessage());
                logger.error("批量更新参数失败: {}", request.getId(), e);
            }
        }
        
        BatchUpdateResult result = new BatchUpdateResult(totalCount, successCount, failureCount);
        result.setErrorMessages(errorMessages);
        
        logger.info("批量更新完成: 总数={}, 成功={}, 失败={}", totalCount, successCount, failureCount);
        return result;
    }
    
    @Override
    public ImportResult importParametersFromDatabase(Long dbConfigId) {
        DatabaseConfig dbConfig = databaseConfigRepository.findById(dbConfigId)
                .orElseThrow(() -> new RuntimeException("数据库配置不存在: " + dbConfigId));
        
        try {
            // 测试数据库连接
            ConnectionTestResult testResult = databaseConnectionService.testConnection(dbConfig);
            if (!testResult.isSuccess()) {
                return ImportResult.failure("数据库连接失败: " + testResult.getMessage());
            }
            
            // 读取数据库参数
            List<DatabaseParameter> dbParameters = parameterReaderService.readAllParameters(dbConfig);
            
            int totalCount = dbParameters.size();
            int importedCount = 0;
            int updatedCount = 0;
            int skippedCount = 0;
            List<String> errorMessages = new ArrayList<>();
            
            for (DatabaseParameter dbParam : dbParameters) {
                try {
                    Optional<ParameterTemplate> existingOpt = 
                        parameterTemplateRepository.findByParamName(dbParam.getName());
                    
                    if (existingOpt.isPresent()) {
                        // 更新现有参数
                        ParameterTemplate existing = existingOpt.get();
                        existing.setDefaultValue(dbParam.getValue());
                        existing.setDescription(dbParam.getDescription());
                        existing.setParamType(dbParam.getType());
                        existing.setCategory(dbParam.getCategory());
                        
                        // 更新推荐配置
                        updateParameterRecommendations(existing, dbParam);
                        
                        parameterTemplateRepository.save(existing);
                        updatedCount++;
                    } else {
                        // 创建新参数
                        ParameterTemplate newParam = new ParameterTemplate();
                        newParam.setParamName(dbParam.getName());
                        newParam.setDefaultValue(dbParam.getValue());
                        newParam.setDescription(dbParam.getDescription());
                        newParam.setParamType(dbParam.getType());
                        newParam.setCategory(dbParam.getCategory());
                        newParam.setIsTestDefault(true);
                        
                        // 设置推荐配置
                        updateParameterRecommendations(newParam, dbParam);
                        
                        parameterTemplateRepository.save(newParam);
                        importedCount++;
                    }
                } catch (Exception e) {
                    skippedCount++;
                    errorMessages.add("参数 " + dbParam.getName() + ": " + e.getMessage());
                    logger.error("导入参数失败: {}", dbParam.getName(), e);
                }
            }
            
            // 更新数据库配置状态
            dbConfig.setStatus("导入成功");
            databaseConfigRepository.save(dbConfig);
            
            ImportResult result = ImportResult.success(totalCount, importedCount, updatedCount);
            result.setSkippedCount(skippedCount);
            result.setErrorMessages(errorMessages);
            
            logger.info("参数导入完成: 总数={}, 新增={}, 更新={}, 跳过={}", 
                       totalCount, importedCount, updatedCount, skippedCount);
            
            return result;
            
        } catch (Exception e) {
            logger.error("导入参数失败", e);
            dbConfig.setStatus("导入失败");
            databaseConfigRepository.save(dbConfig);
            return ImportResult.failure("导入失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getDatabaseParameterValues(DatabaseConfig dbConfig) {
        Map<String, DatabaseParameter> parameters = 
            databaseConnectionService.readDatabaseParameters(dbConfig);
        
        return parameters.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getValue()
                ));
    }
    
    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable("categories")
    public List<String> getAllCategories() {
        return parameterTemplateRepository.findAllCategories();
    }
    
    @Override
    public void deleteParameter(Long id) {
        if (!parameterTemplateRepository.existsById(id)) {
            throw new RuntimeException("参数不存在: " + id);
        }
        
        parameterTemplateRepository.deleteById(id);
        logger.info("参数删除成功: {}", id);
    }
    
    @Override
    public void batchDeleteParameters(List<Long> ids) {
        parameterTemplateRepository.deleteByIdIn(ids);
        logger.info("批量删除参数成功: {} 个", ids.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ValidationResult validateParameterConstraints(String paramName, String value) {
        Optional<ParameterTemplate> parameterOpt = parameterTemplateRepository.findByParamName(paramName);
        if (!parameterOpt.isPresent()) {
            return ValidationResult.error("参数不存在: " + paramName);
        }
        
        return parameterConstraintValidator.validateParameterValue(parameterOpt.get(), value);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getParameterCandidateValues(Long parameterId) {
        ParameterTemplate parameter = parameterTemplateRepository.findById(parameterId)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + parameterId));
        
        return parameter.getCandidateValues();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ParameterConstraintDto getParameterConstraints(Long parameterId) {
        ParameterTemplate parameter = parameterTemplateRepository.findById(parameterId)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + parameterId));
        
        return parameterConstraintValidator.getParameterConstraints(parameter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ParameterTemplateDto getParameterByName(String paramName) {
        ParameterTemplate parameter = parameterTemplateRepository.findByParamName(paramName)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + paramName));
        
        return parameterTemplateMapper.toDto(parameter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllValueRanges() {
        return parameterTemplateRepository.findAllValueRanges();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ParameterTemplateDto> getParametersByValueRange(int page, int size, String valueRange) {
        Specification<ParameterTemplate> spec = (root, query, cb) -> 
            cb.equal(root.get("valueRange"), valueRange);
        
        // 按权重降序排序，权重为null的排在最后
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by(Sort.Order.desc("weight").nullsLast(), Sort.Order.asc("paramName")));
        Page<ParameterTemplate> parameterPage = parameterTemplateRepository.findAll(spec, pageable);
        
        List<ParameterTemplateDto> dtoList = parameterPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
            dtoList,
            parameterPage.getTotalElements(),
            page,
            size
        );
    }
    
    /**
     * 构建查询条件
     */
    private Specification<ParameterTemplate> buildSpecification(String search, String category) {
        Specification<ParameterTemplate> spec = Specification.where(null);
        
        // 搜索条件
        if (StringUtils.hasText(search)) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("paramName")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                )
            );
        }
        
        // 分类过滤
        if (StringUtils.hasText(category)) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("category"), category)
            );
        }
        
        return spec;
    }
    
    /**
     * 转换为DTO（使用新的Mapper）
     */
    private ParameterTemplateDto convertToDto(ParameterTemplate entity) {
        ParameterTemplateDto dto = parameterTemplateMapper.toDto(entity);
        
        // 如果权重为null，设置默认权重为5.0
        if (dto.getWeight() == null) {
            dto.setWeight(5.0);
            // 同时更新实体，保存默认权重到数据库
            entity.setWeight(5.0);
            parameterTemplateRepository.save(entity);
        }

        return dto;
    }
    
    /**
     * 更新参数推荐配置
     */
    private void updateParameterRecommendations(ParameterTemplate parameter, DatabaseParameter dbParam) {
        Map<String, Object> recommendations = 
            parameterReaderService.getParameterRecommendations(dbParam.getName(), dbParam.getType());
        
        if (recommendations.containsKey("minValue")) {
            parameter.setMinValue(String.valueOf(recommendations.get("minValue")));
        }
        
        if (recommendations.containsKey("maxValue")) {
            parameter.setMaxValue(String.valueOf(recommendations.get("maxValue")));
        }
        
        if (recommendations.containsKey("allowedValues")) {
            try {
                @SuppressWarnings("unchecked")
                List<String> allowedValues = (List<String>) recommendations.get("allowedValues");
                parameter.setAllowedValues(objectMapper.writeValueAsString(allowedValues));
            } catch (Exception e) {
                logger.warn("序列化允许值失败: {}", parameter.getParamName(), e);
            }
        }
    }
        // ===================== 新增：权重相关方法实现 =====================
    @Override
    public ParameterTemplateDto updateParameterWeight(Long id, Double weight) {
        // 1. 校验权重范围（必须在0-10之间，否则报错）
        if (weight < 0 || weight > 10) {
            throw new IllegalArgumentException("权重必须在0-10之间！");
        }

        // 2. 根据ID查询参数实体（从数据库拿数据）
        ParameterTemplate param = parameterTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("参数不存在，ID：" + id));

        // 3. 设置新权重，并保存到数据库
        param.setWeight(weight);
        ParameterTemplate updatedParam = parameterTemplateRepository.save(param);

        // 4. 把实体转换为DTO（给前端返回）
        return convertToDto(updatedParam);
    }

    @Override
    public void adjustWeightByCoverage(Long paramId, Double coverage) {
        // 1. 校验覆盖率范围（必须在0-100之间，百分比）
        if (coverage < 0 || coverage > 100) {
            throw new IllegalArgumentException("覆盖率必须在0-100之间（百分比）！");
        }

        // 2. 查询参数实体
        ParameterTemplate param = parameterTemplateRepository.findById(paramId)
                .orElseThrow(() -> new RuntimeException("参数不存在，ID：" + paramId));

        // 3. 核心逻辑：根据覆盖率计算新权重（可按需修改公式）
        // 公式示例：权重 = 覆盖率 / 10 → 100%覆盖率对应10.0权重，50%对应5.0权重
        Double newWeight = coverage / 10.0;

        // 4. 限制权重在0-10之间（防止异常值）
        newWeight = Math.max(0.0, Math.min(10.0, newWeight));

        // 5. 更新参数的覆盖率和权重，保存到数据库（updateTime会自动刷新）
        param.setCoverage(coverage);
        param.setWeight(newWeight);
        parameterTemplateRepository.save(param);
    }

    @Override
    public Map<String, Double> getParameterWeightAndCoverage(Long id) {
        // 1. 查询参数DTO（复用已有的getParameterById方法）
        ParameterTemplateDto paramDto = getParameterById(id);

        // 2. 封装权重和覆盖率到Map（给前端返回）
        Map<String, Double> result = new HashMap<>();
        result.put("weight", paramDto.getWeight()); // 权重
        result.put("coverage", paramDto.getCoverage()); // 覆盖率

        return result;
    }

    @Override
    public ParameterTemplate getById(Long id) {
    // 调用 JPA 的 findById 方法查询，为空返回 null
    return parameterTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public List<ParameterTemplateDto> getAllParameters() {
    // 1. 查询数据库中所有参数实体
    List<ParameterTemplate> paramEntities = parameterTemplateRepository.findAll();
    // 2. 转换为 Dto（和分页查询的转换逻辑一致，复用已有的转换方法）
    return paramEntities.stream()
            .map(this::convertToDto) // 假设你已有 convertToDto 方法（将实体转为Dto）
            .collect(Collectors.toList());
}
}
 
