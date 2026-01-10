package com.example.campusCircle.controller;

import com.example.campusCircle.model.Subscription;
import com.example.campusCircle.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Subscription>> getChannelSubscribers(@PathVariable Long channelId) {
        return ResponseEntity.ok(subscriptionService.getChannelSubscribers(channelId));
    }

    @GetMapping("/channel/{channelId}/count")
    public ResponseEntity<Map<String, Object>> getSubscriberCount(@PathVariable Long channelId) {
        Map<String, Object> response = new HashMap<>();
        response.put("channelId", channelId);
        response.put("subscriberCount", subscriptionService.getSubscriberCount(channelId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> isSubscribed(
            @RequestParam Long userId,
            @RequestParam Long channelId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("channelId", channelId);
        response.put("isSubscribed", subscriptionService.isSubscribed(userId, channelId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Subscription> subscribe(@RequestBody SubscribeRequest request) {
        Subscription subscription = subscriptionService.subscribe(
                request.getUserId(),
                request.getChannelId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(
            @RequestParam Long userId,
            @RequestParam Long channelId) {
        subscriptionService.unsubscribe(userId, channelId);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Unsubscribed successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/notifications")
    public ResponseEntity<Subscription> toggleNotifications(
            @RequestParam Long userId,
            @RequestParam Long channelId,
            @RequestParam boolean enabled) {
        Subscription subscription = subscriptionService.toggleNotifications(userId, channelId, enabled);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/notifications")
    public ResponseEntity<List<Subscription>> getSubscriptionsWithNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsWithNotifications(userId));
    }

    // DTO
    public static class SubscribeRequest {
        private Long userId;
        private Long channelId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getChannelId() { return channelId; }
        public void setChannelId(Long channelId) { this.channelId = channelId; }
    }
}
