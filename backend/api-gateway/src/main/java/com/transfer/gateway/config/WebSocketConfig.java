package com.transfer.gateway.config;

import com.transfer.gateway.websocket.GatewayWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GatewayWebSocketHandler gatewayWebSocketHandler;

    public WebSocketConfig(GatewayWebSocketHandler gatewayWebSocketHandler) {
        this.gatewayWebSocketHandler = gatewayWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gatewayWebSocketHandler, "/gateway/ws")
                .setAllowedOrigins("*");
    }
}
