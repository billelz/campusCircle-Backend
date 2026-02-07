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
    public ResponseEntity<SavedPost> getSavedPostsByUserId(@PathVariable Long userId) {
        return savedPostService.getSavedPostsByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<SavedPost> getSavedPostsByUsername(@PathVariable String username) {
        return savedPostService.getSavedPostsByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/folder/{folder}")
    public ResponseEntity<List<SavedPost.SavedItem>> getSavedItemsByFolder(
            @PathVariable Long userId,
            @PathVariable String folder) {
        return ResponseEntity.ok(savedPostService.getSavedItemsByFolder(userId, folder));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> isPostSaved(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("postId", postId);
        response.put("isSaved", savedPostService.isPostSaved(userId, postId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<SavedPost> savePost(@RequestBody SavePostRequest request) {
        SavedPost saved = savedPostService.savePost(
                request.getUserId(),
                request.getUsername(),
                request.getPostId(),
                request.getPostTitle(),
                request.getChannelId(),
                request.getChannelName(),
                request.getFolder()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/unsave")
    public ResponseEntity<SavedPost> unsavePost(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        SavedPost updated = savedPostService.unsavePost(userId, postId);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllSavedPosts(@PathVariable Long userId) {
        savedPostService.deleteSavedPosts(userId);
        return ResponseEntity.noContent().build();
    }

    // DTO
    public static class SavePostRequest {
        private Long userId;
        private String username;
        private Long postId;
        private String postTitle;
        private Long channelId;
        private String channelName;
        private String folder;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public String getPostTitle() { return postTitle; }
        public void setPostTitle(String postTitle) { this.postTitle = postTitle; }
        public Long getChannelId() { return channelId; }
        public void setChannelId(Long channelId) { this.channelId = channelId; }
        public String getChannelName() { return channelName; }
        public void setChannelName(String channelName) { this.channelName = channelName; }
        public String getFolder() { return folder; }
        public void setFolder(String folder) { this.folder = folder; }
    }
}
