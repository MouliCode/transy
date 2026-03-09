package com.transfer.signaling.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import com.transfer.signaling.model.SignalMessage;
import com.transfer.signaling.redis.SignalingRelayPublisher;
import com.transfer.signaling.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final SessionService sessionService;
    private final SignalingRelayPublisher signalingRelayPublisher;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger;

    public SignalingHandler(SessionService sessionService, SignalingRelayPublisher signalingRelayPublisher) {
        this.sessionService = sessionService;
        this.signalingRelayPublisher = signalingRelayPublisher;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            SignalMessage signal = mapper.readValue(message.getPayload(), SignalMessage.class);

            if ("REGISTER".equals(signal.getType()) && signal.getFrom() != null) {
                sessionService.register(signal.getFrom(), session);
                logger.info("Registered signaling device: " + signal.getFrom());
                return;
            }

            if (signal.getTo() != null) {
                WebSocketSession targetSession = sessionService.getSession(signal.getTo());
                if (targetSession != null && targetSession.isOpen()) {
                    targetSession.sendMessage(message);
                    return;
                }

                String ownerInstanceId = sessionService.getOwnerInstanceId(signal.getTo());
                if (ownerInstanceId != null && !sessionService.isCurrentInstance(ownerInstanceId)) {
                    signalingRelayPublisher.publish(signal.getTo(), message.getPayload());
                } else {
                    logger.warning("Target signaling device unavailable: " + signal.getTo());
                }
            }
        } catch (Exception exception) {
            logger.error("Failed to process signaling message: " + exception.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionService.removeBySessionId(session.getId());
        logger.info("Client disconnected: " + session.getId());
    }
}
