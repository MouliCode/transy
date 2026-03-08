package com.transfer.device.config;

import com.transfer.device.websocket.DeviceWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DeviceWebSocketHandler deviceWebSocketHandler;
    private final WsAuthInterceptor wsAuthInterceptor;

    public WebSocketConfig(DeviceWebSocketHandler deviceWebSocketHandler, WsAuthInterceptor wsAuthInterceptor) {
        this.deviceWebSocketHandler = deviceWebSocketHandler;
        this.wsAuthInterceptor = wsAuthInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deviceWebSocketHandler, "/devices/ws")
                .addInterceptors(wsAuthInterceptor)
                .setAllowedOrigins("*");
    }
}
