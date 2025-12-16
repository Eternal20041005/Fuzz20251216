package com.fuzz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单的健康检查控制器（不依赖数据库）
 */
@RestController
@RequestMapping("/simple")
public class SimpleHealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "fuzz-backend");
        response.put("version", "1.0.0");
        response.put("message", "后端服务正常运行");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API测试成功");
        response.put("timestamp", LocalDateTime.now());
        response.put("data", "这是一个测试响应");
        return ResponseEntity.ok(response);
    }
}