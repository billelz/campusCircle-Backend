package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.ModerationQueue;
import com.example.campusCircle.service.ModerationQueueService;
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
@RequestMapping("/api/moderation-queue")
@CrossOrigin(origins = "*")
public class ModerationQueueController {

    @Autowired
    private ModerationQueueService moderationQueueService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ModerationQueue> queue = moderationQueueService.getAllModerationQueue();
            response.put("status", "success");
            response.put("message", "MongoDB ModerationQueue collection is working!");
            response.put("totalItems", queue.size());
            response.put("data", queue);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<ModerationQueue>> getAllModerationQueue() {
        return ResponseEntity.ok(moderationQueueService.getAllModerationQueue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModerationQueue> getModerationQueueById(@PathVariable String id) {
        return moderationQueueService.getModerationQueueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/content/{contentId}")
    public ResponseEntity<ModerationQueue> getModerationQueueByContentId(@PathVariable String contentId) {
        return moderationQueueService.getModerationQueueByContentId(contentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ModerationQueue>> getModerationQueueByStatus(@PathVariable String status) {
        return ResponseEntity.ok(moderationQueueService.getModerationQueueByStatus(status));
    }

    @GetMapping("/type/{contentType}")
    public ResponseEntity<List<ModerationQueue>> getModerationQueueByContentType(@PathVariable String contentType) {
        return ResponseEntity.ok(moderationQueueService.getModerationQueueByContentType(contentType));
    }

    @GetMapping("/author/{authorUsername}")
    public ResponseEntity<List<ModerationQueue>> getModerationQueueByAuthor(@PathVariable String authorUsername) {
        return ResponseEntity.ok(moderationQueueService.getModerationQueueByAuthor(authorUsername));
    }

    @GetMapping("/score-above")
    public ResponseEntity<List<ModerationQueue>> getModerationQueueByScoreAbove(@RequestParam Double score) {
        return ResponseEntity.ok(moderationQueueService.getModerationQueueByScoreAbove(score));
    }

    @GetMapping("/flagged-after")
    public ResponseEntity<List<ModerationQueue>> getModerationQueueFlaggedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(moderationQueueService.getModerationQueueFlaggedAfter(date));
    }

    @GetMapping("/reviewer/{reviewedBy}")
    public ResponseEntity<List<ModerationQueue>> getModerationQueueByReviewer(@PathVariable String reviewedBy) {
        return ResponseEntity.ok(moderationQueueService.getModerationQueueByReviewer(reviewedBy));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ModerationQueue>> getModerationQueueByStatusAndType(
            @RequestParam String status,
            @RequestParam String contentType) {
        return ResponseEntity.ok(moderationQueueService.getModerationQueueByStatusAndType(status, contentType));
    }

    @PostMapping
    public ResponseEntity<ModerationQueue> createModerationQueue(@RequestBody ModerationQueue moderationQueue) {
        ModerationQueue savedQueue = moderationQueueService.saveModerationQueue(moderationQueue);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQueue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModerationQueue> updateModerationQueue(
            @PathVariable String id,
            @RequestBody ModerationQueue moderationQueue) {
        ModerationQueue updatedQueue = moderationQueueService.updateModerationQueue(id, moderationQueue);
        return ResponseEntity.ok(updatedQueue);
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<ModerationQueue> reviewContent(
            @PathVariable String id,
            @RequestParam String reviewedBy,
            @RequestParam String status,
            @RequestParam String action) {
        ModerationQueue reviewed = moderationQueueService.reviewContent(id, reviewedBy, status, action);
        if (reviewed != null) {
            return ResponseEntity.ok(reviewed);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModerationQueue(@PathVariable String id) {
        moderationQueueService.deleteModerationQueue(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/content/{contentId}")
    public ResponseEntity<Void> deleteModerationQueueByContentId(@PathVariable String contentId) {
        moderationQueueService.deleteModerationQueueByContentId(contentId);
        return ResponseEntity.noContent().build();
    }
}
