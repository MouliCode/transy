package com.transfer.signaling.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(String deviceId, WebSocketSession session) {
        sessions.put(deviceId, session);
    }

    public void remove(String deviceId) {
        if (deviceId != null) {
            sessions.remove(deviceId);
        }
    }

    public WebSocketSession getSession(String deviceId) {
        return sessions.get(deviceId);
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return sessions;
    }

    public void removeBySessionId(String sessionId) {
        sessions.entrySet().removeIf(entry -> entry.getValue().getId().equals(sessionId));
    }
}
