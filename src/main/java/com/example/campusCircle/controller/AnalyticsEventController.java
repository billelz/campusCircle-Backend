package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.AnalyticsEvent;
import com.example.campusCircle.service.AnalyticsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsEventController {

    @Autowired
    private AnalyticsEventService analyticsEventService;

    @GetMapping("/events")
    public ResponseEntity<List<AnalyticsEvent>> getAllEvents() {
        return ResponseEntity.ok(analyticsEventService.getAllEvents());
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<AnalyticsEvent> getEventById(@PathVariable String id) {
        return analyticsEventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/events/type/{eventType}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByType(@PathVariable String eventType) {
        return ResponseEntity.ok(analyticsEventService.getEventsByType(eventType));
    }

    @GetMapping("/events/category/{eventCategory}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByCategory(@PathVariable String eventCategory) {
        return ResponseEntity.ok(analyticsEventService.getEventsByCategory(eventCategory));
    }

    @GetMapping("/events/user/{userId}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(analyticsEventService.getEventsByUser(userId));
    }

    @GetMapping("/events/session/{sessionId}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsBySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(analyticsEventService.getEventsBySession(sessionId));
    }

    @GetMapping("/events/university/{universityId}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByUniversity(@PathVariable Long universityId) {
        return ResponseEntity.ok(analyticsEventService.getEventsByUniversity(universityId));
    }

    @GetMapping("/events/channel/{channelId}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByChannel(@PathVariable Long channelId) {
        return ResponseEntity.ok(analyticsEventService.getEventsByChannel(channelId));
    }

    @GetMapping("/events/content")
    public ResponseEntity<List<AnalyticsEvent>> getEventsForContent(
            @RequestParam Long contentId,
            @RequestParam String contentType) {
        return ResponseEntity.ok(analyticsEventService.getEventsForContent(contentId, contentType));
    }

    @GetMapping("/events/range")
    public ResponseEntity<List<AnalyticsEvent>> getEventsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(analyticsEventService.getEventsInRange(start, end));
    }

    @GetMapping("/count/{eventType}")
    public ResponseEntity<Map<String, Object>> countEventsByType(@PathVariable String eventType) {
        Map<String, Object> response = new HashMap<>();
        response.put("eventType", eventType);
        response.put("count", analyticsEventService.countEventsByType(eventType));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/{eventType}/range")
    public ResponseEntity<Map<String, Object>> countEventsByTypeInRange(
            @PathVariable String eventType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Map<String, Object> response = new HashMap<>();
        response.put("eventType", eventType);
        response.put("start", start);
        response.put("end", end);
        response.put("count", analyticsEventService.countEventsByTypeInRange(eventType, start, end));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getEventSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(analyticsEventService.getEventCounts(start, end));
    }

    @PostMapping("/events")
    public ResponseEntity<AnalyticsEvent> createEvent(@RequestBody AnalyticsEvent event) {
        AnalyticsEvent saved = analyticsEventService.saveEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/track")
    public ResponseEntity<AnalyticsEvent> trackEvent(@RequestBody TrackEventRequest request) {
        AnalyticsEvent event = analyticsEventService.trackEvent(
                request.getEventType(),
                request.getEventCategory(),
                request.getUserId(),
                request.getUsername(),
                request.getSessionId(),
                request.getUniversityId(),
                request.getChannelId(),
                request.getContentId(),
                request.getContentType(),
                request.getEventData()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/track/page-view")
    public ResponseEntity<AnalyticsEvent> trackPageView(@RequestBody PageViewRequest request) {
        AnalyticsEvent event = analyticsEventService.trackPageView(
                request.getUserId(),
                request.getUsername(),
                request.getSessionId(),
                request.getUniversityId(),
                request.getPageUrl(),
                request.getReferrer(),
                request.getDeviceInfo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/track/engagement")
    public ResponseEntity<AnalyticsEvent> trackEngagement(@RequestBody EngagementRequest request) {
        AnalyticsEvent event = analyticsEventService.trackContentEngagement(
                request.getEngagementType(),
                request.getUserId(),
                request.getUsername(),
                request.getContentId(),
                request.getContentType(),
                request.getChannelId(),
                request.getUniversityId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/track/search")
    public ResponseEntity<AnalyticsEvent> trackSearch(@RequestBody SearchRequest request) {
        AnalyticsEvent event = analyticsEventService.trackSearch(
                request.getUserId(),
                request.getUsername(),
                request.getSessionId(),
                request.getUniversityId(),
                request.getQuery(),
                request.getResultCount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        analyticsEventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanupOldEvents(
            @RequestParam(defaultValue = "365") int daysOld) {
        analyticsEventService.cleanupOldEvents(daysOld);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Old events cleaned up");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event-types")
    public ResponseEntity<AnalyticsEvent.EventType[]> getEventTypes() {
        return ResponseEntity.ok(AnalyticsEvent.EventType.values());
    }

    // DTOs
    public static class TrackEventRequest {
        private String eventType;
        private String eventCategory;
        private Long userId;
        private String username;
        private String sessionId;
        private Long universityId;
        private Long channelId;
        private Long contentId;
        private String contentType;
        private Map<String, Object> eventData;

        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getEventCategory() { return eventCategory; }
        public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Long getUniversityId() { return universityId; }
        public void setUniversityId(Long universityId) { this.universityId = universityId; }
        public Long getChannelId() { return channelId; }
        public void setChannelId(Long channelId) { this.channelId = channelId; }
        public Long getContentId() { return contentId; }
        public void setContentId(Long contentId) { this.contentId = contentId; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public Map<String, Object> getEventData() { return eventData; }
        public void setEventData(Map<String, Object> eventData) { this.eventData = eventData; }
    }

    public static class PageViewRequest {
        private Long userId;
        private String username;
        private String sessionId;
        private Long universityId;
        private String pageUrl;
        private String referrer;
        private AnalyticsEvent.DeviceInfo deviceInfo;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Long getUniversityId() { return universityId; }
        public void setUniversityId(Long universityId) { this.universityId = universityId; }
        public String getPageUrl() { return pageUrl; }
        public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }
        public String getReferrer() { return referrer; }
        public void setReferrer(String referrer) { this.referrer = referrer; }
        public AnalyticsEvent.DeviceInfo getDeviceInfo() { return deviceInfo; }
        public void setDeviceInfo(AnalyticsEvent.DeviceInfo deviceInfo) { this.deviceInfo = deviceInfo; }
    }

    public static class EngagementRequest {
        private String engagementType;
        private Long userId;
        private String username;
        private Long contentId;
        private String contentType;
        private Long channelId;
        private Long universityId;

        public String getEngagementType() { return engagementType; }
        public void setEngagementType(String engagementType) { this.engagementType = engagementType; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Long getContentId() { return contentId; }
        public void setContentId(Long contentId) { this.contentId = contentId; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public Long getChannelId() { return channelId; }
        public void setChannelId(Long channelId) { this.channelId = channelId; }
        public Long getUniversityId() { return universityId; }
        public void setUniversityId(Long universityId) { this.universityId = universityId; }
    }

    public static class SearchRequest {
        private Long userId;
        private String username;
        private String sessionId;
        private Long universityId;
        private String query;
        private int resultCount;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Long getUniversityId() { return universityId; }
        public void setUniversityId(Long universityId) { this.universityId = universityId; }
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public int getResultCount() { return resultCount; }
        public void setResultCount(int resultCount) { this.resultCount = resultCount; }
    }
}
