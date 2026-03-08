package com.transfer.discovery.config;

import com.transfer.discovery.websocket.DiscoveryWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DiscoveryWebSocketHandler discoveryWebSocketHandler;

    public WebSocketConfig(DiscoveryWebSocketHandler discoveryWebSocketHandler) {
        this.discoveryWebSocketHandler = discoveryWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(discoveryWebSocketHandler, "/discovery/ws")
                .setAllowedOrigins("*");
    }
}
