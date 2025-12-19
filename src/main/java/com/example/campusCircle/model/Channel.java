package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String rules;

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    private Long createdBy;

    private Integer subscriberCount = 0;

    private java.time.LocalDateTime createdAt;
}
