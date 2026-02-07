package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_events")
public class AnalyticsEvent {

    @Id
    private String id;

    @Indexed
    private String eventType; // "page_view", "click", "post_view", "search", "login", "signup", "vote", etc.

    @Indexed
    private String eventCategory; // "engagement", "navigation", "content", "user", "system"

    @Indexed
    private Long userId;

    private String username;

    private String sessionId;

    @Indexed
    private Long universityId;

    private String universityName;

    @Indexed
    private Long channelId;

    private String channelName;

    private Long contentId;

    private String contentType; // "post", "comment", "channel", "user", "message"

    // Event-specific data
    private Map<String, Object> eventData;

    // Device and client info
    private DeviceInfo deviceInfo;

    // Location info (from university, not actual geolocation for privacy)
    private String region;
    private String country;

    // Engagement metrics
    private Long duration; // Time spent in milliseconds
    private Double scrollDepth; // For page views
    private Integer clickCount;

    // Referrer info
    private String referrer;
    private String referrerType; // "direct", "search", "social", "internal"

    // Performance metrics
    private Long loadTime;
    private Long responseTime;

    @Indexed
    private LocalDateTime timestamp;

    private LocalDateTime processedAt;

    private Boolean isBot;

    private String abTestVariant;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfo {
        private String deviceType; // "mobile", "tablet", "desktop"
        private String os;
        private String osVersion;
        private String browser;
        private String browserVersion;
        private String appVersion;
        private String platform; // "web", "ios", "android"
        private Integer screenWidth;
        private Integer screenHeight;
    }

    public enum EventType {
        // User events
        LOGIN,
        LOGOUT,
        SIGNUP,
        PROFILE_UPDATE,
        PASSWORD_CHANGE,
        
        // Content events
        POST_VIEW,
        POST_CREATE,
        POST_EDIT,
        POST_DELETE,
        COMMENT_CREATE,
        COMMENT_EDIT,
        COMMENT_DELETE,
        
        // Engagement events
        UPVOTE,
        DOWNVOTE,
        SHARE,
        SAVE,
        UNSAVE,
        REPORT,
        
        // Navigation events
        PAGE_VIEW,
        SEARCH,
        CHANNEL_VIEW,
        USER_PROFILE_VIEW,
        
        // Social events
        FOLLOW,
        UNFOLLOW,
        MESSAGE_SEND,
        MESSAGE_READ,
        
        // System events
        NOTIFICATION_RECEIVED,
        NOTIFICATION_CLICKED,
        ERROR,
        PERFORMANCE
    }
}
