package com.transfer.discovery.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.discovery.model.DiscoveryMessage;
import com.transfer.discovery.service.DiscoveryRegistryService;
import com.transfer.discovery.service.DiscoverySessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Component
public class DiscoveryWebSocketHandler extends TextWebSocketHandler {

    private final DiscoveryRegistryService discoveryRegistryService;
    private final DiscoverySessionService discoverySessionService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public DiscoveryWebSocketHandler(DiscoveryRegistryService discoveryRegistryService,
                                     DiscoverySessionService discoverySessionService) {
        this.discoveryRegistryService = discoveryRegistryService;
        this.discoverySessionService = discoverySessionService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        discoverySessionService.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        DiscoveryMessage input = objectMapper.readValue(message.getPayload(), DiscoveryMessage.class);
        if (input.getType() == null) {
            return;
        }

        switch (input.getType()) {
            case "REGISTER_SERVICE" -> {
                if (input.getServiceName() != null && input.getHost() != null && input.getPort() != null) {
                    discoveryRegistryService.register(input.getServiceName(), input.getHost(), input.getPort(), session.getId());
                    broadcastServiceList();
                }
            }
            case "HEARTBEAT_SERVICE" -> {
                if (input.getServiceName() != null) {
                    discoveryRegistryService.heartbeat(input.getServiceName());
                }
            }
            case "UNREGISTER_SERVICE" -> {
                if (input.getServiceName() != null) {
                    discoveryRegistryService.unregister(input.getServiceName());
                    broadcastServiceList();
                }
            }
            case "LIST_SERVICES" -> sendServiceList(session);
            default -> {
                DiscoveryMessage error = DiscoveryMessage.ofType("ERROR");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        discoverySessionService.remove(session.getId());
        String removed = discoveryRegistryService.unregisterBySession(session.getId());
        if (removed != null) {
            broadcastServiceList();
        }
        super.afterConnectionClosed(session, status);
    }

    private void sendServiceList(WebSocketSession session) throws Exception {
        DiscoveryMessage list = DiscoveryMessage.ofType("SERVICE_LIST");
        list.setServices(List.copyOf(discoveryRegistryService.list()));
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(list)));
    }

    private void broadcastServiceList() throws Exception {
        DiscoveryMessage list = DiscoveryMessage.ofType("SERVICE_LIST");
        list.setServices(List.copyOf(discoveryRegistryService.list()));
        String payload = objectMapper.writeValueAsString(list);

        for (WebSocketSession wsSession : discoverySessionService.all().values()) {
            if (wsSession.isOpen()) {
                wsSession.sendMessage(new TextMessage(payload));
            }
        }
    }
}
