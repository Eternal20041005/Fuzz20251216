package com.fuzz.repository;

import com.fuzz.entity.DbParameter;
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
 * 数据库参数数据访问接口
 */
@Repository
public interface DbParameterRepository extends JpaRepository<DbParameter, Long>,
                                               JpaSpecificationExecutor<DbParameter> {

    /**
     * 根据数据库类型和参数名查找参数
     */
    Optional<DbParameter> findByDbTypeAndParamName(String dbType, String paramName);

    /**
     * 检查数据库类型和参数名是否存在
     */
    boolean existsByDbTypeAndParamName(String dbType, String paramName);

    /**
     * 根据数据库类型查找参数
     */
    List<DbParameter> findByDbType(String dbType);

    /**
     * 根据数据库类型分页查找参数
     */
    Page<DbParameter> findByDbType(String dbType, Pageable pageable);

    /**
     * 根据参数类型查找参数
     */
    List<DbParameter> findByParamType(String paramType);

    /**
     * 查找所有测试启用的参数
     */
    List<DbParameter> findByIsTestTrue();

    /**
     * 根据数据库类型和关键词搜索
     */
    @Query("SELECT p FROM DbParameter p WHERE " +
           "p.dbType = :dbType AND " +
           "(LOWER(p.paramName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<DbParameter> findByDbTypeAndKeyword(@Param("dbType") String dbType,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    /**
     * 获取所有不同的数据库类型
     */
    @Query("SELECT DISTINCT p.dbType FROM DbParameter p ORDER BY p.dbType")
    List<String> findAllDbTypes();

    /**
     * 获取所有不同的参数类型
     */
    @Query("SELECT DISTINCT p.paramType FROM DbParameter p ORDER BY p.paramType")
    List<String> findAllParamTypes();

    /**
     * 根据数据库类型统计参数数量
     */
    @Query("SELECT p.dbType, COUNT(p) FROM DbParameter p GROUP BY p.dbType")
    List<Object[]> countByDbType();

    /**
     * 根据参数类型统计参数数量
     */
    @Query("SELECT p.paramType, COUNT(p) FROM DbParameter p GROUP BY p.paramType")
    List<Object[]> countByParamType();

    /**
     * 根据数据库类型查找有取值范围的参数
     */
    @Query("SELECT p FROM DbParameter p WHERE p.dbType = :dbType AND p.valueRange IS NOT NULL AND p.valueRange != ''")
    List<DbParameter> findParametersWithValueRangeByDbType(@Param("dbType") String dbType);

    /**
     * 根据数据库类型查找测试启用的参数
     */
    @Query("SELECT p FROM DbParameter p WHERE p.dbType = :dbType AND p.isTest = true")
    List<DbParameter> findTestEnabledParametersByDbType(@Param("dbType") String dbType);

    /**
     * 批量删除指定数据库类型的参数
     */
    void deleteByDbType(String dbType);

    /**
     * 批量删除指定ID的参数
     */
    void deleteByIdIn(List<Long> ids);
}