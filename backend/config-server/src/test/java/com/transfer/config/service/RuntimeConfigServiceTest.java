package com.transfer.config.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RuntimeConfigServiceTest {

    @Test
    void supportsGetAndPut() {
        RuntimeConfigService service = new RuntimeConfigService();
        assertNotNull(service.get("transfer.chunkSizeBytes"));

        service.put("transfer.maxFileSizeMb", "20480");

        assertEquals("20480", service.get("transfer.maxFileSizeMb"));
    }
}
