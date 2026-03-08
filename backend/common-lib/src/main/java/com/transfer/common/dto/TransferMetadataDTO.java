package com.transfer.common.dto;

import java.time.Instant;

public record TransferMetadataDTO(
        String transferId,
        String senderDeviceId,
        String receiverDeviceId,
        String fileName,
        long fileSizeBytes,
        long transferredBytes,
        String status,
        Instant updatedAt
) {
}
