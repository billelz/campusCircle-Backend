package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String verificationMethod;

    private String token;

    private LocalDateTime verifiedAt;

    private LocalDateTime expiresAt;
}
