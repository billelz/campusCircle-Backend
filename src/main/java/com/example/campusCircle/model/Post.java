package com.example.campusCircle.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorUsername;
    private Long channelId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private LocalDateTime deletedAt;

}

