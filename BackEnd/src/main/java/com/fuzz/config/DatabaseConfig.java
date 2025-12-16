package com.fuzz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库配置类
 */
// @Configuration
// @EnableJpaRepositories(basePackages = "com.fuzz.repository")
// @EnableTransactionManagement
public class DatabaseConfig {
    // JPA配置已在application.yml中定义
}