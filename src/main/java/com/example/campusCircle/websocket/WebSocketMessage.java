package com.example.campusCircle.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    private String type;
    private String sender;
    private String recipient;
    private Object payload;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;

    // Message types
    public static final String TYPE_NOTIFICATION = "NOTIFICATION";
    public static final String TYPE_DIRECT_MESSAGE = "DIRECT_MESSAGE";
    public static final String TYPE_TYPING = "TYPING";
    public static final String TYPE_ONLINE_STATUS = "ONLINE_STATUS";
    public static final String TYPE_NEW_POST = "NEW_POST";
    public static final String TYPE_NEW_COMMENT = "NEW_COMMENT";
    public static final String TYPE_VOTE_UPDATE = "VOTE_UPDATE";
    public static final String TYPE_CHANNEL_UPDATE = "CHANNEL_UPDATE";
    public static final String TYPE_READ_RECEIPT = "READ_RECEIPT";
    public static final String TYPE_ERROR = "ERROR";

    public static WebSocketMessage notification(String recipient, Object payload) {
        return WebSocketMessage.builder()
                .type(TYPE_NOTIFICATION)
                .recipient(recipient)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static WebSocketMessage directMessage(String sender, String recipient, Object payload) {
        return WebSocketMessage.builder()
                .type(TYPE_DIRECT_MESSAGE)
                .sender(sender)
                .recipient(recipient)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static WebSocketMessage typing(String sender, String recipient, boolean isTyping) {
        return WebSocketMessage.builder()
                .type(TYPE_TYPING)
                .sender(sender)
                .recipient(recipient)
                .payload(Map.of("isTyping", isTyping))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static WebSocketMessage onlineStatus(String username, boolean isOnline) {
        return WebSocketMessage.builder()
                .type(TYPE_ONLINE_STATUS)
                .sender(username)
                .payload(Map.of("isOnline", isOnline, "username", username))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static WebSocketMessage error(String recipient, String errorMessage) {
        return WebSocketMessage.builder()
                .type(TYPE_ERROR)
                .recipient(recipient)
                .payload(Map.of("error", errorMessage))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
