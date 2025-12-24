package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.AnalyticsEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends MongoRepository<AnalyticsEvent, String> {

    List<AnalyticsEvent> findByEventType(String eventType);

    List<AnalyticsEvent> findByUsernameHashed(String usernameHashed);

    List<AnalyticsEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AnalyticsEvent> findByEventTypeAndUsernameHashed(String eventType, String usernameHashed);

    List<AnalyticsEvent> findByEventTypeAndTimestampBetween(String eventType, LocalDateTime start, LocalDateTime end);

    Long countByEventType(String eventType);
}
