package com.transfer.gateway.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import com.transfer.gateway.model.GatewayMessage;
import com.transfer.gateway.service.GatewayRegistryService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GatewayWebSocketHandler extends TextWebSocketHandler {

    private final GatewayRegistryService gatewayRegistryService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Logger logger;

    public GatewayWebSocketHandler(GatewayRegistryService gatewayRegistryService) {
        this.gatewayRegistryService = gatewayRegistryService;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        GatewayMessage input = objectMapper.readValue(message.getPayload(), GatewayMessage.class);
        if (input.getType() == null) {
            return;
        }

        switch (input.getType()) {
            case "PING" -> {
                GatewayMessage pong = GatewayMessage.ofType("PONG");
                pong.setMessage("gateway-alive");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pong)));
                logger.info("Handled gateway ping from session: " + session.getId());
            }
            case "LIST_ROUTES" -> {
                GatewayMessage routes = GatewayMessage.ofType("ROUTES");
                routes.setRoutes(gatewayRegistryService.getRoutes());
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(routes)));
            }
            default -> {
                GatewayMessage unsupported = GatewayMessage.ofType("ERROR");
                unsupported.setMessage("Unsupported gateway message type");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(unsupported)));
                logger.warning("Unsupported gateway message type: " + input.getType());
            }
        }
    }
}
