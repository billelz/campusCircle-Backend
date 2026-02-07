package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentId;
    
    @Column(name = "content_type")
    private String contentType; // "POST" or "COMMENT"
    
    private Long reporterUserId;
    
    @Column(name = "reporter_username")
    private String reporterUsername;
    
    private String reason;
    
    private String description;
    
    private String status; // "PENDING", "REVIEWED", "RESOLVED", "DISMISSED"
    
    private String resolvedBy;
    
    private String resolution;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}
