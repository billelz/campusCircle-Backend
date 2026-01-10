package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.Notification;
import com.example.campusCircle.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getNotificationById(String id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByRecipientUsernameSorted(username);
    }

    public List<Notification> getUnreadNotifications(String username) {
        return notificationRepository.findUnreadByRecipient(username);
    }

    public long getUnreadCount(String username) {
        return notificationRepository.countByRecipientUsernameAndReadStatusFalse(username);
    }

    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setReadStatus(false);
        return notificationRepository.save(notification);
    }

    public Notification sendNotification(
            String recipientUsername,
            Long recipientUserId,
            Notification.NotificationType type,
            String title,
            String message,
            String senderUsername,
            Notification.ContentReference contentRef,
            Map<String, Object> metadata) {
        
        Notification notification = new Notification();
        notification.setRecipientUsername(recipientUsername);
        notification.setRecipientUserId(recipientUserId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSenderUsername(senderUsername);
        notification.setContentReference(contentRef);
        notification.setMetadata(metadata);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setReadStatus(false);
        
        return notificationRepository.save(notification);
    }

    public Notification markAsRead(String notificationId) {
        return notificationRepository.findById(notificationId)
                .map(notification -> {
                    notification.setReadStatus(true);
                    notification.setReadAt(LocalDateTime.now());
                    return notificationRepository.save(notification);
                })
                .orElse(null);
    }

    public void markAllAsRead(String username) {
        List<Notification> unread = notificationRepository.findUnreadByRecipient(username);
        LocalDateTime now = LocalDateTime.now();
        unread.forEach(notification -> {
            notification.setReadStatus(true);
            notification.setReadAt(now);
        });
        notificationRepository.saveAll(unread);
    }

    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }

    // Convenience methods for common notification types
    public void notifyReply(String recipientUsername, Long recipientUserId, String senderUsername, Long postId, String postTitle) {
        Notification.ContentReference ref = new Notification.ContentReference("POST", postId, postTitle, null);
        sendNotification(recipientUsername, recipientUserId, Notification.NotificationType.REPLY,
                "New Reply", senderUsername + " replied to your post", senderUsername, ref, null);
    }

    public void notifyMention(String recipientUsername, Long recipientUserId, String senderUsername, Long contentId, String contentType) {
        Notification.ContentReference ref = new Notification.ContentReference(contentType, contentId, null, null);
        sendNotification(recipientUsername, recipientUserId, Notification.NotificationType.MENTION,
                "You were mentioned", senderUsername + " mentioned you", senderUsername, ref, null);
    }

    public void notifyUpvote(String recipientUsername, Long recipientUserId, Long contentId, String contentType) {
        Notification.ContentReference ref = new Notification.ContentReference(contentType, contentId, null, null);
        sendNotification(recipientUsername, recipientUserId, Notification.NotificationType.UPVOTE,
                "Your " + contentType.toLowerCase() + " was upvoted", "Someone upvoted your " + contentType.toLowerCase(), null, ref, null);
    }

    public void notifyBadgeEarned(String recipientUsername, Long recipientUserId, String badgeType) {
        sendNotification(recipientUsername, recipientUserId, Notification.NotificationType.BADGE_EARNED,
                "Badge Earned!", "You earned the " + badgeType + " badge!", null, null, Map.of("badgeType", badgeType));
    }
}
