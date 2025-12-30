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

    private Long contentId;

    @ManyToOne
    @JoinColumn(name = "reporter_user_id", insertable = false, updatable = false)
    private Users reporter;

    private Long reporterUserId;
    private String reason;
    private String status;

    @ManyToOne
    @JoinColumn(name = "resolved_by", referencedColumnName = "username", insertable = false, updatable = false)
    private Users resolver;

    private String resolvedBy;
    private String resolution;
}
