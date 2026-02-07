package com.example.campusCircle.controller;

import com.example.campusCircle.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/universities")
public class UniversityAnalyticsController {

    @Autowired
    private UniversityService universityService;

    /**
     * Get AI-powered analytics for a specific university.
     * Features: Sentiment analysis, campus pulse, and toxicity trends.
     */
    @GetMapping("/{id}/analytics")
    public ResponseEntity<Map<String, Double>> getUniversityAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(universityService.getUniversityInsights(id));
    }

    /**
     * Trigger a manual AI re-scan of recent campus activity.
     */
    @PostMapping("/{id}/analytics/recalculate")
    public ResponseEntity<String> recalculateAnalytics(@PathVariable Long id) {
        // Implementation for triggering background task
        return ResponseEntity.ok("AI analytics recalculation started for university ID: " + id);
    }
}
