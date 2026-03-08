package com.transfer.device.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeviceSessionServiceTest {

    @Test
    void bindAndUnbindBySessionIdWork() {
        DeviceSessionService service = new DeviceSessionService(null);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("ws-1");

        service.bind("d1", session);
        assertEquals(1, service.getAllSessions().size());

        String deviceId = service.unbindBySessionId("ws-1");
        assertEquals("d1", deviceId);
        assertEquals(0, service.getAllSessions().size());
    }
}
