package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.AnalyticsEvent;
import com.example.campusCircle.repository.AnalyticsEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<AnalyticsEvent> getEventsByCategory(String eventCategory) {
        return analyticsEventRepository.findByEventCategory(eventCategory);
    }

    public List<AnalyticsEvent> getEventsByUser(Long userId) {
        return analyticsEventRepository.findByUserId(userId);
    }

    public List<AnalyticsEvent> getEventsBySession(String sessionId) {
        return analyticsEventRepository.findBySessionId(sessionId);
    }

    public List<AnalyticsEvent> getEventsByUniversity(Long universityId) {
        return analyticsEventRepository.findByUniversityId(universityId);
    }

    public List<AnalyticsEvent> getEventsByChannel(Long channelId) {
        return analyticsEventRepository.findByChannelId(channelId);
    }

    public List<AnalyticsEvent> getEventsForContent(Long contentId, String contentType) {
        return analyticsEventRepository.findByContentIdAndContentType(contentId, contentType);
    }

    public List<AnalyticsEvent> getEventsByTypeInRange(String eventType, LocalDateTime start, LocalDateTime end) {
        return analyticsEventRepository.findByEventTypeAndTimestampBetween(eventType, start, end);
    }

    public List<AnalyticsEvent> getEventsInRange(LocalDateTime start, LocalDateTime end) {
        return analyticsEventRepository.findByTimestampBetween(start, end);
    }

    public List<AnalyticsEvent> getNonBotEventsInRange(LocalDateTime start, LocalDateTime end) {
        return analyticsEventRepository.findByIsBotFalseAndTimestampBetween(start, end);
    }

    public Long countEventsByType(String eventType) {
        return analyticsEventRepository.countByEventType(eventType);
    }

    public Long countEventsByTypeInRange(String eventType, LocalDateTime start, LocalDateTime end) {
        return analyticsEventRepository.countByEventTypeAndTimestampBetween(eventType, start, end);
    }

    public AnalyticsEvent saveEvent(AnalyticsEvent event) {
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        return analyticsEventRepository.save(event);
    }

    public AnalyticsEvent trackEvent(String eventType, String eventCategory, Long userId, String username,
                                      String sessionId, Long universityId, Long channelId,
                                      Long contentId, String contentType, Map<String, Object> eventData) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType(eventType);
        event.setEventCategory(eventCategory);
        event.setUserId(userId);
        event.setUsername(username);
        event.setSessionId(sessionId);
        event.setUniversityId(universityId);
        event.setChannelId(channelId);
        event.setContentId(contentId);
        event.setContentType(contentType);
        event.setEventData(eventData);
        event.setTimestamp(LocalDateTime.now());
        event.setIsBot(false);

        return analyticsEventRepository.save(event);
    }

    public AnalyticsEvent trackPageView(Long userId, String username, String sessionId,
                                         Long universityId, String pageUrl, String referrer,
                                         AnalyticsEvent.DeviceInfo deviceInfo) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType("page_view");
        event.setEventCategory("navigation");
        event.setUserId(userId);
        event.setUsername(username);
        event.setSessionId(sessionId);
        event.setUniversityId(universityId);
        event.setReferrer(referrer);
        event.setDeviceInfo(deviceInfo);
        event.setTimestamp(LocalDateTime.now());
        event.setIsBot(false);

        Map<String, Object> data = new HashMap<>();
        data.put("pageUrl", pageUrl);
        event.setEventData(data);

        return analyticsEventRepository.save(event);
    }

    public AnalyticsEvent trackContentEngagement(String engagementType, Long userId, String username,
                                                   Long contentId, String contentType,
                                                   Long channelId, Long universityId) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType(engagementType); // "upvote", "downvote", "share", "save", etc.
        event.setEventCategory("engagement");
        event.setUserId(userId);
        event.setUsername(username);
        event.setContentId(contentId);
        event.setContentType(contentType);
        event.setChannelId(channelId);
        event.setUniversityId(universityId);
        event.setTimestamp(LocalDateTime.now());
        event.setIsBot(false);

        return analyticsEventRepository.save(event);
    }

    public AnalyticsEvent trackSearch(Long userId, String username, String sessionId,
                                        Long universityId, String query, int resultCount) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType("search");
        event.setEventCategory("navigation");
        event.setUserId(userId);
        event.setUsername(username);
        event.setSessionId(sessionId);
        event.setUniversityId(universityId);
        event.setTimestamp(LocalDateTime.now());
        event.setIsBot(false);

        Map<String, Object> data = new HashMap<>();
        data.put("query", query);
        data.put("resultCount", resultCount);
        event.setEventData(data);

        return analyticsEventRepository.save(event);
    }

    public Map<String, Long> getEventCounts(LocalDateTime start, LocalDateTime end) {
        Map<String, Long> counts = new HashMap<>();
        List<AnalyticsEvent> events = analyticsEventRepository.findByTimestampBetween(start, end);
        
        for (AnalyticsEvent event : events) {
            counts.merge(event.getEventType(), 1L, Long::sum);
        }
        
        return counts;
    }

    public void deleteEvent(String id) {
        analyticsEventRepository.deleteById(id);
    }

    public void cleanupOldEvents(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        analyticsEventRepository.deleteByTimestampBefore(cutoff);
    }

    public void deleteUserEvents(Long userId, int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        analyticsEventRepository.deleteByUserIdAndTimestampBefore(userId, cutoff);
    }
}
