package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.ModerationQueue;
import com.example.campusCircle.model.CommentContent;
import com.example.campusCircle.repository.CommentContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentContentService {

    @Autowired
    private CommentContentRepository commentContentRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private ModerationQueueService moderationQueueService;

    public List<CommentContent> getAllComments() {
        return commentContentRepository.findAll();
    }

    public Optional<CommentContent> getCommentById(String id) {
        return commentContentRepository.findById(id);
    }

    public CommentContent getCommentContent(Long commentId) {
        return commentContentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("Comment content not found"));
    }

    public Optional<CommentContent> getCommentByCommentId(Long commentId) {
        return commentContentRepository.findByCommentId(commentId);
    }

    public List<CommentContent> searchComments(String keyword) {
        return commentContentRepository.findByBodyTextContaining(keyword);
    }

    public CommentContent createCommentContent(CommentContent commentContent) {
        analyzeAndFlagComment(commentContent);
        return commentContentRepository.save(commentContent);
    }

    private void analyzeAndFlagComment(CommentContent commentContent) {
        Map<String, Object> aiResults = aiService.analyzeContent(commentContent.getContent());
        double toxicityScore = (double) aiResults.get("score");
        List<String> flags = (List<String>) aiResults.get("flags");

        if (toxicityScore > 0.6 || (boolean) aiResults.get("isCrisis")) {
            ModerationQueue moderationQueue = new ModerationQueue();
            moderationQueue.setContentId(String.valueOf(commentContent.getCommentId()));
            moderationQueue.setContentType("comment");
            moderationQueue.setContentText(commentContent.getContent());
            moderationQueue.setAiModerationScore(toxicityScore);
            moderationQueue.setAiFlags(flags);
            moderationQueue.setStatus("pending");
            moderationQueue.setFlaggedAt(LocalDateTime.now());
            moderationQueueService.saveModerationQueue(moderationQueue);
        }
    }

    public CommentContent saveComment(CommentContent commentContent) {
        analyzeAndFlagComment(commentContent);
        return commentContentRepository.save(commentContent);
    }

    public CommentContent updateCommentContent(Long commentId, String content, List<String> mediaUrls) {
        Optional<CommentContent> existing = commentContentRepository.findByCommentId(commentId);
        if (existing.isPresent()) {
            CommentContent commentContent = existing.get();
            commentContent.setContent(content);
            commentContent.setMediaUrls(mediaUrls);
            return commentContentRepository.save(commentContent);
        }
        throw new RuntimeException("Comment content not found");
    }

    public void deleteComment(String id) {
        commentContentRepository.deleteById(id);
    }

    public void deleteCommentByCommentId(Long commentId) {
        commentContentRepository.deleteByCommentId(commentId);
    }
}