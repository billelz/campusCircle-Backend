package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "bans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long bannedBy;

    private String reason;

    private Integer duration;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
}