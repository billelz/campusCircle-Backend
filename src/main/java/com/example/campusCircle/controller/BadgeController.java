package com.example.campusCircle.controller;

import com.example.campusCircle.model.Badge;
import com.example.campusCircle.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @GetMapping
    public ResponseEntity<List<Badge>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        return badgeService.getBadgeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Badge>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }

    @GetMapping("/user/{userId}/has/{badgeType}")
    public ResponseEntity<Boolean> hasBadge(
            @PathVariable Long userId,
            @PathVariable Badge.BadgeType badgeType) {
        return ResponseEntity.ok(badgeService.hasBadge(userId, badgeType));
    }

    @PostMapping("/award")
    public ResponseEntity<Badge> awardBadge(@RequestBody AwardBadgeRequest request) {
        Badge badge = badgeService.awardBadge(
                request.getUserId(),
                request.getBadgeType(),
                request.getChannelId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(badge);
    }

    @DeleteMapping("/user/{userId}/type/{badgeType}")
    public ResponseEntity<Void> revokeBadge(
            @PathVariable Long userId,
            @PathVariable Badge.BadgeType badgeType) {
        badgeService.revokeBadge(userId, badgeType);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-karma/{userId}")
    public ResponseEntity<List<Badge>> checkAndAwardKarmaBadges(@PathVariable Long userId) {
        badgeService.checkAndAwardKarmaBadges(userId);
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }

    @GetMapping("/types")
    public ResponseEntity<Badge.BadgeType[]> getAllBadgeTypes() {
        return ResponseEntity.ok(Badge.BadgeType.values());
    }

    // DTO
    public static class AwardBadgeRequest {
        private Long userId;
        private Badge.BadgeType badgeType;
        private Long channelId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Badge.BadgeType getBadgeType() { return badgeType; }
        public void setBadgeType(Badge.BadgeType badgeType) { this.badgeType = badgeType; }
        public Long getChannelId() { return channelId; }
        public void setChannelId(Long channelId) { this.channelId = channelId; }
    }
}
