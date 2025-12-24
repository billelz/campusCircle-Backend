package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.CommentContent;
import com.example.campusCircle.service.CommentContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment-content")
@CrossOrigin(origins = "*")
public class CommentContentController {

    @Autowired
    private CommentContentService commentContentService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CommentContent> comments = commentContentService.getAllComments();
            response.put("status", "success");
            response.put("message", "MongoDB CommentContent collection is working!");
            response.put("totalComments", comments.size());
            response.put("data", comments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<CommentContent>> getAllComments() {
        return ResponseEntity.ok(commentContentService.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentContent> getCommentById(@PathVariable String id) {
        return commentContentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<CommentContent> getCommentByCommentId(@PathVariable String commentId) {
        return commentContentService.getCommentByCommentId(commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommentContent>> searchComments(@RequestParam String keyword) {
        return ResponseEntity.ok(commentContentService.searchComments(keyword));
    }

    @PostMapping
    public ResponseEntity<CommentContent> createComment(@RequestBody CommentContent commentContent) {
        CommentContent savedComment = commentContentService.saveComment(commentContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentContent> updateComment(
            @PathVariable String id,
            @RequestBody CommentContent commentContent) {
        commentContent.setId(id);
        CommentContent updatedComment = commentContentService.saveComment(commentContent);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentContentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteCommentByCommentId(@PathVariable String commentId) {
        commentContentService.deleteCommentByCommentId(commentId);
        return ResponseEntity.noContent().build();
    }
}