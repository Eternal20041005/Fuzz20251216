package com.fuzz.service.impl;

import com.fuzz.dto.BugReportDto;
import com.fuzz.dto.PagedResponse;
import com.fuzz.entity.BugReport;
import com.fuzz.repository.BugReportRepository;
import com.fuzz.service.BugReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bug报告服务实现类
 */
@Service
@Transactional
public class BugReportServiceImpl implements BugReportService {

    private static final Logger logger = LoggerFactory.getLogger(BugReportServiceImpl.class);

    @Autowired
    private BugReportRepository bugReportRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BugReportDto> getBugReports(int page, int size) {
        logger.info("获取Bug报告列表: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<BugReport> bugPage = bugReportRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<BugReportDto> bugDtos = bugPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        PagedResponse<BugReportDto> response = new PagedResponse<>();
        response.setContent(bugDtos);
        response.setTotalElements(bugPage.getTotalElements());
        response.setTotalPages(bugPage.getTotalPages());
        response.setCurrentPage(bugPage.getNumber());
        response.setPageSize(bugPage.getSize());
        response.setHasNext(bugPage.hasNext());
        response.setHasPrevious(bugPage.hasPrevious());
        response.setFirst(bugPage.isFirst());
        response.setLast(bugPage.isLast());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BugReportDto getBugReportById(Long id) {
        logger.info("获取Bug报告详情: id={}", id);

        BugReport bugReport = bugReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug报告不存在: " + id));

        return convertToDto(bugReport);
    }

    @Override
    public BugReportDto createBugReport(String bugType, String targetDatabase, String oracleType,
                                      String testCase, Map<String, String> parameterSettings,
                                      String errorMessage) {
        logger.info("创建Bug报告: bugType={}, targetDatabase={}, oracleType={}",
                   bugType, targetDatabase, oracleType);

        BugReport bugReport = new BugReport(bugType, targetDatabase, oracleType, testCase, parameterSettings);
        bugReport.setErrorMessage(errorMessage);
        bugReport.setTestTime(LocalDateTime.now());

        BugReport savedBug = bugReportRepository.save(bugReport);
        logger.info("Bug报告创建成功: id={}", savedBug.getId());

        return convertToDto(savedBug);
    }

    @Override
    public BugReportDto updateBugReport(Long id, String bugType, String targetDatabase,
                                      String oracleType, String testCase,
                                      Map<String, String> parameterSettings, String errorMessage) {
        logger.info("更新Bug报告: id={}", id);

        BugReport bugReport = bugReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug报告不存在: " + id));

        bugReport.setBugType(bugType);
        bugReport.setTargetDatabase(targetDatabase);
        bugReport.setOracleType(oracleType);
        bugReport.setTestCase(testCase);
        bugReport.setParameterSettings(parameterSettings);
        bugReport.setErrorMessage(errorMessage);
        bugReport.setUpdatedAt(LocalDateTime.now());

        BugReport updatedBug = bugReportRepository.save(bugReport);
        logger.info("Bug报告更新成功: id={}", updatedBug.getId());

        return convertToDto(updatedBug);
    }

    @Override
    public void deleteBugReport(Long id) {
        logger.info("删除Bug报告: id={}", id);

        if (!bugReportRepository.existsById(id)) {
            throw new RuntimeException("Bug报告不存在: " + id);
        }

        bugReportRepository.deleteById(id);
        logger.info("Bug报告删除成功: id={}", id);
    }

    @Override
    public void batchDeleteBugReports(List<Long> ids) {
        logger.info("批量删除Bug报告: count={}", ids.size());

        List<BugReport> bugsToDelete = bugReportRepository.findAllById(ids);
        bugReportRepository.deleteAll(bugsToDelete);

        logger.info("批量删除Bug报告成功: count={}", bugsToDelete.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BugReportDto> getBugReportsByType(String bugType) {
        logger.info("根据Bug类型获取Bug报告: bugType={}", bugType);

        List<BugReport> bugReports = bugReportRepository.findByBugType(bugType);
        return bugReports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BugReportDto> getBugReportsByDatabase(String targetDatabase) {
        logger.info("根据目标数据库获取Bug报告: targetDatabase={}", targetDatabase);

        List<BugReport> bugReports = bugReportRepository.findByTargetDatabase(targetDatabase);
        return bugReports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BugReportDto> getBugReportsByOracleType(String oracleType) {
        logger.info("根据Oracle类型获取Bug报告: oracleType={}", oracleType);

        List<BugReport> bugReports = bugReportRepository.findByOracleType(oracleType);
        return bugReports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BugReportDto> getRecentBugReports(int limit) {
        logger.info("获取最近的Bug报告: limit={}", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<BugReport> recentBugs = bugReportRepository.findRecentBugs(pageable);

        return recentBugs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getBugStatisticsByType() {
        logger.info("统计Bug类型分布");

        List<Object[]> results = bugReportRepository.countBugsByType();
        Map<String, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            String bugType = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(bugType, count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getBugStatisticsByDatabase() {
        logger.info("统计数据库Bug分布");

        List<Object[]> results = bugReportRepository.countBugsByDatabase();
        Map<String, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            String database = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(database, count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalBugCount() {
        return bugReportRepository.countAllBugs();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportBugReports(String format) {
        logger.info("导出Bug报告数据: format={}", format);

        List<BugReport> allBugs = bugReportRepository.findAll();

        if ("json".equalsIgnoreCase(format)) {
            // 简化的JSON导出实现
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < allBugs.size(); i++) {
                BugReport bug = allBugs.get(i);
                json.append("{")
                    .append("\"id\":").append(bug.getId()).append(",")
                    .append("\"bugType\":\"").append(bug.getBugType()).append("\",")
                    .append("\"targetDatabase\":\"").append(bug.getTargetDatabase()).append("\",")
                    .append("\"oracleType\":\"").append(bug.getOracleType()).append("\",")
                    .append("\"testTime\":\"").append(bug.getTestTime()).append("\",")
                    .append("\"createdAt\":\"").append(bug.getCreatedAt()).append("\"")
                    .append("}");
                if (i < allBugs.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }

        throw new IllegalArgumentException("不支持的导出格式: " + format);
    }

    /**
     * 将BugReport实体转换为BugReportDto
     */
    private BugReportDto convertToDto(BugReport bugReport) {
        BugReportDto dto = new BugReportDto();
        dto.setId(bugReport.getId());
        dto.setBugType(bugReport.getBugType());
        dto.setTargetDatabase(bugReport.getTargetDatabase());
        dto.setOracleType(bugReport.getOracleType());
        dto.setTestTime(bugReport.getTestTime());
        dto.setTestCase(bugReport.getTestCase());
        dto.setErrorMessage(bugReport.getErrorMessage());
        dto.setFormattedParameterSettings(bugReport.getFormattedParameterSettings());
        dto.setParameterSettings(bugReport.getParameterSettings());
        dto.setCreatedAt(bugReport.getCreatedAt());
        dto.setUpdatedAt(bugReport.getUpdatedAt());

        return dto;
    }
}
