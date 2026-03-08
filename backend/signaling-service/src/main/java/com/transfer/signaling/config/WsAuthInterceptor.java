package com.transfer.signaling.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Component
public class WsAuthInterceptor implements HandshakeInterceptor {

    private final String expectedApiKey;

    public WsAuthInterceptor(@Value("${app.security.ws-api-key:}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (expectedApiKey == null || expectedApiKey.isBlank()) {
            return true;
        }

        String providedApiKey = request.getHeaders().getFirst("X-WS-API-KEY");
        if (providedApiKey == null || providedApiKey.isBlank()) {
            providedApiKey = queryParam(request.getURI(), "apiKey");
        }

        boolean authorized = Objects.equals(expectedApiKey, providedApiKey);
        if (!authorized) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
        }
        return authorized;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }

    private String queryParam(URI uri, String key) {
        String query = uri.getRawQuery();
        if (query == null || query.isBlank()) {
            return null;
        }
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && key.equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }
}
