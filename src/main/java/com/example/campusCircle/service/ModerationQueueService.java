package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.ModerationQueue;
import com.example.campusCircle.repository.ModerationQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ModerationQueueService {

    @Autowired
    private ModerationQueueRepository moderationQueueRepository;

    public List<ModerationQueue> getAllModerationQueue() {
        return moderationQueueRepository.findAll();
    }

    public Optional<ModerationQueue> getModerationQueueById(String id) {
        return moderationQueueRepository.findById(id);
    }

    public Optional<ModerationQueue> getModerationQueueByContentId(String contentId) {
        return moderationQueueRepository.findByContentId(contentId);
    }

    public List<ModerationQueue> getModerationQueueByStatus(String status) {
        return moderationQueueRepository.findByStatus(status);
    }

    public List<ModerationQueue> getModerationQueueByContentType(String contentType) {
        return moderationQueueRepository.findByContentType(contentType);
    }

    public List<ModerationQueue> getModerationQueueByAuthor(String authorUsername) {
        return moderationQueueRepository.findByAuthorUsername(authorUsername);
    }

    public List<ModerationQueue> getModerationQueueByScoreAbove(Double score) {
        return moderationQueueRepository.findByAiModerationScoreGreaterThan(score);
    }

    public List<ModerationQueue> getModerationQueueFlaggedAfter(LocalDateTime date) {
        return moderationQueueRepository.findByFlaggedAtAfter(date);
    }

    public List<ModerationQueue> getModerationQueueByReviewer(String reviewedBy) {
        return moderationQueueRepository.findByReviewedBy(reviewedBy);
    }

    public List<ModerationQueue> getModerationQueueByStatusAndType(String status, String contentType) {
        return moderationQueueRepository.findByStatusAndContentType(status, contentType);
    }

    public ModerationQueue saveModerationQueue(ModerationQueue moderationQueue) {
        if (moderationQueue.getFlaggedAt() == null) {
            moderationQueue.setFlaggedAt(LocalDateTime.now());
        }
        if (moderationQueue.getStatus() == null) {
            moderationQueue.setStatus("pending");
        }
        return moderationQueueRepository.save(moderationQueue);
    }

    public ModerationQueue updateModerationQueue(String id, ModerationQueue moderationQueue) {
        moderationQueue.setId(id);
        return moderationQueueRepository.save(moderationQueue);
    }

    public ModerationQueue reviewContent(String id, String reviewedBy, String status, String action) {
        Optional<ModerationQueue> existing = moderationQueueRepository.findById(id);
        if (existing.isPresent()) {
            ModerationQueue item = existing.get();
            item.setReviewedBy(reviewedBy);
            item.setReviewedAt(LocalDateTime.now());
            item.setStatus(status);
            item.setModerationAction(action);
            return moderationQueueRepository.save(item);
        }
        return null;
    }

    public void deleteModerationQueue(String id) {
        moderationQueueRepository.deleteById(id);
    }

    public void deleteModerationQueueByContentId(String contentId) {
        moderationQueueRepository.deleteByContentId(contentId);
    }
}
