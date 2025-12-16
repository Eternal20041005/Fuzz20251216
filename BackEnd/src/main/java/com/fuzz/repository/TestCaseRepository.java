package com.fuzz.repository;

import com.fuzz.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 测试用例数据访问接口
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, String> {

    /**
     * 分页查询所有测试用例，按测试时间倒序排列
     */
    Page<TestCase> findAllByOrderByTestTimeDesc(Pageable pageable);

    /**
     * 根据是否触发漏洞查询测试用例，按测试时间倒序排列
     */
    Page<TestCase> findByTriggeredBugOrderByTestTimeDesc(boolean triggeredBug, Pageable pageable);

    /**
     * 根据参数组合包含指定字符串查询测试用例，按测试时间倒序排列
     */
    @Query("SELECT t FROM TestCase t WHERE t.paramCombo LIKE %:paramValue% ORDER BY t.testTime DESC")
    Page<TestCase> findByParamComboContainingOrderByTestTimeDesc(@Param("paramValue") String paramValue, Pageable pageable);

    /**
     * 根据是否触发漏洞和参数组合包含指定字符串查询测试用例，按测试时间倒序排列
     */
    @Query("SELECT t FROM TestCase t WHERE t.triggeredBug = :triggeredBug AND t.paramCombo LIKE %:paramValue% ORDER BY t.testTime DESC")
    Page<TestCase> findByTriggeredBugAndParamComboContainingOrderByTestTimeDesc(
            @Param("triggeredBug") boolean triggeredBug,
            @Param("paramValue") String paramValue,
            Pageable pageable);
    
    /**
     * 根据参数组合同时包含两个指定字符串查询测试用例，按测试时间倒序排列
     */
    @Query("SELECT t FROM TestCase t WHERE t.paramCombo LIKE %:param1% AND t.paramCombo LIKE %:param2% ORDER BY t.testTime DESC")
    Page<TestCase> findByParamComboContainingAndParamComboContainingOrderByTestTimeDesc(
            @Param("param1") String param1,
            @Param("param2") String param2,
            Pageable pageable);
    
    /**
     * 根据是否触发漏洞和参数组合同时包含两个指定字符串查询测试用例，按测试时间倒序排列
     */
    @Query("SELECT t FROM TestCase t WHERE t.triggeredBug = :triggeredBug AND t.paramCombo LIKE %:param1% AND t.paramCombo LIKE %:param2% ORDER BY t.testTime DESC")
    Page<TestCase> findByTriggeredBugAndParamComboContainingAndParamComboContainingOrderByTestTimeDesc(
            @Param("triggeredBug") boolean triggeredBug,
            @Param("param1") String param1,
            @Param("param2") String param2,
            Pageable pageable);
}
