package com.fuzz.repository;

import com.fuzz.entity.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试状态数据访问接口
 */
@Repository
public interface TestStatusRepository extends JpaRepository<TestStatus, Long> {

    /**
     * 根据任务状态查找测试状态
     */
    List<TestStatus> findByTaskStatus(String taskStatus);

    /**
     * 根据测试Oracle查找测试状态
     */
    List<TestStatus> findByTestOracle(String testOracle);

    /**
     * 查找最新创建的测试状态记录
     */
    TestStatus findTopByOrderByCreatedAtDesc();

    /**
     * 查找最近修改的测试状态记录
     */
    TestStatus findTopByOrderByUpdatedAtDesc();

    TestStatus findTopByOrderByIdDesc();
}