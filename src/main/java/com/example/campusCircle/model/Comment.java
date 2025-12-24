package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    
    private Post post;

    private String authorUsername;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    private LocalDateTime createdAt;

}
