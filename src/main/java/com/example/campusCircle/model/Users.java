package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    @Column(name = "verification_status")
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;
    
    @Column(name = "real_name")
    private String realName;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "graduation_year")
    private Integer graduationYear;
    
    private String major;
    
    @Column(name = "total_karma")
    private Integer totalKarma;
    
    @Column(name = "post_karma")
    private Integer postKarma;
    
    @Column(name = "comment_karma")
    private Integer commentKarma;

    @Column(name = "profile_visibility")
    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility;

    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "is_banned")
    private Boolean isBanned;
    
    @Column(name = "ban_reason")
    private String banReason;
    
    @Column(name = "ban_expires_at")
    private LocalDateTime banExpiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }

    public enum ProfileVisibility {
        PUBLIC,       // Visible to everyone
        UNIVERSITY,   // Visible only to same university users
        PRIVATE       // Visible only to self
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isActive = true;
        isBanned = false;
        totalKarma = 0;
        postKarma = 0;
        commentKarma = 0;
        profileVisibility = ProfileVisibility.PUBLIC;
        verificationStatus = VerificationStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to recalculate total karma
    public void updateTotalKarma() {
        this.totalKarma = (this.postKarma != null ? this.postKarma : 0) 
                        + (this.commentKarma != null ? this.commentKarma : 0);
    }
}
