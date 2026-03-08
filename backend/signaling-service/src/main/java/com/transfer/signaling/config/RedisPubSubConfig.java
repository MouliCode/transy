package com.transfer.signaling.config;

import com.transfer.signaling.redis.SignalingRelaySubscriber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@ConditionalOnProperty(name = "app.redis.pubsub-enabled", havingValue = "true")
public class RedisPubSubConfig {

    @Bean
    public ChannelTopic relayTopic(org.springframework.core.env.Environment environment) {
        return new ChannelTopic(environment.getProperty("app.redis.relay-channel", "signaling:relay"));
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            SignalingRelaySubscriber signalingRelaySubscriber,
            ChannelTopic relayTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(signalingRelaySubscriber, relayTopic);
        return container;
    }
}
