package com.transfer.config.config;

import com.transfer.config.websocket.ConfigWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ConfigWebSocketHandler configWebSocketHandler;
    private final WsAuthInterceptor wsAuthInterceptor;

    public WebSocketConfig(ConfigWebSocketHandler configWebSocketHandler, WsAuthInterceptor wsAuthInterceptor) {
        this.configWebSocketHandler = configWebSocketHandler;
        this.wsAuthInterceptor = wsAuthInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(configWebSocketHandler, "/config/ws")
                .addInterceptors(wsAuthInterceptor)
                .setAllowedOrigins("*");
    }
}
