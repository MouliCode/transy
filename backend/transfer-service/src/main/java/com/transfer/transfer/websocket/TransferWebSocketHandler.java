package com.transfer.transfer.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.common.logging.core.Logger;
import com.transfer.common.logging.core.LoggerFactory;
import com.transfer.transfer.model.TransferMessage;
import com.transfer.transfer.model.TransferSession;
import com.transfer.transfer.service.TransferSessionHubService;
import com.transfer.transfer.service.TransferSessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Component
public class TransferWebSocketHandler extends TextWebSocketHandler {

    private final TransferSessionService transferSessionService;
    private final TransferSessionHubService transferSessionHubService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Logger logger;

    public TransferWebSocketHandler(TransferSessionService transferSessionService,
                                    TransferSessionHubService transferSessionHubService) {
        this.transferSessionService = transferSessionService;
        this.transferSessionHubService = transferSessionHubService;
        this.logger = LoggerFactory.createFromClasspathProperties(getClass().getSimpleName());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        transferSessionHubService.add(session);
        logger.info("Transfer websocket client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        TransferMessage input = objectMapper.readValue(message.getPayload(), TransferMessage.class);
        if (input.getType() == null) {
            return;
        }

        switch (input.getType()) {
            case "CREATE_TRANSFER" -> {
                if (input.getSenderDeviceId() != null && input.getReceiverDeviceId() != null
                        && input.getFileName() != null && input.getFileSizeBytes() != null) {
                    TransferSession created = transferSessionService.create(
                            input.getSenderDeviceId(),
                            input.getReceiverDeviceId(),
                            input.getFileName(),
                            input.getFileSizeBytes()
                    );
                    broadcastSession("TRANSFER_CREATED", created);
                    logger.info("Transfer created: " + created.transferId());
                }
            }
            case "TRANSFER_PROGRESS" -> {
                if (input.getTransferId() != null && input.getTransferredBytes() != null) {
                    TransferSession updated = transferSessionService.updateProgress(input.getTransferId(), input.getTransferredBytes());
                    if (updated != null) {
                        broadcastSession("TRANSFER_PROGRESS", updated);
                    }
                }
            }
            case "TRANSFER_COMPLETED" -> {
                if (input.getTransferId() != null) {
                    TransferSession completed = transferSessionService.complete(input.getTransferId());
                    if (completed != null) {
                        broadcastSession("TRANSFER_COMPLETED", completed);
                        logger.info("Transfer completed: " + completed.transferId());
                    }
                }
            }
            case "TRANSFER_FAILED" -> {
                if (input.getTransferId() != null) {
                    TransferSession failed = transferSessionService.fail(input.getTransferId());
                    if (failed != null) {
                        broadcastSession("TRANSFER_FAILED", failed);
                        logger.error("Transfer marked failed: " + failed.transferId());
                    }
                }
            }
            case "GET_TRANSFER" -> {
                if (input.getTransferId() != null) {
                    TransferSession found = transferSessionService.get(input.getTransferId());
                    if (found != null) {
                        TransferMessage out = TransferMessage.ofType("TRANSFER");
                        out.setSessions(List.of(found));
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(out)));
                    }
                }
            }
            case "LIST_TRANSFERS" -> {
                TransferMessage out = TransferMessage.ofType("TRANSFERS");
                out.setSessions(List.copyOf(transferSessionService.list()));
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(out)));
            }
            default -> {
                TransferMessage out = TransferMessage.ofType("ERROR");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(out)));
                logger.warning("Unsupported transfer message type: " + input.getType());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        transferSessionHubService.remove(session.getId());
        logger.info("Transfer websocket client disconnected: " + session.getId());
        super.afterConnectionClosed(session, status);
    }

    private void broadcastSession(String type, TransferSession session) throws Exception {
        TransferMessage out = TransferMessage.ofType(type);
        out.setSessions(List.of(session));
        String payload = objectMapper.writeValueAsString(out);

        for (WebSocketSession ws : transferSessionHubService.getAll().values()) {
            if (ws.isOpen()) {
                ws.sendMessage(new TextMessage(payload));
            }
        }
    }
}
