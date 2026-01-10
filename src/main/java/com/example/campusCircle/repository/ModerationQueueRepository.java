package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.ModerationQueue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModerationQueueRepository extends MongoRepository<ModerationQueue, String> {
    
    Optional<ModerationQueue> findByContentId(String contentId);
    
    List<ModerationQueue> findByStatus(String status);
    
    List<ModerationQueue> findByContentType(String contentType);
    
    List<ModerationQueue> findByAuthorUsername(String authorUsername);
    
    List<ModerationQueue> findByAiModerationScoreGreaterThan(Double score);
    
    List<ModerationQueue> findByFlaggedAtAfter(LocalDateTime date);
    
    List<ModerationQueue> findByReviewedBy(String reviewedBy);
    
    List<ModerationQueue> findByStatusAndContentType(String status, String contentType);
    
    void deleteByContentId(String contentId);
}
