package com.example.campusCircle.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send a message to a specific user
     */
    public void sendToUser(String username, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(username, destination, payload);
    }

    /**
     * Send a notification to a specific user
     */
    public void sendNotification(String username, Object notification) {
        WebSocketMessage message = WebSocketMessage.notification(username, notification);
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }

    /**
     * Send a direct message to a specific user
     */
    public void sendDirectMessage(String sender, String recipient, Object messagePayload) {
        WebSocketMessage message = WebSocketMessage.directMessage(sender, recipient, messagePayload);
        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", message);
    }

    /**
     * Send typing indicator to a specific user
     */
    public void sendTypingIndicator(String sender, String recipient, boolean isTyping) {
        WebSocketMessage message = WebSocketMessage.typing(sender, recipient, isTyping);
        messagingTemplate.convertAndSendToUser(recipient, "/queue/typing", message);
    }

    /**
     * Broadcast to a channel/topic
     */
    public void broadcastToChannel(Long channelId, Object payload) {
        messagingTemplate.convertAndSend("/topic/channel/" + channelId, payload);
    }

    /**
     * Broadcast new post to channel subscribers
     */
    public void broadcastNewPost(Long channelId, Object post) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE_NEW_POST)
                .payload(post)
                .build();
        messagingTemplate.convertAndSend("/topic/channel/" + channelId + "/posts", message);
    }

    /**
     * Broadcast new comment to post subscribers
     */
    public void broadcastNewComment(Long postId, Object comment) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE_NEW_COMMENT)
                .payload(comment)
                .build();
        messagingTemplate.convertAndSend("/topic/post/" + postId + "/comments", message);
    }

    /**
     * Broadcast vote update
     */
    public void broadcastVoteUpdate(Long contentId, String contentType, Object voteData) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE_VOTE_UPDATE)
                .payload(voteData)
                .metadata(Map.of("contentId", contentId, "contentType", contentType))
                .build();
        messagingTemplate.convertAndSend("/topic/votes/" + contentType + "/" + contentId, message);
    }

    /**
     * Send read receipt
     */
    public void sendReadReceipt(String sender, String recipient, String conversationId) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE_READ_RECEIPT)
                .sender(sender)
                .recipient(recipient)
                .payload(Map.of("conversationId", conversationId, "readBy", sender))
                .build();
        messagingTemplate.convertAndSendToUser(recipient, "/queue/read-receipts", message);
    }

    /**
     * Broadcast to university
     */
    public void broadcastToUniversity(Long universityId, Object payload) {
        messagingTemplate.convertAndSend("/topic/university/" + universityId, payload);
    }

    /**
     * Check if user is online
     */
    public boolean isUserOnline(String username) {
        return WebSocketEventListener.isUserOnline(username);
    }

    /**
     * Get all online users
     */
    public Set<String> getOnlineUsers() {
        return WebSocketEventListener.getOnlineUsers();
    }

    /**
     * Get online user count
     */
    public int getOnlineUserCount() {
        return WebSocketEventListener.getOnlineUserCount();
    }
}
