package com.fuzz.repository;

import com.fuzz.entity.ParameterTemplate;
import com.fuzz.entity.ParameterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 参数模板数据访问接口
 */
@Repository
public interface ParameterTemplateRepository extends JpaRepository<ParameterTemplate, Long>, 
                                                   JpaSpecificationExecutor<ParameterTemplate> {

    /**
     * 根据参数名查找参数模板
     */
    Optional<ParameterTemplate> findByParamName(String paramName);

    /**
     * 检查参数名是否存在
     */
    boolean existsByParamName(String paramName);

    /**
     * 根据类别查找参数模板
     */
    List<ParameterTemplate> findByCategory(String category);

    /**
     * 根据参数类型查找参数模板
     */
    List<ParameterTemplate> findByParamType(ParameterType paramType);

    /**
     * 查找所有测试默认启用的参数
     */
    List<ParameterTemplate> findByIsTestDefaultTrue();

    /**
     * 根据参数名模糊搜索
     */
    @Query("SELECT p FROM ParameterTemplate p WHERE " +
           "LOWER(p.paramName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ParameterTemplate> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据类别和关键词搜索
     */
    @Query("SELECT p FROM ParameterTemplate p WHERE " +
           "p.category = :category AND " +
           "(LOWER(p.paramName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ParameterTemplate> findByCategoryAndKeyword(@Param("category") String category, 
                                                    @Param("keyword") String keyword, 
                                                    Pageable pageable);

    /**
     * 获取所有不同的类别
     */
    @Query("SELECT DISTINCT p.category FROM ParameterTemplate p ORDER BY p.category")
    List<String> findAllCategories();

    /**
     * 统计各类别的参数数量
     */
    @Query("SELECT p.category, COUNT(p) FROM ParameterTemplate p GROUP BY p.category")
    List<Object[]> countByCategory();

    /**
     * 批量删除指定ID的参数
     */
    void deleteByIdIn(List<Long> ids);
    
    /**
     * 获取所有不同的设置范围
     */
    @Query("SELECT DISTINCT p.valueRange FROM ParameterTemplate p WHERE p.valueRange IS NOT NULL ORDER BY p.valueRange")
    List<String> findAllValueRanges();
    
    /**
     * 根据设置范围查找参数
     */
    List<ParameterTemplate> findByValueRange(String valueRange);
    
    /**
     * 统计各设置范围的参数数量
     */
    @Query("SELECT p.valueRange, COUNT(p) FROM ParameterTemplate p WHERE p.valueRange IS NOT NULL GROUP BY p.valueRange")
    List<Object[]> countByValueRange();
    
    /**
     * 查找有候选取值的参数
     */
    @Query("SELECT p FROM ParameterTemplate p WHERE p.allowedValues IS NOT NULL AND p.allowedValues != ''")
    List<ParameterTemplate> findParametersWithCandidateValues();
    
    /**
     * 查找有范围约束的参数
     */
    @Query("SELECT p FROM ParameterTemplate p WHERE p.minValue IS NOT NULL OR p.maxValue IS NOT NULL")
    List<ParameterTemplate> findParametersWithRangeConstraints();
}