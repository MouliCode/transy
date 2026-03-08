package com.transfer.transfer.service;

import com.transfer.transfer.model.TransferSession;
import com.transfer.transfer.model.TransferStatus;
import com.transfer.transfer.repository.TransferSessionEntity;
import com.transfer.transfer.repository.TransferSessionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransferSessionService {

    private final TransferSessionJpaRepository transferSessionJpaRepository;

    public TransferSessionService(TransferSessionJpaRepository transferSessionJpaRepository) {
        this.transferSessionJpaRepository = transferSessionJpaRepository;
    }

    @Transactional
    public TransferSession create(String senderDeviceId, String receiverDeviceId, String fileName, long fileSizeBytes) {
        Instant now = Instant.now();

        TransferSessionEntity entity = new TransferSessionEntity();
        entity.setTransferId(UUID.randomUUID().toString());
        entity.setSenderDeviceId(senderDeviceId);
        entity.setReceiverDeviceId(receiverDeviceId);
        entity.setFileName(fileName);
        entity.setFileSizeBytes(fileSizeBytes);
        entity.setTransferredBytes(0);
        entity.setStatus(TransferStatus.PENDING);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return toDomain(transferSessionJpaRepository.save(entity));
    }

    @Transactional
    public TransferSession updateProgress(String transferId, long transferredBytes) {
        return transferSessionJpaRepository.findById(transferId)
                .map(existing -> {
                    existing.setTransferredBytes(transferredBytes);
                    existing.setStatus(TransferStatus.IN_PROGRESS);
                    existing.setUpdatedAt(Instant.now());
                    return toDomain(transferSessionJpaRepository.save(existing));
                })
                .orElse(null);
    }

    @Transactional
    public TransferSession complete(String transferId) {
        return transferSessionJpaRepository.findById(transferId)
                .map(existing -> {
                    existing.setTransferredBytes(existing.getFileSizeBytes());
                    existing.setStatus(TransferStatus.COMPLETED);
                    existing.setUpdatedAt(Instant.now());
                    return toDomain(transferSessionJpaRepository.save(existing));
                })
                .orElse(null);
    }

    @Transactional
    public TransferSession fail(String transferId) {
        return transferSessionJpaRepository.findById(transferId)
                .map(existing -> {
                    existing.setStatus(TransferStatus.FAILED);
                    existing.setUpdatedAt(Instant.now());
                    return toDomain(transferSessionJpaRepository.save(existing));
                })
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public TransferSession get(String transferId) {
        return transferSessionJpaRepository.findById(transferId)
                .map(this::toDomain)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Collection<TransferSession> list() {
        return transferSessionJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private TransferSession toDomain(TransferSessionEntity entity) {
        return new TransferSession(
                entity.getTransferId(),
                entity.getSenderDeviceId(),
                entity.getReceiverDeviceId(),
                entity.getFileName(),
                entity.getFileSizeBytes(),
                entity.getTransferredBytes(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
