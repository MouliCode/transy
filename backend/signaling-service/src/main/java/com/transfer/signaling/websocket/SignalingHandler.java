package com.transfer.signaling.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.signaling.model.SignalMessage;
import com.transfer.signaling.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final SessionService sessionService;
    private final ObjectMapper mapper = new ObjectMapper();

    public SignalingHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SignalMessage signal = mapper.readValue(message.getPayload(), SignalMessage.class);

        if ("REGISTER".equals(signal.getType()) && signal.getFrom() != null) {
            sessionService.register(signal.getFrom(), session);
            return;
        }

        if (signal.getTo() != null) {
            WebSocketSession targetSession = sessionService.getSession(signal.getTo());
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionService.removeBySessionId(session.getId());
        System.out.println("Client disconnected: " + session.getId());
    }
}
