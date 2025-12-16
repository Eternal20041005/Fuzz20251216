package com.fuzz.service;

import com.fuzz.dto.PagedResponse;
import com.fuzz.dto.ParameterTemplateDto;
import com.fuzz.entity.ParameterTemplate;
import com.fuzz.entity.ParameterType;
import com.fuzz.repository.ParameterTemplateRepository;
import com.fuzz.service.impl.ParameterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParameterServiceTest {

    @Mock
    private ParameterTemplateRepository parameterTemplateRepository;

    @InjectMocks
    private ParameterServiceImpl parameterService;

    private ParameterTemplate testParameter;

    @BeforeEach
    void setUp() {
        testParameter = new ParameterTemplate();
        testParameter.setId(1L);
        testParameter.setParamName("max_connections");
        testParameter.setDescription("最大连接数");
        testParameter.setCategory("CONNECTION");
        testParameter.setDefaultValue("151");
        testParameter.setParamType(ParameterType.INTEGER);
        testParameter.setIsTestDefault(true);
        testParameter.setCreateTime(LocalDateTime.now());
        testParameter.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testGetParameters() {
        // 准备测试数据
        List<ParameterTemplate> parameters = Arrays.asList(testParameter);
        Page<ParameterTemplate> page = new PageImpl<>(parameters, PageRequest.of(0, 20), 1);

        // 模拟仓库调用
        when(parameterTemplateRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // 执行测试
        PagedResponse<ParameterTemplateDto> result = parameterService.getParameters(0, 20, null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("max_connections", result.getContent().get(0).getParamName());
    }

    @Test
    void testGetAllCategories() {
        // 准备测试数据
        List<String> categories = Arrays.asList("CONNECTION", "MEMORY", "CACHE");

        // 模拟仓库调用
        when(parameterTemplateRepository.findAllCategories()).thenReturn(categories);

        // 执行测试
        List<String> result = parameterService.getAllCategories();

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("CONNECTION"));
        assertTrue(result.contains("MEMORY"));
        assertTrue(result.contains("CACHE"));
    }
}