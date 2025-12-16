package com.fuzz.repository;

import com.fuzz.entity.DbParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据库参数数据访问接口
 */
@Repository
public interface DbParameterRepository extends JpaRepository<DbParameter, Long> {

    /**
     * 根据数据库类型查找参数
     */
    List<DbParameter> findByDbType(String dbType);

    /**
     * 查找是否测试的参数
     */
    List<DbParameter> findByIsTest(Boolean isTest);

    /**
     * 根据数据库类型和参数名称查找参数
     */
    DbParameter findByDbTypeAndParamName(String dbType, String paramName);
}