package com.transfer.gateway.model;

import java.util.List;

public class GatewayMessage {

    private String type;
    private String message;
    private List<GatewayRoute> routes;

    public static GatewayMessage ofType(String type) {
        GatewayMessage value = new GatewayMessage();
        value.setType(type);
        return value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GatewayRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GatewayRoute> routes) {
        this.routes = routes;
    }
}
