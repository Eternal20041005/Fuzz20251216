package com.fuzz.service;

import com.fuzz.dto.PagedResponse;
import com.fuzz.dto.TestCaseDto;

/**
 * 测试用例服务接口
 */
public interface TestCaseService {

    /**
     * 分页获取测试用例列表，按测试时间倒序排列
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页的测试用例列表
     */
    PagedResponse<TestCaseDto> getTestCases(int page, int size);

    /**
     * 按条件分页筛选测试用例，按测试时间倒序排列
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param triggeredBug 是否触发漏洞（null表示全部）
     * @param param1 参数1的名称或值（可选）
     * @param param2 参数2的名称或值（可选）
     * @return 分页的测试用例列表
     */
    PagedResponse<TestCaseDto> getTestCasesByFilter(int page, int size, Boolean triggeredBug, String param1, String param2);
}
