package com.fuzz.controller;

import com.fuzz.entity.TestStatus;
import com.fuzz.repository.TestStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试状态控制器
 */
@RestController
@RequestMapping("/test-status")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TestStatusController {

    private static final Logger logger = LoggerFactory.getLogger(TestStatusController.class);

    @Autowired
    private TestStatusRepository testStatusRepository;

    /**
     * 获取所有测试状态
     */
    @GetMapping
    public ResponseEntity<List<TestStatus>> getAllTestStatus() {
        logger.info("获取所有测试状态");
        List<TestStatus> testStatusList = testStatusRepository.findAll();
        return ResponseEntity.ok(testStatusList);
    }

    /**
     * 获取最新的测试状态（按更新时间排序）
     */
    @GetMapping("/latest")
    public ResponseEntity<TestStatus> getLatestTestStatus() {
        logger.info("获取最新的测试状态");
        // 按updatedAt降序排序，确保获取最近修改的记录
        TestStatus latestTestStatus = testStatusRepository.findTopByOrderByUpdatedAtDesc();
        if (latestTestStatus != null) {
            return ResponseEntity.ok(latestTestStatus);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * 根据任务状态获取测试状态
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<TestStatus>> getTestStatusByStatus(@PathVariable String status) {
        logger.info("根据任务状态获取测试状态: {}", status);
        List<TestStatus> testStatusList = testStatusRepository.findByTaskStatus(status);
        return ResponseEntity.ok(testStatusList);
    }

    /**
     * 根据测试Oracle获取测试状态
     */
    @GetMapping("/by-oracle/{oracle}")
    public ResponseEntity<List<TestStatus>> getTestStatusByOracle(@PathVariable String oracle) {
        logger.info("根据测试Oracle获取测试状态: {}", oracle);
        List<TestStatus> testStatusList = testStatusRepository.findByTestOracle(oracle);
        return ResponseEntity.ok(testStatusList);
    }
}