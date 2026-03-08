package com.transfer.transfer.model;

import java.util.List;

public class TransferMessage {

    private String type;
    private String transferId;
    private String senderDeviceId;
    private String receiverDeviceId;
    private String fileName;
    private Long fileSizeBytes;
    private Long transferredBytes;
    private String status;
    private List<TransferSession> sessions;

    public static TransferMessage ofType(String type) {
        TransferMessage message = new TransferMessage();
        message.setType(type);
        return message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getSenderDeviceId() {
        return senderDeviceId;
    }

    public void setSenderDeviceId(String senderDeviceId) {
        this.senderDeviceId = senderDeviceId;
    }

    public String getReceiverDeviceId() {
        return receiverDeviceId;
    }

    public void setReceiverDeviceId(String receiverDeviceId) {
        this.receiverDeviceId = receiverDeviceId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public Long getTransferredBytes() {
        return transferredBytes;
    }

    public void setTransferredBytes(Long transferredBytes) {
        this.transferredBytes = transferredBytes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TransferSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<TransferSession> sessions) {
        this.sessions = sessions;
    }
}
