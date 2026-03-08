package com.transfer.common.dto;

public record SignalDTO(
        String type,
        String from,
        String to,
        String data
) {
}
