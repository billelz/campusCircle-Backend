package com.example.campusCircle.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.campusCircle.dto.UserProfileResponse;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.service.UsersService;
import com.example.campusCircle.service.BadgeService;
import com.example.campusCircle.repository.PostRepository;
import com.example.campusCircle.repository.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;
    private final BadgeService badgeService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UsersController(UsersService usersService, BadgeService badgeService,
                          PostRepository postRepository, CommentRepository commentRepository) {
        this.usersService = usersService;
        this.badgeService = badgeService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        try {
            Users user = usersService.getUserByUsername(auth.getName());
            return ResponseEntity.ok(mapToResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            Users user = usersService.getUser(id);
            
            // Check visibility settings
            String currentUsername = getCurrentUsername();
            if (!canViewProfile(user, currentUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Profile is private");
            }
            
            return ResponseEntity.ok(mapToResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable String username) {
        try {
            Users user = usersService.getUserByUsername(username);
            
            String currentUsername = getCurrentUsername();
            if (!canViewProfile(user, currentUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Profile is private");
            }
            
            return ResponseEntity.ok(mapToResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAll() {
        List<Users> users = usersService.getAllUsers();
        List<UserProfileResponse> responses = users.stream()
                .filter(u -> u.getIsActive())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserProfileResponse>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        Page<Users> topUsers = usersService.getTopKarmaUsers(limit);
        List<UserProfileResponse> responses = topUsers.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponse>> search(@RequestParam String q) {
        List<Users> users = usersService.searchUsers(q);
        List<UserProfileResponse> responses = users.stream()
                .filter(u -> u.getIsActive())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Users user) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            Users existing = usersService.getUser(id);
            if (!existing.getUsername().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can only update own profile");
            }

            Users updated = usersService.updateUser(id, user);
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

            Users existing = usersService.getUser(id);
            if (!existing.getUsername().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can only delete own account");
            }

            usersService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
    }

    private boolean canViewProfile(Users user, String currentUsername) {
        if (user.getProfileVisibility() == Users.ProfileVisibility.PUBLIC) {
            return true;
        }
        if (currentUsername == null) {
            return false;
        }
        if (user.getUsername().equals(currentUsername)) {
            return true;
        }
        if (user.getProfileVisibility() == Users.ProfileVisibility.UNIVERSITY) {
            // Check if same university
            try {
                Users currentUser = usersService.getUserByUsername(currentUsername);
                return currentUser.getUniversity() != null && user.getUniversity() != null
                       && currentUser.getUniversity().getId().equals(user.getUniversity().getId());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private UserProfileResponse mapToResponse(Users user) {
        // Get user stats
        Long postCount = postRepository.countByAuthor(user.getUsername());
        Long commentCount = commentRepository.countByAuthor(user.getUsername());
        
        // Get badges - map badge types to readable names
        List<String> badges = badgeService.getUserBadges(user.getId()).stream()
                .map(b -> b.getBadgeType().name())
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .realName(user.getRealName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .universityId(user.getUniversity() != null ? user.getUniversity().getId() : null)
                .universityName(user.getUniversity() != null ? user.getUniversity().getName() : null)
                .graduationYear(user.getGraduationYear())
                .major(user.getMajor())
                .totalKarma(user.getTotalKarma())
                .postKarma(user.getPostKarma())
                .commentKarma(user.getCommentKarma())
                .postCount(postCount != null ? postCount.intValue() : 0)
                .commentCount(commentCount != null ? commentCount.intValue() : 0)
                .badges(badges)
                .isVerified(user.getVerificationStatus() == Users.VerificationStatus.VERIFIED)
                .profileVisibility(user.getProfileVisibility())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
