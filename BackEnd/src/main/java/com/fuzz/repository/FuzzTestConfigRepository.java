package com.fuzz.repository;

import com.fuzz.entity.FuzzTestConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 模糊测试配置数据访问接口
 */
@Repository
public interface FuzzTestConfigRepository extends JpaRepository<FuzzTestConfig, Long> {

    /**
     * 根据配置名称查找配置
     */
    Optional<FuzzTestConfig> findByConfigName(String configName);

    /**
     * 获取所有配置，按更新时间倒序排列
     */
    List<FuzzTestConfig> findAllByOrderByUpdatedAtDesc();

    /**
     * 检查配置名称是否存在
     */
    boolean existsByConfigName(String configName);

    /**
     * 根据配置名称删除配置
     */
    void deleteByConfigName(String configName);

    /**
     * 获取默认配置（通常是名为"default"的配置）
     */
    @Query("SELECT f FROM FuzzTestConfig f WHERE f.configName = :configName")
    Optional<FuzzTestConfig> findDefaultConfig(@Param("configName") String configName);
}
