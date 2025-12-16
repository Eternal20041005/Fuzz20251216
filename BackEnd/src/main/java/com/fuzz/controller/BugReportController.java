package com.fuzz.controller;

import com.fuzz.dto.BugReportDto;
import com.fuzz.dto.PagedResponse;
import com.fuzz.service.BugReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Bug报告控制器
 */
@RestController
@RequestMapping("/bug-reports")
public class BugReportController {

    private static final Logger logger = LoggerFactory.getLogger(BugReportController.class);

    @Autowired
    private BugReportService bugReportService;

    /**
     * 分页获取Bug报告列表
     */
    @GetMapping
    public ResponseEntity<PagedResponse<BugReportDto>> getBugReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("分页获取Bug报告列表: page={}, size={}", page, size);

        PagedResponse<BugReportDto> response = bugReportService.getBugReports(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取Bug报告详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<BugReportDto> getBugReport(@PathVariable Long id) {
        logger.info("获取Bug报告详情: id={}", id);

        BugReportDto bugReport = bugReportService.getBugReportById(id);
        return ResponseEntity.ok(bugReport);
    }

    /**
     * 创建新的Bug报告
     */
    @PostMapping
    public ResponseEntity<BugReportDto> createBugReport(@RequestBody CreateBugReportRequest request) {
        logger.info("创建Bug报告: bugType={}, targetDatabase={}, oracleType={}",
                   request.getBugType(), request.getTargetDatabase(), request.getOracleType());

        BugReportDto createdBug = bugReportService.createBugReport(
                request.getBugType(),
                request.getTargetDatabase(),
                request.getOracleType(),
                request.getTestCase(),
                request.getParameterSettings(),
                request.getErrorMessage()
        );

        return ResponseEntity.ok(createdBug);
    }

    /**
     * 更新Bug报告
     */
    @PutMapping("/{id}")
    public ResponseEntity<BugReportDto> updateBugReport(
            @PathVariable Long id,
            @RequestBody UpdateBugReportRequest request) {

        logger.info("更新Bug报告: id={}", id);

        BugReportDto updatedBug = bugReportService.updateBugReport(
                id,
                request.getBugType(),
                request.getTargetDatabase(),
                request.getOracleType(),
                request.getTestCase(),
                request.getParameterSettings(),
                request.getErrorMessage()
        );

        return ResponseEntity.ok(updatedBug);
    }

    /**
     * 删除Bug报告
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBugReport(@PathVariable Long id) {
        logger.info("删除Bug报告: id={}", id);

        bugReportService.deleteBugReport(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除Bug报告
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteBugReports(@RequestBody List<Long> ids) {
        logger.info("批量删除Bug报告: count={}", ids.size());

        bugReportService.batchDeleteBugReports(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据Bug类型获取Bug报告
     */
    @GetMapping("/type/{bugType}")
    public ResponseEntity<List<BugReportDto>> getBugReportsByType(@PathVariable String bugType) {
        logger.info("根据Bug类型获取Bug报告: bugType={}", bugType);

        List<BugReportDto> bugReports = bugReportService.getBugReportsByType(bugType);
        return ResponseEntity.ok(bugReports);
    }

    /**
     * 根据目标数据库获取Bug报告
     */
    @GetMapping("/database/{targetDatabase}")
    public ResponseEntity<List<BugReportDto>> getBugReportsByDatabase(@PathVariable String targetDatabase) {
        logger.info("根据目标数据库获取Bug报告: targetDatabase={}", targetDatabase);

        List<BugReportDto> bugReports = bugReportService.getBugReportsByDatabase(targetDatabase);
        return ResponseEntity.ok(bugReports);
    }

    /**
     * 根据Oracle类型获取Bug报告
     */
    @GetMapping("/oracle/{oracleType}")
    public ResponseEntity<List<BugReportDto>> getBugReportsByOracleType(@PathVariable String oracleType) {
        logger.info("根据Oracle类型获取Bug报告: oracleType={}", oracleType);

        List<BugReportDto> bugReports = bugReportService.getBugReportsByOracleType(oracleType);
        return ResponseEntity.ok(bugReports);
    }

    /**
     * 获取最近的Bug报告
     */
    @GetMapping("/recent")
    public ResponseEntity<List<BugReportDto>> getRecentBugReports(
            @RequestParam(defaultValue = "10") int limit) {
        logger.info("获取最近的Bug报告: limit={}", limit);

        List<BugReportDto> recentBugs = bugReportService.getRecentBugReports(limit);
        return ResponseEntity.ok(recentBugs);
    }

    /**
     * 获取Bug统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getBugStatistics() {
        logger.info("获取Bug统计信息");

        Map<String, Object> statistics = Map.of(
                "totalCount", bugReportService.getTotalBugCount(),
                "byType", bugReportService.getBugStatisticsByType(),
                "byDatabase", bugReportService.getBugStatisticsByDatabase()
        );

        return ResponseEntity.ok(statistics);
    }

    /**
     * 导出Bug报告数据
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportBugReports(@RequestParam(defaultValue = "json") String format) {
        logger.info("导出Bug报告数据: format={}", format);

        String exportData = bugReportService.exportBugReports(format);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Content-Disposition", "attachment; filename=\"bug-reports." + format + "\"")
                .body(exportData);
    }

    // 请求DTO类
    public static class CreateBugReportRequest {
        private String bugType;
        private String targetDatabase;
        private String oracleType;
        private String testCase;
        private Map<String, String> parameterSettings;
        private String errorMessage;

        // Getter 和 Setter
        public String getBugType() { return bugType; }
        public void setBugType(String bugType) { this.bugType = bugType; }

        public String getTargetDatabase() { return targetDatabase; }
        public void setTargetDatabase(String targetDatabase) { this.targetDatabase = targetDatabase; }

        public String getOracleType() { return oracleType; }
        public void setOracleType(String oracleType) { this.oracleType = oracleType; }

        public String getTestCase() { return testCase; }
        public void setTestCase(String testCase) { this.testCase = testCase; }

        public Map<String, String> getParameterSettings() { return parameterSettings; }
        public void setParameterSettings(Map<String, String> parameterSettings) { this.parameterSettings = parameterSettings; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    public static class UpdateBugReportRequest extends CreateBugReportRequest {
        // 继承CreateBugReportRequest的所有字段
    }
}
