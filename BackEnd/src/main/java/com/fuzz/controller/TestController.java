package com.fuzz.controller;

import com.fuzz.service.TestStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test") // 适配context-path: /api
@CrossOrigin // 允许前端跨域
public class TestController {
    @Autowired
    private TestStatusService testStatusService;

    // 开始测试接口（前端点击按钮调用）
    @PostMapping("/start")
    public Map<String, String> startTest() {
        return testStatusService.startTest();
    }

    // 结束测试接口（前端点击按钮调用）
    @PostMapping("/stop")
    public Map<String, String> stopTest() {
        return testStatusService.stopTest();
    }

    // 最新状态接口（前端轮询调用）
    @GetMapping("/latest-status")
    public Map<String, Object> getLatestStatus() {
        return testStatusService.getLatestStatus();
    }

    // 检查测试是否运行接口
    @GetMapping("/is-testing")
    public Map<String, Boolean> isTesting() {
        return Map.of("isTesting", testStatusService.isTesting());
    }
}