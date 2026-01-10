package com.example.campusCircle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.campusCircle.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Top-level comments for a post (no parent)
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.parentCommentId IS NULL AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findTopLevelByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.parentCommentId IS NULL AND c.isDeleted = false ORDER BY c.createdAt ASC")
    Page<Comment> findTopLevelByPostId(@Param("postId") Long postId, Pageable pageable);

    // Replies to a comment
    @Query("SELECT c FROM Comment c WHERE c.parentCommentId = :parentId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);

    // All comments by a user
    List<Comment> findByAuthorUsernameAndIsDeletedFalseOrderByCreatedAtDesc(String authorUsername);

    // Count comments on a post
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.postId = :postId AND c.isDeleted = false")
    Long countByPostId(@Param("postId") Long postId);

    // Count comments by user
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.authorUsername = :username AND c.isDeleted = false")
    Long countByAuthor(@Param("username") String username);
}
