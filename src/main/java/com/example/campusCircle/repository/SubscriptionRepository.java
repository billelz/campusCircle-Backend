package com.example.campusCircle.repository;

import com.example.campusCircle.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByUserId(Long userId);
    
    List<Subscription> findByChannelId(Long channelId);
    
    Optional<Subscription> findByUserIdAndChannelId(Long userId, Long channelId);
    
    boolean existsByUserIdAndChannelId(Long userId, Long channelId);
    
    void deleteByUserIdAndChannelId(Long userId, Long channelId);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.channelId = :channelId")
    Long countByChannelId(@Param("channelId") Long channelId);
    
    List<Subscription> findByUserIdAndNotificationEnabledTrue(Long userId);
}
