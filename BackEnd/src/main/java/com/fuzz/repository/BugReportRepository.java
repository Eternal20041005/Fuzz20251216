package com.fuzz.repository;

import com.fuzz.entity.BugReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Bug报告数据访问接口
 */
@Repository
public interface BugReportRepository extends JpaRepository<BugReport, Long> {

    /**
     * 根据Bug类型查找Bug报告
     */
    List<BugReport> findByBugType(String bugType);

    /**
     * 根据目标数据库查找Bug报告
     */
    List<BugReport> findByTargetDatabase(String targetDatabase);

    /**
     * 根据Oracle类型查找Bug报告
     */
    List<BugReport> findByOracleType(String oracleType);

    /**
     * 根据Bug类型和目标数据库查找Bug报告
     */
    List<BugReport> findByBugTypeAndTargetDatabase(String bugType, String targetDatabase);

    /**
     * 根据时间范围查找Bug报告
     */
    List<BugReport> findByTestTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询所有Bug报告，按创建时间倒序排列
     */
    Page<BugReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 统计不同类型的Bug数量
     */
    @Query("SELECT br.bugType, COUNT(br) FROM BugReport br GROUP BY br.bugType")
    List<Object[]> countBugsByType();

    /**
     * 统计不同数据库的Bug数量
     */
    @Query("SELECT br.targetDatabase, COUNT(br) FROM BugReport br GROUP BY br.targetDatabase")
    List<Object[]> countBugsByDatabase();

    /**
     * 查找最近的Bug报告
     */
    @Query("SELECT br FROM BugReport br ORDER BY br.createdAt DESC")
    List<BugReport> findRecentBugs(Pageable pageable);

    /**
     * 根据ID查找Bug报告，包含参数设置
     */
    @Query("SELECT br FROM BugReport br WHERE br.id = :id")
    BugReport findByIdWithParameterSettings(@Param("id") Long id);

    /**
     * 获取Bug总数
     */
    @Query("SELECT COUNT(br) FROM BugReport br")
    long countAllBugs();
}
