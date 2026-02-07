package com.example.campusCircle.repository;

import com.example.campusCircle.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    List<Badge> findByUserId(Long userId);
    
    Optional<Badge> findByUserIdAndBadgeType(Long userId, Badge.BadgeType badgeType);
    
    Optional<Badge> findByUserIdAndBadgeTypeAndChannelId(Long userId, Badge.BadgeType badgeType, Long channelId);
    
    List<Badge> findByUserIdAndChannelId(Long userId, Long channelId);
    
    List<Badge> findByBadgeType(Badge.BadgeType badgeType);
    
    List<Badge> findByChannelId(Long channelId);
    
    boolean existsByUserIdAndBadgeType(Long userId, Badge.BadgeType badgeType);
    
    boolean existsByUserIdAndBadgeTypeAndChannelId(Long userId, Badge.BadgeType badgeType, Long channelId);
}
