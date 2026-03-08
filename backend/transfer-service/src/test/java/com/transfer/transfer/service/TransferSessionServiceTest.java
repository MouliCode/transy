package com.transfer.transfer.service;

import com.transfer.transfer.model.TransferSession;
import com.transfer.transfer.model.TransferStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransferSessionServiceTest {

    @Test
    void createProgressCompleteAndFailAreHandled() {
        TransferSessionService service = new TransferSessionService();

        TransferSession created = service.create("d1", "d2", "movie.mp4", 1024);
        assertNotNull(created.transferId());
        assertEquals(TransferStatus.PENDING, created.status());

        TransferSession progress = service.updateProgress(created.transferId(), 512);
        assertEquals(TransferStatus.IN_PROGRESS, progress.status());
        assertEquals(512, progress.transferredBytes());

        TransferSession completed = service.complete(created.transferId());
        assertEquals(TransferStatus.COMPLETED, completed.status());

        TransferSession failed = service.fail(created.transferId());
        assertEquals(TransferStatus.FAILED, failed.status());
    }
}
