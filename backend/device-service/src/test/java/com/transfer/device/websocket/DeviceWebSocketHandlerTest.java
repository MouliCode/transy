package com.transfer.device.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.device.model.Device;
import com.transfer.device.model.DeviceStatus;
import com.transfer.device.service.DeviceService;
import com.transfer.device.service.DeviceSessionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceWebSocketHandlerTest {

    private static class TestableDeviceWebSocketHandler extends DeviceWebSocketHandler {
        TestableDeviceWebSocketHandler(DeviceService deviceService, DeviceSessionService deviceSessionService) {
            super(deviceService, deviceSessionService);
        }

        void invokeHandle(WebSocketSession session, TextMessage message) throws Exception {
            super.handleTextMessage(session, message);
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void registerMessageSendsRegisteredJoinedAndList() throws Exception {
        DeviceService deviceService = mock(DeviceService.class);
        DeviceSessionService sessionService = mock(DeviceSessionService.class);
        TestableDeviceWebSocketHandler handler = new TestableDeviceWebSocketHandler(deviceService, sessionService);

        Device device = new Device("d1", "Pixel", "ANDROID", DeviceStatus.ONLINE, Instant.now());
        when(deviceService.register(any())).thenReturn(device);
        when(deviceService.list()).thenReturn(List.of(device));

        WebSocketSession ws = mock(WebSocketSession.class);
        when(ws.isOpen()).thenReturn(true);
        Map<String, WebSocketSession> sessions = new HashMap<>();
        sessions.put("d1", ws);
        when(sessionService.getAllSessions()).thenReturn(sessions);

        handler.invokeHandle(ws, new TextMessage("{\"type\":\"REGISTER\",\"deviceId\":\"d1\",\"deviceName\":\"Pixel\",\"platform\":\"ANDROID\"}"));

        verify(sessionService).bind("d1", ws);
        ArgumentCaptor<TextMessage> msgCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(ws, times(3)).sendMessage(msgCaptor.capture());

        List<String> types = msgCaptor.getAllValues().stream().map(m -> {
            try {
                return mapper.readTree(m.getPayload()).get("type").asText();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();

        assertTrue(types.contains("REGISTERED"));
        assertTrue(types.contains("DEVICE_JOINED"));
        assertTrue(types.contains("DEVICE_LIST"));
    }

    @Test
    void invalidRegisterSendsError() throws Exception {
        DeviceService deviceService = mock(DeviceService.class);
        DeviceSessionService sessionService = mock(DeviceSessionService.class);
        TestableDeviceWebSocketHandler handler = new TestableDeviceWebSocketHandler(deviceService, sessionService);

        WebSocketSession ws = mock(WebSocketSession.class);

        handler.invokeHandle(ws, new TextMessage("{\"type\":\"REGISTER\",\"deviceId\":\"\"}"));

        ArgumentCaptor<TextMessage> msgCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(ws).sendMessage(msgCaptor.capture());
        assertTrue(mapper.readTree(msgCaptor.getValue().getPayload()).get("type").asText().equals("ERROR"));
    }

    @Test
    void listMessageReturnsDeviceList() throws Exception {
        DeviceService deviceService = mock(DeviceService.class);
        DeviceSessionService sessionService = mock(DeviceSessionService.class);
        TestableDeviceWebSocketHandler handler = new TestableDeviceWebSocketHandler(deviceService, sessionService);

        when(deviceService.list()).thenReturn(List.of());
        WebSocketSession ws = mock(WebSocketSession.class);

        handler.invokeHandle(ws, new TextMessage("{\"type\":\"LIST\"}"));

        ArgumentCaptor<TextMessage> msgCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(ws).sendMessage(msgCaptor.capture());
        assertTrue(mapper.readTree(msgCaptor.getValue().getPayload()).get("type").asText().equals("DEVICE_LIST"));
    }

    @Test
    void closeBroadcastsDeviceLeftAndList() throws Exception {
        DeviceService deviceService = mock(DeviceService.class);
        DeviceSessionService sessionService = mock(DeviceSessionService.class);
        TestableDeviceWebSocketHandler handler = new TestableDeviceWebSocketHandler(deviceService, sessionService);

        WebSocketSession closingSession = mock(WebSocketSession.class);
        when(closingSession.getId()).thenReturn("s1");

        WebSocketSession active = mock(WebSocketSession.class);
        when(active.isOpen()).thenReturn(true);
        Map<String, WebSocketSession> sessions = new HashMap<>();
        sessions.put("d2", active);

        when(sessionService.unbindBySessionId("s1")).thenReturn("d1");
        when(sessionService.getAllSessions()).thenReturn(sessions);
        when(deviceService.list()).thenReturn(List.of());

        handler.afterConnectionClosed(closingSession, CloseStatus.NORMAL);

        verify(deviceService).remove("d1");
        verify(active, times(2)).sendMessage(any(TextMessage.class));
    }
}
