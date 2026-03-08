package com.transfer.signaling.model;

public class SignalMessage {

    private String type;
    private String from;
    private String to;
    private String data;

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getData() {
        return data;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setData(String data) {
        this.data = data;
    }
}
