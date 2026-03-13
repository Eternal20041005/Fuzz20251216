package com.fuzz.service.impl;

import com.fuzz.dto.DbParameterDto;
import com.fuzz.dto.PagedResponse;
import com.fuzz.entity.DbParameter;
import com.fuzz.repository.DbParameterRepository;
import com.fuzz.service.DbParameterService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据库参数管理服务实现类
 */
@Service
@Transactional
public class DbParameterServiceImpl implements DbParameterService {

    private static final Logger logger = LoggerFactory.getLogger(DbParameterServiceImpl.class);

    @Autowired
    private DbParameterRepository dbParameterRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DbParameterDto> getParameters(String dbType, int page, int size, String search, String category, Boolean testStatus) {
        Specification<DbParameter> spec = buildSpecification(dbType, search, category, testStatus);

        // 按权重降序排序，权重为null的排在最后
        Pageable pageable = PageRequest.of(page, size,
            Sort.by(Sort.Order.desc("weight").nullsLast(), Sort.Order.asc("paramName")));

        Page<DbParameter> parameterPage = dbParameterRepository.findAll(spec, pageable);

        List<DbParameterDto> dtoList = parameterPage.getContent().stream()
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
    public DbParameterDto getParameterById(Long id) {
        DbParameter parameter = dbParameterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + id));
        return convertToDto(parameter);
    }

    @Override
    @Transactional(readOnly = true)
    public DbParameterDto getParameterByName(String dbType, String paramName) {
        DbParameter parameter = dbParameterRepository.findByDbTypeAndParamName(dbType, paramName)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + dbType + "/" + paramName));
        return convertToDto(parameter);
    }

    @Override
    public DbParameterDto updateParameter(Long id, String defaultValue, Boolean isTest, Double weight) {
        DbParameter parameter = dbParameterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("参数不存在: " + id));

        if (defaultValue != null) {
            parameter.setDefaultValue(defaultValue);
        }

        if (isTest != null) {
            parameter.setIsTest(isTest);
        }

        if (weight != null) {
            parameter.setWeight(BigDecimal.valueOf(weight));
        }

        DbParameter saved = dbParameterRepository.save(parameter);
        logger.info("参数更新成功: {}", saved.getParamName());

        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDbTypes() {
        return dbParameterRepository.findAllDbTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCategoriesByDbType(String dbType) {
        // 返回完整的标准MySQL参数类型列表
        return java.util.Arrays.asList(
            "Bitmap",
            "Boolean",
            "Directory name",
            "Enumeration",
            "File name",
            "Integer",
            "Numeric",
            "Set",
            "String"
        );
    }

    @Override
    public void deleteParameter(Long id) {
        if (!dbParameterRepository.existsById(id)) {
            throw new RuntimeException("参数不存在: " + id);
        }

        dbParameterRepository.deleteById(id);
        logger.info("参数删除成功: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DbParameterDto> getAllParameters(String dbType) {
        List<DbParameter> parameters = dbParameterRepository.findByDbType(dbType);
        return parameters.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 构建查询条件
     */
    private Specification<DbParameter> buildSpecification(String dbType, String search, String category, Boolean testStatus) {
        Specification<DbParameter> spec = Specification.where(null);

        // 数据库类型过滤（必需）
        if (StringUtils.hasText(dbType)) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("dbType"), dbType)
            );
        }

        // 搜索条件
        if (StringUtils.hasText(search)) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("paramName")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                )
            );
        }

        // 类别筛选条件
        if (StringUtils.hasText(category)) {
            // 将前端传递的类别名称转换为数据库中存储的paramType值
            String dbParamType = mapDisplayCategoryToDbType(category);
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("paramType"), dbParamType)
            );
        }

        // 测试状态筛选条件
        if (testStatus != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("isTest"), testStatus)
            );
        }

        return spec;
    }

    /**
     * 将前端显示的类别名称映射为数据库中存储的paramType值
     */
    private String mapDisplayCategoryToDbType(String displayCategory) {
        switch (displayCategory) {
            case "Integer": return "Integer";
            case "Boolean": return "Boolean";
            case "Numeric": return "Numeric";
            case "String": return "String";
            case "Enumeration": return "Enumeration";
            case "Set": return "Set";
            case "Bitmap": return "Bitmap";
            case "File name": return "File name";
            case "Directory name": return "Directory name";
            default: return displayCategory;
        }
    }

    /**
     * 转换为DTO
     */
    private DbParameterDto convertToDto(DbParameter entity) {
        return new DbParameterDto(
            entity.getId(),
            entity.getDbType(),
            entity.getParamName(),
            entity.getParamType(),
            entity.getDescription(),
            entity.getDefaultValue(),
            entity.getValueRange(),
            entity.getWeight(),
            entity.getCoverage(),
            entity.getIsTest(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    @Override
    public DbParameterDto updateParameterWeight(Long id, Double weight) {
        // 校验权重范围（必须在0-10之间，否则报错）
        if (weight < 0 || weight > 10) {
            throw new IllegalArgumentException("权重必须在0-10之间！");
        }

        // 根据ID查询参数实体（从数据库拿数据）
        DbParameter param = dbParameterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("参数不存在，ID：" + id));

        // 设置新权重，并保存到数据库
        param.setWeight(BigDecimal.valueOf(weight));
        DbParameter updatedParam = dbParameterRepository.save(param);

        // 把实体转换为DTO（给前端返回）
        return convertToDto(updatedParam);
    }

    @Override
    public void adjustWeightByCoverage(Long paramId, Double coverage) {
        // 校验覆盖率范围（必须在0-100之间，百分比）
        if (coverage < 0 || coverage > 100) {
            throw new IllegalArgumentException("覆盖率必须在0-100之间（百分比）！");
        }

        // 查询参数实体
        DbParameter param = dbParameterRepository.findById(paramId)
                .orElseThrow(() -> new RuntimeException("参数不存在，ID：" + paramId));

        // 核心逻辑：根据覆盖率计算新权重（可按需修改公式）
        // 公式示例：权重 = 覆盖率 / 10 → 100%覆盖率对应10.0权重，50%对应5.0权重
        Double newWeight = coverage / 10.0;

        // 限制权重在0-10之间（防止异常值）
        newWeight = Math.max(0.0, Math.min(10.0, newWeight));

        // 更新参数的覆盖率和权重，保存到数据库（updateTime会自动刷新）
        param.setCoverage(BigDecimal.valueOf(coverage));
        param.setWeight(BigDecimal.valueOf(newWeight));
        dbParameterRepository.save(param);
    }

    @Override
    public java.util.Map<String, Double> getParameterWeightAndCoverage(Long id) {
        // 查询参数DTO（复用已有的getParameterById方法）
        DbParameterDto paramDto = getParameterById(id);

        // 封装权重和覆盖率到Map（给前端返回）
        java.util.Map<String, Double> result = new java.util.HashMap<>();
        result.put("weight", paramDto.getWeightAsDouble()); // 权重
        result.put("coverage", paramDto.getCoverageAsDouble()); // 覆盖率

        return result;
    }

    @Override
    public com.fuzz.entity.DbParameter getById(Long id) {
        // 调用 JPA 的 findById 方法查询，为空返回 null
        return dbParameterRepository.findById(id).orElse(null);
    }

    @Override
    public com.fuzz.dto.BatchUpdateResult batchUpdateParameters(java.util.List<com.fuzz.dto.UpdateParameterRequest> requests) {
        int totalCount = requests.size();
        int successCount = 0;
        int failureCount = 0;
        java.util.List<String> errorMessages = new java.util.ArrayList<>();

        for (com.fuzz.dto.UpdateParameterRequest request : requests) {
            try {
                updateParameter(request.getId(), request.getDefaultValue(), request.getIsTestDefault(), request.getWeight());
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errorMessages.add("参数ID " + request.getId() + ": " + e.getMessage());
                logger.error("批量更新参数失败: {}", request.getId(), e);
            }
        }

        com.fuzz.dto.BatchUpdateResult result = new com.fuzz.dto.BatchUpdateResult(totalCount, successCount, failureCount);
        result.setErrorMessages(errorMessages);

        logger.info("批量更新完成: 总数={}, 成功={}, 失败={}", totalCount, successCount, failureCount);
        return result;
    }
}
