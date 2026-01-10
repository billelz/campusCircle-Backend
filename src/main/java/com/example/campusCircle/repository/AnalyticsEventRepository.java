package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.AnalyticsEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends MongoRepository<AnalyticsEvent, String> {

    List<AnalyticsEvent> findByEventType(String eventType);

    List<AnalyticsEvent> findByEventCategory(String eventCategory);

    List<AnalyticsEvent> findByUserId(Long userId);

    List<AnalyticsEvent> findBySessionId(String sessionId);

    List<AnalyticsEvent> findByUniversityId(Long universityId);

    List<AnalyticsEvent> findByChannelId(Long channelId);

    List<AnalyticsEvent> findByContentIdAndContentType(Long contentId, String contentType);

    List<AnalyticsEvent> findByEventTypeAndTimestampBetween(String eventType, LocalDateTime start, LocalDateTime end);

    List<AnalyticsEvent> findByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<AnalyticsEvent> findByUniversityIdAndTimestampBetween(Long universityId, LocalDateTime start, LocalDateTime end);

    List<AnalyticsEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AnalyticsEvent> findByIsBotFalseAndTimestampBetween(LocalDateTime start, LocalDateTime end);

    Long countByEventType(String eventType);

    Long countByEventTypeAndTimestampBetween(String eventType, LocalDateTime start, LocalDateTime end);

    Long countByUniversityIdAndEventTypeAndTimestampBetween(Long universityId, String eventType, LocalDateTime start, LocalDateTime end);

    void deleteByTimestampBefore(LocalDateTime dateTime);

    void deleteByUserIdAndTimestampBefore(Long userId, LocalDateTime dateTime);
}
