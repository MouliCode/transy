package com.transfer.signaling.redis;

public class RelayMessage {

    private String toDeviceId;
    private String payload;

    public RelayMessage() {
    }

    public RelayMessage(String toDeviceId, String payload) {
        this.toDeviceId = toDeviceId;
        this.payload = payload;
    }

    public String getToDeviceId() {
        return toDeviceId;
    }

    public void setToDeviceId(String toDeviceId) {
        this.toDeviceId = toDeviceId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
