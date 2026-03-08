package com.transfer.discovery.model;

import java.time.Instant;

public record ServiceNode(
        String serviceName,
        String host,
        int port,
        Instant lastSeenAt
) {
}
