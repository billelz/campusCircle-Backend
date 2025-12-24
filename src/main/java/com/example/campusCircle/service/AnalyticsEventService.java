package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.AnalyticsEvent;
import com.example.campusCircle.repository.AnalyticsEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsEventService {

    @Autowired
    private AnalyticsEventRepository analyticsEventRepository;

    public List<AnalyticsEvent> getAllEvents() {
        return analyticsEventRepository.findAll();
    }

    public Optional<AnalyticsEvent> getEventById(String id) {
        return analyticsEventRepository.findById(id);
    }

    public List<AnalyticsEvent> getEventsByType(String eventType) {
        return analyticsEventRepository.findByEventType(eventType);
    }

    public List<AnalyticsEvent> getEventsByUser(String usernameHashed) {
        return analyticsEventRepository.findByUsernameHashed(usernameHashed);
    }

    public List<AnalyticsEvent> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return analyticsEventRepository.findByTimestampBetween(start, end);
    }

    public List<AnalyticsEvent> getEventsByTypeAndUser(String eventType, String usernameHashed) {
        return analyticsEventRepository.findByEventTypeAndUsernameHashed(eventType, usernameHashed);
    }

    public List<AnalyticsEvent> getEventsByTypeAndDateRange(String eventType, LocalDateTime start, LocalDateTime end) {
        return analyticsEventRepository.findByEventTypeAndTimestampBetween(eventType, start, end);
    }

    public Long countEventsByType(String eventType) {
        return analyticsEventRepository.countByEventType(eventType);
    }

    public AnalyticsEvent saveEvent(AnalyticsEvent analyticsEvent) {
        if (analyticsEvent.getTimestamp() == null) {
            analyticsEvent.setTimestamp(LocalDateTime.now());
        }
        return analyticsEventRepository.save(analyticsEvent);
    }

    public void deleteEvent(String id) {
        analyticsEventRepository.deleteById(id);
    }
}
