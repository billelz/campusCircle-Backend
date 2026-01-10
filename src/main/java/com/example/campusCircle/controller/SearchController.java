package com.example.campusCircle.controller;

import com.example.campusCircle.dto.ChannelResponse;
import com.example.campusCircle.dto.PostResponse;
import com.example.campusCircle.dto.UserProfileResponse;
import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Post;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.model.Users.VerificationStatus;
import com.example.campusCircle.model.nosql.PostContent;
import com.example.campusCircle.service.ChannelService;
import com.example.campusCircle.service.PostService;
import com.example.campusCircle.service.PostContentService;
import com.example.campusCircle.service.UsersService;
import com.example.campusCircle.service.ChannelSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostContentService postContentService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ChannelSubscriptionService subscriptionService;

    /**
     * Unified search endpoint - searches channels, posts, and users
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> results = new HashMap<>();
        String currentUser = getCurrentUsername();

        if (q == null || q.trim().isEmpty()) {
            results.put("channels", List.of());
            results.put("posts", List.of());
            results.put("users", List.of());
            return ResponseEntity.ok(results);
        }

        String query = q.trim();

        if ("all".equals(type) || "channels".equals(type)) {
            List<Channel> channels = channelService.searchChannels(query);
            List<ChannelResponse> channelResponses = channels.stream()
                    .limit(limit)
                    .map(c -> mapChannelToResponse(c, currentUser))
                    .collect(Collectors.toList());
            results.put("channels", channelResponses);
        }

        if ("all".equals(type) || "posts".equals(type)) {
            List<Post> posts = postService.searchPosts(query);
            List<PostResponse> postResponses = posts.stream()
                    .limit(limit)
                    .map(this::mapPostToResponse)
                    .collect(Collectors.toList());
            results.put("posts", postResponses);
        }

        if ("all".equals(type) || "users".equals(type)) {
            List<Users> users = usersService.searchUsers(query);
            List<UserProfileResponse> userResponses = users.stream()
                    .filter(Users::getIsActive)
                    .limit(limit)
                    .map(this::mapUserToResponse)
                    .collect(Collectors.toList());
            results.put("users", userResponses);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Search only channels
     */
    @GetMapping("/channels")
    public ResponseEntity<List<ChannelResponse>> searchChannels(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        
        String currentUser = getCurrentUsername();
        List<Channel> channels = channelService.searchChannels(q);
        
        List<ChannelResponse> responses = channels.stream()
                .limit(limit)
                .map(c -> mapChannelToResponse(c, currentUser))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Search only posts
     */
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> searchPosts(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<Post> posts = postService.searchPosts(q);
        
        List<PostResponse> responses = posts.stream()
                .limit(limit)
                .map(this::mapPostToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Search only users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<Users> users = usersService.searchUsers(q);
        
        List<UserProfileResponse> responses = users.stream()
                .filter(u -> u.getIsActive() == null || u.getIsActive())
                .limit(limit)
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }

    private ChannelResponse mapChannelToResponse(Channel channel, String currentUser) {
        boolean isSubscribed = false;
        if (currentUser != null) {
            isSubscribed = subscriptionService.isSubscribed(currentUser, channel.getId());
        }

        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .rules(channel.getRules())
                .category(channel.getCategory() != null ? channel.getCategory().name() : "GENERAL")
                .subscriberCount(channel.getSubscriberCount())
                .universityId(channel.getUniversityId())
                .createdByUsername(channel.getCreatedBy())
                .createdAt(channel.getCreatedAt())
                .isSubscribed(isSubscribed)
                .build();
    }

    private PostResponse mapPostToResponse(Post post) {
        PostContent content = null;
        try {
            content = postContentService.getPostContent(post.getId());
        } catch (Exception ignored) {}

        Channel channel = null;
        try {
            channel = channelService.getChannel(post.getChannelId());
        } catch (Exception ignored) {}

        return PostResponse.builder()
                .id(post.getId())
                .authorUsername(post.getAuthorUsername())
                .channelId(post.getChannelId())
                .channelName(channel != null ? channel.getName() : null)
                .title(post.getTitle())
                .content(content != null ? content.getBodyText() : null)
                .mediaUrls(content != null ? content.getMediaUrls() : null)
                .upvoteCount(post.getUpvoteCount() != null ? post.getUpvoteCount() : 0)
                .downvoteCount(post.getDownvoteCount() != null ? post.getDownvoteCount() : 0)
                .netScore((post.getUpvoteCount() != null ? post.getUpvoteCount() : 0) - 
                         (post.getDownvoteCount() != null ? post.getDownvoteCount() : 0))
                .commentCount(post.getCommentCount() != null ? post.getCommentCount() : 0)
                .isPinned(post.getIsPinned())
                .isLocked(post.getIsLocked())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private UserProfileResponse mapUserToResponse(Users user) {
        Long universityId = user.getUniversity() != null ? user.getUniversity().getId() : null;
        boolean isVerified = user.getVerificationStatus() != null && 
                             user.getVerificationStatus() == VerificationStatus.VERIFIED;
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .realName(user.getRealName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .universityId(universityId)
                .universityName(user.getUniversity() != null ? user.getUniversity().getName() : null)
                .graduationYear(user.getGraduationYear())
                .major(user.getMajor())
                .totalKarma(user.getTotalKarma() != null ? user.getTotalKarma() : 0)
                .postKarma(user.getPostKarma() != null ? user.getPostKarma() : 0)
                .commentKarma(user.getCommentKarma() != null ? user.getCommentKarma() : 0)
                .isVerified(isVerified)
                .profileVisibility(user.getProfileVisibility())
                .isOnline(false)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
