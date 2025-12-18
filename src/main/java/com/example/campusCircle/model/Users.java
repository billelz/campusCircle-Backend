package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String username;
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    private String verificationStatus;
    private String realName;
    private String profileVisibilitySettings;

    private java.time.LocalDateTime createdAt;
}
