package com.transfer.signaling.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SignalingRelayPublisher {

    private final StringRedisTemplate redisTemplate;
    private final String relayChannel;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Logger logger;

    public SignalingRelayPublisher(StringRedisTemplate redisTemplate,
                                   @Value("${app.redis.relay-channel:signaling:relay}") String relayChannel) {
        this.redisTemplate = redisTemplate;
        this.relayChannel = relayChannel;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    public void publish(String toDeviceId, String payload) {
        try {
            RelayMessage relayMessage = new RelayMessage(toDeviceId, payload);
            redisTemplate.convertAndSend(relayChannel, objectMapper.writeValueAsString(relayMessage));
        } catch (Exception exception) {
            logger.warning("Best-effort relay publish failed: " + exception.getMessage());
        }
    }
}
