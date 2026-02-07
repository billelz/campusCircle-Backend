package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByRecipientUsername(String recipientUsername);
    
    List<Notification> findByRecipientUserId(Long recipientUserId);
    
    List<Notification> findByRecipientUsernameAndReadStatusFalse(String recipientUsername);
    
    @Query(value = "{ 'recipient_username': ?0 }", sort = "{ 'created_at': -1 }")
    List<Notification> findByRecipientUsernameSorted(String recipientUsername);
    
    @Query("{ 'recipient_username': ?0, 'read_status': false }")
    List<Notification> findUnreadByRecipient(String recipientUsername);
    
    long countByRecipientUsernameAndReadStatusFalse(String recipientUsername);
    
    List<Notification> findByType(Notification.NotificationType type);
}
