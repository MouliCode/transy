package com.transfer.transfer.config;

import com.transfer.transfer.websocket.TransferWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TransferWebSocketHandler transferWebSocketHandler;

    public WebSocketConfig(TransferWebSocketHandler transferWebSocketHandler) {
        this.transferWebSocketHandler = transferWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(transferWebSocketHandler, "/transfers/ws")
                .setAllowedOrigins("*");
    }
}
