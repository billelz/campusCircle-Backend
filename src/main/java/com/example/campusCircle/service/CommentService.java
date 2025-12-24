package com.example.campusCircle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Comment;
import com.example.campusCircle.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment updateComment(Long id, Comment updated) {
        Comment existing = getCommentById(id);

        existing.setPost(updated.getPost());
        existing.setAuthorUsername(updated.getAuthorUsername());
        existing.setParentComment(updated.getParentComment());

        return commentRepository.save(existing);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
