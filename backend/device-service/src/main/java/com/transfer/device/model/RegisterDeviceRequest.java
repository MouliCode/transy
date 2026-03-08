package com.transfer.device.model;

public record RegisterDeviceRequest(
        String deviceId,
        String deviceName,
        String platform
) {
}
