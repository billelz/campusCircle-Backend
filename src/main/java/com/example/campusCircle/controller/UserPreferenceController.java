package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.UserPreference;
import com.example.campusCircle.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-preferences")
@CrossOrigin(origins = "*")
public class UserPreferenceController {

    @Autowired
    private UserPreferenceService userPreferenceService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserPreference> preferences = userPreferenceService.getAllUserPreferences();
            response.put("status", "success");
            response.put("message", "MongoDB connection is working!");
            response.put("totalPreferences", preferences.size());
            response.put("data", preferences);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserPreference>> getAllUserPreferences() {
        return ResponseEntity.ok(userPreferenceService.getAllUserPreferences());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserPreference> getUserPreferenceByUsername(@PathVariable String username) {
        return userPreferenceService.getUserPreferenceByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserPreference> createUserPreference(@RequestBody UserPreference userPreference) {
        UserPreference savedPreference = userPreferenceService.saveUserPreference(userPreference);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPreference);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserPreference> updateUserPreference(
            @PathVariable String id,
            @RequestBody UserPreference userPreference) {
        userPreference.setId(id);
        UserPreference updatedPreference = userPreferenceService.saveUserPreference(userPreference);
        return ResponseEntity.ok(updatedPreference);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserPreference(@PathVariable String id) {
        userPreferenceService.deleteUserPreference(id);
        return ResponseEntity.noContent().build();
    }
}
