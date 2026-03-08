package com.transfer.device.repository;

import com.transfer.device.model.Device;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDeviceRepository implements DeviceRepository {

    private final Map<String, Device> storage = new ConcurrentHashMap<>();

    @Override
    public Device save(Device device) {
        storage.put(device.deviceId(), device);
        return device;
    }

    @Override
    public Optional<Device> findById(String deviceId) {
        return Optional.ofNullable(storage.get(deviceId));
    }

    @Override
    public Collection<Device> findAll() {
        return storage.values();
    }

    @Override
    public void deleteById(String deviceId) {
        storage.remove(deviceId);
    }
}
