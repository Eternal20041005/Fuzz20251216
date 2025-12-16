package com.fuzz.service.impl;

import com.fuzz.dto.PagedResponse;
import com.fuzz.dto.TestCaseDto;
import com.fuzz.entity.TestCase;
import com.fuzz.repository.TestCaseRepository;
import com.fuzz.service.TestCaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试用例服务实现类
 */
@Service
@Transactional
public class TestCaseServiceImpl implements TestCaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TestCaseDto> getTestCases(int page, int size) {
        logger.info("获取测试用例列表: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<TestCase> testCasePage = testCaseRepository.findAllByOrderByTestTimeDesc(pageable);

        return convertToPagedResponse(testCasePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TestCaseDto> getTestCasesByFilter(int page, int size, Boolean triggeredBug, String param1, String param2) {
        logger.info("筛选测试用例: page={}, size={}, triggeredBug={}, param1={}, param2={}", page, size, triggeredBug, param1, param2);

        Pageable pageable = PageRequest.of(page, size);
        Page<TestCase> testCasePage;

        boolean hasParam1 = param1 != null && !param1.isEmpty();
        boolean hasParam2 = param2 != null && !param2.isEmpty();

        if (triggeredBug == null && !hasParam1 && !hasParam2) {
            // 没有筛选条件，返回所有
            testCasePage = testCaseRepository.findAllByOrderByTestTimeDesc(pageable);
        } else if (triggeredBug == null && hasParam1 && !hasParam2) {
            // 只有param1筛选
            testCasePage = testCaseRepository.findByParamComboContainingOrderByTestTimeDesc(param1, pageable);
        } else if (triggeredBug == null && !hasParam1 && hasParam2) {
            // 只有param2筛选
            testCasePage = testCaseRepository.findByParamComboContainingOrderByTestTimeDesc(param2, pageable);
        } else if (triggeredBug == null && hasParam1 && hasParam2) {
            // 同时有param1和param2筛选
            testCasePage = testCaseRepository.findByParamComboContainingAndParamComboContainingOrderByTestTimeDesc(param1, param2, pageable);
        } else if (triggeredBug != null && !hasParam1 && !hasParam2) {
            // 只有是否触发漏洞筛选
            testCasePage = testCaseRepository.findByTriggeredBugOrderByTestTimeDesc(triggeredBug, pageable);
        } else if (triggeredBug != null && hasParam1 && !hasParam2) {
            // 有触发漏洞和param1筛选
            testCasePage = testCaseRepository.findByTriggeredBugAndParamComboContainingOrderByTestTimeDesc(triggeredBug, param1, pageable);
        } else if (triggeredBug != null && !hasParam1 && hasParam2) {
            // 有触发漏洞和param2筛选
            testCasePage = testCaseRepository.findByTriggeredBugAndParamComboContainingOrderByTestTimeDesc(triggeredBug, param2, pageable);
        } else {
            // 所有筛选条件都有
            testCasePage = testCaseRepository.findByTriggeredBugAndParamComboContainingAndParamComboContainingOrderByTestTimeDesc(triggeredBug, param1, param2, pageable);
        }

        return convertToPagedResponse(testCasePage);
    }

    /**
     * 将Page<TestCase>转换为PagedResponse<TestCaseDto>
     */
    private PagedResponse<TestCaseDto> convertToPagedResponse(Page<TestCase> testCasePage) {
        List<TestCaseDto> testCaseDtos = testCasePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        PagedResponse<TestCaseDto> response = new PagedResponse<>();
        response.setContent(testCaseDtos);
        response.setTotalElements(testCasePage.getTotalElements());
        response.setTotalPages(testCasePage.getTotalPages());
        response.setCurrentPage(testCasePage.getNumber());
        response.setPageSize(testCasePage.getSize());
        response.setHasNext(testCasePage.hasNext());
        response.setHasPrevious(testCasePage.hasPrevious());
        response.setFirst(testCasePage.isFirst());
        response.setLast(testCasePage.isLast());

        return response;
    }

    /**
     * 将TestCase实体转换为TestCaseDto
     */
    private TestCaseDto convertToDto(TestCase testCase) {
        return new TestCaseDto(
                testCase.getCaseId(),
                testCase.getTestTime(),
                testCase.getTargetDb(),
                testCase.getOracleType(),
                testCase.isTriggeredBug(),
                testCase.getBugId(),
                testCase.getParamCombo(),
                testCase.getWeightValue(),
                testCase.getSqlStatement()
        );
    }
}
