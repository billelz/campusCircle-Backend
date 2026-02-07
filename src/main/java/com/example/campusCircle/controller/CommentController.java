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
import com.example.campusCircle.model.Users;
import com.example.campusCircle.model.nosql.ModerationQueue;
import com.example.campusCircle.service.CommentService;
import com.example.campusCircle.service.CommentContentService;
import com.example.campusCircle.service.UsersService;
import com.example.campusCircle.service.AIService;
import com.example.campusCircle.service.ModerationQueueService;
import com.example.campusCircle.service.BadgeService;
import com.example.campusCircle.service.PostService;
import com.example.campusCircle.model.Badge;
import com.example.campusCircle.model.Post;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentContentService commentContentService;
    private final UsersService usersService;
    private final AIService aiService;
    private final ModerationQueueService moderationQueueService;
    private final BadgeService badgeService;
    private final PostService postService;

    public CommentController(CommentService commentService, CommentContentService commentContentService, 
            UsersService usersService, AIService aiService, ModerationQueueService moderationQueueService,
            BadgeService badgeService, PostService postService) {
        this.commentService = commentService;
        this.commentContentService = commentContentService;
        this.usersService = usersService;
        this.aiService = aiService;
        this.moderationQueueService = moderationQueueService;
        this.badgeService = badgeService;
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CommentRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            String username = auth.getName();
            
            // Check content for toxicity
            Map<String, Object> moderationResult = aiService.analyzeContent(request.getContent());
            
            boolean isToxic = (boolean) moderationResult.getOrDefault("isToxic", false);
            boolean isCrisis = (boolean) moderationResult.getOrDefault("isCrisis", false);
            boolean isSpam = (boolean) moderationResult.getOrDefault("isSpam", false);
            
            // If content is toxic, add to moderation queue and reject
            if (isToxic || isCrisis || isSpam) {
                String tempContentId = "pending_comment_" + System.currentTimeMillis();
                
                ModerationQueue moderationItem = new ModerationQueue();
                moderationItem.setContentId(tempContentId);
                moderationItem.setContentType("comment");
                moderationItem.setContentText(request.getContent());
                moderationItem.setAuthorUsername(username);
                moderationItem.setFlaggedAt(LocalDateTime.now());
                moderationItem.setAiModerationScore((Double) moderationResult.getOrDefault("score", 0.0));
                moderationItem.setAiFlags((List<String>) moderationResult.get("flags"));
                moderationItem.setStatus("pending");
                
                moderationQueueService.saveModerationQueue(moderationItem);
                
                Map<String, String> error = new HashMap<>();
                if (isCrisis) {
                    error.put("error", "Your comment contains content that may indicate distress. Please reach out to support services if you need help.");
                } else if (isSpam) {
                    error.put("error", "Your comment has been flagged as potential spam and is under review.");
                } else {
                    error.put("error", "Your comment contains content that violates community guidelines and has been sent for review.");
                }
                error.put("status", "pending_moderation");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Create comment metadata in PostgreSQL
            Comment comment = new Comment();
            comment.setPostId(request.getPostId());
            comment.setParentCommentId(request.getParentCommentId());
            comment.setAuthorUsername(username);
            
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

        // Get author's university name and badge for this channel
        String authorUniversityName = null;
        String authorBadge = null;
        Long channelId = null;
        try {
            // Get channelId from the post
            Post post = postService.getPostById(comment.getPostId());
            if (post != null) {
                channelId = post.getChannelId();
            }
            
            Users author = usersService.getUserByUsername(comment.getAuthorUsername());
            if (author != null) {
                if (author.getUniversity() != null) {
                    authorUniversityName = author.getUniversity().getName();
                }
                // Get user's badge for this channel
                if (channelId != null) {
                    List<Badge> channelBadges = badgeService.getUserBadgesForChannel(author.getId(), channelId);
                    if (!channelBadges.isEmpty()) {
                        // Prioritize MODERATOR badge, otherwise take the first one
                        authorBadge = channelBadges.stream()
                                .filter(b -> b.getBadgeType() == Badge.BadgeType.MODERATOR)
                                .findFirst()
                                .map(b -> b.getBadgeType().name())
                                .orElse(channelBadges.get(0).getBadgeType().name());
                    }
                }
            }
        } catch (Exception ignored) {}

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .channelId(channelId)
                .parentCommentId(comment.getParentCommentId())
                .authorUsername(comment.getAuthorUsername())
                .authorUniversityName(authorUniversityName)
                .authorBadge(authorBadge)
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
