package com.example.campusCircle.controller;

import com.example.campusCircle.model.Karma;
import com.example.campusCircle.service.KarmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/karma")
@CrossOrigin(origins = "*")
public class KarmaController {

    @Autowired
    private KarmaService karmaService;

    @GetMapping
    public ResponseEntity<List<Karma>> getAllKarma() {
        return ResponseEntity.ok(karmaService.getAllKarma());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Karma> getKarmaById(@PathVariable Long id) {
        return karmaService.getKarmaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Karma> getKarmaByUserId(@PathVariable Long userId) {
        return karmaService.getKarmaByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/score")
    public ResponseEntity<Map<String, Object>> getUserKarmaScore(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("karmaScore", karmaService.getUserKarmaScore(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Karma>> getLeaderboard() {
        return ResponseEntity.ok(karmaService.getLeaderboard());
    }

    @GetMapping("/top/{limit}")
    public ResponseEntity<List<Karma>> getTopKarmaUsers(@PathVariable int limit) {
        return ResponseEntity.ok(karmaService.getTopKarmaUsers(limit));
    }

    @GetMapping("/user/{userId}/permissions")
    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("karmaScore", karmaService.getUserKarmaScore(userId));
        response.put("canCreatePolls", karmaService.canCreatePolls(userId));
        response.put("canCreateChannels", karmaService.canCreateChannels(userId));
        response.put("isModeratorEligible", karmaService.isModeratorEligible(userId));
        response.put("thresholds", Map.of(
                "createPolls", KarmaService.THRESHOLD_CREATE_POLLS,
                "createChannels", KarmaService.THRESHOLD_CREATE_CHANNELS,
                "moderatorEligible", KarmaService.THRESHOLD_MODERATOR_ELIGIBLE
        ));
        return ResponseEntity.ok(response);
    }
}
