package com.transfer.discovery.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscoveryRegistryServiceTest {

    @Test
    void registerHeartbeatAndUnregisterWork() {
        DiscoveryRegistryService service = new DiscoveryRegistryService();

        service.register("device-service", "localhost", 8083, "s1");
        assertEquals(1, service.list().size());

        assertNotNull(service.heartbeat("device-service"));

        service.unregister("device-service");
        assertEquals(0, service.list().size());
    }
}
