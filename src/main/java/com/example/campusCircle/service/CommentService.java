package com.example.campusCircle.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Comment;
import com.example.campusCircle.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    public Comment createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpvoteCount(0);
        comment.setDownvoteCount(0);
        comment.setReplyCount(0);
        comment.setIsDeleted(false);
        
        Comment saved = commentRepository.save(comment);
        
        // Update post comment count
        postService.incrementCommentCount(comment.getPostId());
        
        // If this is a reply, update parent's reply count
        if (comment.getParentCommentId() != null) {
            incrementReplyCount(comment.getParentCommentId());
        }
        
        return saved;
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findTopLevelByPostId(postId);
    }

    public Page<Comment> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository.findTopLevelByPostId(postId, pageable);
    }

    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findRepliesByParentId(parentCommentId);
    }

    public List<Comment> getCommentsByUser(String username) {
        return commentRepository.findByAuthorUsernameAndIsDeletedFalseOrderByCreatedAtDesc(username);
    }

    public Comment updateComment(Long id, String newContent) {
        Comment existing = getCommentById(id);
        existing.setEditedAt(LocalDateTime.now());
        // Note: Content is stored in MongoDB via CommentContentService
        return commentRepository.save(existing);
    }

    public void deleteComment(Long id) {
        Comment comment = getCommentById(id);
        comment.setIsDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);
        
        // Update post comment count
        postService.decrementCommentCount(comment.getPostId());
        
        // If this was a reply, update parent's reply count
        if (comment.getParentCommentId() != null) {
            decrementReplyCount(comment.getParentCommentId());
        }
    }

    public void incrementReplyCount(Long commentId) {
        Comment comment = getCommentById(commentId);
        comment.setReplyCount(comment.getReplyCount() + 1);
        commentRepository.save(comment);
    }

    public void decrementReplyCount(Long commentId) {
        Comment comment = getCommentById(commentId);
        if (comment.getReplyCount() > 0) {
            comment.setReplyCount(comment.getReplyCount() - 1);
        }
        commentRepository.save(comment);
    }

    public void updateVoteCounts(Long commentId, int upvoteChange, int downvoteChange) {
        Comment comment = getCommentById(commentId);
        comment.setUpvoteCount(comment.getUpvoteCount() + upvoteChange);
        comment.setDownvoteCount(comment.getDownvoteCount() + downvoteChange);
        commentRepository.save(comment);
    }

    public Long countByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public Long countByUser(String username) {
        return commentRepository.countByAuthor(username);
    }
}
