package com.transfer.device.model;

import java.util.List;

public class DeviceWsMessage {

    private String type;
    private String deviceId;
    private String deviceName;
    private String platform;
    private List<Device> devices;

    public static DeviceWsMessage ofType(String type) {
        DeviceWsMessage message = new DeviceWsMessage();
        message.setType(type);
        return message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
