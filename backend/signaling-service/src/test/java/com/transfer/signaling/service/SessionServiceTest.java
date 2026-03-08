package com.transfer.signaling.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionServiceTest {

    @Test
    void registerAndRemoveBySessionIdWorks() {
        SessionService service = new SessionService();
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("s1");

        service.register("device-1", session);
        assertEquals(session, service.getSession("device-1"));

        service.removeBySessionId("s1");
        assertNull(service.getSession("device-1"));
    }
}
