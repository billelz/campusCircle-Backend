package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.CommentContent;
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

    public Optional<CommentContent> getCommentByCommentId(String commentId) {
        return commentContentRepository.findByCommentId(commentId);
    }

    public List<CommentContent> searchComments(String keyword) {
        return commentContentRepository.findByBodyTextContaining(keyword);
    }

    public CommentContent saveComment(CommentContent commentContent) {
        return commentContentRepository.save(commentContent);
    }

    public void deleteComment(String id) {
        commentContentRepository.deleteById(id);
    }

    public void deleteCommentByCommentId(String commentId) {
        commentContentRepository.deleteByCommentId(commentId);
    }
}