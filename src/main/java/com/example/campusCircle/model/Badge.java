package com.example.campusCircle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "badge_type", "channel_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "badge_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BadgeType badgeType;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    @Column(name = "channel_id")
    private Long channelId; // for channel-specific badges

    public enum BadgeType {
        TOP_CONTRIBUTOR,    // 🏆 top 1% karma
        HELPFUL,            // 💡 most upvoted answers
        CONSISTENT,         // 🎯 30-day posting streak
        COMMUNITY_HERO,     // 🌟 lots of helpful comments
        MODERATOR,          // 🛡️ moderator badge
        ACADEMIC,           // 📚 high karma in academic channels
        CREATIVE,           // 🎨 high karma in creative channels
        VERIFIED_STUDENT,   // ✓ verified student
        PIONEER,            // 🚀 early adopter
        MENTOR              // 👨‍🏫 helped many new users
    }

    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
    }
}
