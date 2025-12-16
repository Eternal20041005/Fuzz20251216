package com.fuzz.service; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate; // 新增：WebSocket推送工具
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TestStatusService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate; // 新增：注入WebSocket推送工具

    private boolean isTesting = false;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private int runTime = 0;

    /**
     * 开始测试（原有逻辑 + 新增WebSocket推送）
     */
    public Map<String, String> startTest() {
        Map<String, String> result = new HashMap<>();

        if (isTesting) {
            result.put("code", "500");
            result.put("message", "测试已在运行中！");
            return result;
        }

        try {
            isTesting = true;
            runTime = 0;

            // 清空旧数据（你的原有逻辑）
            jdbcTemplate.update("TRUNCATE TABLE test_status");

            // 初始化测试状态（你的原有逻辑）
            jdbcTemplate.update(
                    "INSERT INTO test_status (task_status, test_oracle, run_time, coverage_rate, bug_count, execution_count, current_param_combo, throughput) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    "测试中", "TLP", 0, 0.00, 0, 0, "无", 0.00
            );

            // 异步更新测试数据（你的原有逻辑 + 新增推送）
            executor.submit(() -> {
                while (isTesting) {
                    try {
                        Thread.sleep(1000);
                        runTime++;

                        // 模拟测试数据（你的原有逻辑）
                        String paramCombo = runTime >= 5 ? "foreign_key_checks=OFF;bulk_insert_buffer_size=100" : "无";
                        double coverage = runTime * 0.1;
                        int bugCount = runTime >= 10 ? 2 : 0;
                        int executionCount = runTime * 50;

                        // 更新数据库（你的原有逻辑）
                        jdbcTemplate.update(
                                "UPDATE test_status SET run_time=?, coverage_rate=?, bug_count=?, execution_count=?, current_param_combo=? " +
                                "WHERE task_status='测试中'",
                                runTime, coverage, bugCount, executionCount, paramCombo
                        );

                        // 新增：每次更新后，通过WebSocket推送最新状态到前端
                        Map<String, Object> latestStatus = getLatestStatus();
                        messagingTemplate.convertAndSend("/topic/testStatus", latestStatus);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        isTesting = false;
                    }
                }
            });

            result.put("code", "200");
            result.put("message", "测试启动成功！");
            return result;

        } catch (Exception e) {
            isTesting = false;
            result.put("code", "500");
            result.put("message", "测试启动失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 结束测试（原有逻辑 + 新增推送结束状态）
     */
    public Map<String, String> stopTest() {
        Map<String, String> result = new HashMap<>();

        if (!isTesting) {
            result.put("code", "500");
            result.put("message", "当前无运行中的测试！");
            return result;
        }

        try {
            isTesting = false;
            jdbcTemplate.update(
                    "UPDATE test_status SET task_status=? WHERE task_status=?",
                    "已结束", "测试中"
            );

            // 新增：推送结束后的状态到前端
            Map<String, Object> latestStatus = getLatestStatus();
            messagingTemplate.convertAndSend("/topic/testStatus", latestStatus);

            result.put("code", "200");
            result.put("message", "测试已终止！");
            return result;

        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", "终止测试失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 获取最新状态（你的原有逻辑，无改动）
     */
    public Map<String, Object> getLatestStatus() {
        try {
            return jdbcTemplate.queryForMap(
                   "SELECT task_status, run_time, current_param_combo, coverage_rate, bug_count, execution_count FROM test_status ORDER BY created_at DESC LIMIT 1"
            );
        } catch (Exception e) {
            Map<String, Object> defaultStatus = new HashMap<>();
            defaultStatus.put("run_time", 0);
            defaultStatus.put("coverage_rate", 0.00);
            defaultStatus.put("bug_count", 0);
            defaultStatus.put("execution_count", 0);
            defaultStatus.put("current_param_combo", "无");
            defaultStatus.put("task_status", "未开始");
            return defaultStatus;
        }
    }

    /**
     * 检查测试是否在运行（你的原有逻辑，无改动）
     */
    public boolean isTesting() {
        return isTesting;
    }
}