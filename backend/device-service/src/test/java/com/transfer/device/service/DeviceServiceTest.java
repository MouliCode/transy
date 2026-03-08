package com.transfer.device.service;

import com.transfer.device.model.RegisterDeviceRequest;
import com.transfer.device.repository.InMemoryDeviceRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeviceServiceTest {

    @Test
    void registerListHeartbeatAndRemoveWork() {
        DeviceService service = new DeviceService(new InMemoryDeviceRepository());
        RegisterDeviceRequest request = new RegisterDeviceRequest("d1", "Pixel", "ANDROID");

        service.register(request);
        assertEquals(1, service.list().size());

        assertTrue(service.updateHeartbeat("d1").isPresent());

        service.remove("d1");
        assertEquals(0, service.list().size());
    }
}
