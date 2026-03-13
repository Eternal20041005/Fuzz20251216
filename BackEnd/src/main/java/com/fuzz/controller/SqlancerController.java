package com.fuzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fuzz.SqlancerRunner;
import java.util.Map;

@RestController
@RequestMapping("/sqlancer")
public class SqlancerController {

    private final SqlancerRunner sqlancerRunner;

    // 使用构造函数注入 SqlancerRunner 服务
    @Autowired
    public SqlancerController(SqlancerRunner sqlancerRunner) {
        this.sqlancerRunner = sqlancerRunner;
    }

    /**
     * 启动模糊测试
     * @param dbName 要测试的数据库，例如 "mysql"
     * @return 响应消息
     */
    @PostMapping("/start/{dbName}")
    public ResponseEntity<String> startFuzzing(@PathVariable String dbName) {
        // 为MySQL添加默认的oracle参数，与TestMySQLFuzzing保持一致
        if ("mysql".equalsIgnoreCase(dbName)) {
            sqlancerRunner.startFuzzing("mysql", "--oracle", "TLP_WHERE");
        } else {
            sqlancerRunner.startFuzzing(dbName);
        }
        return ResponseEntity.ok("SQLancer fuzzing started for " + dbName);
    }

    /**
     * 停止模糊测试
     * @return 响应消息
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopFuzzing() {
        sqlancerRunner.stopFuzzing();
        return ResponseEntity.ok("SQLancer fuzzing stopped.");
    }

    /**
     * 获取测试状态
     * @return 包含状态信息的 Map
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = sqlancerRunner.getTestStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * 获取参数权重信息
     * @return 包含参数权重信息的 Map
     */
    @GetMapping("/param-weights")
    public ResponseEntity<Map<String[], Double>> getParamWeights() {
        Map<String[], Double> weights = sqlancerRunner.getParamWeight();
        return ResponseEntity.ok(weights);
    }
}