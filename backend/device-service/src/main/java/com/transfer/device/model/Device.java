package com.transfer.device.model;

import java.time.Instant;

public record Device(
        String deviceId,
        String deviceName,
        String platform,
        DeviceStatus status,
        Instant lastSeenAt
) {
}
