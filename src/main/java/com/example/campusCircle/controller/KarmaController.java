package com.example.campusCircle.controller;

import com.example.campusCircle.model.Karma;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.service.KarmaService;
import com.example.campusCircle.service.PostService;
import com.example.campusCircle.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/karma")
@CrossOrigin(origins = "*")
public class KarmaController {

    @Autowired
    private KarmaService karmaService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PostService postService;

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
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard() {
        // Get top contributors based on most upvoted posts
        List<Object[]> topContributors = postService.getTopContributorsByUpvotes();
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        
        for (Object[] row : topContributors) {
            try {
                String username = (String) row[0];
                Long totalUpvotes = ((Number) row[1]).longValue();
                
                Users user = usersService.getUserByUsername(username);
                Map<String, Object> entry = new HashMap<>();
                entry.put("id", user.getId());
                entry.put("username", user.getUsername());
                entry.put("profilePictureUrl", user.getProfilePictureUrl());
                entry.put("totalUpvotes", totalUpvotes);
                
                // Also include karma info if available
                try {
                    Karma karma = karmaService.getKarmaByUserId(user.getId()).orElse(null);
                    if (karma != null) {
                        entry.put("totalKarma", karma.getKarmaScore());
                        entry.put("postKarma", karma.getPostKarma());
                        entry.put("commentKarma", karma.getCommentKarma());
                    }
                } catch (Exception ignored) {}
                
                leaderboard.add(entry);
            } catch (Exception ignored) {
                // Skip users that don't exist
            }
        }
        
        return ResponseEntity.ok(leaderboard);
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
