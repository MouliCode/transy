package com.transfer.gateway.service;

import com.transfer.gateway.model.GatewayRoute;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatewayRegistryService {

    private final List<GatewayRoute> routes = List.of(
            new GatewayRoute("device-ws", "/devices/ws", "device-service", 8083),
            new GatewayRoute("signal-ws", "/signal", "signaling-service", 8082),
            new GatewayRoute("transfer-ws", "/transfers/ws", "transfer-service", 8085),
            new GatewayRoute("discovery-ws", "/discovery/ws", "discovery-server", 8761),
            new GatewayRoute("config-ws", "/config/ws", "config-server", 8888)
    );

    public List<GatewayRoute> getRoutes() {
        return routes;
    }
}
