package com.fuzz.controller;

import com.fuzz.dto.*;
import com.fuzz.service.ParameterConstraintValidator.ValidationResult;
import com.fuzz.service.ParameterService;
import com.fuzz.service.DbParameterService;
import com.fuzz.entity.ParameterTemplate;
import com.fuzz.entity.ParameterType;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
/**
 * 参数管理控制器
 */
@RestController
@RequestMapping("/parameters")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class ParameterController {
    
    private static final Logger logger = LoggerFactory.getLogger(ParameterController.class);
    
    @Autowired
    private ParameterService parameterService;

    @Autowired
    private DbParameterService dbParameterService;
    
    /**
     * 获取参数列表（支持分页、搜索、过滤）
     */
    @GetMapping
    public ResponseEntity<PagedResponse<ParameterTemplateDto>> getParameters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        
        logger.info("获取参数列表: page={}, size={}, search={}, category={}", 
                   page, size, search, category);
        
        PagedResponse<ParameterTemplateDto> response = 
            parameterService.getParameters(page, size, search, category);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据ID获取参数详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParameterTemplateDto> getParameter(@PathVariable Long id) {
        logger.info("获取参数详情: id={}", id);
        
        ParameterTemplateDto parameter = parameterService.getParameterById(id);
        return ResponseEntity.ok(parameter);
    }
    
    /**
     * 更新参数
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParameterTemplateDto> updateParameter(
            @PathVariable Long id,
            @Valid @RequestBody UpdateParameterRequest request,
            @RequestParam(required = false) String dbType) {

        logger.info("更新参数: id={}, request={}, dbType={}", id, request, dbType);

        ParameterTemplateDto updated;
        if (dbType != null && !dbType.isEmpty()) {
            // 使用 DbParameterService
            DbParameterDto dbParamDto = dbParameterService.updateParameter(
                id,
                request.getDefaultValue(),
                request.getIsTestDefault(),
                request.getWeight()
            );
            updated = convertDbParameterToTemplate(dbParamDto);
        } else {
            // 使用原有的 ParameterService
            updated = parameterService.updateParameter(id, request);
        }

        return ResponseEntity.ok(updated);
    }
    
    /**
     * 批量更新参数
     */
    @PutMapping("/batch")
    public ResponseEntity<BatchUpdateResult> batchUpdateParameters(
            @Valid @RequestBody List<UpdateParameterRequest> requests,
            @RequestParam(required = false) String dbType) {

        logger.info("批量更新参数: count={}, dbType={}", requests.size(), dbType);

        BatchUpdateResult result;
        if (dbType != null && !dbType.isEmpty()) {
            // 使用 DbParameterService
            result = dbParameterService.batchUpdateParameters(requests);
        } else {
            // 使用原有的 ParameterService
            result = parameterService.batchUpdateParameters(requests);
        }

        return ResponseEntity.ok(result);
    }
    
    /**
     * 从数据库导入参数
     */
    @PostMapping("/import/{dbConfigId}")
    public ResponseEntity<ImportResult> importFromDatabase(@PathVariable Long dbConfigId) {
        logger.info("从数据库导入参数: dbConfigId={}", dbConfigId);
        
        ImportResult result = parameterService.importParametersFromDatabase(dbConfigId);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 获取所有参数类别
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories(@RequestParam(required = false) String dbType) {
        logger.info("获取所有参数类别, dbType={}", dbType);

        List<String> categories;
        if (dbType != null && !dbType.isEmpty()) {
            // 使用 DbParameterService 获取指定数据库类型的类别
            categories = dbParameterService.getCategoriesByDbType(dbType);
        } else {
            // 使用原有的 ParameterService
            categories = parameterService.getAllCategories();
        }

        return ResponseEntity.ok(categories);
    }
    
    /**
     * 删除参数
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParameter(@PathVariable Long id) {
        logger.info("删除参数: id={}", id);
        
        parameterService.deleteParameter(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 批量删除参数
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteParameters(@RequestBody List<Long> ids) {
        logger.info("批量删除参数: count={}", ids.size());
        
        parameterService.batchDeleteParameters(ids);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据参数名获取参数详情
     */
    @GetMapping("/name/{paramName}")
    public ResponseEntity<ParameterTemplateDto> getParameterByName(@PathVariable String paramName) {
        logger.info("根据参数名获取参数详情: paramName={}", paramName);
        
        ParameterTemplateDto parameter = parameterService.getParameterByName(paramName);
        return ResponseEntity.ok(parameter);
    }
    
    /**
     * 验证参数值
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validateParameterValue(
            @RequestParam String paramName,
            @RequestParam String value) {
        
        logger.info("验证参数值: paramName={}, value={}", paramName, value);
        
        ValidationResult result = parameterService.validateParameterConstraints(paramName, value);
        
        if (result.isValid()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 获取参数的候选取值
     */
    @GetMapping("/{id}/candidates")
    public ResponseEntity<List<String>> getParameterCandidateValues(@PathVariable Long id) {
        logger.info("获取参数候选取值: id={}", id);
        
        List<String> candidateValues = parameterService.getParameterCandidateValues(id);
        return ResponseEntity.ok(candidateValues);
    }
    
    /**
     * 获取参数的约束信息
     */
    @GetMapping("/{id}/constraints")
    public ResponseEntity<ParameterConstraintDto> getParameterConstraints(@PathVariable Long id) {
        logger.info("获取参数约束信息: id={}", id);
        
        ParameterConstraintDto constraints = parameterService.getParameterConstraints(id);
        return ResponseEntity.ok(constraints);
    }
    
    /**
     * 获取增强的参数列表（支持数据库类型筛选）
     */
    @GetMapping("/enhanced")
    public ResponseEntity<PagedResponse<ParameterTemplateDto>> getEnhancedParameters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String testStatus,
            @RequestParam(required = false) String dbType) {

        // 转换testStatus字符串为Boolean
        Boolean testStatusBool = null;
        if (testStatus != null && !testStatus.isEmpty()) {
            testStatusBool = Boolean.parseBoolean(testStatus);
        }

        logger.info("获取增强参数列表: page={}, size={}, search={}, category={}, testStatus={}, testStatusBool={}, dbType={}",
                   page, size, search, category, testStatus, testStatusBool, dbType);

        PagedResponse<ParameterTemplateDto> response;

        // 如果指定了数据库类型，使用新的 DbParameterService
        if (dbType != null && !dbType.isEmpty()) {
            PagedResponse<DbParameterDto> dbResponse = dbParameterService.getParameters(dbType, page, size, search, category, testStatusBool);
            response = convertDbParameterResponseToTemplateResponse(dbResponse);
        } else {
            // 使用原有的 ParameterService
            response = parameterService.getParameters(page, size, search, category);
        }

        return ResponseEntity.ok(response);
    }
        // ===================== 新增：权重相关接口 =====================
    /**
     * 1. 手动更新参数权重（前端手动设置权重）
     * 接口地址：PUT /parameters/{id}/weight
     * 请求参数：weight=5.5（0-10之间），可选 dbType
     */
    @PutMapping("/{id}/weight")
    public ResponseEntity<ParameterTemplateDto> updateParameterWeight(
            @PathVariable Long id,  // 参数ID（从URL中取）
            @RequestParam Double weight,  // 新权重（从请求参数中取）
            @RequestParam(required = false) String dbType) {  // 可选的数据库类型

        logger.info("手动更新参数权重: id={}, weight={}, dbType={}", id, weight, dbType);

        ParameterTemplateDto updatedDto;
        if (dbType != null && !dbType.isEmpty()) {
            // 使用 DbParameterService
            DbParameterDto dbParamDto = dbParameterService.updateParameterWeight(id, weight);
            updatedDto = convertDbParameterToTemplate(dbParamDto);
        } else {
            // 使用原有的 ParameterService
            updatedDto = parameterService.updateParameterWeight(id, weight);
        }

        return ResponseEntity.ok(updatedDto); // 返回更新后的参数详情
    }

    /**
     * 2. 上传代码覆盖率并自动调整权重（核心接口）
     * 接口地址：POST /parameters/{id}/coverage
     * 请求参数：coverage=85.5（0-100之间，百分比），可选 dbType
     */
    @PostMapping("/{id}/coverage")
    public ResponseEntity<Void> setCoverageAndAdjustWeight(
            @PathVariable Long id,  // 参数ID（从URL中取）
            @RequestParam Double coverage,  // 代码覆盖率（从请求参数中取）
            @RequestParam(required = false) String dbType) {  // 可选的数据库类型

        logger.info("上传覆盖率并自动调整权重: id={}, coverage={}%, dbType={}", id, coverage, dbType);

        if (dbType != null && !dbType.isEmpty()) {
            // 使用 DbParameterService
            dbParameterService.adjustWeightByCoverage(id, coverage);
        } else {
            // 使用原有的 ParameterService
            parameterService.adjustWeightByCoverage(id, coverage);
        }

        return ResponseEntity.noContent().build(); // 成功返回204状态码（无内容）
    }

    /**
     * 3. 获取参数的权重和覆盖率信息（给前端展示用）
     * 接口地址：GET /parameters/{id}/weight-coverage
     * 返回结果：{"weight":5.5, "coverage":85.5}
     */
  @GetMapping("/{id}/weight-coverage")
  public ResponseEntity<Map<String, Double>> getParameterWeightAndCoverage(
          @PathVariable Long id,
          @RequestParam(required = false) String dbType) {
    logger.info("获取参数权重和覆盖率: id={}, dbType={}", id, dbType);

    Map<String, Double> result;
    if (dbType != null && !dbType.isEmpty()) {
        // 使用 DbParameterService
        result = dbParameterService.getParameterWeightAndCoverage(id);
    } else {
        // 使用原有的 ParameterService
        ParameterTemplate param = parameterService.getById(id);
        if (param == null) {
            // 若查询不到数据，返回 404 提示（避免返回空 {}）
            return ResponseEntity.notFound().build();
        }
        // 关键：封装 weight 和 coverage 字段，确保 key 正确
        result = new HashMap<>();
        result.put("weight", param.getWeight()); // 对应实体类的 weight 字段
        result.put("coverage", param.getCoverage()); // 对应实体类的 coverage 字段
    }

    return ResponseEntity.ok(result);
}
/**
 * 4. 获取所有参数列表（无分页，给前端权重调整区展示用）
 * 接口地址：GET /parameters/all
 * 返回结果：[{id:1, paramName:"xxx", weight:1.0, coverage:0.0}, ...]
 */
@GetMapping("/all")
public ResponseEntity<List<ParameterTemplateDto>> getAllParameters(
        @RequestParam(required = false) String dbType) {
    logger.info("获取所有参数列表（无分页）, dbType={}", dbType);

    List<ParameterTemplateDto> allParams;

    if (dbType != null && !dbType.isEmpty()) {
        // 使用 DbParameterService
        List<DbParameterDto> dbParams = dbParameterService.getAllParameters(dbType);
        allParams = dbParams.stream()
                .map(this::convertDbParameterToTemplate)
                .collect(Collectors.toList());
    } else {
        // 使用原有的 ParameterService
        allParams = parameterService.getAllParameters();
    }

    return ResponseEntity.ok(allParams);
}

    /**
     * 将 DbParameterDto 转换为 ParameterTemplateDto
     */
    private ParameterTemplateDto convertDbParameterToTemplate(DbParameterDto dbParam) {
        ParameterTemplateDto template = new ParameterTemplateDto();

        template.setId(dbParam.getId());
        template.setParamName(dbParam.getParamName());
        template.setDescription(dbParam.getDescription());
        template.setCategory(dbParam.getCategory());
        template.setDefaultValue(dbParam.getDefaultValue());
        template.setParamType(mapToParameterType(dbParam.getParamType()));
        template.setIsTestDefault(dbParam.getIsTestDefault());
        template.setValueRange(dbParam.getValueRange());
        template.setWeight(dbParam.getWeightAsDouble());
        template.setCoverage(dbParam.getCoverageAsDouble());
        template.setCandidateValues(dbParam.getCandidateValues());
        template.setAllowedValues(dbParam.getAllowedValues());

        // 设置时间字段
        if (dbParam.getCreatedAt() != null) {
            template.setCreateTime(dbParam.getCreatedAt());
        }
        if (dbParam.getUpdatedAt() != null) {
            template.setUpdateTime(dbParam.getUpdatedAt());
        }

        return template;
    }

    /**
     * 将标准MySQL参数类型映射为ParameterType枚举
     */
    private ParameterType mapToParameterType(String paramType) {
        if (paramType == null) {
            return ParameterType.STRING;
        }

        switch (paramType.toUpperCase()) {
            case "INTEGER":
                return ParameterType.INTEGER;
            case "BOOLEAN":
                return ParameterType.BOOLEAN;
            case "NUMERIC":
            case "DECIMAL":
                return ParameterType.DECIMAL;
            case "STRING":
            case "ENUMERATION":
            case "SET":
            case "BITMAP":
            case "FILE NAME":
            case "DIRECTORY NAME":
            default:
                return ParameterType.STRING;
        }
    }

    /**
     * 将 DbParameter 分页响应转换为 ParameterTemplate 分页响应
     */
    private PagedResponse<ParameterTemplateDto> convertDbParameterResponseToTemplateResponse(
            PagedResponse<DbParameterDto> dbResponse) {

        List<ParameterTemplateDto> templateDtos = dbResponse.getContent().stream()
                .map(this::convertDbParameterToTemplate)
                .collect(Collectors.toList());

        return new PagedResponse<>(
            templateDtos,
            dbResponse.getTotalElements(),
            dbResponse.getCurrentPage(),
            dbResponse.getPageSize()
        );
    }
}