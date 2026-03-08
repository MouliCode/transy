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
    private final WsAuthInterceptor wsAuthInterceptor;

    public WebSocketConfig(GatewayWebSocketHandler gatewayWebSocketHandler, WsAuthInterceptor wsAuthInterceptor) {
        this.gatewayWebSocketHandler = gatewayWebSocketHandler;
        this.wsAuthInterceptor = wsAuthInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gatewayWebSocketHandler, "/gateway/ws")
                .addInterceptors(wsAuthInterceptor)
                .setAllowedOrigins("*");
    }
}
