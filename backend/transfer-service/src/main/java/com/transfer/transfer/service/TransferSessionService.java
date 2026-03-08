package com.transfer.transfer.service;

import com.transfer.transfer.model.TransferSession;
import com.transfer.transfer.model.TransferStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransferSessionService {

    private final Map<String, TransferSession> sessions = new ConcurrentHashMap<>();

    public TransferSession create(String senderDeviceId, String receiverDeviceId, String fileName, long fileSizeBytes) {
        String transferId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        TransferSession session = new TransferSession(
                transferId,
                senderDeviceId,
                receiverDeviceId,
                fileName,
                fileSizeBytes,
                0,
                TransferStatus.PENDING,
                now,
                now
        );
        sessions.put(transferId, session);
        return session;
    }

    public TransferSession updateProgress(String transferId, long transferredBytes) {
        TransferSession existing = sessions.get(transferId);
        if (existing == null) {
            return null;
        }
        TransferSession updated = new TransferSession(
                existing.transferId(),
                existing.senderDeviceId(),
                existing.receiverDeviceId(),
                existing.fileName(),
                existing.fileSizeBytes(),
                transferredBytes,
                TransferStatus.IN_PROGRESS,
                existing.createdAt(),
                Instant.now()
        );
        sessions.put(transferId, updated);
        return updated;
    }

    public TransferSession complete(String transferId) {
        TransferSession existing = sessions.get(transferId);
        if (existing == null) {
            return null;
        }
        TransferSession updated = new TransferSession(
                existing.transferId(),
                existing.senderDeviceId(),
                existing.receiverDeviceId(),
                existing.fileName(),
                existing.fileSizeBytes(),
                existing.fileSizeBytes(),
                TransferStatus.COMPLETED,
                existing.createdAt(),
                Instant.now()
        );
        sessions.put(transferId, updated);
        return updated;
    }

    public TransferSession fail(String transferId) {
        TransferSession existing = sessions.get(transferId);
        if (existing == null) {
            return null;
        }
        TransferSession updated = new TransferSession(
                existing.transferId(),
                existing.senderDeviceId(),
                existing.receiverDeviceId(),
                existing.fileName(),
                existing.fileSizeBytes(),
                existing.transferredBytes(),
                TransferStatus.FAILED,
                existing.createdAt(),
                Instant.now()
        );
        sessions.put(transferId, updated);
        return updated;
    }

    public TransferSession get(String transferId) {
        return sessions.get(transferId);
    }

    public Collection<TransferSession> list() {
        return sessions.values();
    }
}
