package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.Notification;
import com.example.campusCircle.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String username) {
        return ResponseEntity.ok(notificationService.getUserNotifications(username));
    }

    @GetMapping("/user/{username}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String username) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(username));
    }

    @GetMapping("/user/{username}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("unreadCount", notificationService.getUnreadCount(username));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody SendNotificationRequest request) {
        Notification notification = notificationService.sendNotification(
                request.getRecipientUsername(),
                request.getRecipientUserId(),
                request.getType(),
                request.getTitle(),
                request.getMessage(),
                request.getSenderUsername(),
                request.getContentReference(),
                request.getMetadata()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        Notification notification = notificationService.markAsRead(id);
        if (notification != null) {
            return ResponseEntity.ok(notification);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/user/{username}/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable String username) {
        notificationService.markAllAsRead(username);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/types")
    public ResponseEntity<Notification.NotificationType[]> getNotificationTypes() {
        return ResponseEntity.ok(Notification.NotificationType.values());
    }

    // DTO
    public static class SendNotificationRequest {
        private String recipientUsername;
        private Long recipientUserId;
        private Notification.NotificationType type;
        private String title;
        private String message;
        private String senderUsername;
        private Notification.ContentReference contentReference;
        private Map<String, Object> metadata;

        public String getRecipientUsername() { return recipientUsername; }
        public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }
        public Long getRecipientUserId() { return recipientUserId; }
        public void setRecipientUserId(Long recipientUserId) { this.recipientUserId = recipientUserId; }
        public Notification.NotificationType getType() { return type; }
        public void setType(Notification.NotificationType type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
        public Notification.ContentReference getContentReference() { return contentReference; }
        public void setContentReference(Notification.ContentReference contentReference) { this.contentReference = contentReference; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}
