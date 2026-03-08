package com.transfer.signaling.websocket;

import com.transfer.signaling.redis.SignalingRelayPublisher;
import com.transfer.signaling.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignalingHandlerTest {

    private static class TestableSignalingHandler extends SignalingHandler {
        TestableSignalingHandler(SessionService sessionService) {
            super(sessionService, new SignalingRelayPublisher(null, "test-channel"));
        }

        void invokeHandle(WebSocketSession session, TextMessage message) throws Exception {
            super.handleTextMessage(session, message);
        }
    }

    @Test
    void registerMessageStoresSession() throws Exception {
        SessionService sessionService = new SessionService(null);
        TestableSignalingHandler handler = new TestableSignalingHandler(sessionService);
        WebSocketSession sender = mock(WebSocketSession.class);

        handler.invokeHandle(sender, new TextMessage("{\"type\":\"REGISTER\",\"from\":\"a\"}"));

        assertEquals(sender, sessionService.getSession("a"));
    }

    @Test
    void offerMessageRoutesToTargetSession() throws Exception {
        SessionService sessionService = new SessionService(null);
        TestableSignalingHandler handler = new TestableSignalingHandler(sessionService);

        WebSocketSession target = mock(WebSocketSession.class);
        when(target.isOpen()).thenReturn(true);
        sessionService.register("b", target);

        WebSocketSession sender = mock(WebSocketSession.class);
        TextMessage message = new TextMessage("{\"type\":\"OFFER\",\"from\":\"a\",\"to\":\"b\",\"data\":\"x\"}");

        handler.invokeHandle(sender, message);

        verify(target).sendMessage(message);
    }

    @Test
    void closedTargetDoesNotReceiveMessage() throws Exception {
        SessionService sessionService = new SessionService(null);
        TestableSignalingHandler handler = new TestableSignalingHandler(sessionService);

        WebSocketSession target = mock(WebSocketSession.class);
        when(target.isOpen()).thenReturn(false);
        sessionService.register("b", target);

        handler.invokeHandle(mock(WebSocketSession.class),
                new TextMessage("{\"type\":\"ICE\",\"from\":\"a\",\"to\":\"b\",\"data\":\"x\"}"));

        verify(target, never()).sendMessage(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void closeRemovesSessionBySocketId() {
        SessionService sessionService = new SessionService(null);
        TestableSignalingHandler handler = new TestableSignalingHandler(sessionService);

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("sid-1");
        sessionService.register("device-1", session);

        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        assertEquals(0, sessionService.getAllSessions().size());
    }
}
