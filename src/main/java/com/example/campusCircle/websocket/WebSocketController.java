package com.example.campusCircle.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Controller
public class WebSocketController {

    @Autowired
    private WebSocketService webSocketService;

    /**
     * Handle direct messages between users
     * Client sends to: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void sendDirectMessage(@Payload ChatMessage message, Principal principal) {
        if (principal != null) {
            message.setSender(principal.getName());
            message.setTimestamp(LocalDateTime.now());
            
            // Send to recipient
            webSocketService.sendDirectMessage(
                    message.getSender(),
                    message.getRecipient(),
                    message
            );
        }
    }

    /**
     * Handle typing indicators
     * Client sends to: /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void sendTypingIndicator(@Payload TypingMessage message, Principal principal) {
        if (principal != null) {
            webSocketService.sendTypingIndicator(
                    principal.getName(),
                    message.getRecipient(),
                    message.isTyping()
            );
        }
    }

    /**
     * Handle read receipts
     * Client sends to: /app/chat.read
     */
    @MessageMapping("/chat.read")
    public void sendReadReceipt(@Payload ReadReceiptMessage message, Principal principal) {
        if (principal != null) {
            webSocketService.sendReadReceipt(
                    principal.getName(),
                    message.getRecipient(),
                    message.getConversationId()
            );
        }
    }

    /**
     * Subscribe to channel updates
     * Client subscribes to: /topic/channel/{channelId}
     */
    @MessageMapping("/channel.subscribe/{channelId}")
    @SendTo("/topic/channel/{channelId}")
    public WebSocketMessage subscribeToChannel(
            @DestinationVariable Long channelId,
            Principal principal) {
        if (principal != null) {
            return WebSocketMessage.builder()
                    .type("SUBSCRIPTION")
                    .sender(principal.getName())
                    .payload(Map.of("channelId", channelId, "action", "subscribed"))
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        return null;
    }

    /**
     * Get online status of specific users
     * Client sends to: /app/status.check
     */
    @MessageMapping("/status.check")
    @SendToUser("/queue/status")
    public Map<String, Boolean> checkOnlineStatus(@Payload Set<String> usernames) {
        return usernames.stream()
                .collect(java.util.stream.Collectors.toMap(
                        username -> username,
                        webSocketService::isUserOnline
                ));
    }

    /**
     * Get all online users
     * Client sends to: /app/status.online
     */
    @MessageMapping("/status.online")
    @SendToUser("/queue/online-users")
    public Set<String> getOnlineUsers() {
        return webSocketService.getOnlineUsers();
    }

    /**
     * Ping/heartbeat to keep connection alive
     * Client sends to: /app/ping
     */
    @MessageMapping("/ping")
    @SendToUser("/queue/pong")
    public Map<String, Object> ping(Principal principal) {
        return Map.of(
                "status", "pong",
                "timestamp", LocalDateTime.now(),
                "user", principal != null ? principal.getName() : "anonymous"
        );
    }

    // DTOs for WebSocket messages
    public static class ChatMessage {
        private String sender;
        private String recipient;
        private String content;
        private String messageType; // TEXT, IMAGE, FILE
        private LocalDateTime timestamp;

        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class TypingMessage {
        private String recipient;
        private boolean typing;

        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }

    public static class ReadReceiptMessage {
        private String recipient;
        private String conversationId;

        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    }
}
