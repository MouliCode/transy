package com.transfer.signaling.service;

import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private static final String SESSION_OWNER_HASH = "signaling:session-owner";

    private final ConcurrentHashMap<String, WebSocketSession> localSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionToDevice = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;
    private final String instanceId = UUID.randomUUID().toString();
    private final Logger logger;

    public SessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    public void register(String deviceId, WebSocketSession session) {
        if (deviceId == null || session == null) {
            return;
        }
        localSessions.put(deviceId, session);
        String sessionId = session.getId();
        if (sessionId != null) {
            sessionToDevice.put(sessionId, deviceId);
        }
        safeRedisPut(deviceId, instanceId);
        logger.info("Registered local signaling session for device: " + deviceId);
    }

    public void remove(String deviceId) {
        if (deviceId != null) {
            localSessions.remove(deviceId);
            safeRedisDelete(deviceId);
            logger.info("Removed local signaling session for device: " + deviceId);
        }
    }

    public WebSocketSession getSession(String deviceId) {
        return localSessions.get(deviceId);
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return localSessions;
    }

    public void removeBySessionId(String sessionId) {
        if (sessionId == null) {
            return;
        }
        String deviceId = sessionToDevice.remove(sessionId);
        if (deviceId != null) {
            localSessions.remove(deviceId);
            safeRedisDelete(deviceId);
        }
    }

    public String getOwnerInstanceId(String deviceId) {
        try {
            Object value = redisTemplate.opsForHash().get(SESSION_OWNER_HASH, deviceId);
            return value == null ? null : value.toString();
        } catch (Exception exception) {
            logger.warning("Failed to get signaling owner from redis: " + exception.getMessage());
            return null;
        }
    }

    public boolean isCurrentInstance(String ownerInstanceId) {
        return instanceId.equals(ownerInstanceId);
    }

    private void safeRedisPut(String deviceId, String owner) {
        try {
            redisTemplate.opsForHash().put(SESSION_OWNER_HASH, deviceId, owner);
        } catch (Exception exception) {
            logger.warning("Redis put failed for signaling owner, using local fallback: " + exception.getMessage());
        }
    }

    private void safeRedisDelete(String deviceId) {
        try {
            redisTemplate.opsForHash().delete(SESSION_OWNER_HASH, deviceId);
        } catch (Exception exception) {
            logger.warning("Redis delete failed for signaling owner, using local fallback: " + exception.getMessage());
        }
    }
}
