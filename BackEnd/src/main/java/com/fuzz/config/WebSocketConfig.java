package com.fuzz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 启用WebSocket消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 配置消息代理：前端订阅 /topic 开头的地址接收推送
        config.enableSimpleBroker("/topic");
        // 前端发送消息的前缀（你的场景用不到，仅配置）
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点：前端通过 /api/ws-test 连接（匹配你的context-path）
        registry.addEndpoint("/ws-test")
                .setAllowedOriginPatterns("*") // 允许跨域
                .withSockJS(); // 降级支持（兼容不支持WebSocket的浏览器）
    }
}