package com.example.campusCircle.dto;

import com.example.campusCircle.model.Users.ProfileVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String realName;
    private String profilePictureUrl;
    private String bio;
    private Long universityId;
    private String universityName;
    private Integer graduationYear;
    private String major;
    private Boolean isVerified;
    
    // Karma stats
    private Integer totalKarma;
    private Integer postKarma;
    private Integer commentKarma;
    
    // Badges
    private List<String> badges;
    
    // Activity stats
    private Integer postCount;
    private Integer commentCount;
    
    private ProfileVisibility profileVisibility;
    private LocalDateTime createdAt;
    private Boolean isOnline;
    private LocalDateTime lastActive;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BadgeInfo {
        private String type;
        private String name;
        private String description;
        private String icon;
        private LocalDateTime earnedAt;
    }
}
