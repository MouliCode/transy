package com.transfer.common.constants;

public final class MessageTypes {

    private MessageTypes() {
    }

    public static final String REGISTER = "REGISTER";
    public static final String DEVICE_LIST = "DEVICE_LIST";
    public static final String DEVICE_JOINED = "DEVICE_JOINED";
    public static final String DEVICE_LEFT = "DEVICE_LEFT";
    public static final String OFFER = "OFFER";
    public static final String ANSWER = "ANSWER";
    public static final String ICE = "ICE";
    public static final String HEARTBEAT = "HEARTBEAT";
    public static final String CREATE_TRANSFER = "CREATE_TRANSFER";
    public static final String TRANSFER_PROGRESS = "TRANSFER_PROGRESS";
    public static final String TRANSFER_COMPLETED = "TRANSFER_COMPLETED";
    public static final String TRANSFER_FAILED = "TRANSFER_FAILED";
}
