package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "direct_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessage {
    
    @Id
    private String id;
    
    @Field("conversation_id")
    private String conversationId;
    
    @Field("participants")
    private List<String> participants; // usernames
    
    @Field("messages")
    private List<Message> messages = new ArrayList<>();
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("last_message_at")
    private LocalDateTime lastMessageAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String messageId;
        private String sender; // username
        private String text;
        private List<String> mediaUrls;
        private LocalDateTime timestamp;
        private boolean read;
        private LocalDateTime readAt;
    }
    
    public void addMessage(Message message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
        this.lastMessageAt = message.getTimestamp();
        this.updatedAt = LocalDateTime.now();
    }
}
