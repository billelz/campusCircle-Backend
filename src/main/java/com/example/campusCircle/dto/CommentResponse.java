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
public class CommentResponse {

    private Long id;
    private Long postId;
    private String authorUsername;
    private Long parentCommentId;
    private String content;
    private List<String> mediaUrls;
    
    private Integer upvoteCount;
    private Integer downvoteCount;
    private Integer netScore;
    private Integer replyCount;
    private Integer userVote; // 1, -1, or null
    
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    
    private List<CommentResponse> replies;
}
