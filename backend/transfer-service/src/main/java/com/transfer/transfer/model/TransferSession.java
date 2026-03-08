package com.transfer.transfer.model;

import java.time.Instant;

public record TransferSession(
        String transferId,
        String senderDeviceId,
        String receiverDeviceId,
        String fileName,
        long fileSizeBytes,
        long transferredBytes,
        TransferStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
