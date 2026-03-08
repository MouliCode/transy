package com.transfer.discovery.model;

import java.util.List;

public class DiscoveryMessage {

    private String type;
    private String serviceName;
    private String host;
    private Integer port;
    private List<ServiceNode> services;

    public static DiscoveryMessage ofType(String type) {
        DiscoveryMessage message = new DiscoveryMessage();
        message.setType(type);
        return message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<ServiceNode> getServices() {
        return services;
    }

    public void setServices(List<ServiceNode> services) {
        this.services = services;
    }
}
