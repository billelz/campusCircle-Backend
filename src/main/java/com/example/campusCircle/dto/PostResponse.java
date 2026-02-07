package com.example.campusCircle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private String authorUsername;
    private Long channelId;
    private String channelName;
    private String channelCategory;
    private String title;
    private String content;
    private List<String> mediaUrls;
    private PollData poll;
    private String linkUrl;
    private String linkMetadata;
    
    private Integer upvoteCount;
    private Integer downvoteCount;
    private Integer netScore;
    private Integer commentCount;
    private Long viewCount;
    
    private Boolean isPinned;
    private Boolean isLocked;
    private Boolean isSaved;
    private Integer userVote; // 1, -1, or null
    
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PollData {
        private String question;
        private List<PollOption> options;
        private Integer totalVotes;
        private LocalDateTime endsAt;
        private Boolean hasVoted;
        private Integer userVotedOption;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PollOption {
        private Integer index;
        private String text;
        private Integer votes;
        private Double percentage;
    }
}
