package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "moderation_queue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationQueue {
    
    @Id
    private String id;
    
    @Field("content_id")
    private String contentId;
    
    @Field("content_type")
    private String contentType; // "post" or "comment"
    
    @Field("content_text")
    private String contentText;
    
    @Field("author_username")
    private String authorUsername;
    
    @Field("flagged_at")
    private LocalDateTime flaggedAt;
    
    @Field("ai_moderation_score")
    private Double aiModerationScore;
    
    @Field("ai_flags")
    private List<String> aiFlags; // List of AI-detected issues (e.g., "hate_speech", "spam", "violence")
    
    @Field("user_reports")
    private List<Map<String, Object>> userReports; // List of user reports with reporter and reason
    
    @Field("status")
    private String status; // "pending", "approved", "rejected", "escalated"
    
    @Field("reviewed_by")
    private String reviewedBy;
    
    @Field("reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Field("moderation_action")
    private String moderationAction; // "none", "warning", "content_removed", "user_banned"
}
