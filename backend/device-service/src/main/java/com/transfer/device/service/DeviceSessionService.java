package com.transfer.device.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceSessionService {

    private final Map<String, WebSocketSession> deviceSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionDeviceMapping = new ConcurrentHashMap<>();

    public void bind(String deviceId, WebSocketSession session) {
        deviceSessions.put(deviceId, session);
        sessionDeviceMapping.put(session.getId(), deviceId);
    }

    public String unbindBySessionId(String sessionId) {
        String deviceId = sessionDeviceMapping.remove(sessionId);
        if (deviceId != null) {
            deviceSessions.remove(deviceId);
        }
        return deviceId;
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return deviceSessions;
    }
}
