package com.example.campusCircle.service;

import com.example.campusCircle.model.CommentContent;
import com.example.campusCircle.repository.CommentContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentContentService {

    @Autowired
    private CommentContentRepository commentContentRepository;

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
        return commentContentRepository.save(commentContent);
    }

    public CommentContent saveComment(CommentContent commentContent) {
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