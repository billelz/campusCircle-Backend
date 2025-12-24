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
@RequestMapping("/api/analytics-events")
@CrossOrigin(origins = "*")
public class AnalyticsEventController {

    @Autowired
    private AnalyticsEventService analyticsEventService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AnalyticsEvent> events = analyticsEventService.getAllEvents();
            response.put("status", "success");
            response.put("message", "MongoDB AnalyticsEvent collection is working!");
            response.put("totalEvents", events.size());
            response.put("data", events);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsEvent>> getAllEvents() {
        return ResponseEntity.ok(analyticsEventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsEvent> getEventById(@PathVariable String id) {
        return analyticsEventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByType(@PathVariable String eventType) {
        return ResponseEntity.ok(analyticsEventService.getEventsByType(eventType));
    }

    @GetMapping("/user/{usernameHashed}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByUser(@PathVariable String usernameHashed) {
        return ResponseEntity.ok(analyticsEventService.getEventsByUser(usernameHashed));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(analyticsEventService.getEventsByDateRange(start, end));
    }

    @GetMapping("/type/{eventType}/user/{usernameHashed}")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByTypeAndUser(
            @PathVariable String eventType,
            @PathVariable String usernameHashed) {
        return ResponseEntity.ok(analyticsEventService.getEventsByTypeAndUser(eventType, usernameHashed));
    }

    @GetMapping("/type/{eventType}/date-range")
    public ResponseEntity<List<AnalyticsEvent>> getEventsByTypeAndDateRange(
            @PathVariable String eventType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(analyticsEventService.getEventsByTypeAndDateRange(eventType, start, end));
    }

    @GetMapping("/count/{eventType}")
    public ResponseEntity<Map<String, Object>> countEventsByType(@PathVariable String eventType) {
        Map<String, Object> response = new HashMap<>();
        Long count = analyticsEventService.countEventsByType(eventType);
        response.put("eventType", eventType);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AnalyticsEvent> createEvent(@RequestBody AnalyticsEvent analyticsEvent) {
        AnalyticsEvent savedEvent = analyticsEventService.saveEvent(analyticsEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsEvent> updateEvent(
            @PathVariable String id,
            @RequestBody AnalyticsEvent analyticsEvent) {
        analyticsEvent.setId(id);
        AnalyticsEvent updatedEvent = analyticsEventService.saveEvent(analyticsEvent);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        analyticsEventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
