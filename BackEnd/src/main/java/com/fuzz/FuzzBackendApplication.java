package com.fuzz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
/**
 * 参数敏感数据库模糊测试平台后端应用启动类
 */
@SpringBootApplication
@ComponentScan({"com.fuzz"}) // 新增这行！强制扫描com.fuzz下所有包（包括controller
public class FuzzBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuzzBackendApplication.class, args);
    }
}