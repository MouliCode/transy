package com.transfer.device.service;

import com.transfer.device.model.Device;
import com.transfer.device.model.DeviceStatus;
import com.transfer.device.model.RegisterDeviceRequest;
import com.transfer.device.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device register(RegisterDeviceRequest request) {
        Device device = new Device(
                request.deviceId(),
                request.deviceName(),
                request.platform(),
                DeviceStatus.ONLINE,
                Instant.now()
        );
        return deviceRepository.save(device);
    }

    public Collection<Device> list() {
        return deviceRepository.findAll();
    }

    public Optional<Device> updateHeartbeat(String deviceId) {
        Optional<Device> current = deviceRepository.findById(deviceId);
        if (current.isEmpty()) {
            return Optional.empty();
        }
        Device updated = new Device(
                current.get().deviceId(),
                current.get().deviceName(),
                current.get().platform(),
                DeviceStatus.ONLINE,
                Instant.now()
        );
        return Optional.of(deviceRepository.save(updated));
    }

    public void remove(String deviceId) {
        deviceRepository.deleteById(deviceId);
    }
}
