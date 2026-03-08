package com.transfer.config.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuntimeConfigService {

    private final Map<String, String> configs = new ConcurrentHashMap<>();

    public RuntimeConfigService() {
        configs.put("transfer.chunkSizeBytes", "262144");
        configs.put("transfer.maxParallelStreams", "4");
        configs.put("security.requirePairing", "true");
    }

    public Map<String, String> getAll() {
        return Map.copyOf(configs);
    }

    public String get(String key) {
        return configs.get(key);
    }

    public void put(String key, String value) {
        if (key != null && !key.isBlank() && value != null) {
            configs.put(key, value);
        }
    }
}
