package com.transfer.device.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import com.transfer.device.model.Device;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class RedisBackedDeviceRepository implements DeviceRepository {

    private static final String DEVICE_HASH = "device:registry";

    private final StringRedisTemplate redisTemplate;
    private final Map<String, Device> fallbackStorage = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Logger logger;

    public RedisBackedDeviceRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    @Override
    public Device save(Device device) {
        fallbackStorage.put(device.deviceId(), device);
        try {
            redisTemplate.opsForHash().put(DEVICE_HASH, device.deviceId(), objectMapper.writeValueAsString(device));
        } catch (Exception exception) {
            logger.warning("Redis save failed, using local fallback for device " + device.deviceId() + ": " + exception.getMessage());
        }
        return device;
    }

    @Override
    public Optional<Device> findById(String deviceId) {
        try {
            Object raw = redisTemplate.opsForHash().get(DEVICE_HASH, deviceId);
            if (raw != null) {
                return Optional.of(objectMapper.readValue(raw.toString(), Device.class));
            }
        } catch (Exception exception) {
            logger.warning("Redis lookup failed, using local fallback for device " + deviceId + ": " + exception.getMessage());
        }
        return Optional.ofNullable(fallbackStorage.get(deviceId));
    }

    @Override
    public Collection<Device> findAll() {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(DEVICE_HASH);
            if (!entries.isEmpty()) {
                List<Device> devices = new ArrayList<>();
                for (Object value : entries.values()) {
                    devices.add(objectMapper.readValue(value.toString(), Device.class));
                }
                return devices;
            }
        } catch (Exception exception) {
            logger.warning("Redis list failed, using local fallback: " + exception.getMessage());
        }
        return List.copyOf(fallbackStorage.values());
    }

    @Override
    public void deleteById(String deviceId) {
        fallbackStorage.remove(deviceId);
        try {
            redisTemplate.opsForHash().delete(DEVICE_HASH, deviceId);
        } catch (Exception exception) {
            logger.warning("Redis delete failed, fallback cleanup only for device " + deviceId + ": " + exception.getMessage());
        }
    }
}
