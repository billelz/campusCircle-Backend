package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    private String id;
    
    @Field("recipient_username")
    private String recipientUsername;
    
    @Field("recipient_user_id")
    private Long recipientUserId;
    
    @Field("type")
    private NotificationType type;
    
    @Field("title")
    private String title;
    
    @Field("message")
    private String message;
    
    @Field("content_reference")
    private ContentReference contentReference;
    
    @Field("sender_username")
    private String senderUsername;
    
    @Field("read_status")
    private boolean readStatus = false;
    
    @Field("read_at")
    private LocalDateTime readAt;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("metadata")
    private Map<String, Object> metadata;
    
    public enum NotificationType {
        REPLY,
        MENTION,
        UPVOTE,
        DOWNVOTE,
        NEW_FOLLOWER,
        POST_TRENDING,
        BADGE_EARNED,
        MODERATION_ACTION,
        DIRECT_MESSAGE,
        CHANNEL_UPDATE,
        SYSTEM
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentReference {
        private String contentType; // POST, COMMENT, CHANNEL, USER
        private Long contentId;
        private String contentTitle;
        private Long channelId;
    }
}
