package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.UserActivity;
import com.example.campusCircle.service.UserActivityService;
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
@RequestMapping("/api/user-activity")
@CrossOrigin(origins = "*")
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserActivity> activities = userActivityService.getAllUserActivities();
            response.put("status", "success");
            response.put("message", "MongoDB UserActivity collection is working!");
            response.put("totalActivities", activities.size());
            response.put("data", activities);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserActivity>> getAllUserActivities() {
        return ResponseEntity.ok(userActivityService.getAllUserActivities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserActivity> getUserActivityById(@PathVariable String id) {
        return userActivityService.getUserActivityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserActivity> getUserActivityByUsername(@PathVariable String username) {
        return userActivityService.getUserActivityByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active-since")
    public ResponseEntity<List<UserActivity>> getActiveUsersSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(userActivityService.getActiveUsersSince(date));
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<UserActivity>> getUsersByChannel(@PathVariable String channel) {
        return ResponseEntity.ok(userActivityService.getUsersByChannel(channel));
    }

    @PostMapping
    public ResponseEntity<UserActivity> createUserActivity(@RequestBody UserActivity userActivity) {
        if (userActivity.getLastActive() == null) {
            userActivity.setLastActive(LocalDateTime.now());
        }
        UserActivity savedActivity = userActivityService.saveUserActivity(userActivity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedActivity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserActivity> updateUserActivity(
            @PathVariable String id,
            @RequestBody UserActivity userActivity) {
        userActivity.setId(id);
        UserActivity updatedActivity = userActivityService.saveUserActivity(userActivity);
        return ResponseEntity.ok(updatedActivity);
    }

    @PatchMapping("/update-active/{username}")
    public ResponseEntity<UserActivity> updateLastActive(@PathVariable String username) {
        UserActivity updated = userActivityService.updateLastActive(username);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserActivity(@PathVariable String id) {
        userActivityService.deleteUserActivity(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/username/{username}")
    public ResponseEntity<Void> deleteUserActivityByUsername(@PathVariable String username) {
        userActivityService.deleteUserActivityByUsername(username);
        return ResponseEntity.noContent().build();
    }
}
