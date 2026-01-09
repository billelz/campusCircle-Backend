package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id")
    private Long contentId;

    @ManyToOne
    @JoinColumn(name = "reporter_user_id")
    private Users reporter;

    private String reason;
    private String status;

    @ManyToOne
    @JoinColumn(name = "resolved_by", referencedColumnName = "username")
    private Users resolvedBy;

    private String resolution;
}
