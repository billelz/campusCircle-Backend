package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "content_id", "content_type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "vote_value", nullable = false)
    private Integer voteValue; // +1 for upvote, -1 for downvote

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ContentType {
        POST, COMMENT
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
