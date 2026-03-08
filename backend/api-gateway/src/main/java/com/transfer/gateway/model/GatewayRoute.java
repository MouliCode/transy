package com.transfer.gateway.model;

public record GatewayRoute(
        String routeId,
        String websocketPath,
        String targetService,
        int targetPort
) {
}
