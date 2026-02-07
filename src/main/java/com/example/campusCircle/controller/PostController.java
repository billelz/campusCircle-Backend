package com.example.campusCircle.controller;

import com.example.campusCircle.dto.PostRequest;
import com.example.campusCircle.dto.PostResponse;
import com.example.campusCircle.model.Post;
import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.model.nosql.PostContent;
import com.example.campusCircle.model.nosql.ModerationQueue;
import com.example.campusCircle.service.PostService;
import com.example.campusCircle.service.PostContentService;
import com.example.campusCircle.service.ChannelService;
import com.example.campusCircle.service.UsersService;
import com.example.campusCircle.service.AIService;
import com.example.campusCircle.service.ModerationQueueService;
import com.example.campusCircle.service.BadgeService;
import com.example.campusCircle.model.Badge;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostContentService postContentService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private AIService aiService;

    @Autowired
    private ModerationQueueService moderationQueueService;

    @Autowired
    private BadgeService badgeService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PostRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Combine title and content for toxicity check
            String contentToCheck = request.getTitle() + " " + (request.getContent() != null ? request.getContent() : "");
            Map<String, Object> moderationResult = aiService.analyzeContent(contentToCheck);
            
            boolean isToxic = (boolean) moderationResult.getOrDefault("isToxic", false);
            boolean isCrisis = (boolean) moderationResult.getOrDefault("isCrisis", false);
            boolean isSpam = (boolean) moderationResult.getOrDefault("isSpam", false);
            
            // If content is toxic, add to moderation queue and reject
            if (isToxic || isCrisis || isSpam) {
                // Create a temporary ID for the moderation queue
                String tempContentId = "pending_post_" + System.currentTimeMillis();
                
                ModerationQueue moderationItem = new ModerationQueue();
                moderationItem.setContentId(tempContentId);
                moderationItem.setContentType("post");
                moderationItem.setContentText(contentToCheck);
                moderationItem.setAuthorUsername(username);
                moderationItem.setFlaggedAt(LocalDateTime.now());
                moderationItem.setAiModerationScore((Double) moderationResult.getOrDefault("score", 0.0));
                moderationItem.setAiFlags((List<String>) moderationResult.get("flags"));
                moderationItem.setStatus("pending");
                
                moderationQueueService.saveModerationQueue(moderationItem);
                
                Map<String, String> error = new HashMap<>();
                if (isCrisis) {
                    error.put("error", "Your post contains content that may indicate distress. Please reach out to support services if you need help.");
                } else if (isSpam) {
                    error.put("error", "Your post has been flagged as potential spam and is under review.");
                } else {
                    error.put("error", "Your post contains content that violates community guidelines and has been sent for review.");
                }
                error.put("status", "pending_moderation");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Create the post metadata
            Post post = Post.builder()
                    .authorUsername(username)
                    .channelId(request.getChannelId())
                    .title(request.getTitle())
                    .upvoteCount(1) // Auto-upvote by author
                    .downvoteCount(0)
                    .commentCount(0)
                    .build();

            Post savedPost = postService.createPost(post);

            // Create the post content in MongoDB
            PostContent content = new PostContent();
            content.setPostId(String.valueOf(savedPost.getId()));
            content.setBodyText(request.getContent());
            content.setMediaUrls(request.getMediaUrls());
            postContentService.createPostContent(content);

            PostResponse response = mapToResponse(savedPost, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }
            
            PostContent content = postContentService.getPostContent(id);
            PostResponse response = mapToResponse(post, content);
            
            // Increment view count
            postContentService.incrementViewCount(id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        List<Post> posts = postService.getAllActivePosts();
        List<PostResponse> responses = posts.stream()
                .map(post -> {
                    PostContent content = postContentService.getPostContent(post.getId());
                    return mapToResponse(post, content);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<?> getByChannel(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        List<Post> posts = postService.getPostsByChannel(channelId);
        List<PostResponse> responses = posts.stream()
                .map(post -> {
                    PostContent content = postContentService.getPostContent(post.getId());
                    return mapToResponse(post, content);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getByUser(@PathVariable String username) {
        List<Post> posts = postService.getPostsByAuthor(username);
        List<PostResponse> responses = posts.stream()
                .map(post -> {
                    PostContent content = postContentService.getPostContent(post.getId());
                    return mapToResponse(post, content);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Post existingPost = postService.getPostById(id);
            if (existingPost == null) {
                return ResponseEntity.notFound().build();
            }

            if (!existingPost.getAuthorUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only edit your own posts"));
            }

            existingPost.setTitle(request.getTitle());
            Post updatedPost = postService.updatePost(id, existingPost);

            // Update content in MongoDB
            PostContent content = postContentService.getPostContent(id);
            if (content != null) {
                postContentService.updatePostContent(id, request.getContent(), request.getMediaUrls());
            }

            PostResponse response = mapToResponse(updatedPost, content);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Post post = postService.getPostById(id);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            if (!post.getAuthorUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete your own posts"));
            }

            postService.deletePost(id);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private PostResponse mapToResponse(Post post, PostContent content) {
        Channel channel = null;
        try {
            channel = channelService.getChannel(post.getChannelId());
        } catch (Exception ignored) {}

        // Get author's university name and badge for this channel
        String authorUniversityName = null;
        String authorBadge = null;
        try {
            Users author = usersService.getUserByUsername(post.getAuthorUsername());
            if (author != null) {
                if (author.getUniversity() != null) {
                    authorUniversityName = author.getUniversity().getName();
                }
                // Get user's badge for this channel
                List<Badge> channelBadges = badgeService.getUserBadgesForChannel(author.getId(), post.getChannelId());
                if (!channelBadges.isEmpty()) {
                    // Prioritize MODERATOR badge, otherwise take the first one
                    authorBadge = channelBadges.stream()
                            .filter(b -> b.getBadgeType() == Badge.BadgeType.MODERATOR)
                            .findFirst()
                            .map(b -> b.getBadgeType().name())
                            .orElse(channelBadges.get(0).getBadgeType().name());
                }
            }
        } catch (Exception ignored) {}

        return PostResponse.builder()
                .id(post.getId())
                .authorUsername(post.getAuthorUsername())
                .authorUniversityName(authorUniversityName)
                .authorBadge(authorBadge)
                .channelId(post.getChannelId())
                .channelName(channel != null ? channel.getName() : null)
                .channelCategory(channel != null ? channel.getCategory().name() : null)
                .title(post.getTitle())
                .content(content != null ? content.getBodyText() : null)
                .mediaUrls(content != null ? content.getMediaUrls() : null)
                .upvoteCount(post.getUpvoteCount())
                .downvoteCount(post.getDownvoteCount())
                .netScore(post.getNetScore())
                .commentCount(post.getCommentCount())
                .viewCount(content != null ? content.getViewCount() : 0L)
                .isPinned(post.getIsPinned())
                .isLocked(post.getIsLocked())
                .createdAt(post.getCreatedAt())
                .editedAt(post.getEditedAt())
                .build();
    }
}

