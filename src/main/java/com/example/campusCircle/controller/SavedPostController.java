package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.SavedPost;
import com.example.campusCircle.service.SavedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved-posts")
@CrossOrigin(origins = "*")
public class SavedPostController {

    @Autowired
    private SavedPostService savedPostService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SavedPost> savedPosts = savedPostService.getAllSavedPosts();
            response.put("status", "success");
            response.put("message", "MongoDB SavedPost collection is working!");
            response.put("totalSavedPosts", savedPosts.size());
            response.put("data", savedPosts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<SavedPost>> getAllSavedPosts() {
        return ResponseEntity.ok(savedPostService.getAllSavedPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedPost> getSavedPostById(@PathVariable String id) {
        return savedPostService.getSavedPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<SavedPost> getSavedPostsByUserId(@PathVariable String userId) {
        return savedPostService.getSavedPostsByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}/users")
    public ResponseEntity<List<SavedPost>> getUsersWhoSavedPost(@PathVariable String postId) {
        return ResponseEntity.ok(savedPostService.getUsersWhoSavedPost(postId));
    }

    @GetMapping("/user/{userId}/post/{postId}/check")
    public ResponseEntity<Map<String, Boolean>> checkIfPostIsSaved(
            @PathVariable String userId,
            @PathVariable String postId) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isSaved = savedPostService.isPostSavedByUser(userId, postId);
        response.put("isSaved", isSaved);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SavedPost> createSavedPost(@RequestBody SavedPost savedPost) {
        SavedPost saved = savedPostService.savePost(savedPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<SavedPost> addPostToUserSaved(
            @PathVariable String userId,
            @PathVariable String postId) {
        SavedPost savedPost = savedPostService.addPostToUserSaved(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @DeleteMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<SavedPost> removePostFromUserSaved(
            @PathVariable String userId,
            @PathVariable String postId) {
        SavedPost savedPost = savedPostService.removePostFromUserSaved(userId, postId);
        if (savedPost == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedPost> updateSavedPost(
            @PathVariable String id,
            @RequestBody SavedPost savedPost) {
        savedPost.setId(id);
        SavedPost updatedSavedPost = savedPostService.savePost(savedPost);
        return ResponseEntity.ok(updatedSavedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavedPost(@PathVariable String id) {
        savedPostService.deleteSavedPost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteSavedPostsByUserId(@PathVariable String userId) {
        savedPostService.deleteSavedPostsByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
