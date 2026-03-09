package com.transfer.signaling.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import com.transfer.signaling.service.SessionService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SignalingRelaySubscriber implements MessageListener {

    private final SessionService sessionService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Logger logger;

    public SignalingRelaySubscriber(SessionService sessionService) {
        this.sessionService = sessionService;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String raw = new String(message.getBody());
            RelayMessage relayMessage = objectMapper.readValue(raw, RelayMessage.class);
            WebSocketSession target = sessionService.getSession(relayMessage.getToDeviceId());
            if (target != null && target.isOpen()) {
                target.sendMessage(new TextMessage(relayMessage.getPayload()));
            }
        } catch (Exception exception) {
            logger.warning("Best-effort relay consume failed: " + exception.getMessage());
        }
    }
}
