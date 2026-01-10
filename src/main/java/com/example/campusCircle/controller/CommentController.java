package com.example.campusCircle.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.campusCircle.dto.CommentRequest;
import com.example.campusCircle.dto.CommentResponse;
import com.example.campusCircle.model.Comment;
import com.example.campusCircle.model.CommentContent;
import com.example.campusCircle.service.CommentService;
import com.example.campusCircle.service.CommentContentService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentContentService commentContentService;

    public CommentController(CommentService commentService, CommentContentService commentContentService) {
        this.commentService = commentService;
        this.commentContentService = commentContentService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CommentRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            // Create comment metadata in PostgreSQL
            Comment comment = new Comment();
            comment.setPostId(request.getPostId());
            comment.setParentCommentId(request.getParentCommentId());
            comment.setAuthorUsername(auth.getName());
            
            Comment saved = commentService.createComment(comment);
            
            // Store content in MongoDB
            CommentContent content = new CommentContent();
            content.setCommentId(saved.getId());
            content.setContent(request.getContent());
            content.setMediaUrls(request.getMediaUrls());
            commentContentService.createCommentContent(content);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            return ResponseEntity.ok(mapToResponse(comment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getCommentsByPost(postId, pageable);
        
        List<CommentResponse> responses = comments.getContent().stream()
                .map(this::mapToResponseWithReplies)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable Long id) {
        List<Comment> replies = commentService.getReplies(id);
        
        List<CommentResponse> responses = replies.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentResponse>> getByUser(@PathVariable String username) {
        List<Comment> comments = commentService.getCommentsByUser(username);
        
        List<CommentResponse> responses = comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CommentRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            Comment existing = commentService.getCommentById(id);
            if (!existing.getAuthorUsername().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only author can edit comment");
            }

            // Update metadata
            Comment updated = commentService.updateComment(id, request.getContent());
            
            // Update content in MongoDB
            commentContentService.updateCommentContent(id, request.getContent(), request.getMediaUrls());
            
            return ResponseEntity.ok(mapToResponse(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            Comment existing = commentService.getCommentById(id);
            if (!existing.getAuthorUsername().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only author can delete comment");
            }

            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private CommentResponse mapToResponse(Comment comment) {
        CommentContent content = null;
        try {
            content = commentContentService.getCommentContent(comment.getId());
        } catch (Exception e) {
            // Content not found
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .parentCommentId(comment.getParentCommentId())
                .authorUsername(comment.getAuthorUsername())
                .content(content != null ? content.getContent() : "[deleted]")
                .mediaUrls(content != null ? content.getMediaUrls() : null)
                .upvoteCount(comment.getUpvoteCount())
                .downvoteCount(comment.getDownvoteCount())
                .replyCount(comment.getReplyCount())
                .isDeleted(comment.getIsDeleted())
                .createdAt(comment.getCreatedAt())
                .editedAt(comment.getEditedAt())
                .build();
    }

    private CommentResponse mapToResponseWithReplies(Comment comment) {
        CommentResponse response = mapToResponse(comment);
        
        // Load replies (first level only)
        List<Comment> replies = commentService.getReplies(comment.getId());
        List<CommentResponse> replyResponses = replies.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        response.setReplies(replyResponses);
        
        return response;
    }
}
