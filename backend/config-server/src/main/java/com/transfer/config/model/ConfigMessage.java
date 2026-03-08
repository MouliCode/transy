package com.transfer.config.model;

import java.util.Map;

public class ConfigMessage {

    private String type;
    private String key;
    private String value;
    private Map<String, String> configs;

    public static ConfigMessage ofType(String type) {
        ConfigMessage message = new ConfigMessage();
        message.setType(type);
        return message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, String> configs) {
        this.configs = configs;
    }
}
