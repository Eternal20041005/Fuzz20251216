package com.fuzz.controller;

import com.fuzz.entity.ParamComboWeight;
import com.fuzz.entity.TestStatus;
import com.fuzz.repository.ParamComboWeightRepository;
import com.fuzz.repository.TestStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;

/**
 * 状态监测控制器（修复所有类型错误）
 */
@RestController
@RequestMapping("/status-monitor") // 去掉/api前缀，匹配Tomcat路径
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StatusMonitorController {

    @Autowired
    private TestStatusRepository testStatusRepo;
    
    @Autowired
    private ParamComboWeightRepository paramWeightRepo;

    // 获取最新测试状态（用你Repository里有的方法）
    @GetMapping("/test-status")
    public ResponseEntity<TestStatus> getLatestTestStatus() {
        // 用findTopByOrderByCreatedAtDesc()，你的Repository里有这个方法
        TestStatus latestStatus = testStatusRepo.findTopByOrderByCreatedAtDesc();
        return ResponseEntity.ok(latestStatus);
    }

    // 获取参数组合权重列表
    @GetMapping("/param-weights")
    public ResponseEntity<List<ParamComboWeight>> getParamWeights() {
        List<ParamComboWeight> weightList = paramWeightRepo.findAll();
        return ResponseEntity.ok(weightList);
    }

    // 保存参数权重（关键：id用Long类型，匹配Repository）
    @PostMapping("/save-weight/{id}")
    public ResponseEntity<ParamComboWeight> saveParamWeight(
            @PathVariable Long id,  // 这里必须是Long，和Repository一致
            @RequestParam BigDecimal weightValue) {
        // findById接收Long类型，不再报错
        ParamComboWeight param = paramWeightRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("参数ID:" + id + "不存在！"));
        param.setWeightValue(weightValue);
        ParamComboWeight updatedParam = paramWeightRepo.save(param);
        return ResponseEntity.ok(updatedParam);
    }
}