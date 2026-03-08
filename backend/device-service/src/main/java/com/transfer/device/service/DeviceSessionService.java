package com.transfer.device.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceSessionService {

    private static final String SESSION_OWNER_HASH = "device:session-owner";

    private final Map<String, WebSocketSession> deviceSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionDeviceMapping = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;
    private final String instanceId = UUID.randomUUID().toString();

    public DeviceSessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void bind(String deviceId, WebSocketSession session) {
        deviceSessions.put(deviceId, session);
        sessionDeviceMapping.put(session.getId(), deviceId);
        safeRedisPut(deviceId, instanceId);
    }

    public String unbindBySessionId(String sessionId) {
        String deviceId = sessionDeviceMapping.remove(sessionId);
        if (deviceId != null) {
            deviceSessions.remove(deviceId);
            safeRedisDelete(deviceId);
        }
        return deviceId;
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return deviceSessions;
    }

    private void safeRedisPut(String deviceId, String ownerInstanceId) {
        try {
            redisTemplate.opsForHash().put(SESSION_OWNER_HASH, deviceId, ownerInstanceId);
        } catch (Exception ignored) {
            // local fallback mode
        }
    }

    private void safeRedisDelete(String deviceId) {
        try {
            redisTemplate.opsForHash().delete(SESSION_OWNER_HASH, deviceId);
        } catch (Exception ignored) {
            // local fallback mode
        }
    }
}
