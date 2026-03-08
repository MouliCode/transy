package com.transfer.gateway.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayRegistryServiceTest {

    @Test
    void returnsExpectedWebsocketRoutes() {
        GatewayRegistryService service = new GatewayRegistryService();

        assertFalse(service.getRoutes().isEmpty());
        assertTrue(service.getRoutes().stream().anyMatch(route -> route.websocketPath().equals("/signal")));
        assertTrue(service.getRoutes().stream().anyMatch(route -> route.websocketPath().equals("/devices/ws")));
    }
}
