package com.transfer.device.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import com.transfer.device.model.Device;
import com.transfer.device.model.DeviceWsMessage;
import com.transfer.device.model.RegisterDeviceRequest;
import com.transfer.device.service.DeviceService;
import com.transfer.device.service.DeviceSessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Component
public class DeviceWebSocketHandler extends TextWebSocketHandler {

    private final DeviceService deviceService;
    private final DeviceSessionService deviceSessionService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Logger logger;

    public DeviceWebSocketHandler(DeviceService deviceService, DeviceSessionService deviceSessionService) {
        this.deviceService = deviceService;
        this.deviceSessionService = deviceSessionService;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        DeviceWsMessage incoming = objectMapper.readValue(message.getPayload(), DeviceWsMessage.class);
        if (incoming.getType() == null) {
            return;
        }

        switch (incoming.getType()) {
            case "REGISTER" -> handleRegister(session, incoming);
            case "LIST" -> sendDeviceList(session);
            case "HEARTBEAT" -> {
                if (incoming.getDeviceId() != null) {
                    deviceService.updateHeartbeat(incoming.getDeviceId());
                }
            }
            case "UNREGISTER" -> {
                if (incoming.getDeviceId() != null) {
                    deviceService.remove(incoming.getDeviceId());
                }
                sendDeviceList(session);
            }
            default -> {
                logger.warning("Unsupported device message type: " + incoming.getType());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String disconnectedDeviceId = deviceSessionService.unbindBySessionId(session.getId());
        if (disconnectedDeviceId != null) {
            deviceService.remove(disconnectedDeviceId);
            logger.info("Device disconnected and removed: " + disconnectedDeviceId);
            DeviceWsMessage left = DeviceWsMessage.ofType("DEVICE_LEFT");
            left.setDeviceId(disconnectedDeviceId);
            broadcast(left);
            broadcastDeviceList();
        }
        super.afterConnectionClosed(session, status);
    }

    private void handleRegister(WebSocketSession session, DeviceWsMessage incoming) throws Exception {
        if (isBlank(incoming.getDeviceId()) || isBlank(incoming.getDeviceName()) || isBlank(incoming.getPlatform())) {
            DeviceWsMessage error = DeviceWsMessage.ofType("ERROR");
            send(session, error);
            logger.warning("Invalid REGISTER payload received on device websocket");
            return;
        }

        RegisterDeviceRequest request = new RegisterDeviceRequest(
                incoming.getDeviceId(),
                incoming.getDeviceName(),
                incoming.getPlatform()
        );
        Device registered = deviceService.register(request);
        deviceSessionService.bind(registered.deviceId(), session);
        logger.info("Registered device via websocket: " + registered.deviceId());

        DeviceWsMessage ack = DeviceWsMessage.ofType("REGISTERED");
        ack.setDeviceId(registered.deviceId());
        ack.setDeviceName(registered.deviceName());
        ack.setPlatform(registered.platform());
        send(session, ack);

        DeviceWsMessage joined = DeviceWsMessage.ofType("DEVICE_JOINED");
        joined.setDeviceId(registered.deviceId());
        joined.setDeviceName(registered.deviceName());
        joined.setPlatform(registered.platform());
        broadcast(joined);

        broadcastDeviceList();
    }

    private void sendDeviceList(WebSocketSession session) throws Exception {
        DeviceWsMessage list = DeviceWsMessage.ofType("DEVICE_LIST");
        list.setDevices(List.copyOf(deviceService.list()));
        send(session, list);
    }

    private void broadcastDeviceList() throws Exception {
        DeviceWsMessage list = DeviceWsMessage.ofType("DEVICE_LIST");
        list.setDevices(List.copyOf(deviceService.list()));
        broadcast(list);
    }

    private void broadcast(DeviceWsMessage message) throws Exception {
        for (WebSocketSession wsSession : deviceSessionService.getAllSessions().values()) {
            if (wsSession.isOpen()) {
                send(wsSession, message);
            }
        }
    }

    private void send(WebSocketSession session, DeviceWsMessage message) throws Exception {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
