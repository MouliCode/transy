package com.transfer.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import com.transfer.config.model.ConfigMessage;
import com.transfer.config.service.RuntimeConfigService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ConfigWebSocketHandler extends TextWebSocketHandler {

    private final RuntimeConfigService runtimeConfigService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Logger logger;

    public ConfigWebSocketHandler(RuntimeConfigService runtimeConfigService) {
        this.runtimeConfigService = runtimeConfigService;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ConfigMessage input = objectMapper.readValue(message.getPayload(), ConfigMessage.class);
        if (input.getType() == null) {
            return;
        }

        switch (input.getType()) {
            case "GET_ALL" -> {
                ConfigMessage out = ConfigMessage.ofType("CONFIGS");
                out.setConfigs(runtimeConfigService.getAll());
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(out)));
            }
            case "GET" -> {
                ConfigMessage out = ConfigMessage.ofType("CONFIG");
                out.setKey(input.getKey());
                out.setValue(runtimeConfigService.get(input.getKey()));
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(out)));
            }
            case "SET" -> {
                runtimeConfigService.put(input.getKey(), input.getValue());
                ConfigMessage out = ConfigMessage.ofType("CONFIG_UPDATED");
                out.setKey(input.getKey());
                out.setValue(input.getValue());
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(out)));
                logger.info("Updated runtime config key: " + input.getKey());
            }
            default -> {
                ConfigMessage out = ConfigMessage.ofType("ERROR");
                out.setValue("Unsupported config message type");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(out)));
                logger.warning("Unsupported config message type: " + input.getType());
            }
        }
    }
}
