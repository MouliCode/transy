package com.transfer.common.dto;

import java.time.Instant;

public record DeviceDTO(
        String deviceId,
        String deviceName,
        String platform,
        Instant lastSeenAt
) {
}
