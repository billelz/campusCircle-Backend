package com.example.campusCircle.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trending_cache")
public class TrendingCache {

    @Id
    private String id;

    @Indexed
    private String cacheType; // "posts", "channels", "topics", "hashtags"

    @Indexed
    private Long universityId;

    private String universityName;

    private List<TrendingItem> items;

    private String timeframe; // "hourly", "daily", "weekly"

    private LocalDateTime computedAt;

    @Indexed
    private LocalDateTime expiresAt;

    private Integer totalItems;

    private ComputationMetadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendingItem {
        private Long itemId;
        private String itemType; // "post", "channel", "hashtag"
        private String title;
        private String preview;
        private Double trendingScore;
        private Long engagementCount;
        private Long viewCount;
        private Integer rank;
        private Double velocityScore; // How fast it's trending
        private String imageUrl;
        private Long channelId;
        private String channelName;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComputationMetadata {
        private Long postsAnalyzed;
        private Long engagementsProcessed;
        private Double algorithmVersion;
        private Long computationTimeMs;
    }
}
