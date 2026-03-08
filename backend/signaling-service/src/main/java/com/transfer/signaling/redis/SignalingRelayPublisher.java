package com.transfer.signaling.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SignalingRelayPublisher {

    private final StringRedisTemplate redisTemplate;
    private final String relayChannel;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public SignalingRelayPublisher(StringRedisTemplate redisTemplate,
                                   @Value("${app.redis.relay-channel:signaling:relay}") String relayChannel) {
        this.redisTemplate = redisTemplate;
        this.relayChannel = relayChannel;
    }

    public void publish(String toDeviceId, String payload) {
        try {
            RelayMessage relayMessage = new RelayMessage(toDeviceId, payload);
            redisTemplate.convertAndSend(relayChannel, objectMapper.writeValueAsString(relayMessage));
        } catch (Exception ignored) {
            // best-effort cross-instance relay
        }
    }
}
