package com.transfer.discovery.service;

import com.transfer.discovery.model.ServiceNode;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DiscoveryRegistryService {

    private final Map<String, ServiceNode> services = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToService = new ConcurrentHashMap<>();

    public ServiceNode register(String serviceName, String host, int port, String sessionId) {
        ServiceNode node = new ServiceNode(serviceName, host, port, Instant.now());
        services.put(serviceName, node);
        sessionToService.put(sessionId, serviceName);
        return node;
    }

    public ServiceNode heartbeat(String serviceName) {
        ServiceNode existing = services.get(serviceName);
        if (existing == null) {
            return null;
        }
        ServiceNode updated = new ServiceNode(existing.serviceName(), existing.host(), existing.port(), Instant.now());
        services.put(serviceName, updated);
        return updated;
    }

    public void unregister(String serviceName) {
        services.remove(serviceName);
    }

    public String unregisterBySession(String sessionId) {
        String serviceName = sessionToService.remove(sessionId);
        if (serviceName != null) {
            services.remove(serviceName);
        }
        return serviceName;
    }

    public Collection<ServiceNode> list() {
        return services.values();
    }
}
