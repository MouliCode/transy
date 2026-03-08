package com.transfer.signaling.config;

import com.transfer.signaling.websocket.SignalingHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SignalingHandler signalingHandler;
    private final WsAuthInterceptor wsAuthInterceptor;

    public WebSocketConfig(SignalingHandler signalingHandler, WsAuthInterceptor wsAuthInterceptor) {
        this.signalingHandler = signalingHandler;
        this.wsAuthInterceptor = wsAuthInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingHandler, "/signal")
                .addInterceptors(wsAuthInterceptor)
                .setAllowedOrigins("*");
    }
}
