package com.fuzz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    // 配置CORS跨域过滤器
    @Bean
    public CorsFilter corsFilter() {
        // 1. 配置跨域信息
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端的域名（这里允许所有域名，开发环境用完全没问题）
        config.addAllowedOriginPattern("*");
        // 允许前端发送Cookie（如果需要）
        config.setAllowCredentials(true);
        // 允许所有请求方法（GET/POST/PUT/DELETE等）
        config.addAllowedMethod("*");
        // 允许所有请求头（比如Token、Content-Type等）
        config.addAllowedHeader("*");
        // 允许暴露的响应头（前端能获取的响应头）
        config.addExposedHeader("*");
        // 跨域请求有效期（3600秒，避免频繁预检请求）
        config.setMaxAge(3600L);

        // 2. 配置哪些接口需要跨域支持（这里配置所有接口）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // "/**" 表示所有接口

        // 3. 返回CORS过滤器
        return new CorsFilter(source);
    }
}