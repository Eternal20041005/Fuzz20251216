package com.fuzz.controller;

import com.fuzz.dto.PagedResponse;
import com.fuzz.dto.TestCaseDto;
import com.fuzz.service.TestCaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用例控制器
 */
@RestController
@RequestMapping("/test-cases")
public class TestCaseController {

    private static final Logger logger = LoggerFactory.getLogger(TestCaseController.class);

    @Autowired
    private TestCaseService testCaseService;

    /**
     * 分页获取测试用例列表，支持筛选
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param triggeredBug 是否触发漏洞（可选，值为true/false，不提供则表示全部）
     * @param param1 参数1的名称或值（可选）
     * @param param2 参数2的名称或值（可选）
     * @return 分页的测试用例列表
     */
    @GetMapping
    public ResponseEntity<PagedResponse<TestCaseDto>> getTestCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean triggeredBug,
            @RequestParam(required = false) String param1,
            @RequestParam(required = false) String param2) {

        logger.info("分页获取测试用例列表: page={}, size={}, triggeredBug={}, param1={}, param2={}", page, size, triggeredBug, param1, param2);

        PagedResponse<TestCaseDto> response = testCaseService.getTestCasesByFilter(page, size, triggeredBug, param1, param2);
        return ResponseEntity.ok(response);
    }
}
