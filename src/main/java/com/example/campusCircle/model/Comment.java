package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "author_username", nullable = false)
    private String authorUsername;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "upvote_count")
    private Integer upvoteCount = 0;

    @Column(name = "downvote_count")
    private Integer downvoteCount = 0;

    @Column(name = "reply_count")
    private Integer replyCount = 0;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Transient
    public Integer getNetScore() {
        return (upvoteCount != null ? upvoteCount : 0) - (downvoteCount != null ? downvoteCount : 0);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        editedAt = LocalDateTime.now();
    }
}

