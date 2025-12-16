package com.fuzz.repository;

import com.fuzz.entity.DatabaseConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据库配置数据访问接口
 */
@Repository
public interface DatabaseConfigRepository extends JpaRepository<DatabaseConfig, Long> {

    /**
     * 根据名称查找数据库配置
     */
    Optional<DatabaseConfig> findByName(String name);

    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据数据库类型查找配置
     */
    List<DatabaseConfig> findByDbType(String dbType);

    /**
     * 根据状态查找配置
     */
    List<DatabaseConfig> findByStatus(String status);

    /**
     * 查找所有可用的数据库配置（状态不为"连接失败"）
     */
    @Query("SELECT d FROM DatabaseConfig d WHERE d.status != '连接失败' ORDER BY d.createTime DESC")
    List<DatabaseConfig> findAllAvailable();

    /**
     * 根据数据库类型和状态查找配置
     */
    List<DatabaseConfig> findByDbTypeAndStatus(String dbType, String status);

    /**
     * 统计各数据库类型的配置数量
     */
    @Query("SELECT d.dbType, COUNT(d) FROM DatabaseConfig d GROUP BY d.dbType")
    List<Object[]> countByDbType();

    /**
     * 查找最近创建的配置
     */
    @Query("SELECT d FROM DatabaseConfig d ORDER BY d.createTime DESC")
    List<DatabaseConfig> findAllOrderByCreateTimeDesc();
}