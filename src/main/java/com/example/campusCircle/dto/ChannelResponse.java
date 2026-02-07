package com.example.campusCircle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelResponse {

    private Long id;
    private String name;
    private String description;
    private String rules;
    private Long universityId;
    private String universityName;
    private Long createdBy;
    private String createdByUsername;
    private Integer subscriberCount;
    private String category;
    private Boolean isActive;
    private Boolean isSubscribed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
