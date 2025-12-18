package com.fuzz.controller;

import com.fuzz.entity.DbParameter;
import com.fuzz.repository.DbParameterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库参数控制器
 */
@RestController
@RequestMapping("/")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class DbParameterController {

    private static final Logger logger = LoggerFactory.getLogger(DbParameterController.class);

    @Autowired
    private DbParameterRepository dbParameterRepository;

    /**
     * 获取所有数据库参数
     */
    @GetMapping("/db-parameters")
    public ResponseEntity<List<DbParameter>> getAllDbParameters() {
        logger.info("获取所有数据库参数");
        List<DbParameter> dbParameters = dbParameterRepository.findAll();
        return ResponseEntity.ok(dbParameters);
    }

    /**
     * 获取当前测试数据库参数
     */
    @GetMapping("/current-test-database")
    public ResponseEntity<List<DbParameter>> getCurrentTestDatabaseParameters() {
        logger.info("获取当前测试数据库参数");
        List<DbParameter> testParameters = dbParameterRepository.findByIsTest(true);
        return ResponseEntity.ok(testParameters);
    }

    /**
     * 根据数据库类型获取参数
     */
    @GetMapping("/db-parameters/by-type/{dbType}")
    public ResponseEntity<List<DbParameter>> getDbParametersByType(@PathVariable String dbType) {
        logger.info("根据数据库类型获取参数: {}", dbType);
        List<DbParameter> dbParameters = dbParameterRepository.findByDbType(dbType);
        return ResponseEntity.ok(dbParameters);
    }
}