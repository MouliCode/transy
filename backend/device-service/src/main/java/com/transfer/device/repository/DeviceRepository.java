package com.transfer.device.repository;

import com.transfer.device.model.Device;

import java.util.Collection;
import java.util.Optional;

public interface DeviceRepository {

    Device save(Device device);

    Optional<Device> findById(String deviceId);

    Collection<Device> findAll();

    void deleteById(String deviceId);
}
