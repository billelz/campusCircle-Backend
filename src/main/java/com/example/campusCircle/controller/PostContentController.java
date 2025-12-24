package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.PostContent;
import com.example.campusCircle.service.PostContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post-content")
@CrossOrigin(origins = "*")
public class PostContentController {

    @Autowired
    private PostContentService postContentService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PostContent> posts = postContentService.getAllPosts();
            response.put("status", "success");
            response.put("message", "MongoDB PostContent collection is working!");
            response.put("totalPosts", posts.size());
            response.put("data", posts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<PostContent>> getAllPosts() {
        return ResponseEntity.ok(postContentService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostContent> getPostById(@PathVariable String id) {
        return postContentService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostContent> getPostByPostId(@PathVariable String postId) {
        return postContentService.getPostByPostId(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PostContent>> getPopularPosts(
            @RequestParam(defaultValue = "100") Long minViews) {
        return ResponseEntity.ok(postContentService.getPopularPosts(minViews));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<PostContent>> getTrendingPosts(
            @RequestParam(defaultValue = "5.0") Double minScore) {
        return ResponseEntity.ok(postContentService.getTrendingPosts(minScore));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostContent>> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postContentService.searchPosts(keyword));
    }

    @PostMapping
    public ResponseEntity<PostContent> createPost(@RequestBody PostContent postContent) {
        PostContent savedPost = postContentService.savePost(postContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostContent> updatePost(
            @PathVariable String id,
            @RequestBody PostContent postContent) {
        postContent.setId(id);
        PostContent updatedPost = postContentService.savePost(postContent);
        return ResponseEntity.ok(updatedPost);
    }

    @PatchMapping("/increment-views/{postId}")
    public ResponseEntity<PostContent> incrementViewCount(@PathVariable String postId) {
        PostContent updated = postContentService.incrementViewCount(postId);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/update-score/{postId}")
    public ResponseEntity<PostContent> updateTrendingScore(
            @PathVariable String postId,
            @RequestParam Double score) {
        PostContent updated = postContentService.updateTrendingScore(postId, score);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        postContentService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deletePostByPostId(@PathVariable String postId) {
        postContentService.deletePostByPostId(postId);
        return ResponseEntity.noContent().build();
    }
}