package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String rules;

    @Column(name = "university_id")
    private Long universityId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "subscriber_count")
    private Integer subscriberCount = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelCategory category = ChannelCategory.GENERAL;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    public enum ChannelCategory {
        ACADEMICS,
        MENTAL_HEALTH,
        CAREER,
        CAMPUS_LIFE,
        SOCIAL,
        ENTERTAINMENT,
        MARKETPLACE,
        META,
        GENERAL
    }

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
